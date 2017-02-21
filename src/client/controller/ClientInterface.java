package client.controller;

/**
 * Created by Jesper Nylend on 21.02.2017.
 * s305070
 */
public interface ClientInterface
{
    String getMessage();
    void setMessage(String message);
    void setValidLogin(boolean value);
    void setSearcher(String str);

}
