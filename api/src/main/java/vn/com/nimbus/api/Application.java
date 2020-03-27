package vn.com.nimbus.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vn.com.nimbus.common.BaseApplication;

@SpringBootApplication
public class Application extends BaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
