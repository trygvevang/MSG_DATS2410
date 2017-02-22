package client.model;

public class ClientUser
{
    private final String uid;
    private final String name;


    private int status = 0;

    public ClientUser(String uid, String name)
    {
        this.uid = uid;
        this.name = name;

    }

    public String getUid()
    {
        return uid;
    }

    public String getName()
    {
        return name;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }
}


