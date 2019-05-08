package com.peerfintech.bean;

import java.util.List;

public class Factor {
    private String createBy;
    private long createTime;
    private String sender;
    private String[] receiver;
    private String txData;
    private List<FileInfo> attachmentList;
    private long lastUpdateTime;
    private String lastUpdateBy;
    private int cryptoFlag;
    private String cryptoAlgorithm;
    private String docType;
    private String fabricTxId;
    private String businessNo;
    private String expand1;
    private String expand2;
    private String dateVersion;

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String[] getReceiver() {
        return receiver;
    }

    public void setReceiver(String[] receiver) {
        this.receiver = receiver;
    }

    public String getTxData() {
        return txData;
    }

    public void setTxData(String txData) {
        this.txData = txData;
    }

    public List<FileInfo> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<FileInfo> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public int getCryptoFlag() {
        return cryptoFlag;
    }

    public void setCryptoFlag(int cryptoFlag) {
        this.cryptoFlag = cryptoFlag;
    }

    public String getCryptoAlgorithm() {
        return cryptoAlgorithm;
    }

    public void setCryptoAlgorithm(String cryptoAlgorithm) {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFabricTxId() {
        return fabricTxId;
    }

    public void setFabricTxId(String fabricTxId) {
        this.fabricTxId = fabricTxId;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public String getExpand1() {
        return expand1;
    }

    public void setExpand1(String expand1) {
        this.expand1 = expand1;
    }

    public String getExpand2() {
        return expand2;
    }

    public void setExpand2(String expand2) {
        this.expand2 = expand2;
    }

    public String getDateVersion() {
        return dateVersion;
    }

    public void setDateVersion(String dateVersion) {
        this.dateVersion = dateVersion;
    }
}
