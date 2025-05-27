package com.example.demo.controller;

import com.example.demo.service.FileService;
import com.example.demo.service.UserService;
import com.example.demo.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException; // Added for response.getWriter()
import java.util.Map;
// Removed unused imports: Result, UserModel, FilesModel, JdbcTemplate, Resource, HashMap, List

@CrossOrigin
@RestController
public class DownloadController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @RequestMapping("/download")
    public void downloadFile (@RequestParam("id") String userId, @RequestParam("token") String token, @RequestParam("fileid") String fileId, HttpServletResponse response, HttpServletRequest request) {
        if (!userService.checkToken(token, Integer.parseInt(userId))) {
            try {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token error or invalid.");
                Log.ln("Token validation failed for userId: " + userId);
            } catch (IOException e) {
                Log.ln("Error writing unauthorized response: " + e.getMessage());
            }
            return;
        }

        Map<String, Object> pathDetails = fileService.getPath(fileId, userId);

        if (pathDetails == null || pathDetails.get("file_path") == null || pathDetails.get("name") == null) {
            try {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found or path/name is missing.");
                Log.ln("File not found for fileId: " + fileId + ", userId: " + userId);
            } catch (IOException e) {
                Log.ln("Error writing not found response: " + e.getMessage());
            }
            return;
        }

        String filePath = pathDetails.get("file_path").toString();
        String fileName = pathDetails.get("name").toString();

        File file = new File(System.getProperty("user.dir") + filePath);

        if(file.exists()){
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
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
                // Log actual error
                Log.ln("Error during file streaming for fileId: " + fileId + " - " + e.getMessage());
                e.printStackTrace(); // Consider a proper logger
                // It might be too late to set response status if headers are already sent
            } finally {
                try {
                    if (bis != null) bis.close();
                    if (fis != null) fis.close();
                } catch (IOException e) {
                    Log.ln("Error closing streams for fileId: " + fileId + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            try {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File does not exist on server.");
                Log.ln("File does not exist on server: " + System.getProperty("user.dir") + filePath);
            } catch (IOException e) {
                Log.ln("Error writing file not exist response: " + e.getMessage());
            }
        }
    }
}
