/* References:
 *   Collins, O (2016) Java Smack Client For Beginners. https://youtu.be/iAuc3wp5Mt4
 * 
 */

import java.util.Collection;

import java.util.Scanner;


import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;




public class Client {
    Scanner sc = new Scanner(System.in);
    Roster roster;

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

    public void contactInfo(XMPPConnection con, String user_name) throws XMPPException {
        roster = con.getRoster();
        RosterEntry r = roster.getEntry(user_name);
        Presence presence = roster.getPresence(user_name);
        System.out.println("\n ~ Username: " + r.getName() + "\n ~ Status: " + presence.getType() + "\n ~ ID: " + presence.getPacketID() + "\n ~ Subscription mode: " + roster.getSubscriptionMode() + "\n");

    }


    public void addContact(XMPPConnection con, String user_name) throws XMPPException {
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
        roster = con.getRoster();
        
        // create space for new user to be added
        String[] contact_list = new String[1];
        contact_list[0] = "Friends";
        roster.createEntry(user_name, user_name, contact_list);

        // subscription to user
        Presence presence = new Presence(Presence.Type.subscribe);
        presence.setTo(user_name);
        con.sendPacket(presence);
       
    }

    public void getStatus(XMPPConnection con) throws XMPPException {
        roster = con.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        System.out.println("\n\n" + entries.size() + " Friends 龴ↀ◡ↀ龴:");

        for (RosterEntry r : entries) {
            String user = r.getUser();
            Presence.Mode status = roster.getPresence(user).getMode();
            Presence.Type type = roster.getPresence(user).getType();

            if (type.equals(null) || type.equals(Presence.Type.available)) {
                if (status == null || status.equals(Presence.Mode.available)) {
                    
                    System.out.println(user + " : Online");
                } else if (status.equals(Presence.Mode.chat)) {
                    
                    System.out.println(user + " : Ready to chat");
                }

            } else {
           
                System.out.println(user + " : Offline");
            }
            
        }

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
        String msg_sent;
        System.out.println("\n+                       Private chatroom                       +\n");
        System.out.print("Enter username of the contact you want to chat with (write ~bye to quit chat): ");
        String contactName = sc.nextLine();
        // class chat in smack lib "pruebauser@alumchat.fun"
        Chat chat = con.getChatManager().createChat(contactName, new MessageListener() {
            
            @Override
            // method called each time a message is received
            public void processMessage(Chat chat, Message msg) {
                System.out.println(chat.getParticipant() + " said: " + msg.getBody());
            }
        });
        
        do {
            System.out.print(">> ");
            msg_sent = sc.nextLine();
            System.out.print(">> ");
            chat.sendMessage(msg_sent);
        }
        while(!(msg_sent.equals("~bye")));
    }

    
}
