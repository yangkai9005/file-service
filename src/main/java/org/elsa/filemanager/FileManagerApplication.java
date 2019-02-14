package org.elsa.filemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
@SpringBootApplication
public class FileManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileManagerApplication.class, args);

        // 不可随意更改 与jenkins启动脚本有关
        System.out.println("=====running.=====");
    }
}
