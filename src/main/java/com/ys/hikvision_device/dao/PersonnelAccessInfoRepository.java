package com.ys.hikvision_device.dao;

import com.ys.hikvision_device.Entity.PersonnelAccessInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface PersonnelAccessInfoRepository extends JpaRepository<PersonnelAccessInfoEntity, Long> {


    @Query(value = "SELECT * FROM PERSONNEL_ACCESS_INFO_ENTITY WHERE  time>= ?1 and time <= ?2", nativeQuery = true)
    Page<PersonnelAccessInfoEntity> findByLastname(Long startTime, Long endTime, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE  FROM PERSONNEL_ACCESS_INFO_ENTITY  WHERE  time <= ?1 ", nativeQuery = true)
    void deleteAccessInfo(Long accessTime);
}
