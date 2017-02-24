package server.controller;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
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
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Multi client server.
 */
public class ServerConnection extends Task<Void>
{

    private ObservableMap<String, String> clientMessageMap = FXCollections.observableMap(new HashMap<>());
    private ArrayList<User> users;
    private int port;
    private HashMap<String, ConcurrentLinkedQueue<String>> userChatQueues;
    private ServerInterface serverInterface;


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

    private String getQueueMsg(String username)
    {
        ConcurrentLinkedQueue<String> userQueue = userChatQueues.get(username);
        if (userQueue != null && userQueue.size() != 0) {
            return userQueue.poll();
        }
        return null;
    }

    ArrayList<User> getUser(){
        return users;
    }

    ObservableMap<String, String> getClientMessageMap()
    {
        return clientMessageMap;
    }

    public void updateUserConnection(String username, String host, int port, int status)
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

    private String sendUserList()
    {
        return IOUser.getUserList(users);
    }

    private void registerUser(String input){
        IOUser.register(users, input);
    }

    private String loginUser(String input)
    {
        return IOUser.logIn(users, input);
    }

    public void start()
    {
        Thread th = new Thread(this);
        th.start();
    }



    private static class ClientService extends Service<Void>
    {

        Socket socket;
        private String cAddr;
        ServerConnection connection;

        public ClientService(Socket socket, ServerConnection connection)
        {
            this.socket = socket;
            cAddr = socket.getInetAddress().getHostAddress();
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
//                    sleep(1000);
                    switch (input.charAt(0))
                    {
                        case ((char) 182) : // ¶
                        {
                            //register user
                            updateMessage(input);
                            connection.registerUser(input);
                            username = input.split((char) 182 + "")[1];
                            connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 1);
                            out.println(connection.sendUserList());
                            break;
                        }
                        case ((char) 169) : // ©
                        {
                            //login
                            updateMessage(input);
                            String s = connection.loginUser(input);
                            if (s.equals("true")){
                                username = input.split((char) 169 + "")[1];
                                connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 1);
                                out.println(s + (char) 169 + connection.sendUserList());
                            }
                            break;
                        }
                        case ((char) 181) : //µ
                        {
//                            connection.requestChat(input.substring(1));
                            //chat request
                            //must handle showUserList()
                            out.println((char)181 + "Connected to user");
                            break;
                        }
                        case ((char) 209) : //Gets a normal message from this client
                        {   // 209 USERNAME 209 MESSAGE
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
