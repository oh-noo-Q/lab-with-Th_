package controller.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class toolOption extends Component  implements Initializable
{
    public ListView toolOptionListMenu;
    public SplitPane toolOptionSplitPane;
    public AnchorPane toolOptionSplitPanLeftPane;
    public Button toolOptionOKButton;
    public Button toolOptionCancelButton;
    public Pane toolOptionRightPane;
    public toolOptionGeneral toolOptionGeneralControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
//        mnuFileClose.setGraphic(new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/menuFile/close.png"))));

        try
        {
            Parent newLoadedPane = null;

            newLoadedPane = FXMLLoader.load(getClass().getResource("/FXML/controls/toolOptionGeneral.fxml"));

            toolOptionRightPane.getChildren().clear();
            toolOptionRightPane.getChildren().add(newLoadedPane);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    protected void toolOptionGeneralTabButton_Clicked(ActionEvent event) throws Exception
    {
        try
        {
            Parent newLoadedPane = null;

            newLoadedPane = FXMLLoader.load(getClass().getResource("/FXML/controls/toolOptionGeneral.fxml"));

            toolOptionRightPane.getChildren().clear();
            toolOptionRightPane.getChildren().add(newLoadedPane);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @FXML
    protected void toolOptionAdvancedTabButton_Clicked(ActionEvent event) throws Exception
    {
        try
        {
            Parent newLoadedPane = null;

            newLoadedPane = FXMLLoader.load(getClass().getResource("/FXML/controls/toolOptionAdvanced.fxml"));

            toolOptionRightPane.getChildren().clear();
            toolOptionRightPane.getChildren().add(newLoadedPane);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    protected void toolOptionOKButton_Clicked(ActionEvent event) throws Exception
    {
        try {
            Stage stage = (Stage) toolOptionOKButton.getScene().getWindow();
            stage.close();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
        }
    }
    @FXML
    protected void toolOptionCancelButton_Clicked(ActionEvent event) throws Exception
    {
        try {
            Stage stage = (Stage) toolOptionCancelButton.getScene().getWindow();
            stage.close();
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
        }
    }
}
