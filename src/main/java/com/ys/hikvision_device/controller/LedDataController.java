package com.ys.hikvision_device.controller;

import com.google.common.base.Strings;
import com.ys.hikvision_device.Entity.LedDataEntity;
import com.ys.hikvision_device.Entity.LedPageEntity;
import com.ys.hikvision_device.dao.LedDataRepository;
import com.ys.hikvision_device.dao.LedPageRepository;
import com.ys.hikvision_device.service.LedControllerService;
import com.ys.hikvision_device.view.AddLedDataView;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@FXMLController
public class LedDataController extends AbstractJavaFxApplicationSupport implements Initializable {
    public TableView ledDataTable;
    public TableColumn ledDataCell;
    public TableColumn ledDataName;
    public TableColumn ledPageName;
    public TableColumn ledPageTypeName;
    public Button selectLedData;
    public Button editLedData;
    public Button addLedData;
    public Button deleteLedData;
    public ChoiceBox addLedDataType;
    public ChoiceBox addLedDataPage;
    public TextField addLedDataFontSize;
    public TextField addLedDataStayTime;
    public TextArea addLedDataContent;
    public Button saveLedData;
    public Button cancelLedData;
    public TextField addLedDataName;
    public ChoiceBox addPositionName;
    public ChoiceBox addFontType;

    @Autowired
    private LedDataRepository ledDataRepository;

    @Autowired
    private AddLedDataView addLedDataView;

    @Autowired
    private LedPageRepository ledPageRepository;

    private LedDataEntity editEntity;

