package com.ys.hikvision_device.dao;

import com.ys.hikvision_device.Entity.PersonnelEmployeeNoAndDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelEmployeeNoAndDepartmentRepository extends JpaRepository<PersonnelEmployeeNoAndDepartment, Long> {
}
