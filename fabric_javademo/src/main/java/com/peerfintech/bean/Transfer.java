package com.peerfintech.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Transfer {
    private String dbdbh;
    private String ebeln;
    private String drfgcbm;
    private String drfkcddbm;
    private String dblx;

    private List<TMTransfer> TM;

    public String getDbdbh() {
        return dbdbh;
    }

    public void setDbdbh(String dbdbh) {
        this.dbdbh = dbdbh;
    }

    public String getEbeln() {
        return ebeln;
    }

    public void setEbeln(String ebeln) {
        this.ebeln = ebeln;
    }

    public String getDrfgcbm() {
        return drfgcbm;
    }

    public void setDrfgcbm(String drfgcbm) {
        this.drfgcbm = drfgcbm;
    }

    public String getDrfkcddbm() {
        return drfkcddbm;
    }

    public void setDrfkcddbm(String drfkcddbm) {
        this.drfkcddbm = drfkcddbm;
    }

    public String getDblx() {
        return dblx;
    }

    public void setDblx(String dblx) {
        this.dblx = dblx;
    }

    @JsonProperty("TM")
    public List<TMTransfer> getTM() {
        return TM;
    }

    public void setTM(List<TMTransfer> TM) {
        this.TM = TM;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "dbdbh='" + dbdbh + '\'' +
                ", ebeln='" + ebeln + '\'' +
                ", drfgcbm='" + drfgcbm + '\'' +
                ", drfkcddbm='" + drfkcddbm + '\'' +
                ", dblx='" + dblx + '\'' +
                ", TM=" + TM +
                '}';
    }
}
