package com.example.demo.controller;

import com.example.demo.model.FilesModel;
import com.example.demo.model.Result;
import com.example.demo.model.UserModel;
import com.example.demo.util.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
public class UpdateController {
    @Resource

    private JdbcTemplate jdbcTemplate;
    @RequestMapping("/upload")
    @ResponseBody
    public Result upload (@RequestParam("file") MultipartFile file, @RequestParam("id") String id, @RequestParam("token") String token,@RequestParam("fid") String fid) {

        Boolean result = new UserModel(jdbcTemplate).checkToken(token, Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        String fileName = file.getOriginalFilename();
        String path = "/src/main/resources/static/" + fileName;
        String size = setFileSize((int)file.getSize(),0);
        String type = getExtensionName(fileName);
        try {
            FileOutputStream out = new FileOutputStream(System.getProperty("user.dir") + path);
            out.write(file.getBytes());
            out.flush();
            out.close();
            int res = new FilesModel(jdbcTemplate).updateFile(fileName,id,fid,path,size,type);
            return Result.success(res);
        } catch (Exception e) {
            System.out.println("文件上传失败");
            Log.ln(e);
        }
        return Result.success("上传失败");
    }
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    public String setFileSize(Integer num,int type) {
        String size = null;
        if (num >= 1024) {
            type = type+1;
            num = (num/1024);
            if (num >= 1024){
                return setFileSize(num, type);
            }
        }
        switch (type) {
            case 0 :
                size = "B";
                break;
            case 1 :
                size = "KB";
                break;
            case 2 :
                size = "MB";
                break;
            case 3 :
                size = "GB";
                break;
        }
        size = num.toString() + size;
        return size;
    }


}
