package server.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import server.model.IOUser;
import server.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class describes how the multi client server works, and handles the connection with every client.
 */
public class ServerConnection extends Task<Void>
{

    private ObservableMap<String, String> clientMessageMap = FXCollections.observableMap(new HashMap<>());
    private ArrayList<User> users;
    private int port;
    private HashMap<String, ConcurrentLinkedQueue<String>> userChatQueues;
    private ServerInterface serverInterface;


    /**
     * Constructor for ServerConnection.
     * @param port to run the server on
     * @param serverInterface to communicate with non-static methods in ServerController
     */
    public ServerConnection(int port, ServerInterface serverInterface)
    {
        userChatQueues = new HashMap<>();
        users = IOUser.read();
        this.serverInterface = serverInterface;
        this.port = port;

    }

    @Override
    protected Void call() throws Exception
    {
        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            while (true)
            {
                Socket sock = serverSocket.accept();

                ClientService cs = new ClientService(sock, this);
                String client = sock.getInetAddress().getHostAddress() + ":" + sock.getPort();

                cs.messageProperty().addListener((obs, oldMessage, newMessage) ->
                {
                    switch (newMessage)
                    {
                        case "connected" :
                            clientMessageMap.put(client, ""); break;
                        case "disconnected" :
                            clientMessageMap.remove(client); break;
                        default :
                            clientMessageMap.put(client, newMessage);
                    }
                    updateMessage(client + ": " + newMessage);
                });
                cs.start();
            }
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }

        return null;
    }

    /**
     * Adds a message to the recipient of this message.
     * @param username recipient of the message
     * @param msg message sent from a user to the recipient
     */
    private void addPersonalMessage(String username, String msg)
    {
        ConcurrentLinkedQueue<String> correctQueue;
        if (userChatQueues.get(username) == null){
            correctQueue = new ConcurrentLinkedQueue<>();
            userChatQueues.put(username, correctQueue);
        }else {
            correctQueue = userChatQueues.get(username);
        }
        correctQueue.add(msg);
    }

    /**
     * Checks if a user has a message waiting to be sent. If so the Server sends this to the appropriate user.
     * @param username of the user that asks for a check in his messagequeue
     * @return the message if there is any
     */
    private String getQueueMsg(String username)
    {
        ConcurrentLinkedQueue<String> userQueue = userChatQueues.get(username);
        if (userQueue != null && userQueue.size() != 0) {
            return userQueue.poll();
        }
        return null;
    }


    /*
     * getters for private fields
     */
    ArrayList<User> getUser(){
        return users;
    }

    ObservableMap<String, String> getClientMessageMap()
    {
        return clientMessageMap;
    }


    /**
     * Updates the status, address, port of the client that asks for this to be called.
     * @param username of the client
     * @param host of the client
     * @param port of the client
     * @param status set the status of the user
     */
    private void updateUserConnection(String username, String host, int port, int status)
    {
        host = host.substring(1);
        for (User user : users)
        {
            if (username.equals(user.getName())){
                user.setSocketInfo(host, port, status);
                break;
            }
        }
        serverInterface.updateUserListServer();
    }

    /**
     * Sends the updated user list to the client. The list has to be in the format of a String
     * so the PrintWriter is able to send the information.
     * @return the list in the format of a String
     */
    private String sendUserList()
    {
        return IOUser.getUserList(users);
    }

    private void registerUser(String input){
        IOUser.register(users, input);
    }

    /**
     * Sends the boolean value of the login credentials to the client. The list has to be in the format of a String
     * so the PrintWriter is able to send the information.
     * @return a boolean in the format of a String (e.g "true" | "false")
     */
    private String loginUser(String input)
    {
        return IOUser.logIn(users, input);
    }

    /**
     * Starts the thread.
     */
    public void start()
    {
        Thread th = new Thread(this);
        th.start();
    }


    /**
     * Static class used by the ServerConnection to instantiate a thread for a connection with a client
     */
    private static class ClientService extends Service<Void>
    {

        Socket socket;
        ServerConnection connection;

        /**
         * Constructor for this class.
         * @param socket used by the client connected to this thread
         * @param connection takes the outer class as a parameter
         */
        ClientService(Socket socket, ServerConnection connection)
        {
            this.socket = socket;
            this.connection = connection;
        }

        @Override
        protected Task<Void> createTask()
        {
            Task<Void> task = new Task<Void>()
            {
                @Override
                protected Void call() throws InterruptedException
                {
            updateMessage("connected");
            String username = "";

            try
            (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            )
            {
                // Task: here is connection with a client. As long as we are here, the client has a connection with the server
                String input;
                while (!(input = in.readLine()).equals("EXIT")) // TODO: Change condition to some type of logout
                {
                    switch (input.charAt(0))
                    {
                        case ((char) 182) : // ¶ - Register
                        {
                            updateMessage(input);
                            connection.registerUser(input);
                            username = input.split((char) 182 + "")[1];
                            connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 1);
                            out.println(connection.sendUserList());
                            break;
                        }
                        case ((char) 169) : // © - Login
                        {
                            updateMessage(input);
                            String s = connection.loginUser(input);
                            if (s.equals("true")){
                                username = input.split((char) 169 + "")[1];
                                connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 1);
                                out.println(s + (char) 169 + connection.sendUserList());
                            }
                            break;
                        }
                        case ((char) 209) : //Gets a normal message from this client
                        {
                            String[] info = input.split(String.valueOf((char) 209));
                            connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 2);
                            connection.addPersonalMessage(info[1], info[2]);
                            out.println(connection.sendUserList());
                            break;
                        }
                        case ((char) 210) : //Disconnect from chat
                        {
                            String[] info = input.substring(1).split(String.valueOf((char) 209));
                            connection.addPersonalMessage(info[1], (char) 231 + info[2]);
                            connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 1);
                            break;
                        }
                        case ((char) 222) : //Logging off
                            connection.updateUserConnection(username, String.valueOf(0), 0, 0);
                            break;
                        case ((char) 223) : //client asks for updated userlist
                            out.println(connection.sendUserList());
                            break;
                        case ((char) 224) :
                            out.println(connection.getQueueMsg(username));
                            break;
                        case ((char) 199) :
                            connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 1);
                            break;
                        default:

                            break;
                    }
                }
            }
            catch (IOException e)
            {
                connection.updateUserConnection(username, null, 0, 0);
                System.out.println("I/O Exception, with error: " + e.getMessage());
            }
            finally
            {
                updateMessage("disconnected"); // Indicate that client has disconnected
                connection.updateUserConnection(username, null, 0, 0);
            }

            return null;
                }

            };
            return task;
        }
    }
}
