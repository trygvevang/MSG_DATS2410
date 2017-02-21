package server.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import server.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

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
                Socket connection = serverSocket.accept();

                ClientService cs = new ClientService(connection, this.connection);
                String client = connection.getInetAddress().getHostAddress() + ":" + connection.getPort();

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
                            System.out.println("infinite loop");
                            switch (input.charAt(0))
                            {
                                case ((char) 182) : // ¶
                                {
                                    //register user
                                    updateMessage(input);
                                    System.out.println("Registrerer ny bruker: " + input);
//                                    out.println(connection.updateUsers(input));
                                    break;
                                }
                                case ((char) 169) : // ©
                                {
                                    //login
                                    updateMessage(input);
                                    out.println(connection.loginUser(input));
                                    break;
                                }
                                case ((char) 181) : //µ
                                {
                                    //chat request
                                    System.out.print(in.readLine());
                                }
                                case ((char) 209) :
                                {
                                    updateMessage(input);
//                                    connection.updateUserConnection(Integer.parseInt(input.split((char) 209 + "")[1]), socket.getInetAddress().toString(), socket.getPort());
                                    System.out.println(input);
                                }
                                case ((char) 210) :
                                    updateMessage(input);
                                    break;
                                default:
                                    updateMessage(input);
                                    out.println("Not implemented yet!");
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        System.out.println("I/O Exception, with error: " + e.getMessage());
                    }
                    finally
                    {
                        updateMessage("disconnected"); // Indicate that client has disconnected
                    }

                    return null;
                }

            };
            return task;
        }
    }
}