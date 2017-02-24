package client.controller;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class ClientThread extends Task<Void>
{
    private ClientInterface connection;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String host;
    private int port;

    ClientThread(String host, int port, ClientInterface connection)
    {
        this.host = host;
        this.port = port;
        this.connection = connection;
    }

    void start(){
        Thread th = new Thread(this);
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
                sleep(200);
                switch (connection.getMessage().charAt(0)){
                    case ((char) 182) :
                        //Register
                        out.println(connection.getMessage());
                        connection.setUserList(in.readLine());
                        connection.setMessage("Finnished registering");
                        break;

                    case ((char) 169) :
                        out.println(connection.getMessage());
                        String[] tokens = in.readLine().split((char) 169 + "");
                        recieved = tokens[0];
                        if (recieved.equals("true"))
                        {
                            connection.setValidLogin();
                            connection.setUserList(tokens[1]);
                        }
                        connection.setMessage("Finnished logging in");
                        break;

                    case ((char) 209) :
                        //Send message
                        System.out.println(connection.getMessage());
                        out.println(connection.getMessage());
                        connection.setMessage("Message Sent!");
                        connection.setUserList(in.readLine());
                        break;

                    case ((char) 210) :
                        out.println(connection.getMessage());
//                        connection.printServerMessage(in.readLine());
                        connection.setMessage("Disconnected from chat.");
                        break;

                    //set message til char 222 som ber om ny oppdatert ulist
                    case ((char) 223) :
                        out.println((char)  223 + " Update List");
                        connection.setUserList(in.readLine());
                        connection.setMessage("Updated userlist");
                        break;
                    case ((char) 199):
                        System.out.println("Client thread 199:    " + connection.getMessage());
                        out.println(connection.getMessage());
                        connection.setMessage("Sent disconnect");
                        break;
                    default :
                        System.out.println("Default here");
                        out.println((char) 224);
                        connection.printMessage(in.readLine());
                        break;
                }
            }
        }catch (IOException e){
            System.err.println("I/O Error: " + e.getMessage());
        }
        finally
        {
            out.println((char) 222 + "Logging out");
            System.out.println(System.currentTimeMillis());
            sleep(1000);
            System.out.println(System.currentTimeMillis());
            out.println("EXIT");
            socket.close();
            sleep(500);
            System.exit(0);
        }
        return null;
    }
}
