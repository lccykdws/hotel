package com.laizhong.hotel.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class PayInfo {
	 
	private String tradeNo;
 
 
	private String payTradeNo;
	
	/**
	 * 支付方式（Alipay：支付宝； Wechat：微信）
	 */
	private String payTradeType;
	
	/*WAIT_BUYER_PAY	交易创建，等待买家付款。
	TRADE_CLOSED	在指定时间段内未支付时关闭的交易；
	客户主动关闭订单。
	TRADE_SUCCESS	交易成功，且可对该交易做操作，如：多级分润、退款等。
	TRADE_PART_REFUND	部分退款成功。
	TRADE_ALL_REFUND	全部退款成功。
	TRADE_PROCESS	交易正在处理中，可对该交易做查询，避免重复支付。*/
	private String payTradeStatus;
	
	private String returnCode;
	private String returnInfo;
	private int deposit;
	private int roomPrice;
	private int insurePrice;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
	@JsonIgnore  
	private Date createdDate;
	//修改时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
	@JsonIgnore  
	private Date updatedDate;
	
	private String accountDate;
	private String outRequestNo;
	private int refundDeposit;
 
}
