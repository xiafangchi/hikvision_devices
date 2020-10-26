package com.ys.hikvision_device.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LedDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 姓名
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 节目
     */
    private String page;
    /**
     * 字体大小
     */
    private String fontSize;
    /**
     * 停留时间
     */
    private String stayTime;
    /**
     * 内容
     */
    @Column(columnDefinition = "varchar(102400) default null", nullable = true, length = 102400)
    private String content;
    /**
     * 对齐方式
     */
    private String position;
    /**
     * 字体
     */
    private String fontType;
}
