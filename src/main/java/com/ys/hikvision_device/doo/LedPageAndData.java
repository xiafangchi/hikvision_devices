package com.ys.hikvision_device.doo;

import com.ys.hikvision_device.Entity.LedDataEntity;
import com.ys.hikvision_device.Entity.LedPageEntity;
import lombok.Data;

import java.util.List;

@Data
public class LedPageAndData {

    private LedPageEntity ledPage;

    private List<LedDataEntity> ledDataList;

}
