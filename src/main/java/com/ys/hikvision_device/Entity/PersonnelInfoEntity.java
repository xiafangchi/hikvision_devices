package com.ys.hikvision_device.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelInfoEntity {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 工号
     */
    private String employeeNo;
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 通过时间
     */
    private Long accessTime;
    /**
     * 进出类型
     */
    private String direction;
    /**
     * 部门
     */
    private String department;
}
