package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.entity.User;
import com.example.demo.util.MD5;
// import com.example.demo.util.Log; // Will not include Log for now, as it was commented out or not directly used in methods being moved.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

// Removed Resource import as Autowired is used.
// Removed SQLException as it's not directly handled by the moved methods.

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Constructor public UserModel(JdbcTemplate jdbcTemplate) removed as jdbcTemplate will be autowired.

    /**
    * 创建用户
    * */
    public int createUser (User user) {
        // Assuming user.getPassword() returns a String and is not null.
        // If user.getPassword() could be null, a null check would be needed before calling MD5.md5().
        int info = jdbcTemplate.update("INSERT INTO USER (name, password) VALUES(?, ?)",
            new Object[] {
                user.getName(),
                MD5.md5(user.getPassword()), // Simplified from MD5.md5(user.getPassword().toString())
            });
        return info;
    }

    /**
    * 用户登录
    * */
    public int userLogin (String name, String pwd){
        Integer nameid;
        String sql = "SELECT id FROM USER WHERE name = ? and password = ?";
        try{
            // Assuming pwd is not null. If it can be, a null check would be needed.
            nameid =  jdbcTemplate.queryForObject(sql, new Object[] {name, MD5.md5(pwd)}, java.lang.Integer.class);
        }catch (InvalidResultSetAccessException e){ // This specific exception might be rare. EmptyResultDataAccessException is more common for no rows.
            throw new RuntimeException(e);
        }catch (DataAccessException e){ // Catches EmptyResultDataAccessException among others.
            // It's generally better to handle EmptyResultDataAccessException explicitly
            // to distinguish "user not found" from other DB errors.
            // For now, re-throwing as per existing logic.
            throw new RuntimeException(e);
        }
        // The original code would throw if nameid is null due to queryForObject's contract.
        // If it could return null (e.g. query for list and get first), then null check is good.
        // Given current try-catch, this line is likely only reached if queryForObject succeeds.
        return nameid;
    }

    /**
    * 更新用户token
    * */
    public int setUpdateToken (String token, int id) {
        jdbcTemplate.update("UPDATE USER SET token = ? WHERE id = ?", new Object[] {token, id});
        return 1; // Consider returning void or boolean for clarity.
    }

    public String getToken(String name, String password) {
        // The 'password' parameter is not used in this token generation logic.
        String token="";
        token= JWT.create()
                .withClaim("name", name)
                .withClaim("generatetime", System.currentTimeMillis())
                .withClaim("exptime", 1000L * 60 * 60) // Used L for long literal
                .sign(Algorithm.HMAC256("mimimininini"));
                // 以 mimimininini 作为 token 的密钥
        return token;
    }

    /*
    * 验证token
    * */
    public Boolean checkToken (String token, int id) {
        // The 'id' parameter is not used in this token verification logic.
        // It could potentially be used to fetch a user-specific secret or for logging.
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("mimimininini")).build();
        try {
            jwtVerifier.verify(token);
            return true; // Token is valid
        } catch (JWTVerificationException e) {
            // Log the exception (optional, using a standard logger is preferred)
            // System.err.println("Token verification failed: " + e.getMessage());
            return false; // Token is invalid
        }
    }
}
