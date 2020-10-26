package com.ys.hikvision_device.controller;

import com.google.common.base.Strings;
import com.ys.hikvision_device.Entity.FaceDeviceEntity;
import com.ys.hikvision_device.dao.FaceDeviceRepository;
import com.ys.hikvision_device.service.HikvisionFaceUpload;
import com.ys.hikvision_device.view.AddFaceDeviceView;
import com.ys.hikvision_device.view.EditFaceDeviceView;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@FXMLController
@RestController
public class FaceDeviceController extends AbstractJavaFxApplicationSupport implements Initializable {
    @FXML
    public TableView faceDeviceTable;
    @FXML
    public TableColumn cellNo;
    @FXML
    public TableColumn name;
    @FXML
    public TableColumn ip;
    @FXML
    public Button select;
    @FXML
    public Button add;
    @FXML
    public Button edit;
    @FXML
    public Button delete;
    @FXML
    public TextField selectDeviceName;
    @FXML
    public TextField selectDeviceIp;
    public TableColumn direction;
    public TextField addName;
    public TextField addIp;
    public Button saveAddDevice;
    public Button cancelAddSave;
    public TextField addUserName;
    public PasswordField addPassword;
    public ChoiceBox addDirection;
    public TextField editName;
    public TextField editIp;
    public Button saveEditDevice;
    public Button cancelEditSave;
    public TextField editUserName;
    public PasswordField editPassword;
    public ChoiceBox editDirection;
    public TableColumn status;
    public Button deploy;

    private FaceDeviceEntity editEntity;

    @Autowired
    private FaceDeviceRepository faceDeviceRepository;
    @Autowired
    AddFaceDeviceView addFaceDeviceView;
    @Autowired
    EditFaceDeviceView editFaceDeviceView;

    @Autowired
    private HikvisionFaceUpload hikvisionFaceUpload;

    /**
     * 登录
     */
    @RequestMapping(value = "manager/login", method = RequestMethod.GET, consumes = "application/json")
    public Object managerRegister() {
        faceDeviceRepository.findAll();
        return "1111";
    }


