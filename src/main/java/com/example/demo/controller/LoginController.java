package com.example.demo.controller;

//import com.example.demo.model.Result;
import com.alibaba.fastjson.JSON; // Keep if Result or other parts use it
import com.example.demo.entity.Files; // Keep if Result or other parts use it
import com.example.demo.entity.User;
import com.example.demo.model.Result;
import com.example.demo.service.FileService; // Added
import com.example.demo.service.UserService; // Added
import com.example.demo.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired; // Added
import org.springframework.web.bind.annotation.*;

// Removed: javax.annotation.Resource;
// Removed: java.text.SimpleDateFormat; (unused)
import java.util.HashMap; // Keep
import java.util.List;    // Keep for List<Map<String, Object>>
import java.util.Map;     // Keep
// Removed: import static javafx.scene.input.KeyCode.T; (unused)
// Removed: import com.example.demo.model.UserModel;
// Removed: import org.springframework.jdbc.core.JdbcTemplate;


/**
 * @author dell
 */
@CrossOrigin
@RestController
public class LoginController {

    @Autowired // Changed from @Resource
    private UserService userService; // Changed from JdbcTemplate

    @Autowired // Added
    private FileService fileService; // Added

    /**
    * 用户登录
    * */
    @RequestMapping(value = "login", method= RequestMethod.POST)
    public Result login (@RequestBody HashMap<String, String> map) {
        String userName = map.get("name");
        String userPwd = map.get("pwd");
        // UserModel um = new UserModel(jdbcTemplate); // Removed
        // 验证用户，返回id
        int userId = userService.userLogin(userName, userPwd); // Changed to userService
        if (userId != -1){ // Changed condition based on new return type (-1 for failure)
            // 登录成功
            // 获取token
            String token = userService.getToken(userName,userPwd); // Changed to userService
            // 更新token
            userService.setUpdateToken(token,userId); // Changed to userService
            Map<String, String> m1 = new HashMap<>(); // Typed map
            m1.put("id", String.valueOf(userId)); // result to userId, and String.valueOf()
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
        }else if (checkUserPwd(map.get("pwd"))){ // Corrected to checkUserPwd
            return Result.error("密码不规范");
        }

        // UserModel um = new UserModel(jdbcTemplate); // Removed
        User user = new User();
        user.setName(map.get("name"));
        user.setPassword(map.get("pwd"));
        int result = userService.createUser(user); // Changed to userService, returns int
        if (result == 1) { // Changed condition for int
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
        String userId = map.get("id"); // Renamed id to userId for clarity
        String token = map.get("token");
        // UserModel um = new UserModel(jdbcTemplate); // Removed
        // Boolean result = um.checkToken(token, Integer.parseInt(id)); // Removed
        if (!userService.checkToken(token, Integer.parseInt(userId))) { // Corrected token check logic
            return Result.error("token error");
        }
        // String sql = "SELECT * FROM files WHERE uid = ? AND p_id = ?"; // Removed
        // List list = jdbcTemplate.queryForList(sql,new Object[]{id,0}); // Removed
        List<Map<String, Object>> fileList = fileService.getFirstFold(userId, "0"); // Used fileService
        Log.ln(fileList); // Updated variable name
        return Result.success(fileList); // Updated variable name
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
