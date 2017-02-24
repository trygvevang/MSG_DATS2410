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
    ListView<User> lwUsers;
    @FXML
    TextArea taInfo;

    private ServerConnection serverConnection;
    private ArrayList<User> users;
    private ObservableList<User> oUsers;

    public ServerController()
    {
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
        users = serverConnection.getUser();
        oUsers = FXCollections.observableArrayList(users);
        try{
        lwUsers.setItems(oUsers);
        } catch (Exception e){
            //
        }
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
        try
        {
            lwUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>()
            {
                @Override
                public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue)
                {
                    users = serverConnection.getUser();
                    int i = lwUsers.getSelectionModel().getSelectedIndex();
                    User temp = users.get(i);
                    taInfo.setText("Username: " + temp.getName() + "\n" + "Password: " + temp.getPassword() + "\n" + "Port: " + temp.getPort() + "\n" + "Ipadress: " + temp.getHostname() + "\n" +

                            "Status: " + temp.getStatusString());
                }
            });
        } catch(Exception e){
            //
        }
    }

    @Override
    public void updateUserListServer()
    {
        updateList();
    }
}
