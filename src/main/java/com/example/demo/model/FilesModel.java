package com.example.demo.model;

import com.example.demo.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FilesModel {
    @Resource
    private JdbcTemplate jdbcTemplate;
    public FilesModel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /*
    * 获取当前用户首层文件列表
    * */
    public List getFirstFold (String id, String fid) {
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
//        Long.toString(new Date().getTime())
        String sql = "INSERT INTO files(name,p_id,is_folder,created_at,uid,size) VALUES (?, ?, ?, ?, ?, ?)";
        return  jdbcTemplate.update(sql, new Object[] {name,pid,"1",System.currentTimeMillis()/1000,id,"-"});
    }
    /*
    * 重命名
    * */
    public int renameFile (String name, String id) {
        String sql = "UPDATE files SET name = ? where id = ?";
        return  jdbcTemplate.update(sql, new Object[] {name,id});
//        return 1;
    }
    /*
    * 移动文件夹
    * */
    public int moveFile (String pid, String id) {
        String sql = "UPDATE files SET p_id = ? where id = ?";
        return  jdbcTemplate.update(sql, new Object[] {pid,id});
//        return 1;
    }
    /*
     * 删除文件
     * */
    public int deleteFile ( String id) {
        String sql = "UPDATE files SET p_id = -1 where id = ?";
        return  jdbcTemplate.update(sql, new Object[] {id});
//        return 1;
    }
    /*
     * 复制文件夹
     * */
    public int copyFile (String pid, String id) {

        this.eachFolderCopy(pid, id);
        return 1;
    }

    /*
    * 遍历复制文件夹
    * */
    private String eachFolderCopy(String pid, String id){
        String sql = "SELECT * FROM files WHERE id  = ?";
        Map<String, Object> list = jdbcTemplate.queryForMap(sql,new Object[]{id});

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
                PreparedStatement ps = connection.prepareStatement("INSERT INTO files VALUES (?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, null);
                ps.setString(2, list.get("name").toString());
                ps.setString(3, list.get("is_share").toString());
                ps.setString(4, pid);
                ps.setString(5, null);
                ps.setString(6, null);
                ps.setString(7, list.get("is_folder").toString());
                ps.setString(8, list.get("created_at").toString());
                ps.setString(9, list.get("uid").toString());
                ps.setString(10, list.get("size").toString());
                //....
                return ps;
            }
        }, keyHolder);
        int insertResult = keyHolder.getKey().intValue();

        List<Map<String, Object>> sonlist = jdbcTemplate.queryForList("SELECT * FROM files WHERE p_id = ?",new Object[]{id});
        Integer re = null;
        for(int i = 0; i < sonlist.size();i++){
            int I = i;
            Log.ln(sonlist.get(i).get("id").toString());
            Log.ln(String.valueOf(insertResult));
            if (sonlist.get(I).get("is_folder").toString().equals("1")) {
                this.eachFolderCopy(String.valueOf(insertResult), sonlist.get(i).get("id").toString());
            }else{
                jdbcTemplate.update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO files VALUES (?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                        ps.setString(1, null);
                        ps.setString(2, sonlist.get(I).get("name").toString());
                        ps.setString(3, sonlist.get(I).get("is_share").toString());
                        ps.setString(4, String.valueOf(insertResult));
                        ps.setString(5, null);
                        ps.setString(6, null);
                        ps.setString(7, sonlist.get(I).get("is_folder").toString());
                        ps.setString(8, sonlist.get(I).get("created_at").toString());
                        ps.setString(9, sonlist.get(I).get("uid").toString());
                        ps.setString(10, sonlist.get(I).get("size").toString());
                        //....
                        return ps;
                    }
                }, keyHolder);
                int insertRet = keyHolder.getKey().intValue();
            }
        }
        return "";
    }

    /*
    * 展示文件夹列表
    * */
    public List showFolderList(String id, String uid) {
        String sql = "SELECT id,p_id as pid,name as title FROM files WHERE is_folder = 1 AND uid = ?";
        List list = jdbcTemplate.queryForList(sql,new Object[]{uid});
        return list;
    }
    /*
     * 上传文件，更新数据库
     * */
    public int updateFile (String name, String id, String pid, String path,String size,String type) {
        String sql = "INSERT INTO files (name, p_id, file_form, file_path, is_folder, created_at, uid,  size) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        try {
            int i = jdbcTemplate.update(sql, new Object[] {name, pid, type, path, "0", System.currentTimeMillis()/1000, id, size});
            return  i;
        } catch (Exception e) {
            Log.ln(123);
            Log.ln(e);
        }
        return  0;
    }
    /**
    * 分享文件
    * */
    public int share(List<Map<String,Object>> map,String pwd){
        for (int i = 0; i <= map.size(); i++){
            // 如果是文件夹 更新该文件夹下所有文件为分享
            if(map.get(i).get("is_folder").equals(1)){
                try {
//                    jdbcTemplate.update("insert into share (name,share_user_id,fid,path) select name,uid,id,file_path from files where p_id = ?; ", new Object[] {map.get(i).get("id")});
                    // 写入文件分享
                    jdbcTemplate.update(
                            "insert into share (name,share_user_id,fid,password,p_id,is_folder)  VALUES (?,?,?,?,?,?)",
                            new Object[] {map.get(i).get("name"),
                                    map.get(i).get("uid"),
                                    map.get(i).get("id"),
                                    pwd,
                                    map.get(i).get("p_id"),
                                    map.get(i).get("is_folder")});
                } catch (Exception e) {
                    Log.ln(123);
                    Log.ln(e);
                }
            }else{

            }
        }
        return 1;
    }
    /**
    * 个人的文件下载
    * */
    public Map<String, Object> getPath(String fileid, String id) {
        String sql = "SELECT file_path,name FROM files WHERE id = ? AND uid = ?";
        Map<String, Object> path = null;
        try {
            path = jdbcTemplate.queryForMap(sql,new Object[]{fileid,id});
        } catch (Exception e) {
            Log.ln(e);
        }
        return  path;
    }
    /*
    * 分享文件下载
    * */
    public Map<String, Object> shareFileDownload(String fileid, String id){
        // 判断是否为公共分享
        // 判断是否为被分享用户
        String sql = "SELECT file_path,name FROM share WHERE id = ? AND share_user_id = ?";
        Map<String, Object> path = null;
        try {
            path = jdbcTemplate.queryForMap(sql,new Object[]{fileid,id});
        } catch (Exception e) {
            Log.ln(e);
        }
        return  path;
    }
    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }


}
