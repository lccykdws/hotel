package com.laizhong.hotel.model;

import java.io.Serializable;
import java.util.Date;

public class AgainCheckinInfo implements Serializable {
    private String childTradeNo;

    private String tradeNo;

    private Date createdDate;



    private String outTime;

    private static final long serialVersionUID = 1L;

    public String getChildTradeNo() {
        return childTradeNo;
    }

    public void setChildTradeNo(String childTradeNo) {
        this.childTradeNo = childTradeNo == null ? null : childTradeNo.trim();
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime == null ? null : outTime.trim();
    }
}