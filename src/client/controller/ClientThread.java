package client.controller;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Jesper Nylend on 21.02.2017.
 * s305070
 */
public class ClientThread extends Task<Void>
{
    private ClientInterface connection;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    private String host;
    private int port;
    private Thread th;

    public ClientThread(String host, int port, ClientInterface connection)
    {
        this.host = host;
        this.port = port;
        this.connection = connection;
    }

    public void start(){
        th = new Thread(this);
        th.start();
    }


    @Override
    protected Void call() throws Exception
    {
        try
        {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String recieved;
            while (connection.getMessage() != null){
                th.sleep(200);
                System.out.println(connection.getMessage());
                switch (connection.getMessage().charAt(0)){
                    case ((char) 182) :
                        //do something
                        out.println(connection.getMessage());
                        break;
                    case ((char) 209) :
                        //do something
                        out.println(connection.getMessage());
                        break;
                    default :
                        System.out.println("default");
                        break;
                }
            }
        }catch (IOException e){
            System.err.println("I/O Error: " + e.getMessage());
        }
        return null;
    }
}