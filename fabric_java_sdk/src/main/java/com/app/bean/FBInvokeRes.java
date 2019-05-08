package com.app.bean;

import java.io.Serializable;

/**
 * when you do invoke on  a fabric network,this bean will return
 */
public class FBInvokeRes implements Serializable {

    private boolean isSuccess;

    private String txId;

    private String msg;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "FBInvokeRes{" +
                "isSuccess=" + isSuccess +
                ", txId='" + txId + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
