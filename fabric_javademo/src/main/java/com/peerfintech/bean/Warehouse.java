package com.peerfintech.bean;

public class Warehouse {
    private String plant;//工厂编码
    private String stor_loc;//库存地编码
    private String charg;//批次
    private String wlbm;//物料编码
    private int kcsl;  //库存数量

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getStor_loc() {
        return stor_loc;
    }

    public void setStor_loc(String stor_loc) {
        this.stor_loc = stor_loc;
    }

    public String getCharg() {
        return charg;
    }

    public void setCharg(String charg) {
        this.charg = charg;
    }

    public String getWlbm() {
        return wlbm;
    }

    public void setWlbm(String wlbm) {
        this.wlbm = wlbm;
    }

    public int getKcsl() {
        return kcsl;
    }

    public void setKcsl(int kcsl) {
        this.kcsl = kcsl;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "plant='" + plant + '\'' +
                ", stor_loc='" + stor_loc + '\'' +
                ", charg='" + charg + '\'' +
                ", wlbm='" + wlbm + '\'' +
                ", kcsl=" + kcsl +
                '}';
    }
}
