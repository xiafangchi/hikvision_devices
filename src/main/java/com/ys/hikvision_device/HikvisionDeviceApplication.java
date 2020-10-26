package com.ys.hikvision_device;

import com.ys.hikvision_device.view.CustomLoadingView;
import com.ys.hikvision_device.view.HelloworldView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HikvisionDeviceApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(HikvisionDeviceApplication.class, HelloworldView.class, new CustomLoadingView(), args);
        //SpringApplication.run(HikvisionDeviceApplication.class, args);
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        stage.setResizable(false); /* 设置窗口不可改变 */
        stage.setTitle("二道门");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

    }

}
