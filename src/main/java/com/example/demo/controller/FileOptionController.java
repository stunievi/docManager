package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.Result;
import com.example.demo.service.FileService;
import com.example.demo.service.UserService;
import com.example.demo.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
    @RestController
public class FileOptionController {
    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    /*
    * 个人首页文件列表
 * */
    @RequestMapping("files_list")
    public Result filesList (@RequestBody HashMap<String, String> map) {
        String id = map.get("id");
        String fid = map.get("fid");
        if (!userService.checkToken(map.get("token"), Integer.parseInt(id))) {
            return Result.error("token error");
        }
        List<Map<String,Object>> list = fileService.getFirstFold(id, fid);

        return Result.success(list);
    }
    /**
     * 新建文件夹
     * */
    @RequestMapping("create_new")
    public Result createNewFold (@RequestBody HashMap<String, String> map) {
        String id = map.get("id");
        String fid = map.get("fid");
        if (!userService.checkToken(map.get("token"), Integer.parseInt(id))) {
            return Result.error("token error");
        }
//        Log.ln(Long.toString(System.currentTimeMillis()/1000));
        int isNew = fileService.createNewFold(map.get("name"), id, fid);
        return Result.success(isNew);
    }

    @RequestMapping("rename")
    public Result rename (@RequestBody JSONObject info) {
        Log.ln(info);
        String id = info.get("id").toString();
        String fid = info.get("fid").toString();
        if (!userService.checkToken(info.get("token").toString(), Integer.parseInt(id))) {
            return Result.error("token error");
        }
        int re = fileService.renameFile(info.get("name").toString(),info.get("fid").toString());
        return Result.success(re);
    }
    @RequestMapping("move")
    public Result move (@RequestBody JSONObject info) {
        Log.ln(info);
        String id = info.get("id").toString();
        if (!userService.checkToken(info.get("token").toString(), Integer.parseInt(id))) {
            return Result.error("token error");
        }
        int re = fileService.moveFile(info.get("pid").toString(),info.get("fid").toString());
        return Result.success(re);
    }
    @RequestMapping("copy")
    public Result copy (@RequestBody JSONObject info) {
        Log.ln(info);
        String id = info.get("id").toString();
        if (!userService.checkToken(info.get("token").toString(), Integer.parseInt(id))) {
            return Result.error("token error");
        }
        int re = fileService.copyFile(info.get("pid").toString(),info.get("fid").toString());
        return Result.success(re);
    }

    @RequestMapping("folder_list")
    public Result folderList(@RequestBody JSONObject info){
        String id = info.get("id").toString();
        String fid = info.get("fid").toString();
        if (!userService.checkToken(info.get("token").toString(), Integer.parseInt(id))) {
            return Result.error("token error");
        }
        List<Map<String, Object>> list = fileService.showFolderList(fid,id);
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
        if (!userService.checkToken(info.get("token").toString(), Integer.parseInt(id))) {
            return Result.error("token error");
        }
        int re = fileService.deleteFile(fid);
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
