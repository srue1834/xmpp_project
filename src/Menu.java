import java.util.Scanner;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Menu {
    boolean exit;
    boolean signedout;
    Client client = new Client();
    Scanner sc = new Scanner(System.in);

    public void mainMenu() throws Exception {
        header();
        XMPPConnection con = client.connection_cofig();
        while(!exit) {
            optionMenu();
            int adminOption = getInput(3);
            accountAdminOptions(adminOption, con);
        }
    }

    private void header() {
        System.out.println("\n+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+");
        System.out.println("+                                                             +");
        System.out.println("+                          ALUM CHAT                          +");
        System.out.println("+                                                             +");
        System.out.println("+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+");
        
    }
    private void optionMenu() {
        System.out.println("\nSelect an option: \n");

        System.out.println("    1. Register a new account on the server");
        System.out.println("    2. Sign in with an account");
        System.out.println("    3. Exit chat\n");
    }
    private void loginOptionMenu() {
        System.out.println("\nSelect an option: \n");

        System.out.println("    1. Show all contacts and status");
        System.out.println("    2. Add a user to contacts");
        System.out.println("    3. Show contact details of a user");
        System.out.println("    4. 1-to-1 private chat with user/contact");
        System.out.println("    5. Participate in groupchat");
        System.out.println("    6. Define presence message");
        System.out.println("    7. Send/receive notifications");
        System.out.println("    8. Send/receive files");
        System.out.println("    9. Sign out");
        System.out.println("    10. Delete account\n");
    }

    private int getInput(int max_num) {
        int option = 0;
        while(option < 1 || option > max_num) {
            try {
                System.out.print("Enter your selected option: ");
                option = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid option.");
            }
        }
        return option;
    } 

    public void accountAdminOptions(int option, XMPPConnection con) throws XMPPException {

        switch(option) {
            case 1:
                client.createAccount(con);
                break;

            case 2:
                String username = client.login(con);
                System.out.println("\n (♥_♥) " + username + ", welcome back! (♥_♥)\n");

                while(!signedout) {
                    loginOptionMenu();
                    int loginOption = getInput(10);
                    loginMenu(loginOption, con);
                }
                
                break;

            case 3:
                exit = true;
                System.out.println("\nThank you for using Alum Chat. See you another time!\n");
                break; 

            default:
                System.out.println("\nUnknown error!!! :O\n");
        }

    }

    public void loginMenu(int option, XMPPConnection con) throws XMPPException {
        switch(option) {
            case 1:
                // show all contacts
                System.out.println("\ncontacts\n");
                client.getContacts(con);
                break;

            case 2:
                // add user to contacts
                System.out.println("\nadd contacts\n");
                System.out.println("\n+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+");
                System.out.println("\n+                       Add new contact                       +\n");
                System.out.print("Enter username: ");
                String friend = sc.nextLine();
                client.addContact(con, friend);
                break;

            case 3:
                // show contact details
                System.out.println("\nshow contacts details\n");
                System.out.println("\n+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+");
                System.out.println("\n+                       Contact details                       +\n");
                System.out.print("Enter username: ");
                String user_name = sc.nextLine();
                client.contactInfo(con, user_name);
                break;

            case 4:
                // 1-to-1 chat
                System.out.println("\nchat\n");
                client.chat(con);
                break;

            case 5:
                // groupchat
                System.out.println("\ngroupchats\n");
                break;

            case 6:
                // presence message
                System.out.println("\npresence message\n");
                break;
            case 7:
                // send/ receive notifications
                System.out.println("\nnotifications\n");
                break;

            case 8:
                // send/ receive files
                System.out.println("\nfiles\n");
                break;

            case 9:
                // sign out
                signedout = true;
                client.logout(con);
                break;

            case 10:
                // delete account
                System.out.println("\ndelted\n");
                client.deleteAccount(con);
                break; 

            default:
                System.out.println("\nUnknown error!!! :O\n");
        }

    }
    
    
}
