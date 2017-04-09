package app;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pair<Parent, LoginController> root = FXMLUtil.load("login.fxml");
        primaryStage.setTitle("Homewrk Admin Interface");
        root.getValue().setStage(primaryStage);
        Scene scene = new Scene(root.getKey(), 1200, 875);
        primaryStage.setScene(scene);
        scene.getStylesheets().add("app/app.css");

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
