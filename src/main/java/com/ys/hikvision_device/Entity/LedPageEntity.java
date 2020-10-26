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
public class LedPageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 高
     */
    private Integer height;
    /**
     * 宽
     */
    private Integer width;
    /**
     * x
     */
    private Integer x;
    /**
     * y
     */
    private Integer y;
}
