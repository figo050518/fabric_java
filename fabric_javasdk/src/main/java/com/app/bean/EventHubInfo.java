package com.app.bean;

/**
 * Fabric peer event info
 */
public class EventHubInfo {

    private boolean enableTls;
    private String hostname;// for example peer0.org1.example.com
    private String url;  // peer url for example peer0.org1.example.com:7051 or 192.068.66.101:7051
    private String peerTlsFilePath;// peer tls file  relative path, like src/test/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt


    public boolean isEnableTls() {
        return enableTls;
    }

    public void setEnableTls(boolean enableTls) {
        this.enableTls = enableTls;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPeerTlsFilePath() {
        return peerTlsFilePath;
    }

    public void setPeerTlsFilePath(String peerTlsFilePath) {
        this.peerTlsFilePath = peerTlsFilePath;
    }
}
