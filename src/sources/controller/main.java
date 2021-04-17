package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class main extends Application {

    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    private static final Style STYLE = Style.LIGHT;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("/FXML/main.fxml"));//"main.fxml"

        setStage(primaryStage);

        primaryStage.setTitle("VNU-UET Software QUality Assurance System for C/C++");

        Scene scene = new Scene(root, 1200, 700);
//@../../CSS/jmetro/light_theme.css
        //scene.getStylesheets().add(getClass().getResource("/CSS/jmetro/light_theme.css").toExternalForm());

        new JMetro(scene, STYLE);

        primaryStage.setScene(scene);

        primaryStage.show();

        primaryStage.setOnCloseRequest(t ->
        {
            Platform.exit();
            System.exit(0);
        });
    }

    public static Stage getStage()
    {
        return stage;
    }

    public static void setStage(Stage _stage)
    {
        stage = _stage;
    }
}
