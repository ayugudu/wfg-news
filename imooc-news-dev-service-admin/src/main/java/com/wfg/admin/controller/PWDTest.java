package com.wfg.admin.controller;


import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-22 17:06
 */
public class PWDTest {
    public static void main(String[] args) {
       String pwd= BCrypt.hashpw("admin",BCrypt.gensalt());
        System.out.println(pwd);

    }
}
