package com.laizhong.hotel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.laizhong.hotel.constant.HotelConstant;

public class ResponseVo<T> {
    private String code = "success";
    private String message;
    private T data;

    public ResponseVo(){
    }

    public static <T> ResponseVo<T> success() {
        return new ResponseVo<>();
    }

    public static <T> ResponseVo<T> success(T data) {
    	 ResponseVo<T> responseVo = new ResponseVo<>();
         responseVo.code = "success";
         responseVo.data = data;
         return responseVo;
    }
    public static <T> ResponseVo<T> fail(String message) {
        ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.code = "fail";
        responseVo.message = message;
        return responseVo;
    }
    public static <T> ResponseVo<T> fail(String code, String message) {
        ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.code = code;
        responseVo.message = message;
        return responseVo;
    }
    public static <T> ResponseVo<T> fail(String code, T data, String message) {
        ResponseVo<T> responseVo = new ResponseVo<>(data, message);
        responseVo.code = code;
        return responseVo;
    }
    public ResponseVo(T data){
        this.data = data;
    }

    public ResponseVo(T data, String message){
        this.data = data;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
    @JsonIgnore
    public boolean isSuccess() {
        return HotelConstant.SUCCESS_CODE.equals(code);
    }
    
}
