package com.ys.hikvision_device.service;

import com.ys.hikvision_device.Entity.PersonnelAccessInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonnelAccessInfoService {

    Page<PersonnelAccessInfoEntity> findByLastname(Pageable pageable);

    void deleteAccessInfo(Long accessTime);
}
