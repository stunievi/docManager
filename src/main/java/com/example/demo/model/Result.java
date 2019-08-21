package com.example.demo.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author dell
 */
public class Result<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
    public static<T> Result<T> success (T data) {
        return new Result<T>(data);
    }
    /**
     * 失败时候的调用
     * @param codeMsg codeMsg
     * @param <T> t
     * @return Result
     */
    public static <T> Result<T> error(String codeMsg){
        return new Result<T>(400,codeMsg);
    }
    /**
     * 成功的构造函数
     * @param data data
     */
    public  Result(T data){
        this.code = 200;
        //默认200是成功
        this.msg = "SUCCESS";
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
//    private String ok(ArrayList<T> t) {
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonstr = null;
//        try {
//            jsonstr = mapper.writeValueAsString(t);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return jsonstr;
//    }

}
