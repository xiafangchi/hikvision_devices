package com.ys.hikvision_device.task;

import com.ys.hikvision_device.service.HikvisionFaceUpload;
import onbon.bx06.Bx6GEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Runtest {

    @Autowired
    private HikvisionFaceUpload hikvisionFaceUpload;

    @PostConstruct
    public void haha() {
        System.out.println("@Postcontruct’在依赖注入完成后自动调用");
        hikvisionFaceUpload.upload();
        System.out.println("初始化大屏控制卡");
        try {
            String path1 = "";
            Bx6GEnv.initial(path1, 30000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("自动调用完成");

    }
}
