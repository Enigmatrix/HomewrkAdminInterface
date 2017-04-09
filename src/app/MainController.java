package app;

import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class MainController {

    @FXML
    public StackPane root;

    @FXML
    public JFXTabPane tabs;
    @FXML
    public JFXSnackbar messageQueue;

    @FXML
    public TableView userTable;
    @FXML
    public TableView moduleTable;
    @FXML
    public TableView slotTable;
    @FXML
    public TableView homeworkTable;
    @FXML
    public TableView lessonSwapTable;
    @FXML
    public TableView holidayTable;


    @FXML
    public JFXRippler userAdd;
    @FXML
    public JFXRippler moduleAdd;
    @FXML
    public JFXRippler slotAdd;
    @FXML
    public JFXRippler homeworkAdd;
    @FXML
    public JFXRippler lessonSwapAdd;
    @FXML
    public JFXRippler holidayAdd;
    
    @FXML
    public JFXRippler userRmv;
    @FXML
    public JFXRippler moduleRmv;
    @FXML
    public JFXRippler slotRmv;
    @FXML
    public JFXRippler homeworkRmv;
    @FXML
    public JFXRippler lessonSwapRmv;
    @FXML
    public JFXRippler holidayRmv;
    private Connection conn;

    @FXML
    public void initialize(){
        try {
            //for testing, if conn is null create a new one
            Connection connection = conn == null ? DBConnection.getConnection("homewrk", "root", "") : conn;

            Map<String, List> tableToTableView = new HashMap<>();
            tableToTableView.put("user", Arrays.asList(userTable, userAdd, userRmv));
            tableToTableView.put("module", Arrays.asList(moduleTable, moduleAdd, moduleRmv));
            tableToTableView.put("slot", Arrays.asList(slotTable, slotAdd, slotRmv));
            tableToTableView.put("homework", Arrays.asList(homeworkTable, homeworkAdd, homeworkRmv));
            tableToTableView.put("lesson_swap", Arrays.asList(lessonSwapTable, lessonSwapAdd, lessonSwapRmv));
            tableToTableView.put("holiday", Arrays.asList(holidayTable, holidayAdd, holidayRmv));

            Map<String, Table> tables = new HashMap<>();

            tableToTableView.forEach((tableName, dat) -> {
                Table table = new Table(tableName, connection);
                tables.put(tableName, table);
                table.registerMessageImpl(this::sendMessage);
                table.projectOntoTable((TableView) dat.get(0));
                ((TableView) dat.get(0)).getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


                //add
                ((JFXRippler)dat.get(1)).setOnMouseClicked(d -> {
                    Pair<Region, AddEntityController> addEntityContent = FXMLUtil.load("AddEntity.fxml");
                    addEntityContent.getValue().format(table.getMetaData());
                    JFXDialog dialog = new JFXDialog(root, addEntityContent.getKey(), JFXDialog.DialogTransition.TOP, true);
                    addEntityContent.getValue().setOnSubmit(newRow -> {
                        try {
                            System.out.println(newRow);
                            table.add(newRow);
                            dialog.close();
                            sendMessage("Add of " + tableName + " successful!");
                        }
                        catch (SQLException se){
                            sendMessage(se.getMessage());
                        }
                    });
                    dialog.show();
                });

                //rmv
                ((JFXRippler)dat.get(2)).setOnMouseClicked(d -> {
                    ObservableList selectedRows = ((TableView)dat.get(0)).getSelectionModel().getSelectedItems();
                    for(Object row: selectedRows){
                        try {
                            table.delete((ObservableList)row);
                            sendMessage("Successful delete of " + tableName);

                        } catch (SQLException e) {
                            e.printStackTrace();
                            sendMessage(e.getMessage());
                        }
                    }
                });
            });
            messageQueue.registerSnackbarContainer(root);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        messageQueue.fireEvent(new JFXSnackbar.SnackbarEvent(message));
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}
