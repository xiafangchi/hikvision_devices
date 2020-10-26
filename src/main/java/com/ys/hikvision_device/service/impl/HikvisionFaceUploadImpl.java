package com.ys.hikvision_device.service.impl;

import com.sun.jna.Pointer;
import com.ys.hikvision_device.Entity.FaceDeviceEntity;
import com.ys.hikvision_device.Entity.PersonnelAccessInfoEntity;
import com.ys.hikvision_device.Entity.PersonnelEmployeeNoAndDepartment;
import com.ys.hikvision_device.Entity.PersonnelInfoEntity;
import com.ys.hikvision_device.dao.FaceDeviceRepository;
import com.ys.hikvision_device.dao.PersonnelAccessInfoRepository;
import com.ys.hikvision_device.dao.PersonnelEmployeeNoAndDepartmentRepository;
import com.ys.hikvision_device.dao.PersonnelRepository;
import com.ys.hikvision_device.hikvision.HCNetSDK;
import com.ys.hikvision_device.service.HikvisionFaceUpload;
import com.ys.hikvision_device.utils.DatetimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class HikvisionFaceUploadImpl implements HikvisionFaceUpload {

    private Logger logger = LoggerFactory.getLogger(HikvisionFaceUploadImpl.class);

    @Autowired
    private FaceDeviceRepository faceDeviceRepository;

    @Autowired
    private PersonnelEmployeeNoAndDepartmentRepository departmentRepository;

    @Autowired
    private PersonnelAccessInfoRepository accessInfoRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
    HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
    HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息
    String m_sDeviceIP;//已登录设备的IP地址
    String m_sUsername;//设备用户名
    String m_sPassword;//设备密码
    int lUserID;//用户句柄
    int lAlarmHandle;//报警布防句柄
    public static HCNetSDK.FMSGCallBack fMSFCallBack;//报警回调函数实现
    public static HCNetSDK.FMSGCallBack_V31 fMSFCallBack_V31;//报警回调函数实现

    @Override
    @Async("logThread")
    public void upload() {
        try {
            List<FaceDeviceEntity> list = faceDeviceRepository.findAll();
            logger.info("进来了");
            if (list != null && list.size() > 0) {
                logger.info("初始化");
                initDevices();
                if (list != null && list.size() > 0) {
                    for (FaceDeviceEntity entity : list) {
                        logger.info("登录" + entity.getIp());
                        int registResult = deviceRegist(entity);
                        if (registResult == 2) {
                            logger.info("登录失败");
                            entity.setStatus("登录失败");
                        } else {
                            entity.setStatus("登录成功");
                            logger.info("布防");
                            String messageCallResult = setMessageCall();
                            entity.setStatus(messageCallResult);
                        }
                        faceDeviceRepository.save(entity);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化设备
     *
     * @return
     */
    public int initDevices() {
        if (!hCNetSDK.NET_DVR_Init()) return 1;//初始化失败
        return 0;
    }

    /**
     * 注册登录设备
     *
     * @param device
     * @return
     */
    public int deviceRegist(FaceDeviceEntity device) {
        logger.info("登录注册");
//        if (lUserID > -1) {
//            //先注销
//            hCNetSDK.NET_DVR_Logout(lUserID);
//            lUserID = -1;
//        }
        //注册
        m_sDeviceIP = device.getIp();//设备ip地址
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());
        m_sUsername = device.getUserName();//设备用户名
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

        m_sPassword = device.getPassword();//设备密码
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());
        m_strLoginInfo.wPort = (short) Integer.parseInt("8000");
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        m_strLoginInfo.write();
        lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID == -1) {
            return 2;
        }
        return 0;
    }

    public class FMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 {
        //报警信息回调函数

        public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            logger.info("开始监测");
            AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
            return true;
        }
    }

    /**
     * 设置报警回调函数
     */
    public String setMessageCall() {
        if (fMSFCallBack_V31 == null) {
            fMSFCallBack_V31 = new FMSGCallBack_V31() {
            };
            Pointer pUser = null;
            if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_V31, pUser)) {
                logger.info("设置回调函数失败!");
            }
        }
        HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
        m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
        m_strAlarmInfo.byLevel = 1;//智能交通布防优先级：0- 一等级（高），1- 二等级（中），2- 三等级（低）
        m_strAlarmInfo.byAlarmInfoType = 1;//智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
        m_strAlarmInfo.byDeployType = 1; //布防类型(仅针对门禁主机、人证设备)：0-客户端布防(会断网续传)，1-实时布防(只上传实时数据)
        m_strAlarmInfo.write();
        lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
        if (lAlarmHandle == -1) {
            return "布防失败，错误号:" + hCNetSDK.NET_DVR_GetLastError();
        } else {
            logger.info("布防成功");
            return "布防成功";
        }
    }


    public void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        try {
            logger.info("开始监测");
            String sAlarmType = new String();
            String[] newRow = new String[3];
            //报警时间
            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String[] sIP = new String[2];
            sAlarmType = new String("lCommand=0x") + Integer.toHexString(lCommand);
            //lCommand是传的报警类型
            switch (lCommand) {
                case HCNetSDK.COMM_ALARM_ACS: //门禁主机报警信息
                    logger.info("门禁主机报警信息");
                    HCNetSDK.NET_DVR_ACS_ALARM_INFO strACSInfo = new HCNetSDK.NET_DVR_ACS_ALARM_INFO();
                    strACSInfo.write();
                    Pointer pACSInfo = strACSInfo.getPointer();
                    pACSInfo.write(0, pAlarmInfo.getByteArray(0, strACSInfo.size()), 0, strACSInfo.size());
                    strACSInfo.read();
                    sAlarmType = sAlarmType + "：门禁主机报警信息，卡号：" + new String(strACSInfo.struAcsEventInfo.byCardNo).trim() + "，卡类型：" +
                            strACSInfo.struAcsEventInfo.byCardType + "，报警主类型：" + strACSInfo.dwMajor + "，报警次类型：" + strACSInfo.dwMinor
                            + ",工号:" + strACSInfo.struAcsEventInfo.dwEmployeeNo + ";网络操作用户名" + new String(strACSInfo.sNetUser).trim();
                    logger.info(sAlarmType);
                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    logger.info("ip" + sIP[0]);
                    newRow[2] = sIP[0];
                    try {
                        int dwMinor = strACSInfo.dwMinor;
                        if (dwMinor == 75 || dwMinor == 153 || dwMinor == 1) {
                            int employeeNo = strACSInfo.struAcsEventInfo.dwEmployeeNo;
                            if (employeeNo != 0) {
                                String employeeNoStr = String.valueOf(employeeNo);
                                String twoNo = employeeNoStr.substring(0, 2);
                                PersonnelEmployeeNoAndDepartment employeeNoAndDepartmentExample = new PersonnelEmployeeNoAndDepartment();
                                employeeNoAndDepartmentExample.setEmployeeNo(twoNo);
                                Example<PersonnelEmployeeNoAndDepartment> example = Example.of(employeeNoAndDepartmentExample);
                                List<PersonnelEmployeeNoAndDepartment> list = departmentRepository.findAll(example);
                                if (list != null && list.size() > 0) {
                                    PersonnelEmployeeNoAndDepartment employeeNoAndDepartment = list.get(0);
                                    String department = employeeNoAndDepartment.getDepartment();
                                    String cardNo = new String(strACSInfo.struAcsEventInfo.byCardNo).trim();
                                    PersonnelAccessInfoEntity personnelAccessInfoEntity = new PersonnelAccessInfoEntity();
                                    personnelAccessInfoEntity.setCardNo(cardNo);
                                    String ip = sIP[0];
                                    FaceDeviceEntity entity = new FaceDeviceEntity();
                                    entity.setIp(ip);
                                    Example<FaceDeviceEntity> faceDeviceEntityExample = Example.of(entity);
                                    List<FaceDeviceEntity> deviceEntityList = faceDeviceRepository.findAll(faceDeviceEntityExample);
                                    if (deviceEntityList != null && deviceEntityList.size() > 0) {
                                        FaceDeviceEntity deviceEntity = deviceEntityList.get(0);
                                        personnelAccessInfoEntity.setDeviceIp(ip);
                                        personnelAccessInfoEntity.setDirection(deviceEntity.getDirection());
                                        personnelAccessInfoEntity.setEmployeeNo(employeeNoStr);
                                        personnelAccessInfoEntity.setDepartment(department);
                                        Long time = DatetimeUtils.currentTimestamp();
                                        personnelAccessInfoEntity.setTime(time);
                                        accessInfoRepository.save(personnelAccessInfoEntity);
                                        PersonnelInfoEntity personnelInfoEntity = new PersonnelInfoEntity();
                                        personnelInfoEntity.setEmployeeNo(employeeNoStr);
                                        Example<PersonnelInfoEntity> personnelInfoEntityExample = Example.of(personnelInfoEntity);
                                        List<PersonnelInfoEntity> infoEntityList = personnelRepository.findAll(personnelInfoEntityExample);
                                        if (infoEntityList != null && infoEntityList.size() > 0) {
                                            personnelInfoEntity = infoEntityList.get(0);
                                        }
                                        personnelInfoEntity.setAccessTime(time);
                                        personnelInfoEntity.setCardNo(cardNo);
                                        personnelInfoEntity.setEmployeeNo(employeeNoStr);
                                        personnelInfoEntity.setDirection(deviceEntity.getDirection());
                                        personnelInfoEntity.setDepartment(department);
                                        personnelRepository.save(personnelInfoEntity);

                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    newRow[0] = dateFormat.format(today);
                    //报警类型
                    newRow[1] = sAlarmType;
                    //报警设备IP地址
                    sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
                    newRow[2] = sIP[0];
                    break;
            }
        } catch (Exception e) {

        }
    }
}
