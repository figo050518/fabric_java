package com.peerfintech.bean;

public class TMTransfer {
    private String dbdbh;
    private String pch;
    private String wlbh;
    private int dbsl;
    private String dcfgcbm;
    private String dcfkcbm;

    public String getDbdbh() {
        return dbdbh;
    }

    public void setDbdbh(String dbdbh) {
        this.dbdbh = dbdbh;
    }

    public String getPch() {
        return pch;
    }

    public void setPch(String pch) {
        this.pch = pch;
    }

    public String getWlbh() {
        return wlbh;
    }

    public void setWlbh(String wlbh) {
        this.wlbh = wlbh;
    }

    public int getDbsl() {
        return dbsl;
    }

    public void setDbsl(int dbsl) {
        this.dbsl = dbsl;
    }

    public String getDcfgcbm() {
        return dcfgcbm;
    }

    public void setDcfgcbm(String dcfgcbm) {
        this.dcfgcbm = dcfgcbm;
    }

    public String getDcfkcbm() {
        return dcfkcbm;
    }

    public void setDcfkcbm(String dcfkcbm) {
        this.dcfkcbm = dcfkcbm;
    }

    @Override
    public String toString() {
        return "TMTransfer{" +
                "dbdbh='" + dbdbh + '\'' +
                ", pch='" + pch + '\'' +
                ", wlbh='" + wlbh + '\'' +
                ", dbsl='" + dbsl + '\'' +
                ", dcfgcbm='" + dcfgcbm + '\'' +
                ", dcfkcbm='" + dcfkcbm + '\'' +
                '}';
    }
}
