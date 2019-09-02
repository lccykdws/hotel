package com.laizhong.hotel.constant;

public enum  ErrorCodeEnum {
	/**
	 * 您当前入住中的订单是日租房，不可以续住
	 */
    E0001("E0001", "您当前入住中的订单是日租房，不可以续住"),
    /**
     * 请确认支付结果
     */
    E0002("E0002", "请确认支付结果");
    
	
    private String message;
    private String code;

    ErrorCodeEnum(String code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
