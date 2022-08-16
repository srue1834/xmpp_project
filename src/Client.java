/* References:
 *   Collins, O (2016) Java Smack Client For Beginners. https://youtu.be/iAuc3wp5Mt4
 * 
 */

 // Client client = new Client();
// client.connection_cofig();

import java.util.Scanner;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Client {
    Scanner sc = new Scanner(System.in);

    public XMPPConnection connection_cofig() throws Exception {
        // connection object
        ConnectionConfiguration config = new ConnectionConfiguration("alumchat.fun", 5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setDebuggerEnabled(false);
        config.setSendPresence(true);
        
        XMPPConnection con = new XMPPConnection(config);
        con.connect();
        
        System.out.println("Connected to server\n");

        return con;
    }


    public void createAccount(XMPPConnection con) throws XMPPException {

        // registration menu
        System.out.println("\n+            Register a new account on the server             +\n");
        System.out.print("Enter username: ");
        String new_username = sc.nextLine();
        System.out.print("\nEnter password: ");
        String new_password = sc.nextLine();

        // create account manager for connection
        AccountManager manager =  con.getAccountManager();
        manager.createAccount(new_username, new_password);
        // manager.createAccount("pruebauser2", "pass");
    }

    public String login(XMPPConnection con) throws XMPPException {
        
        // login menu
        System.out.println("\n+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+");
        System.out.println("\n+                   Sign in with an account                   +\n");
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("\nEnter password: ");
        String password = sc.nextLine();
        
        // login
        con.login(username, password);
        return username;
    }

    public void logout(XMPPConnection con) throws XMPPException {
        System.out.println("\nLogin out ...");
        con.disconnect();
        System.out.println("\nLogged out");
    }

    public void chat(XMPPConnection con) throws XMPPException {
        // chat menu 
        System.out.println("\n+                       Private chatroom                       +\n");
        System.out.print("Enter username of the contact you want to chat with: ");
        String contactName = sc.nextLine();
        // class chat in smack lib "pruebauser@alumchat.fun"
        Chat chat = con.getChatManager().createChat(contactName, new MessageListener() {
            
            @Override
            // method called each time a message is received
            public void processMessage(Chat chat, Message msg) {
                System.out.println(chat.getParticipant() + " said: " + msg.getBody());
            }
        });
        
        while(con.isConnected()) {
            System.out.print("\n>> ");
            chat.sendMessage(sc.nextLine());
        }
    }



    
}
