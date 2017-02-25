package client.controller;

import client.model.ClientUser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

/**
 * Controls the client stage, and handel the interaction between frontend and backend.
 */
public class ClientController implements Initializable, ClientInterface
{
    @FXML
    private TableView<ClientUser> twUser;
    @FXML
    private TableColumn<ClientUser, String> twBrukerID;
    @FXML
    private TableColumn<ClientUser, String> twStatus;
    @FXML
    private TextArea taMsg;
    @FXML
    private TextArea taConv;

    private ClientThread ct;
    private String host;
    private int port;
    private String message;
    private boolean validLogin;
    private int counter;
    private ObservableList<ClientUser> userObservableList;
    private String yourUsername; // Your username
    private String sendMessageTo; // Username for the person you are chatting with


    /**
     * Instantiated when the client program is launched. Sets every value needed in the beginning.
     * Creates a popup where the user types in what IP the server is running on.
     * As a standard the popup shows a loopback IP (127.0.0.1). If user press cancel instead of assigning a host address,
     * the application will exit.
     * Creates another popup for the user to choose between signing in, signing up or close the program.
     * Handles the login and the registering in these popup
     * @param location of the GUI file path
     * @param resources not used in this program, inherited from Initializable
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        host = "";
        port = 6789;
        validLogin = false;
        counter = 0;
        userObservableList = FXCollections.observableArrayList();
        sendMessageTo = "";
        twBrukerID.setSortable(false);
        twStatus.setSortable(false);

        final Pattern IPPATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}" +
                                                                         "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

        // Ip selection
        TextInputDialog ipdialog = new TextInputDialog("127.0.0.1");
        ipdialog.setTitle("IP address of the server");
        ipdialog.setHeaderText("What IP address do the server have?");
        ipdialog.setContentText(null);

        Optional<String> ipResult = ipdialog.showAndWait();
        if (ipResult.isPresent() && IPPATTERN.matcher(ipResult.get()).matches())
        {
            host = ipResult.get();
        }
        else
            System.exit(0);


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Message Application - Client");
        alert.setHeaderText("Sign in or sign up");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne = new ButtonType("Sign in");
        ButtonType buttonTypeTwo = new ButtonType("Sign up");
        ButtonType buttonTypeCancel2 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel2);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne)
        {

            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Sign in");
            dialog.setHeaderText("Sign in:");

            // Set the button types.
            ButtonType loginButtonType = new ButtonType("Log in", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CLOSE);

            // Create the sendMessageTo and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");
            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);

            // Enable/Disable login button depending on whether a sendMessageTo was entered.
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

            // Do some validation (using the Java 8 lambda syntax).
            username.textProperty().addListener((observable, oldValue, newValue) ->
            {
                loginButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            // Request focus on the sendMessageTo field by default.
            Platform.runLater(() -> username.requestFocus());

            // Convert the result to a sendMessageTo-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton ->
            {
                if (dialogButton == loginButtonType)
                {
                    return new Pair<>(username.getText(), password.getText());
                } else if (dialogButton == ButtonType.CLOSE)
                {
                    System.exit(0);
                }
                return null;
            });

            Optional<Pair<String, String>> resultSubmit = dialog.showAndWait();

            resultSubmit.ifPresent(usernamePassword ->
            {
                String uname = usernamePassword.getKey();
                System.out.println("Username=" + uname + ", Password=" + usernamePassword
                        .getValue());
                setMessage((char) 169 + uname + (char) 169 + usernamePassword.getValue());
                estConnection(host, port);
                while (true)
                {
                    if (validLogin)
                    {
                        setYourUsername(uname);
                        return;
                    } else if (counter < 5)
                    { //counter for how long the while-loop should wait for an answer from server
                        try{
                            sleep(200);
                            counter++;
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                    else
                    {
                        Alert failedLogin = new Alert(Alert.AlertType.ERROR);
                        failedLogin.setTitle("Error");
                        failedLogin.setHeaderText("Failed login");
                        failedLogin.setContentText("Wrong username and password combination or\nuser already in use/online.\nPlease restart the client to try again.");
                        failedLogin.showAndWait();
                        System.exit(0);
                    }
                }
            });
        } else if (result.get() == buttonTypeTwo)
        {
            //New AlertBox for signing up
            // Create the custom dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Sign up");
            dialog.setHeaderText("Sign up:");

            // Set the button types.
            ButtonType registerButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CLOSE);

            // Create the sendMessageTo and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");
            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);

            // Enable/Disable login button depending on whether a sendMessageTo was entered.
            Node registerButton = dialog.getDialogPane().lookupButton(registerButtonType);
            registerButton.setDisable(true);

            // Do some validation (using the Java 8 lambda syntax).
            username.textProperty().addListener((observable, oldValue, newValue) ->
            {
                registerButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            // Request focus on the sendMessageTo field by default.
            Platform.runLater(() -> username.requestFocus());

            // Convert the result to a sendMessageTo-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton ->
            {
                if (dialogButton == registerButtonType)
                {
                    return new Pair<>(username.getText(), password.getText());
                } else if (dialogButton == ButtonType.CLOSE)
                {
                    System.out.println("Please restart the program, to try again.");
                    System.exit(0);
                }
                return null;
            });

            Optional<Pair<String, String>> resultSubmit = dialog.showAndWait();

            resultSubmit.ifPresent(usernamePassword ->
            {
                estConnection(host, port);
                System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword
                        .getValue());
                setYourUsername(usernamePassword.getKey());
                setMessage((char) 182 + getYourUsername() + (char) 182 + usernamePassword.getValue());
            });
        } else
        {
            //Exit the Client
            System.exit(0);
        }
    }

    /**
     * Instantiates a ClientThread and starts it. This tries to create a connection to the server.
     * If it cannot connect to the server a prompt will appear and inform the user.
     * @param host Host IP address to connect to
     * @param port Port to connect to
     */
    private void estConnection(String host, int port)
    {
        ct = new ClientThread(host, port, this);
        ct.start();
    }

