package com.ys.hikvision_device.doo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelInOutRecordDo {
    /**
     * 总人数
     */
    private Long totalNum;
    /**
     * 在场人数
     */
    private Long inNum;
    /**
     * 部门
     */
    private String department;
}
