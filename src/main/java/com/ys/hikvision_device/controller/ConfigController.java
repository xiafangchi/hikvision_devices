package com.ys.hikvision_device.controller;

import com.ys.hikvision_device.Entity.ConfigEntity;
import com.ys.hikvision_device.dao.ConfigRepository;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@FXMLController
@RestController
public class ConfigController extends AbstractJavaFxApplicationSupport implements Initializable {
    public Button saveConfig;
    public Button cancelSysConfig;
    public TextField ledIp;
    public TextField ledPort;
    public TextField refreshTime;

    @Autowired
    private ConfigRepository configRepository;
    private ConfigEntity entity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectConfig();
    }

    public void selectConfig() {
        List<ConfigEntity> list = configRepository.findAll();
        if (list != null && list.size() > 0) {
            entity = list.get(0);
            ledIp.setText(entity.getLedIp());
            ledPort.setText(entity.getLedPort());
            refreshTime.setText(entity.getRefreshTime());
        }
    }

    public void saveConfig(ActionEvent actionEvent) {
        ConfigEntity configEntity = new ConfigEntity();
        if (entity != null) {
            configEntity = entity;
        }
        String ledIpStr = ledIp.getText();
        configEntity.setLedIp(ledIpStr);
        String ledPortStr = ledPort.getText();
        configEntity.setLedPort(ledPortStr);
        String refreshTimeStr = refreshTime.getText();
        configEntity.setRefreshTime(refreshTimeStr);
        ConfigEntity newEntity = configRepository.save(configEntity);
        if (newEntity != null) {
            entity = newEntity;
            Stage stage = (Stage) cancelSysConfig.getScene().getWindow();
            stage.close();
        }
    }

    public void camcelConfig(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelSysConfig.getScene().getWindow();
        ledIp.setText(entity.getLedIp());
        ledPort.setText(entity.getLedPort());
        refreshTime.setText(entity.getRefreshTime());
        stage.close();
    }

}
