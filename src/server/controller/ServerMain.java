package server.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.model.IOUser;
import server.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Starting and ending point of Server-application
 */
public class ServerMain extends Application
{
    /**
     * This method is both the starting- and ending point of this application.
     *
     * @param args Console/terminal arguments by user
     */
    public static void main(String... args)
    {
        launch(args);
    }

    @Override
    public void start(Stage ps) throws Exception
    {

        Parent root = FXMLLoader.load(getClass().getResource("/server/view/Server.fxml"));
        ps.setScene(new Scene(root));
        ps.setTitle("Message Application - Server");
        ps.setResizable(false);
        ps.setOnCloseRequest(e -> System.exit(0));
        ps.show();
    }
}
