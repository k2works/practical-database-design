package com.example.pms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 生産管理システム（Production Management System）のメインクラス.
 */
@SpringBootApplication
public class Application {

    /**
     * アプリケーションのエントリーポイント.
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
