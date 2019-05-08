//package com.peerfintech.sys;
//
//import com.app.bean.FBInfo;
//import com.app.bean.FBOrderInfo;
//import com.app.bean.FBPeerInfo;
//import com.app.util.EndorsementPolicy;
//import com.app.util.FBUtil;
//import com.google.common.collect.Lists;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@Order(1)
//public class ApplicationInitStartup implements ApplicationRunner {
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("系统启动执行 -！");
//        FBInfo fi = new FBInfo();
//        fi.setFileSkDir("src/test/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore");
//        fi.setSignCerts("src/test/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem");
//        fi.setMspId("Org1MSP");
//        fi.setOrgName("org1");
//        fi.setPolicy(EndorsementPolicy.OR);
//        fi.setUserName("admin");
//        List<FBPeerInfo> peerInfoList = Lists.newArrayList();
//        FBPeerInfo fb = new FBPeerInfo();
//        fb.setEnableTls(true);
//        fb.setHostname("peer0.org1.example.com");
//        fb.setPeerTlsFilePath("src/test/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt");
//        fb.setUrl("192.168.66.101:7051");
//        peerInfoList.add(fb);
//        FBPeerInfo fb2 = new FBPeerInfo();
//        fb2.setEnableTls(true);
//        fb2.setHostname("peer0.org2.example.com");
//        fb2.setPeerTlsFilePath("src/test/crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.crt");
//        fb2.setUrl("192.168.66.101:9051");
//        peerInfoList.add(fb2);
//        fi.setPeerList(peerInfoList);
//        FBOrderInfo orderInfo = new FBOrderInfo();
//        orderInfo.setEnableTls(true);
//        orderInfo.setHostname("orderer.example.com");
//        orderInfo.setOrdererTlsFilePath("src/test/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt");
//        orderInfo.setUrl("192.168.66.101:7050");
//        fi.setOrder(orderInfo);
//        FBUtil.init(fi);
//    }
//}
