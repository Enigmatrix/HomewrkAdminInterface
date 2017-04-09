package app;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by enigm on 3/27/2017.
 */
public class LoginController {
    public AnchorPane root;
    public JFXTextField db;
    public JFXTextField user;
    public JFXPasswordField pass;
    private Stage stage;

    public void login(String db, String user, String pass){
        Pair<Parent, MainController> main = FXMLUtil.load("main.fxml");
        try {
            Connection conn = DBConnection.connect(db, user, pass);
            main.getValue().setConnection(conn);
            Scene newScene = new Scene(main.getKey(), 1200, 875);
            stage.setScene(newScene);
        } catch (SQLException e) {
            e.printStackTrace();
            JFXSnackbar sn = new JFXSnackbar(root);
            sn.fireEvent(new JFXSnackbar.SnackbarEvent(e.getMessage()));
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void loginAction(ActionEvent actionEvent) {
        login(db.getText(), user.getText(), pass.getText());
    }
}
