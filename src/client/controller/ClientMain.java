package client.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application
{
    public static void main(String... args)
    {
        launch(args);
    }

    @Override
    public void start(Stage ps) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/view/Client.fxml"));
        Parent root = loader.load();
        ClientController controller = loader.getController();
        ps.setScene(new Scene(root));
        ps.setTitle("Message Application - Server");
        ps.setResizable(false);
        ps.setOnCloseRequest(e -> controller.handleLogout());
        ps.show();
    }
}