    @Autowired
    private LedControllerService ledControllerService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            selectLedData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectLedData() throws Exception {
        TableView<LedDataEntity> sw = ledDataTable;
        List<LedDataEntity> list = ledDataRepository.findAll();
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            if (list != null && list.size() >= 0) {
                ObservableList<LedDataEntity> data = FXCollections.observableArrayList();
                ledDataName.setCellValueFactory(new PropertyValueFactory<LedDataEntity, String>("name"));
                ledDataName.setSortable(false);
                ledPageName.setCellValueFactory(new PropertyValueFactory<LedDataEntity, String>("page"));
                ledPageName.setSortable(false);
                ledPageTypeName.setCellValueFactory(new PropertyValueFactory<LedDataEntity, String>("type"));
                ledPageTypeName.setSortable(false);
                ledDataCell.setCellFactory((col) -> {
                    TableCell<LedPageEntity, String> cell = new TableCell<LedPageEntity, String>() {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            this.setText(null);
                            this.setGraphic(null);
                            if (!empty) {
                                int rowIndex = this.getIndex() + 1;
                                this.setText(String.valueOf(rowIndex));
                            }
                        }
                    };
                    return cell;
                });
                ledDataCell.setSortable(false);
                data.addAll(list);
                ledDataTable.setItems(data);
                ledDataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }
        }
    }

    public void editLedData(ActionEvent actionEvent) throws Exception {
        TableView<LedDataEntity> sw = ledDataTable;
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            ObservableList<LedDataEntity> list = sw.getSelectionModel().getSelectedItems();
            if (list != null && list.size() > 0) {
                if (list.size() > 1) {
                    tips("请选择一条数据");
                } else {
                    LedDataEntity entity = list.get(0);
                    editEntity = entity;
                    AbstractFxmlView view = addLedDataView;
                    List<LedPageEntity> pageList = ledPageRepository.findAll();
                    Stage newStage = new Stage();
                    Scene newScene;
                    if (view.getView().getScene() != null) {
                        newScene = view.getView().getScene();
                    } else {
                        newScene = new Scene(view.getView());
                    }
                    newStage.setScene(newScene);
                    newStage.initModality(Modality.NONE);
                    newStage.initOwner(getStage());
                    newStage.setTitle("修改内容");
                    newStage.show();
                    if (pageList != null && pageList.size() > 0) {
                        for (LedPageEntity infoResp : pageList) {
                            addLedDataPage.getItems().add(infoResp.getName());
                        }
                    }
                    if (entity != null) {
                        addLedDataName.setText(entity.getName());
                        addLedDataPage.setValue(entity.getPage());
                        addLedDataType.setValue(entity.getType());
                        addLedDataFontSize.setText(entity.getFontSize());
                        addLedDataStayTime.setText(entity.getStayTime());
                        addPositionName.setValue(entity.getPosition());
                        addLedDataContent.setText(entity.getContent());
                        addFontType.setValue(entity.getFontType());
                    }
                }
            }
        }
    }

    public void addLedData(ActionEvent actionEvent) {
        AbstractFxmlView view = addLedDataView;
        Stage newStage = new Stage();
        Scene newScene;
        if (view.getView().getScene() != null) {
            newScene = view.getView().getScene();
        } else {
            newScene = new Scene(view.getView());
        }
        newStage.setScene(newScene);
        newStage.initModality(Modality.NONE);
        newStage.initOwner(getStage());
        newStage.setTitle("新增内容");
        newStage.show();
        List<LedPageEntity> pageList = ledPageRepository.findAll();
        if (pageList != null && pageList.size() > 0) {
            for (LedPageEntity infoResp : pageList) {
                addLedDataPage.getItems().add(infoResp.getName());
            }
        }
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                addLedDataName.setText(null);
                addLedDataPage.setValue(null);
                addLedDataType.setValue(null);
                addLedDataFontSize.setText(null);
                addLedDataStayTime.setText(null);
                addLedDataContent.setText(null);
                addFontType.setValue(null);
            }
        });
    }

    public void deleteLedData(ActionEvent actionEvent) throws Exception {
        TableView<LedDataEntity> sw = ledDataTable;
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            ObservableList<LedDataEntity> list = sw.getSelectionModel().getSelectedItems();
            if (list != null && list.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("提示");
                alert.setHeaderText("确定删除？");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    for (LedDataEntity entity : list) {
                        ledDataRepository.deleteById(entity.getId());
                    }
                    ledControllerService.SendDynamicPersonnel(true, false);
                    selectLedData();
                }
            }
        }
    }

    public void tips(String message) throws Exception {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("提示");
        alert.headerTextProperty().set(message);
        alert.showAndWait();
    }

    public void saveLedData(ActionEvent actionEvent) throws Exception {
        LedDataEntity ledDataEntity = new LedDataEntity();
        if (editEntity != null && editEntity.getId() != null) {
            ledDataEntity = editEntity;
        }
        String nameStr = addLedDataName.getText();
        String dataPageStr = addLedDataPage.getValue().toString();
        String dataTypeStr = addLedDataType.getValue().toString();
        String fontSizeStr = addLedDataFontSize.getText();
        String stayTimeStr = addLedDataStayTime.getText();
        String contentStr = addLedDataContent.getText();
        String positionNameStr = addPositionName.getValue().toString();
        String fontType = addFontType.getValue().toString();
        if (Strings.isNullOrEmpty(nameStr)) {
            tips("名称不能为空");
            return;
        }
        if (Strings.isNullOrEmpty(dataPageStr)) {
            tips("节目");
            return;
        }
        if (Strings.isNullOrEmpty(dataTypeStr)) {
            tips("类型");
            return;
        }
        if (Strings.isNullOrEmpty(fontSizeStr)) {
            tips("字体");
            return;
        }
        if (Strings.isNullOrEmpty(stayTimeStr)) {
            tips("停留时间");
            return;
        }
        ledDataEntity.setName(nameStr);
        ledDataEntity.setContent(contentStr);
        ledDataEntity.setFontSize(fontSizeStr);
        ledDataEntity.setPage(dataPageStr);
        ledDataEntity.setPosition(positionNameStr);
        ledDataEntity.setStayTime(stayTimeStr);
        ledDataEntity.setType(dataTypeStr);
        ledDataEntity.setFontType(fontType);
        LedDataEntity entity = ledDataRepository.save(ledDataEntity);
        editEntity = null;
        if (entity != null) {
            ledControllerService.SendDynamicPersonnel(false, false);
            Stage stage = (Stage) cancelLedData.getScene().getWindow();
            addLedDataName.setText(null);
            addLedDataPage.setValue(null);
            addLedDataType.setValue(null);
            addLedDataFontSize.setText(null);
            addLedDataStayTime.setText(null);
            addLedDataContent.setText(null);
            addFontType.setValue(null);
            stage.close();
            selectLedData();
        }
    }

    public void cancelLedData(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) cancelLedData.getScene().getWindow();
        addLedDataName.setText(null);
        addLedDataPage.setValue(null);
        addLedDataType.setValue(null);
        addLedDataFontSize.setText(null);
        addLedDataStayTime.setText(null);
        addLedDataContent.setText(null);
        addFontType.setValue(null);
        stage.close();
        selectLedData();
    }
}
