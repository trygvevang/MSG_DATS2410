package client.controller;

/**
 * This interface is used to communicate between the JavaFX thread and the ClientThread.
 */
public interface ClientInterface
{
    String getMessage();
    void setMessage(String message);
    void setValidLogin();
    void setUserList(String token);

    void printMessage(String s);
}
