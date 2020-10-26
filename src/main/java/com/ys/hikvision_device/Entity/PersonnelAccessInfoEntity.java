package com.ys.hikvision_device.Entity;

import com.ys.hikvision_device.utils.DatetimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelAccessInfoEntity {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 进出⽅向
     */
    private String direction;
    /**
     * 通过地点的⼈脸识别设 备Id
     */
    private String deviceIp;
    /**
     * 通过
     */
    private Long time;
    /**
     * 刷卡进⼊时的卡号
     */
    private String cardNo;
    /**
     * 工号
     */
    private String employeeNo;
    /**
     * 部门
     */
    private String department;

    /**
     * 通过
     */
    private String timeStr;

    public String getTimeStr() {
        if (time != null && time > 0) {
            timeStr = DatetimeUtils.convertTimestampToDate(time, "yyyy-MM-dd HH:mm:ss");
        }
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
}
