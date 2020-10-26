package com.ys.hikvision_device.dao;

import com.ys.hikvision_device.Entity.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ConfigEntity, Long> {
}
