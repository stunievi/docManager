package com.example.demo.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.entity.User;
import com.example.demo.util.Log;
import com.example.demo.util.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.SQLException;

@Repository
public class UserModel {
//    @Resource
    private JdbcTemplate jdbcTemplate;


    public UserModel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
//    public
    /**
    * 创建用户
    * */
    public int  createUser (User user) {
        int info = jdbcTemplate.update("INSERT INTO USER (name, password) VALUES(?, ?)",
            new Object[] {
                user.getName(),
                MD5.md5(user.getPassword().toString()),
            });
        return info;
    }

    /**
    * 用户登录
    * */
    public int userLogin (String name, String pwd){
//        int count = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM USER");
        Integer nameid;
        String sql = "SELECT id FROM USER WHERE name = ? and password = ?";
        try{
            nameid =  jdbcTemplate.queryForObject(sql, new Object[] {name,MD5.md5(pwd.toString())}, java.lang.Integer.class);
        }catch (InvalidResultSetAccessException e){
            throw new RuntimeException(e);
        }catch (DataAccessException e){
            throw new RuntimeException(e);
        }
        return nameid;
    }

    /**
    * 更新用户token
    * */
    public int setUpdateToken (String token, int id) {
        jdbcTemplate.update("UPDATE USER SET token = ? WHERE id = ?", new Object[] {token, id});
        return 1;
    }
    public String getToken(String name, String password) {

        String token="";
        token= JWT.create()
                .withClaim("name", name)
                .withClaim("generatetime",System.currentTimeMillis())
                .withClaim("exptime",1000*1*60*60)
                .sign(Algorithm.HMAC256("mimimininini"));
                // 以 mimimininini 作为 token 的密钥
        return token;
    }
    /*
    * 验证token
    * */
    public Boolean checkToken (String token,int id) {
        //获取密码
//        String pwd = (String) jdbcTemplate.queryForObject("SELECT password FROM USER WHERE id = ?", new Object[] {id}, java.lang.String.class);
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("mimimininini")).build();
//        try {
            jwtVerifier.verify(token);
//        } catch (JWTVerificationException e) {
//            throw new RuntimeException("401");
//        }
        return true;
    }
}
