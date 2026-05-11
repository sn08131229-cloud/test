package com.example.trace.common;

public record ApiResponse<T>(int code, String message, T data) {
    public static <T> ApiResponse<T> ok(T data){return new ApiResponse<>(200,"success",data);} 
    public static <T> ApiResponse<T> fail(String msg){return new ApiResponse<>(400,msg,null);} 
}
