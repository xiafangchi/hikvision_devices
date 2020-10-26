package com.ys.hikvision_device.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ys.hikvision_device.Entity.ConfigEntity;
import com.ys.hikvision_device.Entity.LedDataEntity;
import com.ys.hikvision_device.Entity.LedPageEntity;
import com.ys.hikvision_device.dao.ConfigRepository;
import com.ys.hikvision_device.dao.LedDataRepository;
import com.ys.hikvision_device.dao.LedPageRepository;
import com.ys.hikvision_device.doo.LedPageAndData;
import com.ys.hikvision_device.doo.PersonnelInOutRecordDo;
import com.ys.hikvision_device.doo.PersonnelInOutReportDo;
import com.ys.hikvision_device.service.LedControllerService;
import com.ys.hikvision_device.service.LedReportService;
import com.ys.hikvision_device.utils.DatetimeUtils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import onbon.bx06.Bx6GScreen;
import onbon.bx06.Bx6GScreenClient;
import onbon.bx06.area.DateStyle;
import onbon.bx06.area.DateTimeBxArea;
import onbon.bx06.area.DynamicBxArea;
import onbon.bx06.area.TimeStyle;
import onbon.bx06.area.page.TextBxPage;
import onbon.bx06.cmd.dyn.DynamicBxAreaRule;
import onbon.bx06.file.ProgramBxFile;
import onbon.bx06.message.global.ACK;
import onbon.bx06.series.Bx6E;
import onbon.bx06.utils.DisplayStyleFactory;
import onbon.bx06.utils.TextBinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LedControllerServiceImpl implements LedControllerService {
    private static Logger logger = LoggerFactory.getLogger(LedControllerServiceImpl.class);

    @Autowired
    LedPageRepository
            ledPageRepository;

    @Autowired
    LedDataRepository ledDataRepository;

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    LedReportService ledReportService;

    @Async
    public void SendDynamicPersonnel(boolean delete, boolean automatic) {
        try {
            List<ConfigEntity> configEntityList = configRepository.findAll();
            if (configEntityList != null && configEntityList.size() > 0) {
                ConfigEntity configEntity = configEntityList.get(0);
                String ledIp = configEntity.getLedIp();
                String ledPort = configEntity.getLedPort();
                if (!Strings.isNullOrEmpty(ledIp) && !Strings.isNullOrEmpty(ledPort)) {
                    List<LedPageAndData> ledPageAndDataList = Lists.newArrayList();
                    List<LedPageEntity> ledPageAndDataRespList = ledPageRepository.findAll();
                    if (ledPageAndDataRespList != null && ledPageAndDataRespList.size() > 0) {
                        for (LedPageEntity ledPageEntity : ledPageAndDataRespList) {
                            LedPageAndData ledPageAndData = new LedPageAndData();
                            ledPageAndData.setLedPage(ledPageEntity);
                            LedDataEntity ledDataEntity = new LedDataEntity();
                            ledDataEntity.setPage(ledPageEntity.getName());
                            Example<LedDataEntity> example = Example.of(ledDataEntity);
                            List<LedDataEntity> list = ledDataRepository.findAll(example);
                            ledPageAndData.setLedDataList(list);
                            ledPageAndDataList.add(ledPageAndData);
                        }
                    }
                    try {
                        SendDynamic(ledIp, Integer.valueOf(ledPort), ledPageAndDataList, delete, automatic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SendDynamic(String ip, Integer port, List<LedPageAndData> respList, boolean delete, boolean automatic) throws Exception {
        if (respList != null && respList.size() > 0) {
            int i = 0;
            DisplayStyleFactory.DisplayStyle[] styles = DisplayStyleFactory.getStyles().toArray(new DisplayStyleFactory.DisplayStyle[2]);
            try {
                for (LedPageAndData pageAndDataResp : respList) {
                    Bx6GScreenClient screen = new Bx6GScreenClient("MyScreen", new Bx6E());
                    screen.connect(ip, port);
                    DynamicBxAreaRule rule = new DynamicBxAreaRule();
                    rule.setId(i);
                    rule.setRunMode((byte) 0);
                    rule.setRelativeAllPrograms(true);
                    boolean dynamicBxAreaFlag = false;
                    if (pageAndDataResp.getLedDataList() != null && pageAndDataResp.getLedDataList().size() > 0) {
                        DynamicBxArea area = new DynamicBxArea(pageAndDataResp.getLedPage().getX(), pageAndDataResp.getLedPage().getY(),
                                pageAndDataResp.getLedPage().getWidth(), pageAndDataResp.getLedPage().getHeight(), screen.getProfile());
                        DateTimeBxArea dateTimeBxArea = new DateTimeBxArea(pageAndDataResp.getLedPage().getX(), pageAndDataResp.getLedPage().getY(),
                                pageAndDataResp.getLedPage().getWidth(), pageAndDataResp.getLedPage().getHeight(), screen.getProfile());
                        if (delete) {
                            screen.deletePrograms();
                        }
                        for (LedDataEntity dataInfoResp : pageAndDataResp.getLedDataList()) {
                            if (!dataInfoResp.getType().equals("日期时间")) {
                                TextBxPage page = new TextBxPage();
                                if (dataInfoResp.getType().equals("自定义内容")) {
                                    String content = dataInfoResp.getContent();
                                    String[] contentList = content.split("\n");
                                    for (int j = 0; j < contentList.length; j++) {
                                        if (j == 0) {
                                            page.setText(contentList[j]);
                                        } else {
                                            page.newLine(contentList[j]);
                                        }
                                    }
                                } else {
                                    Map map = new HashMap();
                                    PersonnelInOutReportDo personnelInOutReportDo = ledReportService.personnelReport();
                                    if (personnelInOutReportDo != null) {
                                        map.put("场内人数", personnelInOutReportDo.getInNum());
                                        map.put("总人数", personnelInOutReportDo.getTotalNum());
                                        map.put("日期", DatetimeUtils.getNowDateString());
                                        if (personnelInOutReportDo.getRecordRespList() != null && personnelInOutReportDo.getRecordRespList().size() > 0) {
                                            for (PersonnelInOutRecordDo recordDo : personnelInOutReportDo.getRecordRespList()) {
                                                Long inNum = recordDo.getInNum();
                                                String inNumStr = null;
                                                if (inNum != null) {
                                                    if (inNum < 10) {
                                                        inNumStr = "a" + inNum.toString();
                                                    } else {
                                                        inNumStr = inNum.toString();
                                                    }
                                                }
                                                map.put(recordDo.getDepartment(), inNumStr);
                                            }
                                        }
                                        String content = processTemplate(dataInfoResp.getContent(), map);
                                        System.out.println(content);
                                        page.setText(content);
                                    }
                                }
                                String fontType = dataInfoResp.getFontType();
                                if (Strings.isNullOrEmpty(fontType)) {
                                    fontType = "宋体";
                                }
                                page.setFont(new Font(fontType, Font.PLAIN, Integer.valueOf(dataInfoResp.getFontSize())));
                                page.setDisplayStyle(styles[2]);
                                page.setStayTime(Integer.valueOf(dataInfoResp.getStayTime()));
                                page.setHorizontalAlignment(TextBinary.Alignment.NEAR);
                                if (dataInfoResp.getPosition() != null) {
                                    if ("左对齐".equals(dataInfoResp.getPosition())) {
                                        page.setHorizontalAlignment(TextBinary.Alignment.NEAR);
                                    } else if ("右对齐".equals(dataInfoResp.getPosition())) {
                                        page.setHorizontalAlignment(TextBinary.Alignment.FAR);
                                    } else if ("居中对齐".equals(dataInfoResp.getPosition())) {
                                        page.setHorizontalAlignment(TextBinary.Alignment.CENTER);
                                        page.setVerticalAlignment(TextBinary.Alignment.CENTER);
                                    }
                                }
                                area.addPage(page);
                                dynamicBxAreaFlag = true;
                            } else {
                                ProgramBxFile pf = new ProgramBxFile(i, screen.getProfile());
                                screen.deletePrograms();
                                dateTimeBxArea.setMultiline(false);
                                dateTimeBxArea.setDateStyle(DateStyle.YYYY_MM_DD_1);
                                dateTimeBxArea.setTimeStyle(TimeStyle.HH12_MM_SS_1);
                                dateTimeBxArea.setFont(new Font("黑体", Font.PLAIN, Integer.valueOf(dataInfoResp.getFontSize())));
                                pf.addArea(dateTimeBxArea);
                                boolean sendResult = screen.writeProgram(pf);
                                if (!sendResult) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                tips("Led屏幕内容写入失败,请重试");
                                                logger.info("Led屏幕内容写入失败,请重试");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        if (delete) {
                            screen.deleteAllDynamic();
                        }
                        if (dynamicBxAreaFlag) {
                            Bx6GScreen.Result<ACK> ackResult = screen.writeDynamic(rule, area);
                            Integer result = ackResult.getError().value;
                            if (result != 0) {
                                String message = ledMessage(result);
                                if (Strings.isNullOrEmpty(message)) {
                                    message = "Led屏幕内容写入失败，请重试";
                                    logger.info("Led屏幕内容写入失败" + message);
                                }
                                String finalMessage = message;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            tips("Led显示屏设置失败，" + finalMessage);
                                            logger.info("Led屏幕内容写入失败,请重试");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            i++;
                            screen.disconnect();
                        }
                    }
                }
                if (automatic == false) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tips("发送成功");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tips("Led显示屏设置失败，请检查相关参数");
                            logger.info("Led显示屏设置失败，请检查相关参数");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }
    }

    public void tips(String message) throws Exception {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("提示");
        alert.headerTextProperty().set(message);
        alert.showAndWait();
    }

    public String ledMessage(Integer code) {
        if (code != null) {
            switch (code) {
                case -2:
                    return "断线";
                case -1:
                    return "逾时未回应";
                case 0:
                    return "发送成功";
                case 1:
                    return "命令組錯誤";
                case 2:
                    return "此命令不存在";
                case 3:
                    return "控制器忙碌";
                case 4:
                    return "存儲器容量越界";
                case 5:
                    return "數據包校驗錯誤";
                case 6:
                    return "文件不存在";
                case 7:
                    return "Flash 訪問錯誤";
                case 8:
                    return "文件下載錯誤";
                case 9:
                    return "文件名錯誤";
                case 10:
                    return "文件類型錯誤";
                case 11:
                    return "文件校驗錯誤";
                case 12:
                    return "字庫文件不存在";
                case 13:
                    return "Firmware 與控制器類型不匹配";
                case 14:
                    return "是其時間格式錯誤";
                case 15:
                    return "此文件已存在";
                case 16:
                    return "文件區塊號錯誤";
                case 17:
                    return "控制器類型不匹配";
                case 18:
                    return "控制器參數越界或錯誤";
                case 19:
                    return "控制器編號錯誤";
                case 20:
                    return "用戶密碼錯誤";
                case 21:
                    return "輸入的舊密碼不正確";
                case 22:
                    return "底層無密碼，上位機有密碼";
                case 23:
                    return "底層有密碼，上位機無密碼";
                case 24:
                    return "時鐘芯片故障";
                case 25:
                    return "命令參數錯誤";
                case 26:
                    return "極聯系統通訊故障";
                case 27:
                    return "無戰鬥時間區域";
                case 28:
                    return "無秒表區域";
                default:
                    return null;
            }
        }
        return null;
    }

    public static String processTemplate(String template, Map<String, Object> params) {
        Matcher m = Pattern.compile("\\$\\{[\\u4E00-\\u9FA5\\w]+\\}").matcher(template);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String param = m.group();
            Object value = params.get(param.substring(2, param.length() - 1));
            String valueStr = "0";
            if (value != null) {
                valueStr = value.toString();
                if (valueStr.startsWith("a")) {
                    valueStr = valueStr.replace("a", " ");
                }
            }
            m.appendReplacement(sb, valueStr);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
