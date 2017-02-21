package client.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Jesper Nylend on 21.02.2017.
 * s305070
 */
public class ClientMain extends Application
{
    public static void main(String... args)
    {
        launch(args);
    }

    @Override
    public void start(Stage ps) throws Exception
    {

        Parent root = FXMLLoader.load(getClass().getResource("/client/view/Client.fxml"));
        ps.setScene(new Scene(root));
        ps.setTitle("Message Application - Server");
        ps.setResizable(false);
        ps.setOnCloseRequest(e -> System.exit(0));
        ps.show();
    }
}
