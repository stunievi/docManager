package com.example.demo.controller;

//import com.example.demo.model.Result;
import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Files;
import com.example.demo.entity.User;
import com.example.demo.model.Result;
import com.example.demo.model.UserModel;
import com.example.demo.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

import static javafx.scene.input.KeyCode.T;

/**
 * @author dell
 */
@CrossOrigin
@RestController
public class LoginController {

    @Resource

    private JdbcTemplate jdbcTemplate;
    /**
    * 用户登录
    * */
    @RequestMapping(value = "login", method= RequestMethod.POST)
    public Result login (@RequestBody HashMap<String, String> map) {
        String userName = map.get("name");
        String userPwd = map.get("pwd");
        UserModel um = new UserModel(jdbcTemplate);
        // 验证用户，返回id
        Integer result = um.userLogin(userName, userPwd);
        if (!result.equals(0)){
            // 登录成功
            // 获取token
            String token = um.getToken(userName,userPwd);
            // 更新token
            um.setUpdateToken(token,result);
            Map m1 = new HashMap(2);
            m1.put("id",result.toString());
            m1.put("token", token);

            return Result.success(m1);
        }else{
            // 登录失败
            return Result.error("登录失败");
        }
    }
    /**
    *  用户注册
    *
    * */
    @RequestMapping(value = "register", method= RequestMethod.POST)
    public  Result register (@RequestBody HashMap<String, String> map) {
        //TODO 判断用户名重复
        if (checkUserName(map.get("name"))) {
            return Result.error("用户名不规范");
        }else if (checkUserName(map.get("pwd"))){
            return Result.error("密码不规范");
        }

        UserModel um = new UserModel(jdbcTemplate);
        User user = new User();
        user.setName(map.get("name"));
        user.setPassword(map.get("pwd"));
        Integer result = um.createUser(user);
        if (result.equals(1)) {
            return Result.success(new int[]{1});
        }else{
            return Result.error("注册失败");
        }
//        return "user";
    }
    /**
    * 个人中心
    *
    * */
    @RequestMapping("user_center")
    public Result userCenter (@RequestBody HashMap<String, String> map) {
        String id = map.get("id");
        String token = map.get("token");
        UserModel um = new UserModel(jdbcTemplate);
        Boolean result = um.checkToken(token, Integer.parseInt(id));
        String sql = "SELECT * FROM files WHERE uid = ? AND p_id = ?";
        List list = jdbcTemplate.queryForList(sql,new Object[]{id,0});
        Log.ln(list);
        return Result.success(list);
    }


    private Boolean checkUserName(String t) {
        if (StringUtils.isBlank(t) || t.length() >= 20) {
            return true;
        }
        return false;
    }
    private Boolean checkUserPwd(String t) {
        if (StringUtils.isBlank(t) || t.length() >= 20 || t.length() < 6) {
            return true;
        }
        return false;
    }

}