    /**
     * Allows the user to send a message when in an active chat with another user.
     * If this is not the case, a prompt will appear and this tells the user what to do.
     */
    public void handleSend()
    {

        if (!getSendMessageTo().equals(""))
        {
            setMessage(getSendMessageTo() + (char) 209 + getYourUsername() + ": " + taMsg.getText());
            taConv.appendText(getYourUsername() + ": " + taMsg.getText() + "\n");
            taMsg.setText("");
        }
        else
        {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);

            dialog.setTitle("Not the right use");
            dialog.setHeaderText(null);
            dialog.setContentText("Please select a user with a status of \"online\" and \nconnect with that user before you can send a message.");

            dialog.showAndWait();
        }
    }

    /**
     * Handles when the user wants to log out from the client.
     * Checks if the user is in a chat and if so sleeps so the server can react to this.
     * When setMessage is set to null the client knows it is time to shut down the connection
     * to the server.
     */
    public void handleLogout()
    {
        if (!sendMessageTo.equals("")){
            handleDisconnectChat();
            try{
                sleep(400);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        setMessage(null);

    }

    /**
     * Called when pressed "File - Connect with" in the menubar in the GUI of the client.
     * Uses the character 223 to ask the server for an updated list of the users with their status.
     * After this, this thread sleeps for 200 milliseconds to give the server time to send back the updated list.
     * Updates the user list and takes out the user logged in from this client.
     * Creates a pop up so the user can choose who to connect with.
     * Only allows connection with a user with status "Online", not "Offline" or "Busy"
     */
    public void requestChat()
    {
        taConv.setText("");

        setMessage((char) 223 + "");
        try
        {
            sleep(200);
        } catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
        }
        String username;
        System.out.println();
        List<String> onlineUsers = new ArrayList<>();
        for (ClientUser user : userObservableList) {
            if (user.getStatus() == 1) onlineUsers.add(user.getName());
        }

        if (onlineUsers.size() > 0) {
            ChoiceDialog<String> cdialog = new ChoiceDialog<>(onlineUsers.get(0), onlineUsers);
            cdialog.setTitle("Users online");
            cdialog.setHeaderText(null);
            cdialog.setContentText("Choose user to connect");
            if (!twUser.getSelectionModel().isEmpty()) {
                ClientUser user = twUser.getSelectionModel().getSelectedItem();
                username = user.getName();
                if (user.getStatus() == 1) cdialog.setSelectedItem(username);
            }
            Optional<String> result = cdialog.showAndWait();
            if (result.isPresent()) {
                setSendMessageTo((char) 209 + result.get());
                System.out.println("Your choice: " + result.get());
                taConv.appendText("You connected with: " + result.get() + "\n");
            }
        }

    }

    /**
     * Requests an updated list of the users and their status by using
     * the character 223.
     */
    public void handleUpdateList()
    {
        setMessage((char) 223 + "");
    }

    /**
     * If in an active chat with another user, this method disconnects this user from the chat.
     */
    public void handleDisconnectChat()
    {
        if (!sendMessageTo.equals("")){
        setMessage((char) 210 + getSendMessageTo() + (char) 209 + getYourUsername());
        setSendMessageTo("");
        taConv.appendText("You have left the chat\n");
        }
    }

    /**
     * Used by ClientThread to speak with the server.
     * @return current message
     */
    @Override
    public String getMessage()
    {
        return message;
    }

    /**
     * Used to know what information to send or request from the server.
     * @param message sets the message to the parameter
     */
    @Override
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Sets validLogin to true
     */
    @Override
    public void setValidLogin()
    {
        validLogin = true;
    }

    /**
     * Creates an ObservableList of the users that are in the database of the server.
     * @param token the list of users and their status in String format.
     * @return the ObservableList used in the GUI
     */
    private ObservableList<ClientUser> createUserList(String token)
    {
        String[] usersString = token.split((char) 208 + "");

        userObservableList = FXCollections.observableArrayList();

        for (String anUsersString : usersString)
        {
            int delimiterIndex = anUsersString.indexOf(182);
            String name = anUsersString.substring(0, delimiterIndex);
            String status = anUsersString.substring(delimiterIndex + 1);
            if (!name.equals(yourUsername))
            {
                userObservableList.add(new ClientUser(name, status));
            }
        }
        return userObservableList;
    }

    /**
     * Commutes users along with their status to the GUI.
     * @param token User list in the format of a String
     */
    @Override
    public void setUserList(String token)
    {
        twUser.setItems(createUserList(token));
        twBrukerID.setCellValueFactory(new PropertyValueFactory("name"));
        twStatus.setCellValueFactory(new PropertyValueFactory("statusString"));
    }

    /**
     * Prints the received message from another user or the server to the TextArea.
     * @param s received message to print
     */
    @Override
    public void printMessage(String s)
    {
        if (s != null && !s.equals("null") && s.length() > 0)
        {
            System.out.println("Print message: " + s);
            if (s.charAt(0) == (char) 231)
            {
                taConv.appendText(s.substring(1) + " left the chat.\n");
                setMessage((char) 199 + "");
                setSendMessageTo("");
            }else{
            setSendMessageTo((char) 209 + s.split(":")[0]);
            taConv.appendText(s + "\n");
            }
        }
    }

    /*
     * Private getters and setters used only in this class
     */
    private void setSendMessageTo(String sendMessageTo)
    {
        this.sendMessageTo = sendMessageTo;
    }

    private String getSendMessageTo()
    {
        return sendMessageTo;
    }

    private void setYourUsername(String yourUsername)
    {
        this.yourUsername = yourUsername;
        handleUpdateList();
    }

    private String getYourUsername()
    {
        return yourUsername;
    }
}
