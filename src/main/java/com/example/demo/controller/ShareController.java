package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

@CrossOrigin
@RestController
public class ShareController {
    @Resource

    private JdbcTemplate jdbcTemplate;
    @RequestMapping("share")
    public Result share(@RequestBody JSONObject json){
        Boolean result = new UserModel(jdbcTemplate).checkToken(json.get("token").toString(), Integer.parseInt(json.get("id").toString()));
        if(result) {
            Result.error("token error");
        }
        List<Map<String,Object>> map = (List<Map<String,Object>>)json.get("arr");
        Log.ln(map.size());
        Integer info = new FilesModel(jdbcTemplate).share(map,json.get("password").toString());
        return Result.success("");
    }
}
