package server.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import jdk.nashorn.internal.ir.Block;
import server.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

/**
 * Multi client server.
 */
public class ServerConnection extends Task<Void>
{

    private ObservableMap<String, String> clientMessageMap = FXCollections.observableMap(new HashMap<>());
    private List<User> users;
    private int port;
    private ServerInterface connection;

    public ServerConnection(int port, ServerInterface connection)
    {
        this.connection = connection;
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

                ClientService cs = new ClientService(sock, this.connection);
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

    public void start()
    {
        Thread th = new Thread(this);
        th.start();
    }

    public ObservableMap<String, String> getClientMessageMap()
    {
        return clientMessageMap;
    }

    private static class ClientService extends Service<Void>
    {

        Socket socket;
        private String cAddr;
        private ServerInterface connection;

        public ClientService(Socket socket, ServerInterface connection)
        {
            this.connection = connection;
            this.socket = socket;
            cAddr = socket.getInetAddress().getHostAddress();
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
                            System.out.println("Registrerer ny bruker: " + input);
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
                            connection.requestChat(input.substring(1));
                            //chat request
                            //must handle showUserList()
                            System.out.println("Connect: " + input);
                            out.println((char)181 + "Connected to user");
                            break;
                            //System.out.print(in.readLine());
                        }
                        case ((char) 209) : //Gets a normal message from this client
                        {   // 209 USERNAME 209 MESSAGE
                            String[] info = input.split(String.valueOf((char) 209));
                            connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 2);
                            connection.addPersonalMessage(info[1], info[2]);
                            out.println(connection.sendUserList());
                            break;
//                           connection.addPersonalMessage(input); //TODO: fix not to send to yourself
                        }
                        case ((char) 210) : //Disconnect from chat
                        {
                            String[] info = input.substring(1).split(String.valueOf((char) 209));
                            connection.addPersonalMessage(info[1], (char) 231 + info[2]);
                            connection.updateUserConnection(username, socket.getInetAddress().toString(), socket.getPort(), 1);
                            out.println("Disconnected from current chat.\n");
                            break;
                        }
                        case ((char) 222) : //Logging off
                            connection.updateUserConnection(username, String.valueOf(0), 0, 0);
                            break;
                        case ((char) 223) : //client asks for updated userlist
                            out.println(connection.sendUserList());
                            break;
                        case ((char) 224) :
                            System.out.println("Ber om nyeste melding");
                            out.println(connection.getQueueMsg(username));
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
