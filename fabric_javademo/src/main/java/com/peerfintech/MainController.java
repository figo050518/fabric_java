package com.peerfintech;


import com.peerfintech.sys.ApplicationStartup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class MainController {


    public static void main(String[] args) {
        SpringApplication.run(MainController.class, args);
    }


    @RequestMapping("/")
    String index(){
        return "Hello Spring Boot";
    }


}
