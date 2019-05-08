package com.peerfintech.sys;

import com.alibaba.fastjson.JSONArray;
import com.app.util.FBUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.hyperledger.fabric.sdk.Channel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

@Component
@Order(1)
public class ApplicationStartup implements ApplicationRunner {
    public static Logger logger = Logger.getLogger(ApplicationStartup.class.getName());


    @Override
    public void run(ApplicationArguments args)  throws Exception {
        System.out.println("系统启动执行 ！");
        FBUtil.init();
        BlockingQueue queue = new ArrayBlockingQueue(Integer.MAX_VALUE >> 12);
        FBUtil.fb.listen(queue);
        while (true) {
            System.out.println("--------start----------");
            String es =queue.take().toString();
            System.out.println(es);
            System.out.println("--------end----------");
        }
    }

    public synchronized Map<Long,Long> checkMaxBlockHeight(String channelName,Long bn) {
        Long blockNumber =3L;
        Long txId = 0L;
        Map<Long,Long> resMap =Maps.newHashMap();
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


    static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();
        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();

    }
    public void getEventInfoByBlock( BlockInfo b,Long txIndex,BlockingQueue queue){
        int i = 0;
        int transactionCount = 0;
        for (BlockInfo.EnvelopeInfo envelopeInfo : b.getEnvelopeInfos()) {
            ++i;

            out("  Transaction number %d has transaction id: %s", i, envelopeInfo.getTransactionID());
            final String channelId = envelopeInfo.getChannelId();
            //assertTrue("foo".equals(channelId) || "bar".equals(channelId));

            out("  Transaction number %d has channel id: %s", i, channelId);
            out("  Transaction number %d has epoch: %d", i, envelopeInfo.getEpoch());
            out("  Transaction number %d has transaction timestamp: %tB %<te,  %<tY  %<tT %<Tp", i, envelopeInfo.getTimestamp());
            out("  Transaction number %d has type id: %s", i, "" + envelopeInfo.getType());
            out("  Transaction number %d has nonce : %s", i, "" + Hex.encodeHexString(envelopeInfo.getNonce()));
            out("  Transaction number %d has submitter mspid: %s,  certificate: %s", i, envelopeInfo.getCreator().getMspid(), envelopeInfo.getCreator().getId());

            if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
                ++transactionCount;
                BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                out("  Transaction number %d has %d actions", i, transactionEnvelopeInfo.getTransactionActionInfoCount());
                //assertEquals(1, transactionEnvelopeInfo.getTransactionActionInfoCount()); // for now there is only 1 action per transaction.
                out("  Transaction number %d isValid %b", i, transactionEnvelopeInfo.isValid());
                //assertEquals(transactionEnvelopeInfo.isValid(), true);
                out("  Transaction number %d validation code %d", i, transactionEnvelopeInfo.getValidationCode());
                //assertEquals(0, transactionEnvelopeInfo.getValidationCode());

                int j = 0;
                List<BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo> list = Lists.newArrayList(transactionEnvelopeInfo.getTransactionActionInfos());
                if(txIndex.intValue()<list.size()){
                    List<BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo>  forList =list.subList(txIndex.intValue(),list.size());
                    for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo :forList ) {

                        ++j;
                        String chaincodeIDName = transactionActionInfo.getChaincodeIDName();
                        String chaincodeIDVersion = transactionActionInfo.getChaincodeIDVersion();
                        String chaincodeIDPath = transactionActionInfo.getChaincodeIDPath();
                        out("Transaction action %d proposal chaincodeIDName: %s, chaincodeIDVersion: %s,  chaincodeIDPath: %s ", j,
                                chaincodeIDName, chaincodeIDVersion, chaincodeIDPath);
                        // Check to see if we have our expected event.
                        ChaincodeEvent chaincodeEvent = transactionActionInfo.getEvent();
                        try {
                            // 处理消息 如 发送到第三方  更新本地文件
                            //  to do 针对 txId 的下标  做精确循环
                            handleMessage(b.getChannelId(),b.getBlockNumber(),j);
                            queue.add(b.getBlockNumber());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                }

                }

                }
            }
}

    public void handleMessage(String channelName,Long  bn, int txIndex) {
        Long blockNumber =0L;
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
            }else{
                map.put("block_number",bn);
                map.put("tx_index",txIndex);
            }
            Files.write(path, JSONArray.toJSONBytes(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
