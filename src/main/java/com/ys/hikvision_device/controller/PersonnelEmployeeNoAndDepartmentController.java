package com.ys.hikvision_device.controller;

import com.google.common.base.Strings;
import com.ys.hikvision_device.Entity.PersonnelEmployeeNoAndDepartment;
import com.ys.hikvision_device.dao.PersonnelEmployeeNoAndDepartmentRepository;
import com.ys.hikvision_device.view.AddPersonnelEmployeeNoAndDepartmentView;
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
public class PersonnelEmployeeNoAndDepartmentController extends AbstractJavaFxApplicationSupport implements Initializable {
    public Button selectDepartment;
    public Button addDepartment;
    public Button editDepartment;
    public Button deleteDepartment;
    public TableView departmentTable;
    public TableColumn departmentCell;
    public TableColumn employeeNo;
    public TableColumn departmentName;
    public TextField addDepartmentName;
    public Button cancelAdd;
    public TextField addEmployeeNo;

    private PersonnelEmployeeNoAndDepartment editEntity;

    @Autowired
    private PersonnelEmployeeNoAndDepartmentRepository repository;

    @Autowired
    private AddPersonnelEmployeeNoAndDepartmentView addPersonnelEmployeeNoAndDepartmentView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            selectDepartment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectDepartment() throws Exception {
        TableView<PersonnelEmployeeNoAndDepartment> sw = departmentTable;
        List<PersonnelEmployeeNoAndDepartment> list = repository.findAll();
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            if (list != null && list.size() >= 0) {
                ObservableList<PersonnelEmployeeNoAndDepartment> data = FXCollections.observableArrayList();
                employeeNo.setCellValueFactory(new PropertyValueFactory<PersonnelEmployeeNoAndDepartment, String>("employeeNo"));
                employeeNo.setSortable(false);
                departmentName.setCellValueFactory(new PropertyValueFactory<PersonnelEmployeeNoAndDepartment, String>("department"));
                departmentName.setSortable(false);
                departmentCell.setCellFactory((col) -> {
                    TableCell<PersonnelEmployeeNoAndDepartment, String> cell = new TableCell<PersonnelEmployeeNoAndDepartment, String>() {
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
                departmentCell.setSortable(false);
                data.addAll(list);
                departmentTable.setItems(data);
                departmentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }
        }
    }

    public void addDepartment(ActionEvent actionEvent) {
        AbstractFxmlView view = addPersonnelEmployeeNoAndDepartmentView;
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
        newStage.setTitle("新增部门");
        newStage.show();
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                addDepartmentName.setText(null);
                addEmployeeNo.setText(null);
            }
        });
    }

    public void editDepartment(ActionEvent actionEvent) throws Exception {
        TableView<PersonnelEmployeeNoAndDepartment> sw = departmentTable;
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            ObservableList<PersonnelEmployeeNoAndDepartment> list = sw.getSelectionModel().getSelectedItems();
            if (list != null && list.size() > 0) {
                if (list.size() > 1) {
                    tips("请选择一条数据");
                } else {
                    PersonnelEmployeeNoAndDepartment entity = list.get(0);
                    editEntity = entity;
                    AbstractFxmlView view = addPersonnelEmployeeNoAndDepartmentView;
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
                        addDepartmentName.setText(entity.getDepartment());
                        addEmployeeNo.setText(entity.getEmployeeNo());
                    }
                }
            }
        }
    }


    public void deleteDepartment(ActionEvent actionEvent) throws Exception {
        TableView<PersonnelEmployeeNoAndDepartment> sw = departmentTable;
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            ObservableList<PersonnelEmployeeNoAndDepartment> list = sw.getSelectionModel().getSelectedItems();
            if (list != null && list.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("提示");
                alert.setHeaderText("确定删除？");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    for (PersonnelEmployeeNoAndDepartment entity : list) {
                        repository.deleteById(entity.getId());
                    }
                    selectDepartment();
                }
            }
        }
    }

    public void saveDepartment(ActionEvent actionEvent) throws Exception {
        PersonnelEmployeeNoAndDepartment employeeNoAndDepartment = new PersonnelEmployeeNoAndDepartment();
        if (editEntity != null && editEntity.getId() != null) {
            employeeNoAndDepartment = editEntity;
        }
        String nameStr = addDepartmentName.getText();
        String employeeNoStr = addEmployeeNo.getText();
        if (Strings.isNullOrEmpty(employeeNoStr)) {
            tips("工号不能为空");
            return;
        }
        if (Strings.isNullOrEmpty(nameStr)) {
            tips("部门名称不能为空");
            return;
        }
        employeeNoAndDepartment.setDepartment(nameStr);
        employeeNoAndDepartment.setEmployeeNo(employeeNoStr);
        PersonnelEmployeeNoAndDepartment entity = repository.save(employeeNoAndDepartment);
        editEntity = null;
        if (entity != null) {
            Stage stage = (Stage) cancelAdd.getScene().getWindow();
            addDepartmentName.setText(null);
            addEmployeeNo.setText(null);
            editEntity = null;
            stage.close();
            selectDepartment();
        }
    }

    public void cancelAdd(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelAdd.getScene().getWindow();
        addDepartmentName.setText(null);
        addEmployeeNo.setText(null);
        editEntity = null;
        stage.close();
    }


    public void tips(String message) throws Exception {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("提示");
        alert.headerTextProperty().set(message);
        alert.showAndWait();
    }
}
