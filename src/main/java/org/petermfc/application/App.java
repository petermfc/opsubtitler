package org.petermfc.application;

import com.airhacks.afterburner.injection.Injector;
import com.google.common.eventbus.EventBus;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.petermfc.model.Config;
import org.petermfc.service.SubtitlesService;
import org.petermfc.view.AppView;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Config config = new Config();
        EventBus eventBus = new EventBus();
        SubtitlesService srv = new SubtitlesService(config);
        Injector.setModelOrService(HostServices.class, getHostServices());
        Injector.setModelOrService(EventBus.class, eventBus);
        Injector.setModelOrService(Config.class, config);
        Injector.setModelOrService(SubtitlesService.class, srv);
        AppView appView = new AppView();
        Scene scene = new Scene(appView.getView(), 640, 480);
        stage.setTitle("opSubtitler");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
