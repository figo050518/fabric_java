package com.app.bean;

/**
 * Fabric order info
 */
public class FBOrderInfo {

    private boolean useTLS;
    private String host;// for example peer0.org1.example.com:7050
    private String tlsPath;// order  tls file relative path, like src/test/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt

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

    public String getHostname() {
        String[] temps = this.getHost().split(":");
        if (temps.length == 2) {
            return temps[0];
        }
        return null;
    }
}
