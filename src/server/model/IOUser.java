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
     * @param path path of the file
     * @return list of users
     * @throws IOException
     */
    public static List<User> read(String path) throws IOException
    {
        List<User> users = new ArrayList<>();
        String reg = (char) 182 + "";
        String[] parts;

        try
        (
            BufferedReader in = new BufferedReader(new FileReader(new File(path)))
        )
        {
            for (String l = in.readLine(); l != null; l = in.readLine())
            {
                parts = l.split(reg);
                users.add(new User(parts[1], parts[2]));
            }
        }
        return users;
    }

    /**
     * Writes user information to the users.txt
     *
     * @param path path of the file users.txt
     * @param u    the user containing the user information
     * @throws IOException
     */
    public static void write(String path, User u) throws IOException
    {
        try
        (
            FileWriter fileWriterw = new FileWriter(path, true);
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
     * @return the user id, so the user know what to log in with
     */
    public static boolean register(List<User> users, String info)
    {
        String[] tokens = info.split((char) 182 + "");

        User tmp = new User(tokens[1], tokens[2]);
        users.add(tmp);

        try
        {
            IOUser.write("src/server/resources/users.txt", tmp);
        } catch (IOException e){
            System.err.println("I/O Exception: " + e.getMessage());
            return false;
        }
        return true;
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
            if (username.equals(user.getName()) && passwordAttempt.equals(user.getPassword())){
                user.setStatus(1);
                return "true";
            }
        }
        return "false";
    }
}
