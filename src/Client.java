/* References:
 *   Collins, O. (2016). Java Smack Client For Beginners. https://youtu.be/iAuc3wp5Mt4
 *   Sclemens. (2015). uni-wuppertal-sw. https://github.com/uni-wuppertal-swt/anIMus/tree/b0d9757d50ae2f272aebea007776388f745900fa
 */



import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// import javax.jws.WebParam.Mode;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.muc.MultiUserChat;




public class Client {
    Scanner sc = new Scanner(System.in);
    Roster roster;
    
    boolean connectedServer;
    Presence presence;
    Map<String, MultiUserChat> chats_ = new HashMap<String, MultiUserChat>();

    public XMPPConnection connection_cofig() throws Exception {
        // connection object
        
        ConnectionConfiguration config = new ConnectionConfiguration("alumchat.fun", 5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setDebuggerEnabled(false);
        config.setSendPresence(true);
        
        XMPPConnection con = new XMPPConnection(config);
        con.connect();
        
        
        System.out.println("Connected to server\n");
    

        setStatus(Type.available, con);

        return con;
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
        mainRoster(con);
        roster = con.getRoster();
        
        Presence.Mode status = roster.getPresence(username).getMode();
        Presence.Type type = setStatus(Type.available, con);
        System.out.println("status -> " + status + "\n");
        System.out.println("type -> " + type + "\n");

        return username;
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

    
    public void logout(XMPPConnection con) throws XMPPException {
        System.out.println("\nLogin out ...");
        con.disconnect();
        System.out.println("\nLogged out");
    }

    public void getContacts(XMPPConnection con) throws XMPPException {
        System.out.println("\n+                       Friends list                       +\n");
        roster = con.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        System.out.println("\n\n" + entries.size() + " Friends 龴ↀ◡ↀ龴:");
       

        getStatus(con);

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

    // different status available for a user
    public Presence.Type setStatus(Presence.Type type, XMPPConnection con) {
        
        switch(type) {
            case available: {
                presence = new Presence(Presence.Type.available);
                presence.setType(Type.available);
                break;
            }
            case unavailable: {
                presence = new Presence(Presence.Type.unavailable);
                presence.setType(Type.unavailable);
                break;
            }
            default:
                return type;
        }
        if (con.isConnected()) {
            con.sendPacket(presence);
        }
        return type;
    }

    public void getStatus( XMPPConnection con) throws XMPPException {
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry r : entries) {
            connectedServer = true;
            Presence presence = roster.getPresence(r.getUser());
            
            if (con.isConnected()) {
                // Type type = setStatus(Type.available, con);
                
                System.out.println("User -> " + r.getName().toString() + " : " + presence.getType().toString());
            } else {
                System.out.println("User -> " + r.getName().toString() + " : " + presence.getType().toString());

            }
            
        } 
        
    }

    public void chat(XMPPConnection con) throws XMPPException {
        // chat menu 
        String msg_sent;
        System.out.println("\n+                       Private chatroom                       +\n");
        System.out.print("Enter username of the contact you want to chat with (write ~bye to quit chat): ");
        String contactName = sc.nextLine();
        // class chat in smack lib "pruebauser@alumchat.fun"
        Chat chat = con.getChatManager().createChat(contactName + "@alumchat.fun", new MessageListener() {
            
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


    // enter and chat in groupchat 
    public void groupchat(XMPPConnection con) throws XMPPException {

        String msg_sent;
        System.out.println("\n+                       Private groupchat                       +\n");
        System.out.print("Enter groupchat name you want to enter (write ~bye to quit chat): ");
        String chatName = sc.nextLine();

        MultiUserChat muc = new MultiUserChat(con, chatName + "@alumchat.fun");
        
        // muc.create("Chat");
        // Form form = muc.getConfigurationForm();
        // Form answerForm = form.createAnswerForm();
        // answerForm.setAnswer("muc#roomconfig_publicroom", true);
        // answerForm.setAnswer("muc#roomconfig_roomname", chatName);

        // muc.sendConfigurationForm(answerForm);
        // System.out.println("New groupchat created!!!");

        try {
            muc.join(chatName + "@alumchat.fun");
            System.out.println("joined groupchat!!");
        } catch (XMPPException e) {
            System.out.println("Couldnt be added to groupchat, too bad! :(");
        }
        
        muc.addMessageListener(new PacketListener() {

            @Override
            public void processPacket(Packet p) {
                Message group_msg = (Message) p;
                if (group_msg.getBody() != null) {
                    System.out.println(group_msg.getFrom() + " said: " + group_msg.getBody());
                }
                
            }
            
        });
        do {
            System.out.print(">> ");
            msg_sent = sc.nextLine();
            System.out.print(">> ");
            muc.sendMessage(msg_sent);
        }
        while(!(msg_sent.equals("~bye")));



    }

    public void presenceMessage(XMPPConnection con) {
        System.out.println("\n+                       Change presence message                       +\n");
        System.out.print("\nSelect one of the following presence: \n");

        System.out.println("    1. Available ");
        System.out.println("    2. Unavailable");

        int option = 0;
        while(option < 1 || option > 2) {
            try {
                System.out.print("Enter your selected option: ");
                option = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid option.");
            }
        }

        switch(option) {
            case 1:
                setStatus(Type.available, con);
                break;

            case 2:
                setStatus(Type.unavailable, con);
                break;

            default:
                System.out.println("\nUnknown error!!! :O\n");
        }
        
        
    }

    public void notifications() {
        // TODO notifications
    }

    public void receiveFiles() {
        // TODO receive files
    }

    

    // delete user account
    public void deleteAccount(XMPPConnection con) throws XMPPException {
        con.getAccountManager().deleteAccount();
        System.out.println("\nYour account has been deleted, see ya!\n");
        con.disconnect();
    }
        

    public void mainRoster(XMPPConnection con) throws XMPPException {
        if (con.isConnected()) {
            roster = con.getRoster();
            // Collection<RosterEntry> entries = roster.getEntries();
            roster.addRosterListener(new RosterListener() {
                
                @Override
                public void entriesAdded(Collection<String> arg0) {
                    System.out.println("Entries added to server");
                }

                @Override
                public void entriesDeleted(Collection<String> arg0) {
                    System.out.println("Entries delete from server");
                }

                @Override
                public void entriesUpdated(Collection<String> arg0) {
                    try {
                        getContacts(con);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    
                }
                @Override
                public void presenceChanged(Presence presence) {
                    try {
                        getStatus(con);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }
                
            });
            
        }

    }



    
    



    
    

    

    

    

    
}
