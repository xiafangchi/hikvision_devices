package com.ys.hikvision_device.dao;

import com.ys.hikvision_device.Entity.PersonnelInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelRepository extends JpaRepository<PersonnelInfoEntity, Long> {
}
