package dev.aisandbox.client.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>FXTools class.</p>
 *
 * @author gde
 * @version $Id: $Id
 */
@Component
public class FXTools {

    private static final Logger LOG = LoggerFactory.getLogger(FXTools.class.getName());

    @Autowired
    private ApplicationContext appContext;

    /**
     * <p>moveToScreen.</p>
     *
     * @param event a {@link javafx.event.ActionEvent} object.
     * @param fxml  a {@link java.lang.String} object.
     */
    public void moveToScreen(ActionEvent event, String fxml) {
        LOG.info("Selecting last screen");
        try {
            LOG.info( "Selecting {} screen", fxml);
            FXMLLoader loader = new FXMLLoader(FXTools.class.getResource(fxml));
            loader.setResources(ResourceBundle.getBundle("dev.aisandbox.client.fx.UI"));
            loader.setControllerFactory(appContext::getBean);
            Scene s1 = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) s1.getWindow();
            Scene s2 = new Scene(loader.load(), s1.getWidth(), s1.getHeight());
            stage.setScene(s2);
        } catch (IOException e) {
            LOG.error("Error switching Javafx scenes", e);
        }
    }

    /**
     * <p>generateEnumCombo.</p>
     *
     * @param eclass a T object.
     * @param <T> a T object.
     * @return a {@link javafx.scene.control.ComboBox} object.
     */
    public static <T extends Enum> ComboBox<T> generateEnumCombo(T eclass) {
        // create combu box
        ComboBox<T> comboBox = new ComboBox<>();
        // get a list of enums
        Enum[] constList = eclass.getClass().getEnumConstants();
        List<T> choiceList = new ArrayList<>();
        // convert them to names
        for (Enum c : constList) {
            choiceList.add((T) c);
        }
        comboBox.getItems().setAll(choiceList);
        return comboBox;
    }
}
