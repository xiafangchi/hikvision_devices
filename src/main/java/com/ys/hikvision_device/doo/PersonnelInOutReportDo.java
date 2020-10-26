package com.ys.hikvision_device.doo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PersonnelInOutReportDo {
    /**
     * 总人数
     */
    private Long totalNum;
    /**
     * 在场人数
     */
    private Long inNum;
    /**
     * 分类统计信息
     */
    List<PersonnelInOutRecordDo> recordRespList;
}
