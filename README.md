# MSG_DATS2410
First obligatory assignment for the subject DATS2410 Networking and Cloud Computing Spring 2017 - Oslo and Akershus University College.

This is a group project by students attending HiOA - HINGDATA15HA
- Gustav Berggren    s305067
- Jesper Nylend      s305070
- Lars Kristian Haga s305357
- Trygve Nybakk Vang s305054

<br />
<br />

**How to use the program:**

*NOTE: The server needs too be launched before you try to Sign in or Sign up a new user!*

**Server part:**

First you have to start up ServerMain, this will promt you with the GUI for our server. In the right colomn you will be able to see all registered users. Here you can click on the names and see their username, password, port, IPadress and status, this will appear in the middle column. In the right column you will be able to see every client connected to the server through the socket.

**Client part:**

WHen you start ClientMain you will get a promt that will ask you to enter a IP address, here you need to enter the IP address of the Server you want to connect to. The default value is '127.0.0.1' which is the localhost IP. After you have entered a valid IP address you will be able to choose between signing in or signing up.
If you are a new user you will need to create a new user, if you press 'Sign up' you will be able to do this. Another window will appear allowing you to type in wanted Username og Password. When you have pressed the Submit button you will be taken to the Client window.

If you allready have an existing user you can press 'Sign in', here you will need to enter username and a password. If the server accepts the username and password combination you will be taken to the Client window, here you can chat with other users if any is online. If the server dosen’t accept the combination the client window will shutdown.
In the client window on the left you will be able to see all registered users and their status.

*NOTE: you can only chat with users where their status is 'Online', not 'Offline' or 'Busy'.*

To chat with someone you go to 'File' -> 'Connect with', this will allow you to select a person you can start a chat with. After you have choosen whom you want to chat with, you can now send message to the 
other person by typing in the textfield at the bottom and hit send. You will then see your message appear in the other big textfield at the top-right of the screen.

*NOTE: if you are the only one online you cannot connect to someone else to start a chat.*

You are also able to disconnect from a chat, to do this you go to 'File' -> 'Disconnect from chat'.
You can also update the userlist and see updated status of all users go to 'File' -> 'Update list'.
When you are done chatting you can go to 'File' -> 'Log out' to disconnect and end the program.
