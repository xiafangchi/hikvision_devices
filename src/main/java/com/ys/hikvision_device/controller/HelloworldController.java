package com.ys.hikvision_device.controller;

import com.google.common.base.Strings;
import com.ys.hikvision_device.Entity.ConfigEntity;
import com.ys.hikvision_device.Entity.PersonnelAccessInfoEntity;
import com.ys.hikvision_device.dao.ConfigRepository;
import com.ys.hikvision_device.doo.PersonnelInOutRecordDo;
import com.ys.hikvision_device.doo.PersonnelInOutReportDo;
import com.ys.hikvision_device.service.LedControllerService;
import com.ys.hikvision_device.service.LedReportService;
import com.ys.hikvision_device.service.PersonnelAccessInfoService;
import com.ys.hikvision_device.view.*;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FXMLController
public class HelloworldController extends AbstractJavaFxApplicationSupport implements Initializable {

    private Logger log = LoggerFactory.getLogger(HelloworldController.class);


    public TableView carInOutDetailTable;
    public TableColumn carNumber;
    public TableColumn carNo;
    public TableColumn carInOut;
    public TableColumn carInOutTime;
    public TableView personnelInOutDetailTable;
    public TableColumn personnelNo;
    public TableColumn personnelInOut;
    public TableColumn personnelTime;
    public Label carPage;
    public Label carTotalPage;
    public TableView personnelInOutTable;
    public TableColumn personnelType;
    public TableColumn personnelTableInNum;
    public TableView carInOutTable;
    public TableColumn carType;
    public TableColumn carTypeInNum;
    public Label carTotalNum;
    public Label carInNum;
    public Label personnelTotalNum;
    public Label personnelInNum;
    public Label personnelPage;
    public Label personnelTotalPage;
    public Label titleTips;
    public TableColumn employeeNo;
    public TableColumn department;
    public Button personnelLast;
    public Button personnelNext;

    public Integer thisPersonnelPageNum;
    public Integer totalPersonnelPageNum;

    @Autowired
    FaceDeviceView faceDeviceView;

    @Autowired
    PersonnelEmployeeNoAndDepartmentView personnelEmployeeNoAndDepartmentView;

    @Autowired
    ConfigView configView;

    @Autowired
    LedPageView ledPageView;

    @Autowired
    LedDataView ledDataView;

    @Autowired
    LedReportService ledReportService;

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    PersonnelAccessInfoService personnelAccessInfoService;

    @Autowired
    LedControllerService ledControllerService;

    public void initialize(URL location, ResourceBundle resources) {

        Integer delay = 60;
        List<ConfigEntity> list = configRepository.findAll();
        if (list != null && list.size() > 0) {
            ConfigEntity entity = list.get(0);
            try {
                String refreshTime = entity.getRefreshTime();
                if (!Strings.isNullOrEmpty(refreshTime)) {
                    Integer refreshTimeInt = Integer.parseInt(refreshTime);
                    if (refreshTimeInt >= 0) {
                        delay = refreshTimeInt;
                    }
                }
            } catch (Exception e) {

            }
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //人员记录报表更新
                        selectPersonnelTotal();
                        selectPersonnelDetail(1);
                        ledControllerService.SendDynamicPersonnel(false, true);
                    }
                });
            }
        }, 1, delay, TimeUnit.SECONDS);
    }

    public void faceDevice(ActionEvent actionEvent) throws Exception {
        showView(faceDeviceView.getClass(), Modality.NONE);
    }

    public void personnelEmployeeNoAndDepartment(ActionEvent actionEvent) {
        showView(personnelEmployeeNoAndDepartmentView.getClass(), Modality.NONE);
    }

    public void config(ActionEvent actionEvent) {
        showView(configView.getClass(), Modality.NONE);
    }

    public void ledPage(ActionEvent actionEvent) {
        showView(ledPageView.getClass(), Modality.NONE);
    }

    public void ledData(ActionEvent actionEvent) {
        showView(ledDataView.getClass(), Modality.NONE);
    }

    public void selectPersonnelTotal() {
        PersonnelInOutReportDo reportDo = ledReportService.personnelReport();
        if (reportDo != null) {
            personnelTotalNum.setText(reportDo.getTotalNum().toString());
            personnelInNum.setText(reportDo.getInNum().toString());
            ObservableList<PersonnelInOutRecordDo> data = FXCollections.observableArrayList();
            personnelType.setCellValueFactory(new PropertyValueFactory<PersonnelInOutRecordDo, String>("department"));
            personnelType.setSortable(false);
            personnelTableInNum.setCellValueFactory(new PropertyValueFactory<PersonnelInOutRecordDo, String>("inNum"));
            personnelTableInNum.setSortable(false);
            data.addAll(reportDo.getRecordRespList());
            personnelInOutTable.setItems(data);
        }
    }

    public void selectPersonnelDetail(Integer pageNum) {
        Sort sort = Sort.by(Sort.Direction.DESC, "time");
        Pageable pageable = PageRequest.of(pageNum - 1, 10, sort);
        Page<PersonnelAccessInfoEntity> list = personnelAccessInfoService.findByLastname(pageable);
        List<PersonnelAccessInfoEntity> entityList = list.getContent();
        String page = "第" + pageNum + "页/共" + list.getTotalPages() + "页";
        thisPersonnelPageNum = pageNum;
        totalPersonnelPageNum = list.getTotalPages();
        personnelPage.setText(page);
        ObservableList<PersonnelAccessInfoEntity> data = FXCollections.observableArrayList();
        employeeNo.setCellValueFactory(new PropertyValueFactory<PersonnelAccessInfoEntity, String>("employeeNo"));
        employeeNo.setSortable(false);
        department.setCellValueFactory(new PropertyValueFactory<PersonnelAccessInfoEntity, String>("department"));
        department.setSortable(false);
        personnelInOut.setCellValueFactory(new PropertyValueFactory<PersonnelAccessInfoEntity, String>("direction"));
        personnelInOut.setSortable(false);
        personnelTime.setCellValueFactory(new PropertyValueFactory<PersonnelAccessInfoEntity, String>("timeStr"));
        personnelTime.setSortable(false);
        personnelNo.setCellFactory((col) -> {
            TableCell<PropertyValueFactory, String> cell = new TableCell<PropertyValueFactory, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        int rowIndex = this.getIndex() + 1 + (pageNum - 1) * 10;
                        this.setText(String.valueOf(rowIndex));
                    }
                }
            };
            return cell;
        });
        personnelNo.setSortable(false);
        data.addAll(entityList);

        personnelInOutDetailTable.setItems(data);
    }

    public void personnelLastPage(ActionEvent actionEvent) {
        if (thisPersonnelPageNum != 1) {
            thisPersonnelPageNum--;
        }
        selectPersonnelDetail(thisPersonnelPageNum);
    }

    public void personnelNextPage(ActionEvent actionEvent) {
        if (thisPersonnelPageNum != totalPersonnelPageNum) {
            thisPersonnelPageNum++;
        }
        selectPersonnelDetail(thisPersonnelPageNum);
    }
}
