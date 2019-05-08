package com.app.bean;

import java.util.List;

/**
 * fb network all info
 */
public class FBInfo {
    private String fileSkDir;//key store relative location，for example src/test/crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/keystore
    private String signCerts;//pem file  relative filepath,for example src/test/crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/signcerts/Admin@org2.example.com-cert.pem
    private List<FBPeerInfo> peerList;
    private List<FBEventPeerInfo> eventPeerList;
    private List<FBOrderInfo> orderList;
    //private EndorsementPolicy policy = EndorsementPolicy.OR;//default policy is OR
    private String orgName="";// org1
    private String userName="admin";//  like  admin
    private String mspId;//like Org1MSP
    private String channelName;//暂时只有一个channel
    private String eventName;

    public String getFileSkDir() {
        return fileSkDir;
    }

    public void setFileSkDir(String fileSkDir) {
        this.fileSkDir = fileSkDir;
    }

    public String getSignCerts() {
        return signCerts;
    }

    public void setSignCerts(String signCerts) {
        this.signCerts = signCerts;
    }

    public List<FBPeerInfo> getPeerList() {
        return peerList;
    }

    public void setPeerList(List<FBPeerInfo> peerList) {
        this.peerList = peerList;
    }

    public List<FBOrderInfo> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<FBOrderInfo> orderList) {
        this.orderList = orderList;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    public List<FBEventPeerInfo> getEventPeerList() {
        return eventPeerList;
    }

    public void setEventPeerList(List<FBEventPeerInfo> eventPeerList) {
        this.eventPeerList = eventPeerList;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
