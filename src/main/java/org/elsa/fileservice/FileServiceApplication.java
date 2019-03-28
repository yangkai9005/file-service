package org.elsa.fileservice;

import io.undertow.UndertowOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;

//@EnableScheduling
@SpringBootApplication
public class FileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);

        // 不可随意更改 与jenkins启动脚本有关
        System.out.println("=====running.=====");
    }

    /**
     * undertow 容器配置
     */
    @Bean
    public UndertowServletWebServerFactory factory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        // 开启http2 开启keep-alive 允许cookie最大化
        factory.addBuilderCustomizers(builder ->
                builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true).
                        setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true).
                        setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, true));
        return factory;
    }
}
