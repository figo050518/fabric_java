package com.peerfintech.test;


import com.alibaba.fastjson.JSONArray;
import com.app.bean.FBInvokeRes;
import com.app.config.Config;
import com.app.util.FBUtil;
import com.google.common.collect.Maps;
import com.peerfintech.bean.Factor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
public class TestController {


    @RequestMapping(value = "/query")
    @ResponseBody
    String query( String businessno){
        try {

//            FbClient fb = new FbClient("src/test/crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/keystore","src/test/crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/signcerts/Admin@org2.example.com-cert.pem");
//            UserContext userContext = fb.initUserContext();
//            FabricClient fabClient = new FabricClient(userContext);
//            Properties prop1 =new Properties();
//            prop1.setProperty("hostnameOverride", "orderer.example.com");
//            prop1.setProperty("sslProvider", "openSSL");
//            prop1.setProperty("negotiationType", "TLS");
//            File ordererCert1 = Paths.get("src/test/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt").toFile();
//            prop1.setProperty("pemFile", ordererCert1.getAbsolutePath());
//            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,prop1);
//            Properties prop =new Properties();
//            prop.setProperty("hostnameOverride", "peer0.org1.example.com");
//            prop.setProperty("sslProvider", "openSSL");
//            prop.setProperty("negotiationType", "TLS");
//            File ordererCert = Paths.get("src/test/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt").toFile();
//            prop.setProperty("pemFile", ordererCert.getAbsolutePath());
//            Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL,prop);
//            Properties prop2 =new Properties();
//            prop2.setProperty("hostnameOverride", "peer0.org2.example.com");
//            prop2.setProperty("sslProvider", "openSSL");
//            prop2.setProperty("negotiationType", "TLS");
//            prop2.setProperty("trustServerCertificate", "true");
//            File peerCert2 = Paths.get("src/test/crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.crt").toFile();
//            prop2.setProperty("pemFile", peerCert2.getAbsolutePath());
//            Peer peer2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL,prop2);
            String res = FBUtil.fb.query(Config.CHANNEL_NAME,"factor","QueryDataByBusinessNo",new String[] {businessno.toString()});
            System.out.println("查询结果"+res);
            return "查询结果"+res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @RequestMapping(value = "/tradeinfo")
    @ResponseBody
    String tradeinfo( String fabrictxid){
        try {
//            FbClient fb = new FbClient("src/test/crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/keystore","src/test/crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/signcerts/Admin@org2.example.com-cert.pem");
//            UserContext userContext = fb.initUserContext();
//            FabricClient fabClient = new FabricClient(userContext);
//            Properties prop1 =new Properties();
//            prop1.setProperty("hostnameOverride", "orderer.example.com");
//            prop1.setProperty("sslProvider", "openSSL");
//            prop1.setProperty("negotiationType", "TLS");
//            prop1.setProperty("trustServerCertificate", "true");
//            File ordererCert1 = Paths.get("D:/workspace/javasdk/src/test/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt").toFile();
//            prop1.setProperty("pemFile", ordererCert1.getAbsolutePath());
//            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,prop1);
//            Properties prop =new Properties();
//            prop.setProperty("hostnameOverride", "peer0.org1.example.com");
//            prop.setProperty("sslProvider", "openSSL");
//            prop.setProperty("negotiationType", "TLS");
//            prop.setProperty("trustServerCertificate", "true");
//            File ordererCert = Paths.get("D:/workspace/javasdk/src/test/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt").toFile();
//            prop.setProperty("pemFile", ordererCert.getAbsolutePath());
//            Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL,prop);
//            Properties prop2 =new Properties();
//            prop2.setProperty("hostnameOverride", "peer0.org2.example.com");
//            prop2.setProperty("sslProvider", "openSSL");
//            prop2.setProperty("negotiationType", "TLS");
//            prop2.setProperty("trustServerCertificate", "true");
//            File peerCert2 = Paths.get("D:/workspace/javasdk/src/test/crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.crt").toFile();
//            prop2.setProperty("pemFile", peerCert2.getAbsolutePath());
//            Peer peer2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL,prop2);
            String res =  FBUtil.fb.query(Config.CHANNEL_NAME,"factor","QueryDataByFabricTxId",new String[] {fabrictxid});
            System.out.println("查询结果"+res);
            return "查询结果"+res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping("/add")
    @ResponseBody
    String add(@RequestBody List<Factor> factor){
        try {

              FBInvokeRes res = new FBInvokeRes();
            for (Factor f:factor
                 ) {
                Map<String,Object> maps = Maps.newHashMap();
                maps.put("key","test");
                maps.put("value",JSONArray.toJSONString(f));
                 res =  FBUtil.fb.invoke(Config.CHANNEL_NAME,"factor","SaveData", new String[]{JSONArray.toJSONString(maps)});
                System.out.println("invoke结果"+res);
            }
            return "invoke结果"+String.valueOf(res.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "success";
        }

    @RequestMapping("/KeepaliveQuery")
    @ResponseBody
    String keepaliveQuery(@RequestBody List<Factor> factor){
        try {
            FBInvokeRes res = new FBInvokeRes();
            for (Factor f:factor
            ) {
                res =  FBUtil.fb.invoke(Config.CHANNEL_NAME,"factor","KeepaliveQuery", new String[]{JSONArray.toJSONString(f)});
                System.out.println("invoke结果"+res);
            }
            return "invoke结果"+String.valueOf(res.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "success";
    }
}
