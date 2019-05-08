package com.peerfintech.test;


import com.alibaba.fastjson.JSONArray;
import com.app.bean.FBInvokeRes;
import com.app.config.Config;
import com.app.util.FBUtil;
import com.peerfintech.bean.Transfer;
import com.peerfintech.bean.Warehouse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class TestWareHouseController {

    @RequestMapping(value = "/Material/GetMaterial")
    @ResponseBody
    String query(String plant,String stor_loc,String charg,String wlbm) {
        try {
            Warehouse h = new Warehouse();
            h.setPlant(plant);
            h.setStor_loc(stor_loc);
            h.setCharg(charg);
            h.setWlbm(wlbm);
            h.setKcsl(55);
            String res = FBUtil.fb.query(Config.CHANNEL_NAME, "warehouse", "queryData", new String[]{"objectType",JSONArray.toJSONString(h)});
            System.out.println("查询结果" + res);
            return "查询结果" + res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @RequestMapping("/Material/WarehouseInfo")
    @ResponseBody
    String save(@RequestBody List<Warehouse> houses) {
        try {
            FBInvokeRes res = new FBInvokeRes();
            for (Warehouse house:houses
            ) {
                res = FBUtil.fb.invoke(Config.CHANNEL_NAME, "warehouse", "SaveData",  new String[]{"objectType",JSONArray.toJSONString(house)});
                System.out.println("invoke结果" + res.toString());
            }
            return "invoke结果" + res.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @RequestMapping(value ="/Material/Transfer")
    @ResponseBody
    String transfer(@RequestBody Transfer transfer) {
        try {
            FBInvokeRes res  = FBUtil.fb.invoke(Config.CHANNEL_NAME, "warehouse", "saveTransfer",  new String[]{"objectType",JSONArray.toJSONString(transfer)});
            System.out.println("invoke结果" + res.toString());
            return "invoke结果" + res.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @RequestMapping(value = "/Material/GetTransfer")
    @ResponseBody
    String GetTransfer(String dbdbh) {
        try {
            String res = FBUtil.fb.query(Config.CHANNEL_NAME, "warehouse", "getTransfer", new String[]{dbdbh});
            System.out.println("查询结果" + res);
            return "查询结果" + res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
