package server.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import server.model.IOUser;
import server.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Controlling all the scenes for the server application.
 */
public class ServerController implements Initializable, ServerInterface
{
    @FXML
    TextArea taLog;
    @FXML
    ListView lwUsers;
    @FXML
    TextArea taInfo;

    ServerConnection serverConnection;
    private List<User> users;
    ObservableList<User> oUsers;
    private String message;
    private HashMap<String, ConcurrentLinkedQueue<String>> userChatQueues;

    public ServerController()
    {
        userChatQueues = new HashMap<>();
        message = "message";
        serverConnection = new ServerConnection(6789, this);
        serverConnection.start();

        try
        {
            users = IOUser.read("src/server/resources/users.txt");
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        serverConnection.getClientMessageMap().addListener(new MapChangeListener<String, String>()
        {
            @Override
            public void onChanged(Change<? extends String, ? extends String> change)
            {
//                String client = serverConnection.getClientMessageMap()
                taLog.setText(serverConnection.getClientMessageMap().toString());
            }
        });
        showUserList();
    }

    @Override
    public void showUserList()
    {
        oUsers = FXCollections.observableArrayList(users);
        lwUsers.setItems(oUsers);
        showUInfo();
    }

    @Override
    public void requestChat(String username)
    {

    }

    @Override
    public void setMessage(String msg)
    {
        this.message = msg;
    }

    @Override
    public String getMessage()
    {
        return message;
    }



    @Override
    public void addPersonalMessage(String username, String msg)
    {
        System.out.println(msg + " : addPersonalMessage");
        ConcurrentLinkedQueue<String> correctQueue;
        if (userChatQueues.get(username) == null){
            correctQueue = new ConcurrentLinkedQueue<>();
            userChatQueues.put(username, correctQueue);
        }else {
            correctQueue = userChatQueues.get(username);
        }
        correctQueue.add(msg);
    }



/*
    @Override
    public void addPersonalMessage(String msg)
    {
        System.out.println(msg + " : addPersonalMessage");

        String[] info = msg.split(String.valueOf(209));
        String username = info[1];
        ConcurrentLinkedQueue<String> correctQueue;
        if (userChatQueues.get(username) == null){
            correctQueue = new ConcurrentLinkedQueue<>();
            userChatQueues.put(username, correctQueue);
        }else {
            correctQueue = userChatQueues.get(username);
        }
        correctQueue.add(info[2]);
    }*/

    @Override
    public String getQueueMsg(String username)
    {
        ConcurrentLinkedQueue<String> userQueue = userChatQueues.get(username);
        if (userQueue != null && userQueue.size() != 0) {
            System.out.println("Pulls message: " + userQueue.peek());
            return userQueue.poll();
        }
        System.out.println("fant ingen melding");
        return null;
    }

    public void showUInfo()
    {
        lwUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>()
        {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue)
            {
                int i = lwUsers.getSelectionModel().getSelectedIndex();
                User temp = users.get(i);
                message = (char) 222 + "";
                taInfo.setText("Username: " + temp.getName() +"\n" + "Password: " + temp.getPassword() + "\n" + "Port: " + temp.getPort() + "\n" + "Ipadress: " + temp.getHostname() + "\n" +
                        "Status: " + temp.getStatusString());
            }
        });
    }

    /**
     * This is supposed to be called when the user has typed in something in the register user stage
     * and then pressed 'Register user'. It is called from the ServerConnection class
     *
     * @param input is the string that is sent from the client to the server
     * @return the User ID generated by the server
     */
    @Override
    public boolean registerUser(String input)
    {
        boolean tmp = IOUser.register(users, input);
        return tmp;
    }

    @Override
    public void updateUserConnection(String username, String host, int port, int status)
    {
        host = host.substring(1);
        System.out.println(username + " : " + host + " : " + port + " : " + status);
        for (User user : users)
        {
            if (username.equals(user.getName())){
                user.setSocketInfo(host, port, status);
                break;
            }
        }
        showUserList();
    }


    /**
     * This is supposed to be called when the user has typed in something in the login stage
     * and then pressed 'Login'. It is called from the ServerConnection class
     *
     * @param input is the string that is sent from the client to the server
     * @return either Accepted or Declined
     */
    @Override
    public String loginUser(String input)
    {
        return IOUser.logIn(users, input);
    }

    /**
     *
     * @return
     */
    @Override
    public String sendUserList()
    {

        return IOUser.getUserList(users);
    }

}
