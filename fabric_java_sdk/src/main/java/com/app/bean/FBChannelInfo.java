package com.app.bean;

/**
 * fb network all info
 */
public class FBChannelInfo {
    private String mspConfigPath;
    private String localMspId;
    private Object channelInfo;



    public String getMspConfigPath() {
        return mspConfigPath;
    }

    public void setMspConfigPath(String mspConfigPath) {
        this.mspConfigPath = mspConfigPath;
    }

    public String getLocalMspId() {
        return localMspId;
    }

    public void setLocalMspId(String localMspId) {
        this.localMspId = localMspId;
    }

    public Object getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(Object channelInfo) {
        this.channelInfo = channelInfo;
    }

}
