package com.example.demo.controller;

import com.example.demo.model.FilesModel;
import com.example.demo.model.Result;
import com.example.demo.model.UserModel;
import com.example.demo.util.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class DownloadController {
    @Resource
    private JdbcTemplate jdbcTemplate;
    @RequestMapping("/download")
    public void downloadFile (@RequestParam("id") String id, @RequestParam("token") String token,@RequestParam("fileid") String fileid, HttpServletResponse response, HttpServletRequest request) {
//        String id = map.get("id");
//        String fileid = map.get("fileid");
//        Log.ln(token);
        Boolean result = new UserModel(jdbcTemplate).checkToken(token, Integer.parseInt(id));
        if(result) {
            Result.error("token error");
        }
        Map<String, Object> path = new FilesModel(jdbcTemplate).getPath(fileid,id);
//        Log.ln(path);
        File file = new File(System.getProperty("user.dir")+path.get("file_path"));
        if(file.exists()){
//            Log.ln(path.get("name"));
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + path.get("name"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        return Result.success("");
    }
}
