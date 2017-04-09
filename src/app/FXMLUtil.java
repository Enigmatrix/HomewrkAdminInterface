package app;

import javafx.fxml.FXMLLoader;
import javafx.util.Pair;

import java.io.IOException;

/**
 * Created by enigm on 3/27/2017.
 */
public class FXMLUtil {
    public static <P,T> Pair<P, T> load(String file){
        FXMLLoader loader = new FXMLLoader(FXMLUtil.class.getResource(file));
        try {
            return new Pair<P, T>(loader.<P>load(), loader.<T>getController());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
