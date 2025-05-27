package com.example.demo.service;

import com.example.demo.util.Log; // Assuming Log is used as per original FilesModel
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

// Removed javax.annotation.Resource since @Autowired is used
// javax.xml.crypto.Data was unused in FilesModel, so not included
// java.io.FileOutputStream was unused in FilesModel, so not included
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
// java.sql.Statement was unused in FilesModel, so not included
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FileService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Constructor public FilesModel(JdbcTemplate jdbcTemplate) is removed.

    /*
    * 获取当前用户首层文件列表
    * */
    public List<Map<String, Object>> getFirstFold (String id, String fid) {
        String sql = "SELECT * FROM files WHERE uid = ? AND p_id = ? ORDER BY is_folder desc";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,new Object[]{id,fid});
        int m = list.size();
        for (int i = 0; i < m; i++) {
            list.get(i).put("created_at",timeStamp2Date(list.get(i).get("created_at").toString(), "YYYY-MM-dd H:m"));
        }
        return list;
    }

    /*
    * 新建文件夹
    * */
    public int createNewFold (String name, String id, String pid) {
        String sql = "INSERT INTO files(name,p_id,is_folder,created_at,uid,size) VALUES (?, ?, ?, ?, ?, ?)";
        return  jdbcTemplate.update(sql, new Object[] {name,pid,"1",System.currentTimeMillis()/1000,id,"-"});
    }

    /*
    * 重命名
    * */
    public int renameFile (String name, String id) {
        String sql = "UPDATE files SET name = ? where id = ?";
        return  jdbcTemplate.update(sql, new Object[] {name,id});
    }

    /*
    * 移动文件夹
    * */
    public int moveFile (String pid, String id) {
        String sql = "UPDATE files SET p_id = ? where id = ?";
        return  jdbcTemplate.update(sql, new Object[] {pid,id});
    }

    /*
     * 删除文件
     * */
    public int deleteFile ( String id) {
        String sql = "UPDATE files SET p_id = -1 where id = ?"; // Logically delete by moving to p_id = -1
        return  jdbcTemplate.update(sql, new Object[] {id});
    }

    /*
     * 复制文件夹
     * */
    public int copyFile (String pid, String id) {
        this.eachFolderCopy(pid, id);
        return 1; // Original logic returned 1, perhaps for success indication
    }

    /*
    * 遍历复制文件夹
    * */
    private String eachFolderCopy(String pid, String id){ // Changed to private
        String sql = "SELECT * FROM files WHERE id  = ?";
        Map<String, Object> list = jdbcTemplate.queryForMap(sql,new Object[]{id});

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
                PreparedStatement ps = connection.prepareStatement("INSERT INTO files VALUES (?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, null); // Assuming ID is auto-generated
                ps.setString(2, list.get("name").toString());
                ps.setString(3, list.get("is_share") != null ? list.get("is_share").toString() : "0"); // Handle null for is_share
                ps.setString(4, pid); // new parent id
                ps.setString(5, list.get("file_form") != null ? list.get("file_form").toString() : null); // Handle null
                ps.setString(6, list.get("file_path") != null ? list.get("file_path").toString() : null); // Handle null
                ps.setString(7, list.get("is_folder").toString());
                ps.setString(8, String.valueOf(System.currentTimeMillis() / 1000)); // New created_at
                ps.setString(9, list.get("uid").toString());
                ps.setString(10, list.get("size").toString());
                return ps;
            }
        }, keyHolder);
        // It's good practice to check if keyHolder.getKey() is null before calling intValue().
        int insertResult = (keyHolder.getKey() != null) ? keyHolder.getKey().intValue() : -1; // Get new ID of copied folder

        if (insertResult == -1) {
             Log.ln("Failed to retrieve key for copied folder: " + list.get("name"));
             return ""; // Or throw exception
        }

        List<Map<String, Object>> sonlist = jdbcTemplate.queryForList("SELECT * FROM files WHERE p_id = ?",new Object[]{id}); // Children of original folder
        for(int i = 0; i < sonlist.size();i++){
            final int I = i; // Final for use in inner class
            Log.ln("Copying child: " + sonlist.get(I).get("id").toString() + " into new parent: " + insertResult);
            if (sonlist.get(I).get("is_folder").toString().equals("1")) { // If child is a folder, recurse
                this.eachFolderCopy(String.valueOf(insertResult), sonlist.get(I).get("id").toString());
            }else{ // If child is a file, copy it directly
                jdbcTemplate.update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO files VALUES (?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                        ps.setString(1, null);
                        ps.setString(2, sonlist.get(I).get("name").toString());
                        ps.setString(3, sonlist.get(I).get("is_share") != null ? sonlist.get(I).get("is_share").toString() : "0" );
                        ps.setString(4, String.valueOf(insertResult)); // new parent_id is the id of the folder we just copied
                        ps.setString(5, sonlist.get(I).get("file_form") != null ? sonlist.get(I).get("file_form").toString() : null);
                        ps.setString(6, sonlist.get(I).get("file_path") != null ? sonlist.get(I).get("file_path").toString() : null);
                        ps.setString(7, sonlist.get(I).get("is_folder").toString());
                        ps.setString(8, String.valueOf(System.currentTimeMillis() / 1000)); // new created_at
                        ps.setString(9, sonlist.get(I).get("uid").toString());
                        ps.setString(10, sonlist.get(I).get("size").toString());
                        return ps;
                    }
                }); // KeyHolder not strictly needed here if we don't use the file's new ID immediately
            }
        }
        return String.valueOf(insertResult); // Return the ID of the newly created folder copy
    }

    /*
    * 展示文件夹列表 (for move/copy dialogs, etc.)
    * */
    public List<Map<String, Object>> showFolderList(String currentFolderIdToExclude, String userId) { // Parameters renamed for clarity
        // Excludes the currentFolderIdToExclude to prevent moving a folder into itself or its descendants if not handled carefully client-side.
        // This is a simplistic view; a full recursive check might be needed to prevent moving into children.
        String sql = "SELECT id, p_id as pid, name as title FROM files WHERE is_folder = 1 AND uid = ? AND id != ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, new Object[]{userId, currentFolderIdToExclude});
        return list;
    }

    /*
     * 上传文件，更新数据库
     * */
    public int updateFile (String name, String userId, String parentId, String path, String size, String type) {
        String sql = "INSERT INTO files (name, p_id, file_form, file_path, is_folder, created_at, uid,  size) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        try {
            int i = jdbcTemplate.update(sql, new Object[] {name, parentId, type, path, "0", System.currentTimeMillis()/1000, userId, size});
            return  i;
        } catch (Exception e) {
            Log.ln("Error updating file in DB: " + e.getMessage());
            Log.ln(e); // Log the full stack trace
            return  0; // Indicate failure
        }
    }

    /**
    * 分享文件 (This method seems incomplete and potentially problematic in original FilesModel)
    * */
    public int share(List<Map<String,Object>> filesToShare, String sharePassword){ // Renamed params for clarity
        // Original code iterates i <= map.size(), which will cause IndexOutOfBoundsException for the last element.
        // Should be i < filesToShare.size().
        // Also, the logic inside seems to only handle the first item if it's a folder and then might error or do nothing for others.
        // This method needs significant review and probably a rewrite based on actual requirements.
        // For now, porting with a fix for the loop bound.
        for (int i = 0; i < filesToShare.size(); i++){ // Corrected loop condition
            Map<String, Object> fileInfo = filesToShare.get(i);
            // Assuming fileInfo contains "is_folder", "name", "uid", "id", "p_id"
            if(fileInfo.get("is_folder") != null && fileInfo.get("is_folder").toString().equals("1")){ // Check for null
                try {
                    jdbcTemplate.update(
                            "INSERT INTO share (name,share_user_id,fid,password,p_id,is_folder) VALUES (?,?,?,?,?,?)",
                            new Object[] {
                                    fileInfo.get("name"),
                                    fileInfo.get("uid"),
                                    fileInfo.get("id"),
                                    sharePassword, // Using the provided password
                                    fileInfo.get("p_id"),
                                    fileInfo.get("is_folder")
                            });
                } catch (Exception e) {
                    Log.ln("Error creating share record for folder: " + fileInfo.get("name") + " - " + e.getMessage());
                    Log.ln(e);
                    // Consider how to handle partial failure. Throw exception or return error count?
                }
            } else {
                // Logic for sharing individual files was missing in the original.
                // Assuming similar logic for files:
                 try {
                    jdbcTemplate.update(
                            "INSERT INTO share (name,share_user_id,fid,password,p_id,is_folder) VALUES (?,?,?,?,?,?)",
                            new Object[] {
                                    fileInfo.get("name"),
                                    fileInfo.get("uid"),
                                    fileInfo.get("id"),
                                    sharePassword,
                                    fileInfo.get("p_id"),
                                    fileInfo.get("is_folder") // This would be "0" or false for files
                            });
                } catch (Exception e) {
                    Log.ln("Error creating share record for file: " + fileInfo.get("name") + " - " + e.getMessage());
                    Log.ln(e);
                }
            }
        }
        return 1; // Original returned 1, likely for success. A count of successful shares might be better.
    }

    /**
    * 个人的文件下载
    * */
    public Map<String, Object> getPath(String fileId, String userId) { // Renamed params
        String sql = "SELECT file_path, name FROM files WHERE id = ? AND uid = ? AND is_folder = '0'"; // Added check for is_folder = 0
        Map<String, Object> path = null;
        try {
            path = jdbcTemplate.queryForMap(sql,new Object[]{fileId, userId});
        } catch (Exception e) { // Catching generic Exception is broad; consider DataAccessException
            Log.ln("Error getting path for fileId: " + fileId + ", userId: " + userId + " - " + e.getMessage());
            Log.ln(e);
            // Return null or throw a custom exception
        }
        return path;
    }

    /*
    * 分享文件下载
    * */
    public Map<String, Object> shareFileDownload(String shareRecordId, String accessingUserId){ // Renamed params
        // The original logic used 'id' for share_user_id which seems incorrect for identifying a share record.
        // Assuming 'fileid' was meant to be the ID of the 'share' table record.
        // And 'id' was the user_id of the person trying to download.
        // The query should probably also join with 'files' table to get actual file_path if 'share' table only stores 'fid'.
        // This method needs clarification on DB schema for 'share' table.
        // For now, adapting based on assumed intent.
        String sql = "SELECT f.file_path, s.name FROM share s JOIN files f ON s.fid = f.id WHERE s.id = ? AND f.is_folder = '0'";
        // This query assumes 's.id' is the primary key of the share table
        // and 's.fid' is the foreign key to the files table.
        // It does not currently check if 'accessingUserId' has rights to this share (e.g. if it's a private share).
        Map<String, Object> path = null;
        try {
            path = jdbcTemplate.queryForMap(sql,new Object[]{shareRecordId});
        } catch (Exception e) {
            Log.ln("Error in shareFileDownload for shareRecordId: " + shareRecordId + " - " + e.getMessage());
            Log.ln(e);
        }
        return path;
    }

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds Epoch seconds as string.
     * @param format Desired date format string.
     * @return Formatted date string or empty string if input is invalid.
     */
    public static String timeStamp2Date(String seconds, String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss"; // Default format
        }
        try {
            long secLong = Long.parseLong(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(new Date(secLong * 1000)); // Convert seconds to milliseconds
        } catch (NumberFormatException e) {
            Log.ln("Invalid seconds format for timeStamp2Date: " + seconds);
            return ""; // Return empty for invalid input
        }
    }
}
