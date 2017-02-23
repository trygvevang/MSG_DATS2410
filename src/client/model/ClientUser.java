package client.model;

public class ClientUser
{
    private final String name;
    private int status = 0;
    private String statusString = "Offline";

    public ClientUser(String name, String status)
    {
        this.name = name;
        this.status = Integer.parseInt(status);
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
        statusString = getStatusString();
    }

    public String getStatusString()
    {
        return status == 0 ? "Offline" : (status == 1 ? "Online" : "Busy");
    }
}


