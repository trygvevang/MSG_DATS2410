package server.model;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Useful static methods for I/O of the users
 */
public class IOUser
{
    /**
     * Reads from a file, and generates a list of users
     *
     * @return list of users
     */
    public static ArrayList<User> read()
    {
        ArrayList<User> users = new ArrayList<>();
        String reg = (char) 182 + "";
        String[] parts;

        try
                (
                        BufferedReader in = new BufferedReader(new FileReader(new File("src/server/resources/users.txt")))
                )
        {
            for (String l = in.readLine(); l != null; l = in.readLine())
            {
                parts = l.split(reg);
                users.add(new User(parts[1], parts[2]));
            }
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return users;
    }

    /**
     * Writes user information to the users.txt
     *
     * @param u    the user containing the user information
     * @throws IOException if the method could not write to the file
     */
    private static void write(User u) throws IOException
    {
        try
                (
                        FileWriter fileWriterw = new FileWriter("src/server/resources/users.txt", true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriterw);
                        PrintWriter out = new PrintWriter(bufferedWriter)
                )
        {
            out.println(u.toString());
        }
    }

    /**
     * Registers a new user in the system. This function updates both, the list of users and
     * the text file where the users are stored between sessions.
     *
     * @param users The list of users known to the server
     * @param info  input from client. Used for creating a new user
     */
    public static void register(ArrayList<User> users, String info)
    {
        String[] tokens = info.split((char) 182 + "");

        User tmp = new User(tokens[1], tokens[2]);
        users.add(tmp);

        try
        {
            IOUser.write(tmp);
        } catch (IOException e)
        {
            System.err.println("I/O Exception: " + e.getMessage());
        }
    }

    /**
     * Checks the input from user to see if the password and User ID corresponds to the registered user
     *
     * @param users List of users, to make the checking faster
     * @param input from the client to the server
     * @return either Accepted or Decliend, basically true or false
     */
    public static String logIn(List<User> users, String input)
    { //TODO: Fix IP and port
        String[] info = input.split((char) 169 + "");
        String username = info[1];
        String passwordAttempt = info[2];

        for (User user : users)
        {
            if (username.equals(user.getName()) && passwordAttempt.equals(user.getPassword()) && user.getStatus() == 0)
            {
                user.setStatus(1);
                return "true";
            }
        }
        return "false";
    }

    public static String getUserList(ArrayList<User> users)
    {
        String s = "";
        User t;
        for (User user : users)
        {
            t = user;
            s += t.getName() + (char) 182 + t.getStatus() + (char) 208;
        }
        return s;
    }

//    public static String getPersonalMessage(){
//
//    }

}
