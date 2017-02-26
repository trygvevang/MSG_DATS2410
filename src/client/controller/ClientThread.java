package client.controller;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * A thread of the connection between the client and the server. It handles
 * what the client sends to the server. The controller needs a instance of this class to be
 * able to connect to the server.
 * Extends the class Task which enables ClientThread to be instantiated as a Thread
 */
public class ClientThread extends Task<Void>
{
    private ClientInterface connection;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final String host;
    private final int port;

    /**
     * Constructor for the ClientThread.
     * @param host sets the IP of the host the client wants to connect to.
     * @param port sets the port of the host the client wants to connect to.
     * @param connection ClientInterface passed from the ClientController
     */
    ClientThread(String host, int port, ClientInterface connection)
    {
        this.host = host;
        this.port = port;
        this.connection = connection;
    }

    /**
     * Creates a Thread of this class and starts it
     */
    void start(){
        Thread th = new Thread(this);
        th.start();
    }


    /**
     * Called when this thread starts. This is where the connection with the server is.
     * Handles every task the client wants to do. Creates a socket with the host and port set in the constructor.
     * Also creates a BufferedReader and a PrintWriter which enables printing and reading to and from the server.
     * Uses the ClientInterface connection to get tasks from the ClientController.
     * While the message is not null the connection is valid. If the message is equal to null
     * the connection to the server ends and the client shuts down.
     * @return null when finished
     * @throws Exception if any problems with the server or thread occurs
     */
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
                        connection.setMessage("Finished registering");
                        break;

                    case ((char) 169) : //Log in
                        out.println(connection.getMessage());
                        String[] tokens = in.readLine().split((char) 169 + "");
                        recieved = tokens[0];
                        if (recieved.equals("true"))
                        {
                            connection.setValidLogin();
                            connection.setUserList(tokens[1]);
                        }
                        connection.setMessage("Finished logging in");
                        break;

                    case ((char) 209) : //Send message
                        out.println(connection.getMessage());
                        connection.setMessage("Message Sent!");
                        connection.setUserList(in.readLine());
                        break;

                    case ((char) 210) : //Disconnect from chat
                        out.println(connection.getMessage());
                        connection.setMessage("Disconnected from chat.");
                        break;

                    case ((char) 223) : // Request updated list
                        out.println((char)  223 + " Update List");
                        connection.setUserList(in.readLine());
                        connection.setMessage("Updated userlist");
                        break;

                    case ((char) 199): //Disconnect from chat
                        out.println(connection.getMessage());
                        connection.setMessage("Sent disconnect");
                        break;

                    default : //Requests latest message for this client
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
            sleep(1000);
            out.println("EXIT");
            socket.close();
            sleep(500);
            System.exit(0);
        }
        return null;
    }
}
