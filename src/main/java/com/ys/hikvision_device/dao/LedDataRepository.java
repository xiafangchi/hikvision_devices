package com.ys.hikvision_device.dao;


import com.ys.hikvision_device.Entity.LedDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedDataRepository extends JpaRepository<LedDataEntity, Long> {
}
