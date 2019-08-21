package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
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

@CrossOrigin
    @RestController
public class FileOptionController {
    @Resource
    private JdbcTemplate jdbcTemplate;

    /*
    * 个人首页文件列表
 * */
    @RequestMapping("files_list")
    public Result filesList (@RequestBody HashMap<String, String> map) {
        String id = map.get("id");
        String fid = map.get("fid");
        Boolean result = new UserModel(jdbcTemplate).checkToken(map.get("token"), Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        List<Map<String,Object>> list = new FilesModel(jdbcTemplate).getFirstFold(id, fid);

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

    @RequestMapping("rename")
    public Result rename (@RequestBody JSONObject info) {
        Log.ln(info);
        String id = info.get("id").toString();
        String fid = info.get("fid").toString();
        Boolean result = new UserModel(jdbcTemplate).checkToken(info.get("token").toString(), Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        int re = new FilesModel(jdbcTemplate).renameFile(info.get("name").toString(),info.get("fid").toString());
        return Result.success(re);
    }
    @RequestMapping("move")
    public Result move (@RequestBody JSONObject info) {
        Log.ln(info);
        String id = info.get("id").toString();
        Boolean result = new UserModel(jdbcTemplate).checkToken(info.get("token").toString(), Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        int re = new FilesModel(jdbcTemplate).moveFile(info.get("pid").toString(),info.get("fid").toString());
        return Result.success(re);
    }
    @RequestMapping("copy")
    public Result copy (@RequestBody JSONObject info) {
        Log.ln(info);
        String id = info.get("id").toString();
        Boolean result = new UserModel(jdbcTemplate).checkToken(info.get("token").toString(), Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        int re = new FilesModel(jdbcTemplate).copyFile(info.get("pid").toString(),info.get("fid").toString());
        return Result.success(re);
    }

    @RequestMapping("folder_list")
    public Result folderList(@RequestBody JSONObject info){
        String id = info.get("id").toString();
        String fid = info.get("fid").toString();
        Boolean result = new UserModel(jdbcTemplate).checkToken(info.get("token").toString(), Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        List<Map<String, Object>> list = new FilesModel(jdbcTemplate).showFolderList(fid,id);
//            for (int i = 0; i < list.size(); i++){
//                Log.ln(list.get(i).get("id"));
//                List<Map<String, Object>> childList = new FilesModel(jdbcTemplate).showFolderList(list.get(i).get("id").toString(),id);
//                Integer size = childList.size();
//                if(!size.equals(0)){
//                    list.get(i).put("children",childList);
//                    list.get(i).put("loading",false);
//                }
//            }
        return Result.success(list);
    }
    @RequestMapping("delete")
    public Result delete (@RequestBody JSONObject info) {
        String id = info.get("id").toString();
        String fid = info.get("fid").toString();
        Boolean result = new UserModel(jdbcTemplate).checkToken(info.get("token").toString(), Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        int re = new FilesModel(jdbcTemplate).deleteFile(fid);
        return Result.success(re);
    }
//        public List<Map<String, Object>> getList(List<Map<String, Object>> list) {
//            for (int i = 0; i < list.size(); i++){
//                Log.ln(list.get(i).get("id"));
//                List<Map<String, Object>> childList = new FilesModel(jdbcTemplate).showFolderList(list.get(i).get("id").toString(),id);
//                Integer size = childList.size();
//                if(!size.equals(0)){
//                    list.get(i).put("children",childList);
//                    getList(list.get(i).get("children"));
//                }
//            }
//            return list;
//        }
}
