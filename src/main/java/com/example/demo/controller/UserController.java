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



}
