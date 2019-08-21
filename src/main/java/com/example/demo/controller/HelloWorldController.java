package com.example.demo.controller;

import com.example.demo.model.Books;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class HelloWorldController {
    @RequestMapping(value = "world")
    public String world () {
        return "hello world";
    }
    @RequestMapping(value = "json", produces="application/json; charset=utf-8")
    public Books json () {
        Books books = new Books();
        books.setId(1);
        books.setTitle("三国演义");
        books.setAuthor("罗贯中");
        return books;
    }
    @RequestMapping("getData")
    public String getData (String name, String pwd) {
        return "name=" + name + ",pwd=" + pwd;
    }
    @RequestMapping("date")
    public Date date () {
        Date date = new Date();
        return date;
    }
}
