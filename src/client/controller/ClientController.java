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
import java.util.Optional;
import java.util.ResourceBundle;

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

    private ClientThread ct;
    private String host;
    private int port;
    private String message;
    private boolean validLogin;
    private String searcher;
    private int counter;
    private ObservableList<ClientUser> userObservableList;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        host = "127.0.0.1";
        port = 6789;
        message = "I'm so fucked";
        validLogin = false;
        searcher = "search";
        counter = 0;
        userObservableList = FXCollections.observableArrayList();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Message Application - Client");
        alert.setHeaderText("Sign in or sign up");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne = new ButtonType("Sign in");
        ButtonType buttonTypeTwo = new ButtonType("Sign up");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne)
        {
            /*
            connect
            if login credentials = true
                logge inn og sett status og lukk alertbox
             */
            //New AlertBox for signing in

            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Sign in");
            dialog.setHeaderText("Sign in:");

            // Set the button types.
            ButtonType loginButtonType = new ButtonType("Log in", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CLOSE);

            // Create the username and password labels and fields.
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

            // Enable/Disable login button depending on whether a username was entered.
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

            // Do some validation (using the Java 8 lambda syntax).
            username.textProperty().addListener((observable, oldValue, newValue) ->
            {
                loginButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            // Request focus on the username field by default.
            Platform.runLater(() -> username.requestFocus());

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton ->
            {
                if (dialogButton == loginButtonType)
                {
                    return new Pair<>(username.getText(), password.getText());
                }else if (dialogButton == ButtonType.CLOSE) {
                    System.exit(0);
                }
                return null;
            });

            Optional<Pair<String, String>> resultSubmit = dialog.showAndWait();

            resultSubmit.ifPresent(usernamePassword ->
            {
                System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword
                        .getValue());
                setMessage((char) 169 + usernamePassword.getKey() + (char) 169 + usernamePassword.getValue());
                estConnection(host, port);
                while (searcher != null){
                    if (validLogin){
                        return;
                    }else if (counter < 75_000){ //counter for how long the while-loop should wait for an answer from server
                        counter++;
                        System.out.println(counter);
                    }else {
                        Alert wrongConfirmation = new Alert(Alert.AlertType.ERROR);
                        wrongConfirmation.setTitle("Wrong login!");
                        wrongConfirmation.setHeaderText(null);
                        wrongConfirmation.setContentText("Submitted name and password combination does not exist!");

                        wrongConfirmation.showAndWait();
                        initialize(location, resources);
                        //System.exit(0);
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

            // Create the username and password labels and fields.
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

            // Enable/Disable login button depending on whether a username was entered.
            Node registerButton = dialog.getDialogPane().lookupButton(registerButtonType);
            registerButton.setDisable(true);

            // Do some validation (using the Java 8 lambda syntax).
            username.textProperty().addListener((observable, oldValue, newValue) ->
            {
                registerButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            // Request focus on the username field by default.
            Platform.runLater(() -> username.requestFocus());

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton ->
            {
                if (dialogButton == registerButtonType)
                {
                    return new Pair<>(username.getText(), password.getText());
                }else if (dialogButton == ButtonType.CLOSE) {
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
                setMessage((char) 182 + usernamePassword.getKey() + (char) 182 + usernamePassword.getValue());
            });
        } else
        {
            //Exit the Client
            System.exit(0);
        }
    }

    public void requestChat ()
    {
        String username = "";
        boolean online = false;
        try
        {
            ClientUser user = twUser.getSelectionModel().getSelectedItem();

            username = user.getName();
        } catch (Exception e)
        {
            System.out.println("exeption in requestChat");
        }
        TextInputDialog dialog = new TextInputDialog(username);

        dialog.setTitle("Connect with user");
        dialog.setHeaderText(null);
        dialog.setContentText("Who would you like to chat with: ");

        Optional<String> result = dialog.showAndWait();

        for (ClientUser user : userObservableList)
        {
            if (user.getName() == username)
            {
                if (user.getStatus() == 1)
                    online = true;
            }
        }

        if (result.isPresent() && online)
        {
            setMessage((char)181 + result.get());
        }
    }

    private void estConnection(String host, int port)
    {
        ct = new ClientThread(host, port, this);
        ct.start();
    }

    public void handleSend()
    {
        setMessage((char) 209 + taMsg.getText());
    }

    public void handleLogout()
    {
        setMessage(null);
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    @Override
    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public void setValidLogin(boolean value)
    {
        validLogin = value;
    }

    @Override
    public void setSearcher(String str)
    {
        searcher = str;
    }

    public ObservableList<ClientUser> createUserList(String token)
    {
        String[] usersString = token.split((char) 208 + "");

        userObservableList = FXCollections.observableArrayList();
        for (int i = 0; i < usersString.length; i++)
        {
            int delimiterIndex = usersString[i].indexOf(182);
            String name = usersString[i].substring(0,delimiterIndex);
            String status = usersString[i].substring(delimiterIndex+1);
            userObservableList.add(new ClientUser(name, status));
        }
        return userObservableList;
    }

    @Override
    public void setUserList(String token)
    {
        twUser.setItems(createUserList(token));
        twBrukerID.setCellValueFactory(new PropertyValueFactory("name"));
        twStatus.setCellValueFactory(new PropertyValueFactory("status"));
    }


}
