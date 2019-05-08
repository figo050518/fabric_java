package com.peerfintech.test;


import com.alibaba.fastjson.JSONArray;
import com.app.bean.FBInvokeRes;
import com.app.config.Config;
import com.app.util.FBUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.peerfintech.bean.Factor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


@RestController
public class LocTestController {




    @RequestMapping("loc/add")
    @ResponseBody
    String add(){
        try {
            String[] strings = new String[3];
            strings[0] = "张三";
            strings[1] ="李四";
            strings[2] ="5";

            String[] strings2 = new String[3];
            strings2[0] ="a";
            strings2[1] ="b";
            strings2[2] ="5";
            FBInvokeRes res =  FBUtil.fb.invoke(Config.CHANNEL_NAME,"loc","invoke",strings);
            System.out.println("invoke结果"+res);
            return "invoke结果"+String.valueOf(res.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "success";
    }


    @RequestMapping("loc/query")
    @ResponseBody
    String query(){
        try {
            String[] strings = new String[1];
            strings[0] ="a";
            FBInvokeRes res =  FBUtil.fb.invoke(Config.CHANNEL_NAME,"loc","query",strings);
            System.out.println("query结果"+res);
            return "query结果"+String.valueOf(res.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "success";
    }
    @RequestMapping("loc/KeepaliveQuery")
    @ResponseBody
    String keepaliveQuery(@RequestBody List<Factor> factor){
        try {
            FBInvokeRes res = new FBInvokeRes();
            for (Factor f:factor
            ) {
                res =  FBUtil.fb.invoke(Config.CHANNEL_NAME,"factor","KeepaliveQuery", new String[]{JSONArray.toJSONString(f)});
                System.out.println("invoke结果"+res);
            }
            return "invoke结果"+String.valueOf(res.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "success";
    }
}
