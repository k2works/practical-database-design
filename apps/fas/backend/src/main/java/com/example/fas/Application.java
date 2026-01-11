package com.example.fas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 財務会計システム（Financial Accounting System）のメインクラス.
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
