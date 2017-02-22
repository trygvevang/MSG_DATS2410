package client.controller;

public interface ClientInterface
{
    String getMessage();
    void setMessage(String message);
    void setValidLogin(boolean value);
    void setSearcher(String str);
    void setUserList(String token);
}
