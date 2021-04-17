package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class mainController extends Component implements Initializable
{
    public MenuItem mnuFileClose;
    public Menu mnuFileOpen;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        mnuFileClose.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/menuFile/close.png"))));

        mnuFileOpen.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/menuFile/open.png"))));
    }

    @FXML
    protected void mnuFileClose_Clicked(ActionEvent event) throws Exception
    {
        Platform.exit();
        System.exit(0);
    }
    @FXML
    protected void mnuHelpAbout_Clicked(ActionEvent event) throws Exception
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/dialogs/helpAbout.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("About");

            Scene scene = new Scene(root1);
            new JMetro(scene, Style.LIGHT);

            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
        }
    }

    @FXML
    protected void mnuToolOption_Clicked(ActionEvent event) throws Exception
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/dialogs/toolOption.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Option");

            Scene scene = new Scene(root1);
            //scene.getStylesheets().add(getClass().getResource("/CSS/jmetro/light_theme.css").toExternalForm());


            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);

            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
        }
    }
}
