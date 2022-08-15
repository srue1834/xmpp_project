/* References:
 *   Collins, O (2016) Java Smack Client For Beginners. https://youtu.be/iAuc3wp5Mt4
 * 
 */

import java.util.Scanner;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

public class Client {
    public Client() {
    }

    public void connection_cofig() throws Exception {
        // create a thread that runs the connection to server
        new Thread() {
            public void run() {
                try {
                    // connection object
                    ConnectionConfiguration config = new ConnectionConfiguration("alumchat.fun", 5222);
                    config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    config.setDebuggerEnabled(false);
                    config.setSendPresence(true);
                    XMPPConnection con = new XMPPConnection(config);
                    con.connect();
                    

                    // create account manager for connection
                    // AccountManager manager =  con.getAccountManager();
                    // manager.createAccount("pruebauser2", "pass");

                    // login
                    con.login("pruebauser", "pass");

                    // class chat in smack lib
                    Chat chat = con.getChatManager().createChat("pruebauser@alumchat.fun", new MessageListener() {

                        @Override
                        // method called each time a message is received
                        public void processMessage(Chat chat, Message msg) {
                            System.out.println(chat.getParticipant() + " said: " + msg.getBody());

                            
                        }
                        
                    });

                    System.out.println("Connected babyyyy");
                    try (
                    Scanner reader = new Scanner(System.in)) {
                        while(con.isConnected()) {
                            chat.sendMessage(reader.nextLine());
                        }
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
    
}
