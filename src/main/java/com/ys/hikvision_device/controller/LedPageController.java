package com.ys.hikvision_device.controller;

import com.google.common.base.Strings;
import com.ys.hikvision_device.Entity.LedPageEntity;
import com.ys.hikvision_device.dao.LedPageRepository;
import com.ys.hikvision_device.service.LedControllerService;
import com.ys.hikvision_device.view.AddLedPageView;
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
public class LedPageController extends AbstractJavaFxApplicationSupport implements Initializable {
    public Button savePage;
    public Button deletePage;
    public Button editPage;
    public Button selectPage;
    public TableView pageTable;
    public TableColumn pageCell;
    public TableColumn pageName;
    public TableColumn pageWidth;
    public TableColumn pageHeight;
    public TextField addPageName;
    public TextField addPageHeight;
    public TextField addPageWidth;
    public Button saveLedPage;
    public Button cancelLedPage;
    public TextField addPageX;
    public TextField addPageY;

    @Autowired
    private LedPageRepository ledPageRepository;

    @Autowired
    private AddLedPageView addLedPageView;

    private LedPageEntity editEntity;

    @Autowired
    private LedControllerService ledControllerService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            selectPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLedPage(ActionEvent actionEvent) {
        AbstractFxmlView view = addLedPageView;
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
        newStage.setTitle("新增节目");
        newStage.show();
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                addPageName.setText(null);
                addPageHeight.setText(null);
                addPageWidth.setText(null);
                addPageX.setText(null);
                addPageY.setText(null);
            }
        });
    }

    public void deletePage(ActionEvent actionEvent) {
        TableView<LedPageEntity> sw = pageTable;
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            ObservableList<LedPageEntity> list = sw.getSelectionModel().getSelectedItems();
            if (list != null && list.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("提示");
                alert.setHeaderText("确定删除？");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    for (LedPageEntity entity : list) {
                        ledPageRepository.deleteById(entity.getId());
                    }
                    selectPage();
                }
            }
        }
    }

    public void editPage(ActionEvent actionEvent) throws Exception {
        TableView<LedPageEntity> sw = pageTable;
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            ObservableList<LedPageEntity> list = sw.getSelectionModel().getSelectedItems();
            if (list != null && list.size() > 0) {
                if (list.size() > 1) {
                    tips("请选择一条数据");
                } else {
                    LedPageEntity entity = list.get(0);
                    editEntity = entity;
                    AbstractFxmlView view = addLedPageView;
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
                    newStage.setTitle("修改节目");
                    newStage.show();
                    if (entity != null) {
                        addPageName.setText(entity.getName());
                        addPageHeight.setText(entity.getHeight().toString());
                        addPageWidth.setText(entity.getWidth().toString());
                        addPageX.setText(entity.getX().toString());
                        addPageY.setText(entity.getY().toString());
                    }
                }
            }
        }
    }

    public void selectPage() {
        TableView<LedPageEntity> sw = pageTable;
        List<LedPageEntity> list = ledPageRepository.findAll();
        if (sw != null && sw.getSelectionModel() != null && sw.getSelectionModel().getSelectedItems() != null) {
            if (list != null && list.size() >= 0) {
                ObservableList<LedPageEntity> data = FXCollections.observableArrayList();
                pageName.setCellValueFactory(new PropertyValueFactory<LedPageEntity, String>("name"));
                pageName.setSortable(false);
                pageWidth.setCellValueFactory(new PropertyValueFactory<LedPageEntity, String>("width"));
                pageWidth.setSortable(false);
                pageHeight.setCellValueFactory(new PropertyValueFactory<LedPageEntity, String>("height"));
                pageHeight.setSortable(false);
                pageCell.setCellFactory((col) -> {
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
                pageCell.setSortable(false);
                data.addAll(list);
                pageTable.setItems(data);
                pageTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }
        }
    }

    public void tips(String message) throws Exception {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("提示");
        alert.headerTextProperty().set(message);
        alert.showAndWait();
    }

    public void saveLedPage(ActionEvent actionEvent) throws Exception {
        LedPageEntity ledPageEntity = new LedPageEntity();
        if (editEntity != null && editEntity.getId() != null) {
            ledPageEntity = editEntity;
        }
        String nameStr = addPageName.getText();
        String heightStr = addPageHeight.getText();
        String widthStr = addPageWidth.getText();
        String xStr = addPageX.getText();
        String yStr = addPageY.getText();
        if (Strings.isNullOrEmpty(nameStr)) {
            tips("名称不能为空");
            return;
        }
        if (Strings.isNullOrEmpty(heightStr)) {
            tips("高不能为空");
            return;
        }
        if (Strings.isNullOrEmpty(widthStr)) {
            tips("宽不能为空");
            return;
        }
        if (Strings.isNullOrEmpty(xStr)) {
            tips("x不能为空");
            return;
        }
        if (Strings.isNullOrEmpty(yStr)) {
            tips("y不能为空");
            return;
        }
        ledPageEntity.setName(nameStr);
        ledPageEntity.setX(Integer.valueOf(xStr));
        ledPageEntity.setY(Integer.valueOf(yStr));
        ledPageEntity.setHeight(Integer.valueOf(heightStr));
        ledPageEntity.setWidth(Integer.valueOf(widthStr));
        LedPageEntity entity = ledPageRepository.save(ledPageEntity);
        editEntity = null;
        if (entity != null) {
            Stage stage = (Stage) cancelLedPage.getScene().getWindow();
            ledControllerService.SendDynamicPersonnel(false, false);
            addPageName.setText(null);
            addPageHeight.setText(null);
            addPageWidth.setText(null);
            addPageX.setText(null);
            addPageY.setText(null);
            editEntity = null;
            stage.close();
            selectPage();
        }
    }

    public void cancelLedPage(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelLedPage.getScene().getWindow();
        addPageName.setText(null);
        addPageHeight.setText(null);
        addPageWidth.setText(null);
        addPageX.setText(null);
        addPageY.setText(null);
        editEntity = null;
        stage.close();
        selectPage();
    }
}
