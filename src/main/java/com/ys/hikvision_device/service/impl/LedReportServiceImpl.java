package com.ys.hikvision_device.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ys.hikvision_device.dao.PersonnelInOutReportRepository;
import com.ys.hikvision_device.doo.PersonnelInOutRecordDo;
import com.ys.hikvision_device.doo.PersonnelInOutReportDo;
import com.ys.hikvision_device.service.LedReportService;
import com.ys.hikvision_device.utils.DatetimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LedReportServiceImpl implements LedReportService {

    @Autowired
    private PersonnelInOutReportRepository personnelInOutReportRepository;

    @Override
    public PersonnelInOutReportDo personnelReport() {
        Long startTime = DatetimeUtils.getToday();
        Long endTime = DatetimeUtils.getTodayEnd();
        List<Object[]> outReport = personnelInOutReportRepository.personnelInOutReport(startTime, endTime);
        PersonnelInOutReportDo reportDo = new PersonnelInOutReportDo();
        if (outReport != null) {
            for (Object[] objs : outReport) {
                for (int i = 0; i < objs.length; i++) {
                    if (i == 0) {
                        String a = String.valueOf(objs[i]);
                        if (!Strings.isNullOrEmpty(a) && !"null".equals(a)) {
                            reportDo.setInNum(Long.valueOf(a));
                        } else {
                            reportDo.setInNum(0L);
                        }
                    } else if (i == 1) {
                        String a = String.valueOf(objs[i]);
                        if (!Strings.isNullOrEmpty(a) && !"null".equals(a)) {
                            reportDo.setTotalNum(Long.valueOf(a));
                        } else {
                            reportDo.setTotalNum(0L);
                        }
                    }
                }
            }
        }
        List<Object[]> personnelRecord = personnelInOutReportRepository.personnelRecord(startTime, endTime);
        if (personnelRecord != null) {
            List<PersonnelInOutRecordDo> list = Lists.newArrayList();
            for (Object[] objs : personnelRecord) {
                PersonnelInOutRecordDo inOutRecordDo = new PersonnelInOutRecordDo();
                for (int i = 0; i < objs.length; i++) {
                    if (i == 0) {
                        String a = String.valueOf(objs[i]);
                        inOutRecordDo.setInNum(Long.valueOf(a));
                    } else if (i == 1) {
                        String a = String.valueOf(objs[i]);
                        inOutRecordDo.setTotalNum(Long.valueOf(a));
                    } else if (i == 2) {
                        String a = String.valueOf(objs[i]);
                        inOutRecordDo.setDepartment(a);
                    }
                }
                list.add(inOutRecordDo);
            }
            reportDo.setRecordRespList(list);
        }
        return reportDo;
    }
}
