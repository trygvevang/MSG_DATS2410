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
import server.model.User;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
    private ArrayList<User> users;
    ObservableList<User> oUsers;
    private String message;

    public ServerController()
    {
        message = "message";
        serverConnection = new ServerConnection(6789, this);
        serverConnection.start();
        users = serverConnection.getUser();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        updateList();
    }

    private void updateShowedList()
    {
        oUsers = FXCollections.observableArrayList(serverConnection.getUser());
        lwUsers.setItems(oUsers);
        showUInfo();
    }

    private void updateList(){
        serverConnection.getClientMessageMap().addListener(new MapChangeListener<String, String>()
        {
            @Override
            public void onChanged(Change<? extends String, ? extends String> change)
            {
//                String client = serverConnection.getClientMessageMap()
                taLog.setText(serverConnection.getClientMessageMap().toString());
            }
        });
        updateShowedList();
    }


    private void showUInfo()
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

    @Override
    public void updateUserListServer()
    {
        updateList();
    }
}
