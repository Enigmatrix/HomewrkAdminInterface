package app;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.util.Pair;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by enigm on 3/28/2017.
 */
public class Table {
    private final String name;
    private final Connection conn;
    private ObservableList<ObservableList> data;
    private Consumer<String> messageImpl;
    private Pair<String,Integer>[] metadata;
    private ArrayList<String> pks;
    private TableColumn[] cols;

    public Table(String tableName, Connection conn){
        this.name = tableName;
        this.conn = conn;
    }

    public void projectOntoTable(TableView table){
        this.data = FXCollections.observableArrayList();
        try {

            pks = new ArrayList<String>();
            ResultSet pkrs = conn.getMetaData().getPrimaryKeys(null, null, name);
            while (pkrs.next())
                pks.add(pkrs.getString("COLUMN_NAME"));

            ResultSet rs = this.conn.createStatement().executeQuery("select * from " + this.name);

            int colCount = rs.getMetaData().getColumnCount();
            cols = new TableColumn[colCount];
            ArrayList<String> colNames = new ArrayList<>(colCount);
            metadata = new Pair[colCount];

            for(int i=0 ; i<colCount; i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = cols[i]= new TableColumn(rs.getMetaData().getColumnName(i+1));
                colNames.add(rs.getMetaData().getColumnName(i+1));
                metadata[i] = new Pair<>(rs.getMetaData().getColumnName(i+1), rs.getMetaData().getColumnType(i+1));

                col.setCellValueFactory((param) -> {
                    Object val =
                            ((TableColumn.CellDataFeatures<ObservableList, String>) param).getValue().get(j);
                    return new SimpleStringProperty(val == null ? "NULL" : val.toString());
                });
                table.getColumns().add(col);
            }

            int rowCount = 0;
            while(rs.next()){
                rowCount++;
                //Iterate Row
                ObservableList row = FXCollections.observableArrayList();

                for(int i=1 ; i<=colCount; i++){
                    //Iterate Column
                    String type = rs.getMetaData().getColumnTypeName(i);
                    String colName = rs.getMetaData().getColumnName(i);
                    int h = rs.getMetaData().getColumnType(i);
                    row.add(rs.getString(i));

                    //only do this once
                    if(rowCount != 1) continue;
                    cols[i-1].setCellFactory(p -> new TextEditCell());
                    cols[i-1].setOnEditCommit(
                            new EventHandler<TableColumn.CellEditEvent<ObservableList, String>>() {
                                @Override
                                public void handle(TableColumn.CellEditEvent<ObservableList, String> t) {
                                    try {
                                        PreparedStatement update = conn.prepareStatement("update " + name + " set " + colName + " = ? where " + idsEquals(pks, cols, t.getRowValue()));
                                        update.setObject(1, t.getNewValue(), h);
                                        update.executeUpdate();
                                        int idx = colNames.indexOf(t.getTableColumn().getText());
                                        t.getRowValue().set(idx, t.getNewValue());
                                        messageImpl.accept("Edit of " + name + " successful!");
                                    } catch (SQLException e) {
                                        //show error message here
                                        //messageImpl.accept("Error! Invalid value for " + colName);
                                        messageImpl.accept(e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );


                }
                //System.out.println("Row [1] added "+row );
                data.add(row);
            }
            //FINALLY ADDED TO TableView
            table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String idsEquals(ArrayList<String> pks, TableColumn[] cols, ObservableList rowValue) {
        String result = "'1' = '1'";
        Map<String, Integer> map = new HashMap<>();
        for(int i = 0; i < cols.length; i++){
            TableColumn tc = cols[i];
            for(String k : pks){
                if(k.equals(tc.getText())){
                    map.put(k, i);
                }
            }
        }
        for(String k : pks){
                result += " and " + k + " = '" + rowValue.get(map.get(k)) + "'";
        }
        return result;
    }

    public Pair<String, Integer>[] getMetaData(){
        return metadata;
    }

    public void registerMessageImpl(Consumer<String> impl) {
        this.messageImpl = impl;
    }

    public void delete(ObservableList row) throws SQLException {
        conn.createStatement().execute("delete from " + name + " where " + idsEquals(pks, cols, row));
        data.remove(row);
    }
    public void add(ObservableList row) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("insert into " + name + " values (" + repeatSeperated("?", ",", row.size()) + ")");
        for(int i = 1; i <= row.size(); i++){
            Object o =row.get(i-1);
            stmt.setObject(i, o == null || "NULL".equals(o) || "null".equals(o) ? null : o);
        }
        stmt.execute();
        data.add(row);
    }
    private String repeatSeperated(String repeat, String seperate, int rpt){
        String res = repeat;
        for(int i = 1; i < rpt; i++){
            res += (seperate + repeat);
        }
        return res;
    }
}