    /**
     * 查询人脸识别设备信息
     *
     * @throws Exception
     */
    @FXML
    public void selectFaceDevice() throws Exception {
        FaceDeviceEntity faceDeviceEntity = new FaceDeviceEntity();
        if (selectDeviceName != null && (!Strings.isNullOrEmpty(selectDeviceName.getText()))) {
            faceDeviceEntity.setName(selectDeviceName.getText());
        }
        if (selectDeviceIp != null && !Strings.isNullOrEmpty(selectDeviceIp.getText())) {
            faceDeviceEntity.setIp(selectDeviceIp.getText());
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("ip", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<FaceDeviceEntity> example = Example.of(faceDeviceEntity, matcher);
        List<FaceDeviceEntity> list = faceDeviceRepository.findAll(example);
        if (list != null && list.size() >= 0) {
            ObservableList<FaceDeviceEntity> data = FXCollections.observableArrayList();
            name.setCellValueFactory(new PropertyValueFactory<FaceDeviceEntity, String>("name"));
            name.setSortable(false);
            ip.setCellValueFactory(new PropertyValueFactory<FaceDeviceEntity, String>("ip"));
            ip.setSortable(false);
            direction.setCellValueFactory(new PropertyValueFactory<FaceDeviceEntity, String>("direction"));
            direction.setSortable(false);
            status.setCellValueFactory(new PropertyValueFactory<FaceDeviceEntity, String>("status"));
            status.setSortable(false);
            cellNo.setCellFactory((col) -> {
                TableCell<FaceDeviceEntity, String> cell = new TableCell<FaceDeviceEntity, String>() {
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
            cellNo.setSortable(false);
            data.addAll(list);
            faceDeviceTable.setItems(data);
            faceDeviceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
    }

    /**
     * 保存人脸识别设备
     *
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    public void saveFaceDevice(ActionEvent actionEvent) throws Exception {
        AbstractFxmlView view = addFaceDeviceView;
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
        newStage.setTitle("新增设备");
        newStage.show();
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                addName.setText(null);
                addIp.setText(null);
                addUserName.setText(null);
                addDirection.setValue("进");
                addPassword.setText(null);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            selectFaceDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFaceDevice() throws Exception {
        TableView<FaceDeviceEntity> sw = faceDeviceTable;
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            ObservableList<FaceDeviceEntity> list = sw.getSelectionModel().getSelectedItems();
            if (list != null && list.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("提示");
                alert.setHeaderText("确定删除？");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    for (FaceDeviceEntity entity : list) {
                        faceDeviceRepository.deleteById(entity.getId());
                    }
                    selectFaceDevice();
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

    public void editFaceDevice(ActionEvent actionEvent) throws Exception {
        TableView<FaceDeviceEntity> sw = faceDeviceTable;
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            ObservableList<FaceDeviceEntity> list = sw.getSelectionModel().getSelectedItems();
            if (list != null && list.size() > 0) {
                if (list.size() > 1) {
                    tips("请选择一条数据");
                } else {
                    FaceDeviceEntity entity = list.get(0);
                    editEntity = entity;
                    AbstractFxmlView view = editFaceDeviceView;
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
                    newStage.setTitle("修改设备");
                    newStage.show();
                    if (entity != null) {
                        editName.setText(entity.getName());
                        editIp.setText(entity.getIp());
                        editUserName.setText(entity.getUserName());
                        editPassword.setText(entity.getPassword());
                        editDirection.setValue(entity.getDirection());
                    }
                }
            }
        }
    }

    public void saveAddDevice(ActionEvent actionEvent) throws Exception {
        FaceDeviceEntity faceDeviceEntity = new FaceDeviceEntity();
        String nameStr = addName.getText();
        if (Strings.isNullOrEmpty(nameStr)) {
            tips("设备名称不能为空");
            return;
        }
        String ipStr = addIp.getText();
        if (Strings.isNullOrEmpty(ipStr)) {
            tips("设备ip不能为空");
            return;
        }
        String userNameStr = addUserName.getText();
        if (Strings.isNullOrEmpty(userNameStr)) {
            tips("设备用户名不能为空");
            return;
        }
        String passwordStr = addPassword.getText();
        if (Strings.isNullOrEmpty(passwordStr)) {
            tips("设备密码不能为空");
            return;
        }
        String directionStr = addDirection.getValue().toString();
        faceDeviceEntity.setDirection(directionStr);
        faceDeviceEntity.setName(nameStr);
        faceDeviceEntity.setIp(ipStr);
        faceDeviceEntity.setUserName(userNameStr);
        faceDeviceEntity.setPassword(passwordStr);
        FaceDeviceEntity entity = faceDeviceRepository.save(faceDeviceEntity);
        if (entity != null) {
            Stage stage = (Stage) cancelAddSave.getScene().getWindow();
            addName.setText(null);
            addIp.setText(null);
            addUserName.setText(null);
            addDirection.setValue("进");
            addPassword.setText(null);
            stage.close();
            selectFaceDevice();
        }
    }

    public void cancelAddSave(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelAddSave.getScene().getWindow();
        addName.setText(null);
        addIp.setText(null);
        addUserName.setText(null);
        addDirection.setValue("进");
        addPassword.setText(null);
        stage.close();
    }

    public void saveEditDevice() throws Exception {
        FaceDeviceEntity faceDeviceEntity = editEntity;
        String nameStr = editName.getText();
        if (Strings.isNullOrEmpty(nameStr)) {
            tips("设备名称不能为空");
            return;
        }
        String ipStr = editIp.getText();
        if (Strings.isNullOrEmpty(ipStr)) {
            tips("设备ip不能为空");
            return;
        }
        String userNameStr = editUserName.getText();
        if (Strings.isNullOrEmpty(userNameStr)) {
            tips("设备用户名不能为空");
            return;
        }
        String passwordStr = editPassword.getText();
        if (Strings.isNullOrEmpty(passwordStr)) {
            tips("设备密码不能为空");
            return;
        }
        String directionStr = editDirection.getValue().toString();
        faceDeviceEntity.setDirection(directionStr);
        faceDeviceEntity.setName(nameStr);
        faceDeviceEntity.setIp(ipStr);
        faceDeviceEntity.setUserName(userNameStr);
        faceDeviceEntity.setPassword(passwordStr);
        FaceDeviceEntity entity = faceDeviceRepository.save(faceDeviceEntity);
        if (entity != null) {
            Stage stage = (Stage) cancelEditSave.getScene().getWindow();
            stage.close();
            selectFaceDevice();
        }
    }

    public void cancelEditSave(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelEditSave.getScene().getWindow();
        stage.close();
    }

    public void deploy(ActionEvent actionEvent) {
        hikvisionFaceUpload.upload();
    }
}
