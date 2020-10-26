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
public class ConfigEntity {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * LEDIP
     */
    private String LedIp;

    /**
     * Ledport
     */
    private String ledPort;

    /**
     * 大屏刷新
     */
    private String refreshTime;


}
