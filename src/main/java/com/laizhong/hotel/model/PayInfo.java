package com.laizhong.hotel.model;

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
	
 
}
