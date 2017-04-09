package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Pair;

import java.util.function.Consumer;

/**
 * Created by enigm on 4/9/2017.
 */
public class AddEntityController {
    public GridPane root;
    public JFXButton submit;
    private ObservableList entity;
    private Consumer<ObservableList> callback;

    public void format(Pair<String, Integer>[] metadata){
        entity = FXCollections.observableArrayList();
        JFXTextField[] vals = new JFXTextField[metadata.length];

        for (int i = 0; i < metadata.length; i++) {
            Pair<String, Integer> col = metadata[i];
            RowConstraints r = new RowConstraints();
            r.setMinHeight(10);
            r.setPrefHeight(50);
            r.setVgrow(Priority.ALWAYS);
            root.getRowConstraints().add(r);

            Label name = new Label(col.getKey());
            root.add(name, 0, i);

            JFXTextField val = new JFXTextField();
            vals[i] = val;
            root.add(val, 1, i);
        }
        submit.setOnAction(e -> {
            for(JFXTextField val : vals){
                entity.add(val.getText());
            }
            callback.accept(entity);
            entity = FXCollections.observableArrayList();
        });
    }

    public void setOnSubmit(Consumer<ObservableList> callback){
        this.callback = callback;
    }
}
