package com.example.demo.entity;

import javax.xml.crypto.Data;

public class Files {
    private int id;
    private String name;
    private int is_share;
    private int p_id;
    private String file_form;
    private String file_path;
    private int is_folder;
    private Data created_at;
    private int uid;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIs_share() {
        return is_share;
    }

    public int getP_id() {
        return p_id;
    }

    public String getFile_form() {
        return file_form;
    }

    public String getFile_path() {
        return file_path;
    }

    public int getIs_folder() {
        return is_folder;
    }

    public Data getCreated_at() {
        return created_at;
    }

    public int getUid() {
        return uid;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIs_share(int is_share) {
        this.is_share = is_share;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public void setFile_form(String file_form) {
        this.file_form = file_form;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public void setIs_folder(int is_folder) {
        this.is_folder = is_folder;
    }

    public void setCreated_at(Data created_at) {
        this.created_at = created_at;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
