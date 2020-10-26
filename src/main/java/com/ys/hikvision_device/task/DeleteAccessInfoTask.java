package com.ys.hikvision_device.task;

import com.ys.hikvision_device.service.PersonnelAccessInfoService;
import com.ys.hikvision_device.utils.DatetimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("deleteAccessInfoTask")
@EnableScheduling
public class DeleteAccessInfoTask {

    @Autowired
    private PersonnelAccessInfoService personnelAccessInfoService;

    private static Log logger = LogFactory.getLog(DeleteAccessInfoTask.class);

    /**
     * 在线商城订单自动确认收货
     */
    @Scheduled(cron = " 0 0 1 * * ?")
    public void autoConfirmTakeDelivery() {
        logger.info("删除七天之前的通过记录");
        try {
            Long nowTime = DatetimeUtils.getToday();
            Long accessTime = nowTime - (7 * 86400);
            personnelAccessInfoService.deleteAccessInfo(accessTime);
            logger.info("删除七天之前的通过记录成功");
        } catch (Exception e) {
            logger.info("删除七天之前的通过记录失败", e);
            e.printStackTrace();
        }
    }
}
