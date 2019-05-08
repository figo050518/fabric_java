package com.app.bean;

/**
 * Fabric peer info
 */
public class FBEventPeerInfo {

    private boolean useTLS;
    private String host;// for example peer0.org1.example.com:7051
    private String tlsPath;// peer tls file  relative path, like src/test/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt
    private String orgName;

    public boolean isUseTLS() {
        return useTLS;
    }

    public void setUseTLS(boolean useTLS) {
        this.useTLS = useTLS;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTlsPath() {
        return tlsPath;
    }

    public void setTlsPath(String tlsPath) {
        this.tlsPath = tlsPath;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
