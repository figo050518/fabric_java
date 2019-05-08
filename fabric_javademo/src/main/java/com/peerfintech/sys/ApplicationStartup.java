package com.peerfintech.sys;

import com.app.util.FBUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args)  throws Exception{
        System.out.println("系统启动执行！");
        FBUtil.init();
    }
}
