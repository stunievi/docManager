package com.example.demo.controller;

import com.example.demo.model.FilesModel;
import com.example.demo.model.Result;
import com.example.demo.model.UserModel;
import com.example.demo.util.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin
@RestController
public class UserController {
    @Resource

    private JdbcTemplate jdbcTemplate;
    @RequestMapping("files_list")
    public Result filesList (@RequestBody HashMap<String, String> map) {
        String id = map.get("id");
        String fid = map.get("fid");
        Boolean result = new UserModel(jdbcTemplate).checkToken(map.get("token"), Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        List list = new FilesModel(jdbcTemplate).getFirstFold(id, fid);
        return Result.success(list);
    }
    /**
    * 新建文件夹
    * */
    @RequestMapping("create_new")
    public Result createNewFold (@RequestBody HashMap<String, String> map) {
        String id = map.get("id");
        String fid = map.get("fid");
        Boolean result = new UserModel(jdbcTemplate).checkToken(map.get("token"), Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
//        Log.ln(Long.toString(System.currentTimeMillis()/1000));
        int isNew = new FilesModel(jdbcTemplate).createNewFold(map.get("name"), id, fid);
        return Result.success(isNew);
    }



}
