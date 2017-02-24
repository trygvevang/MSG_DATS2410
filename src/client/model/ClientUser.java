package client.model;

public class ClientUser
{
    private final String name;
    private int status = 0;
    private String statusString = "Offline";

    /**
     * Constructor for this class
     * @param name sets the name of this user
     * @param status sets the status of this user
     */
    public ClientUser(String name, String status)
    {
        this.name = name;
        this.status = Integer.parseInt(status);
    }

    /**
     * @return the name of the user
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the status of the user
     */
    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
        statusString = getStatusString();
    }

    /**
     * @return the status of this user in String format
     */
    public String getStatusString()
    {
        return status == 0 ? "Offline" : (status == 1 ? "Online" : "Busy");
    }
}


