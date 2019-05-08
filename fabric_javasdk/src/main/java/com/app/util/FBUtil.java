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

package com.app.util;

import com.app.bean.FBChannelInfo;
import com.app.bean.FBInfo;
import com.app.bean.FBOrderInfo;
import com.app.bean.FBPeerInfo;
import com.app.chaincode.invocation.FbClient;
import com.app.client.ChannelClient;
import com.app.client.FabricClient;
import com.app.user.UserContext;
import com.google.common.collect.Lists;
import io.netty.util.internal.StringUtil;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.cglib.beans.BeanMap;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * FBUtil for application init
 *
 */

public class  FBUtil {

	public static FbClient fb;

	public static FBInfo fi;
	public static List<Peer> peerList = Lists.newArrayList();
	public static List<Orderer> ordererList =Lists.newArrayList();
	public static FabricClient fabClient;

	public static Logger logger = Logger.getLogger(FBUtil.class.getName());

	/**
	 * init system peer and orderer
	 */
	public static void init()  {
		try {
			 fi =initFB();
			logger.info("fabric begin init ，params ：" + fi.toString());
			fb = new FbClient(fi.getFileSkDir(), fi.getSignCerts());
			UserContext userContext = fb.initUserContext(fi.getUserName(),fi.getOrgName(),fi.getMspId());
			 fabClient = new FabricClient(userContext);
			if (StringUtil.isNullOrEmpty(fi.getFileSkDir()) || StringUtil.isNullOrEmpty(fi.getSignCerts())) {
				throw new Exception("wrong signature file !");
			}
			if (fi.getOrderList() == null || fi.getPeerList() == null) {
				throw new Exception("order and peer must exist !");
			}
//			if (fi.getPolicy().equals(EndorsementPolicy.AND) && fi.getPeerList().size() < 2) {
//				throw new Exception("peers num not fetch endorsement policy");
//			}
			for (FBPeerInfo p : fi.getPeerList()) {
				Properties prop = new Properties();
				prop.setProperty("hostnameOverride", p.getHostname());
				prop.setProperty("sslProvider", "openSSL");
				String url = "";
				if (p.isUseTLS()) {
					prop.setProperty("negotiationType", "TLS");
					url = "grpcs://" + p.getHost();
					File peerCert = Paths.get(p.getTlsPath()).toFile();
					prop.setProperty("pemFile", peerCert.getAbsolutePath());
				} else {
					url = "grpc://" + p.getHost();
				}
				Peer peer = fabClient.getInstance().newPeer(p.getHostname(), url, prop);
				peerList.add(peer);
			}
			for (FBOrderInfo o : fi.getOrderList()) {
				Properties prop1 = new Properties();
				prop1.setProperty("hostnameOverride",o.getHostname());
				prop1.setProperty("sslProvider", "openSSL");
				String url ="";
				if (o.isUseTLS()) {
					prop1.setProperty("negotiationType", "TLS");
					url = "grpcs://" + o.getHost();
					File ordererCert1 = Paths.get(o.getTlsPath()).toFile();
					prop1.setProperty("pemFile", ordererCert1.getAbsolutePath());
				}else {
					url = "grpc://" + o.getHost();
				}

				Orderer orderer = fabClient.getInstance().newOrderer(o.getHostname(), url, prop1);
				ordererList.add(orderer);
			}

		}catch (Exception e){
			e.printStackTrace();
		}


	}



	private static FBInfo initFB() {
		try {
			Yaml yaml = new Yaml();
			Path path = Paths.get("src/main/resources/","client_sdk.yaml");
			if (Files.exists(path)) {
				//获取test.yaml文件中的配置数据，然后转换为obj，
				Object obj =yaml.load(new FileInputStream(path.toFile()));
				System.out.println(obj);
				//也可以将值转换为Map
				Map<String,Object> map =(Map)yaml.load(new FileInputStream(path.toFile()));
				System.out.println(map);
				FBChannelInfo fc =mapToBean( (Map)map.get("channels"),new FBChannelInfo());
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
				Map<String,Map> eventChannelMap =  (Map)map.get("eventchannels");
				List<String> channeNames = Lists.newArrayList(eventChannelMap.keySet());
				fb.setChannelName(channeNames.get(0));
				fb.setEventName(map.get("eventName").toString());
				return fb;
			}else{
				throw new Exception("file client_sdk not exist!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

	public static Channel createEventChannel(String channelName) throws Exception {
        UserContext adminUserContext = FBUtil.fb.getUserContext();
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
        return channel;
    }

	public static  void createNewOrder(){
		try {
			for (int i=0;i<ordererList.size();i++
			) {
				Orderer orderer = fabClient.getInstance().newOrderer(ordererList.get(i).getName(), ordererList.get(i).getUrl(), ordererList.get(i).getProperties());
				ordererList.set(i,orderer);
			}

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public static  void createNewPeers(){
		try {

			for (int i=0;i<peerList.size();i++
				 ) {
				Peer p1 = fabClient.getInstance().newPeer(peerList.get(i).getName(), peerList.get(i).getUrl(), peerList.get(i).getProperties());
				peerList.set(i,p1);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}



}
