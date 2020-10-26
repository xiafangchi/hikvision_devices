package com.ys.hikvision_device.dao;

import com.ys.hikvision_device.Entity.PersonnelInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonnelInOutReportRepository extends JpaRepository<PersonnelInfoEntity, Long> {

    @Query(value = "SELECT sum(case when DIRECTION  = '进' then 1 else 0 end) as inNum,count(*) as totalNum FROM PERSONNEL_INFO_ENTITY  where ACCESS_TIME >= :startTime and ACCESS_TIME <= :endTime", nativeQuery = true)
    List<Object[]> personnelInOutReport(@Param("startTime") Long startTime, @Param("endTime") Long endTime);


    @Query(value = "SELECT sum(case when DIRECTION  = '进' then 1 else 0 end) as inNum,count(*) as totalNum ,department FROM PERSONNEL_INFO_ENTITY  where ACCESS_TIME >= :startTime and ACCESS_TIME <= :endTime group by  department", nativeQuery = true)
    List<Object[]> personnelRecord(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

}
