package client.controller;

import client.model.ClientUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Jesper Nylend on 21.02.2017.
 * s305070
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

    private ClientThread ct;
    private String host;
    private int port;
    private String message;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        host = "127.0.0.1";
        port = 6789;
        message = "I'm so fucked";
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
            //New AlertBox for signing in
            estConnection(host, port);
        } else if (result.get() == buttonTypeTwo)
        {
            //New AlertBox for signing up
            // Create the custom dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Sign up");
            dialog.setHeaderText("Sign up:");

// Set the icon (must be included in the project).
//            dialog.setGraphic(new ImageView(this.getClass().getResource("resources/signup.png").toString()));

// Set the button types.
            ButtonType loginButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
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
                    estConnection(host, port);
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
                setMessage((char) 182 + usernamePassword.getKey() + (char) 182 + usernamePassword.getValue());
            });
        } else
        {
            //Exit the Client
            System.exit(0);
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
}
