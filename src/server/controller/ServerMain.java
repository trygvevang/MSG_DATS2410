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

    List<User> users;

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
        //network();
    }

    /**
     * To test connection with client. Made for the first pair programming session, intended towards the part of
     * registering a user.
     * @throws IOException if a connection fail.
     */
    public void network() throws IOException
    {
        int port = 6789;

        System.out.println("About to try connecting");
        try
        (
            ServerSocket sersoc = new ServerSocket(port);
            Socket soc = sersoc.accept();

            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

        )
        {
            InetAddress clientAddr = soc.getInetAddress();
            int clientPort = soc.getPort();
            String cliInfo = in.readLine();

            String[] tokens = IOUser.parseLine(cliInfo, "h");

            users = IOUser.read("src/server/resources/users.txt");
            int uid = users.get(users.size() - 1).getUid();
            uid++;

            out.println(uid);

            users.add(new User(uid, tokens[0], tokens[1]));
            IOUser.write("src/server/resources/users.txt", users.get(users.size() - 1));
        }
        catch (IOException e)
        {
            System.err.println("Error");
            System.out.println(e.getMessage());
        }
    }

}
