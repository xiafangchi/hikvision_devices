package com.ys.hikvision_device.service.impl;

import com.ys.hikvision_device.Entity.PersonnelAccessInfoEntity;
import com.ys.hikvision_device.dao.PersonnelAccessInfoRepository;
import com.ys.hikvision_device.service.PersonnelAccessInfoService;
import com.ys.hikvision_device.utils.DatetimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PersonnelAccessInfoServiceImpl implements PersonnelAccessInfoService {


    @Autowired
    PersonnelAccessInfoRepository personnelAccessInfoRepository;

    @Override
    public Page<PersonnelAccessInfoEntity> findByLastname(Pageable pageable) {
        Long startTime = DatetimeUtils.getToday();
        Long endTime = DatetimeUtils.getTodayEnd();
        return personnelAccessInfoRepository.findByLastname(startTime, endTime, pageable);
    }

    @Override
    public void deleteAccessInfo(Long accessTime) {
        personnelAccessInfoRepository.deleteAccessInfo(accessTime);
    }
}
