/****************************************************** 
 *  Copyright 2018 IBM Corporation 
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */
package com.app.chaincode.invocation;

import com.alibaba.fastjson.JSONArray;
import com.app.bean.*;
import com.app.client.ChannelClient;
import com.app.client.FabricClient;
import com.app.user.UserContext;
import com.app.util.FBUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

/**
 * @author Balaji Kadambi
 */

public class FbClient {


    private String fileSKDir;//peer sk file like  peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/keystore
    private String signcertsDir;//peer signcertsDir like peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/signcerts/Admin@org2.example.com-cert.pem

    private UserContext userContext;

    public String getFileSKDir() {
        return fileSKDir;
    }

    public void setFileSKDir(String fileSKDir) {
        this.fileSKDir = fileSKDir;
    }

    public String getSigncertsDir() {
        return signcertsDir;
    }

    public void setSigncertsDir(String signcertsDir) {
        this.signcertsDir = signcertsDir;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }


    public FbClient(String fileSKDir, String signcertsDir) {
        this.fileSKDir = fileSKDir;
        this.signcertsDir = signcertsDir;
    }

    public UserContext initUserContext(String userName, String orgName, String mspId) {
        try {

            this.userContext = this.initAdminUser(findFileSk(fileSKDir),
                    new File(signcertsDir), userName, orgName, mspId);
            return userContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String query(String channelName, String chaincodeName, String funcName, String[] args) {
        try {
            String res = "";
            UserContext adminUserContext = FBUtil.fb.userContext;
            if (null == adminUserContext) {
                throw new Exception("initUserContent before");
            }
            FabricClient fabClient = new FabricClient(adminUserContext);
            ChannelClient channelClient = fabClient.createChannelClient(channelName);
            Channel channel = channelClient.getChannel();
            if (!channel.isInitialized()) {
                FBUtil.createNewOrder();
                FBUtil.createNewPeers();;//clear peer channel before
                for (Peer p : FBUtil.peerList) {
                    channel.addPeer(p);
                }
                for (Orderer o : FBUtil.ordererList) {
                    channel.addOrderer(o);
                }
                channel.initialize();
            }
            Logger.getLogger(FbClient.class.getName()).log(Level.INFO, "channel.isInitialized() = " + channel.isInitialized());
            Logger.getLogger(FbClient.class.getName()).log(Level.INFO, "Querying for " + chaincodeName + "...");
            Collection<ProposalResponse> responsesQuery = channelClient.queryByChainCode(chaincodeName, funcName, args);
            for (ProposalResponse pres : responsesQuery) {
                res = new String(pres.getChaincodeActionResponsePayload());
                Logger.getLogger(FbClient.class.getName()).log(Level.INFO, "查询结果：" + res);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public FBInvokeRes invoke(String channelName, String chaincodeName, String funcName, String[] args) {
        FBInvokeRes res =new FBInvokeRes();
        try {
            FabricClient fabClient = new FabricClient(FBUtil.fb.userContext);
            ChannelClient channelClient = fabClient.createChannelClient(channelName);
            Channel channel = channelClient.getChannel();
            if (!channel.isInitialized()) {
                FBUtil.createNewOrder();//clear order channel before
                FBUtil.createNewPeers();;//clear peer channel before
                for (Peer p : FBUtil.peerList) {
                    channel.addPeer(p);
                }
                for (Orderer o : FBUtil.ordererList) {
                    channel.addOrderer(o);
                }
                channel.initialize();
            }
            Logger.getLogger(FbClient.class.getName()).log(Level.INFO, "channel.isInitialized() = " + channel.isInitialized());
            TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
            ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeName).build();
            request.setChaincodeID(ccid);
            request.setFcn(funcName);
            request.setArgs(args);
            request.setProposalWaitTime(1000);
            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
            tm2.put("result", ":)".getBytes(UTF_8));
            request.setTransientMap(tm2);
            res =  channelClient.sendTransactionProposalWithValidate(res,request);
        } catch (Exception e) {
            e.printStackTrace();
            res.setMsg(e.getMessage());
        }
        return res;
    }

    public static Channel getChannel(){
        try {
            FabricClient fabClient = new FabricClient(FBUtil.fb.userContext);
            ChannelClient channelClient = fabClient.createChannelClient(FBUtil.fi.getChannelName());
            Channel channel = channelClient.getChannel();
            if (!channel.isInitialized()) {
                FBUtil.createNewOrder();//clear order channel before
                FBUtil.createNewPeers();;//clear peer channel before
                for (Peer p : FBUtil.peerList) {
                    channel.addPeer(p);
                }
                for (Orderer o : FBUtil.ordererList) {
                    channel.addOrderer(o);
                }
                channel.initialize();
            }
            return channel;
        } catch (CryptoException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void listen(BlockingQueue queue) throws  Exception{
        Channel channel =FBUtil.fb.getChannel();
        String eventName =FBUtil.fi.getEventName();
        String chaincodeEventListenerHandle = channel.registerChaincodeEventListener(Pattern.compile(".*"),
                Pattern.compile(Pattern.quote(eventName)),
                (handle, blockEvent, chaincodeEvent) -> {
                    // chaincodeEvents.add(new ChaincodeEventCapture(handle, blockEvent, chaincodeEvent));
                    String es = blockEvent.getPeer() != null ? blockEvent.getPeer().getName() : blockEvent.getEventHub().getName();
//                    out("RECEIVED Chaincode event with handle: %s, chaincode Id: %s, : %s, "
//                                    + "transaction id: %s, event payload: \"%s\", from eventhub: %s",
//                            handle, chaincodeEvent.getChaincodeId(),
//                            chaincodeEvent.getEventName(),
//                            chaincodeEvent.getTxId(),
//                            new String(chaincodeEvent.getPayload()), es);
                    Map<String,Object> map =Maps.newHashMap();
                    map.put("blockNumber", blockEvent.getBlockNumber());
                    map.put("chaincodeId",chaincodeEvent.getChaincodeId());
                    map.put("eventName",chaincodeEvent.getEventName());
                    map.put("txId", chaincodeEvent.getTxId());
                    map.put("payload", new String (chaincodeEvent.getPayload()));
                    map.put("eventhub", es);
                    //System.out.println("block number is "+blockEvent.getBlockNumber());
                    Map<Long,Long> txIndexMap = checkMaxBlockHeight(channel.getName(),blockEvent.getBlockNumber());
                    if (txIndexMap.isEmpty()) {
                        return;
                    }
                    List<Long> nums =new ArrayList<>(txIndexMap.keySet());
                    if(!nums.isEmpty()){
                        for (Long num:nums) {
                            try {
                                BlockInfo b =channel.queryBlockByNumber(num);
                                Long txIndex = txIndexMap.get(num);
                                getEventInfoByBlock(b,txIndex,queue);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    queue.add(map);
                    try {
                        Map<String,Object> map2 =Maps.newHashMap();
                        Path path = Paths.get("src/main/resources/",channel.getName()+".current.info");
                        map2.put("block_number",blockEvent.getBlockNumber());
                        map2.put("tx_index",0);
                        Files.write(path, JSONArray.toJSONBytes(map2));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }

    private static void getEventInfoByBlock( BlockInfo b,Long txIndex,BlockingQueue queue){
        int i = 0;
        int transactionCount = 0;
        for (BlockInfo.EnvelopeInfo envelopeInfo : b.getEnvelopeInfos()) {
            ++i;
            final String channelId = envelopeInfo.getChannelId();
            if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
                ++transactionCount;
                BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                int j = 0;
                List<BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo> list = Lists.newArrayList(transactionEnvelopeInfo.getTransactionActionInfos());
                if(txIndex.intValue()<list.size()){
                    List<BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo>  forList =list.subList(txIndex.intValue(),list.size());
                    for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo :forList ) {
                        Map<String,Object> map =Maps.newHashMap();
                        ++j;
                        ChaincodeEvent chaincodeEvent = transactionActionInfo.getEvent();
                        map.put("blockNumber",b.getBlockNumber());
                        map.put("chaincodeId",chaincodeEvent.getChaincodeId());
                        map.put("eventName",chaincodeEvent.getEventName());
                        map.put("txId", chaincodeEvent.getTxId());
                        map.put("payload", new String (chaincodeEvent.getPayload()));
                        map.put("eventhub", "");//主动查询
                        queue.add(b.getBlockNumber());
                        try {
                            Map<String,Object> map2 =Maps.newHashMap();
                            Path path = Paths.get("src/main/resources/",channelId+".current.info");
                            map2.put("block_number",b.getBlockNumber());
                            map2.put("tx_index",j);
                            Files.write(path, JSONArray.toJSONBytes(map2));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
        }
    }


    private static synchronized Map<Long,Long> checkMaxBlockHeight(String channelName,Long bn) {
        Long blockNumber =3L;
        Long txId = 0L;
        Map<Long,Long> resMap = Maps.newHashMap();
        Map<String,Object> map = Maps.newHashMap();
        try {
            Path path = Paths.get("src/main/resources/",channelName+".current.info");
            boolean exist = Files.exists(path);
            if(!exist){
                Files.createFile(path);
            }
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for(String line : lines) {
                sb.append(line);
            }
            if(StringUtils.isEmpty(sb.toString())){
                blockNumber=0L;
                map.put("block_number",blockNumber);
                map.put("tx_index",0);
                Files.write(path, JSONArray.toJSONBytes(map));
            }else{
                Map<String,Object> res = JSONArray.parseObject(sb.toString());
                if(res.keySet().contains("block_number")){
                    blockNumber = Long.valueOf(res.get("block_number").toString());
                    txId =Long.valueOf(res.get("tx_index").toString());
                    int size =bn.intValue()-blockNumber.intValue()+1;
                    if (size>0) {
                        for (int i =0;i<size;i++) {
                            if(i==0){
                                resMap.put(blockNumber+i,txId+1);
                            }else{
                                resMap.put(blockNumber+i,0L);
                            }
                        }
                    }
                }else{
                    map.put("block_number",blockNumber);
                    map.put("tx_index",0);
                    Files.write(path, JSONArray.toJSONBytes(map));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resMap;
    }
    private UserContext initAdminUser(File privateKeyFile,
                                      File certificateFile, String userName, String orgName, String mspId) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        try {
            // Try to get the SampleUser state from the cache
            UserContext sampleUser = null;

            // Create the SampleUser and try to restore it's state from the key value store (if found).
            sampleUser = new UserContext();
            sampleUser.setName(userName);
            sampleUser.setAffiliation(orgName);
            sampleUser.setMspId(mspId);
            String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");

            PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));

            sampleUser.setEnrollment(new FbClient.SampleStoreEnrollement(privateKey, certificate));

            //sampleUser.saveState();

            return sampleUser;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            throw e;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw e;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw e;
        }

    }

    private class SampleStoreEnrollement implements Enrollment, Serializable {

        private static final long serialVersionUID = -2784835212445309006L;
        private final PrivateKey privateKey;
        private final String certificate;

        SampleStoreEnrollement(PrivateKey privateKey, String certificate) {

            this.certificate = certificate;

            this.privateKey = privateKey;
        }

        @Override
        public PrivateKey getKey() {

            return privateKey;
        }

        @Override
        public String getCert() {
            return certificate;
        }

    }

    static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        final Reader pemReader = new StringReader(new String(data));

        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }

        //PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()).getPrivateKey(pemPair);
        return privateKey;
    }


    private File findFileSk(String directorys) {

        File directory = new File(directorys);

        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
            throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }

        return matches[0];

    }

//    public static void main(String[] args) {
//        System.out.println("application start");
//
//    }
public static void main(String[] args) {

    try {
        Yaml yaml = new Yaml();
        Path path = Paths.get("src/main/resources/","client_sdk.yaml");
        if (Files.exists(path)) {
            //获取test.yaml文件中的配置数据，然后转换为obj，
            Object obj =yaml.load(new FileInputStream(path.toFile()));
            System.out.println(obj);
            //也可以将值转换为Map
            Map<String,Map> map =(Map)yaml.load(new FileInputStream(path.toFile()));
            System.out.println(map);
            FBChannelInfo fc =mapToBean( map.get("channels"),new FBChannelInfo());
            FBInfo fb = new FBInfo();
            String configPath =fc.getMspConfigPath();
            fb.setFileSkDir(configPath+"/keystore");
            String [] temps  =configPath.split("/");
            String sinCert =temps[temps.length-2];
            fb.setSignCerts(configPath+"/signcerts/"+sinCert+"-cert.pem");
            fb.setMspId(fc.getLocalMspId());
            //解析peer
            List<FBPeerInfo> peerInfoList = Lists.newArrayList();
            Map<String,Map> map1 =  (Map)map.get("peers");
            List<String> peerNames = Lists.newArrayList(map1.keySet());
            for (String peerName:peerNames
                 ) {
                FBPeerInfo fp =mapToBean(map1.get(peerName),new FBPeerInfo());
                peerInfoList.add(fp);
            }
            List<FBOrderInfo> fbOrderList = Lists.newArrayList();
            Map<String,Map> mapOrder =  (Map)map.get("orderers");
            List<String> orderNames = Lists.newArrayList(mapOrder.keySet());
            for (String orderName:orderNames
            ) {
                FBOrderInfo fp =mapToBean(mapOrder.get(orderName),new FBOrderInfo());
                fbOrderList.add(fp);
            }
            fb.setPeerList(peerInfoList);
            fb.setOrderList(fbOrderList);
            System.out.println(fb.toString());

        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    /**
     * 将map装换为javabean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }
}
