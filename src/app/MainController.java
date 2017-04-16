package app;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MainController {

    @FXML
    public StackPane root;

    @FXML
    public JFXTabPane tabs;
    @FXML
    public JFXSnackbar messageQueue;

    private Connection conn;

    @FXML
    public void initialize(){
        try {
            //for testing, if conn is null create a new one
            Connection connection = conn == null ? DBConnection.getConnection("homewrk", "root", "") : conn;

            ResultSet tableData = connection.createStatement().executeQuery("show tables;");
            System.out.printf("%s,%s",connection, tableData);
            Map<String, List> tableToTableView = new HashMap<>();
            while(tableData.next()){
                String tableName = tableData.getString(1);
                Tab nTab = new Tab(tableName);

                AnchorPane nRoot = new AnchorPane();
                nRoot.setMinHeight(0);
                nRoot.setMinWidth(0);

                TableView tbl = new TableView();
                tbl.setEditable(true);
                AnchorPane.setBottomAnchor(tbl, 50.0);
                AnchorPane.setTopAnchor(tbl, 50.0);
                AnchorPane.setRightAnchor(tbl, 50.0);
                AnchorPane.setLeftAnchor(tbl, 50.0);

                HBox btnRoot = new HBox();
                btnRoot.setSpacing(20);
                AnchorPane.setRightAnchor(btnRoot, 50.0);
                AnchorPane.setTopAnchor(btnRoot, 5.0);

                //Add
                JFXRippler btnR1 = new JFXRippler();
                btnR1.setPosition(JFXRippler.RipplerPos.BACK);
                btnR1.getStyleClass().add("icons-rippler");

                HBox icnRoot1 = new HBox();
                icnRoot1.setPadding(new Insets(10));

                FontAwesomeIcon icn1 = new FontAwesomeIcon();
                icn1.setFill(Paint.valueOf("#4CAF50"));
                icn1.setGlyphName("PLUS");
                icn1.setSize("1.5em");
                icn1.getStyleClass().add("icon");

                Label lbl1 = new Label("   Add");
                lbl1.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50;");


                //Remove
                JFXRippler btnR2 = new JFXRippler();
                btnR2.setPosition(JFXRippler.RipplerPos.BACK);
                btnR2.getStyleClass().add("icons-rippler");

                HBox icnRoot2 = new HBox();
                icnRoot2.setPadding(new Insets(10));

                FontAwesomeIcon icn2 = new FontAwesomeIcon();
                icn2.setFill(Paint.valueOf("#F44336"));
                icn2.setGlyphName("MINUS");
                icn2.setSize("1.5em");
                icn2.getStyleClass().add("icon");

                Label lbl2 = new Label("   Remove");
                lbl2.setStyle("-fx-font-weight: bold; -fx-text-fill: #F44336;");


                icnRoot1.getChildren().addAll(icn1, lbl1);
                btnR1.getChildren().add(icnRoot1);
                icnRoot2.getChildren().addAll(icn2, lbl2);
                btnR2.getChildren().add(icnRoot2);

                btnRoot.getChildren().addAll(btnR1, btnR2);
                nRoot.getChildren().addAll(tbl, btnRoot);
                nTab.setContent(nRoot);
                tabs.getTabs().add(nTab);

                tableToTableView.put(tableName, Arrays.asList(tbl, btnR1, btnR2));
            }

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
