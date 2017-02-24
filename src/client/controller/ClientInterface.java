package client.controller;

public interface ClientInterface
{
    String getMessage();
    void setMessage(String message);
    void setValidLogin();
    void setUserList(String token);

    void printMessage(String s);
}
