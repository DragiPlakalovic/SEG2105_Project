import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import common.*;
import client.*;

public class ClientGUI extends JFrame implements ActionListener, ChatIF {
	// Variables that represent various components of the client
	private JFrame mainProgram;
	
	// Menu bar and associated menus
	private JMenuBar mb;
	private JMenu file;
	private JMenu help;
	
	// Menu items for the client
	private JMenuItem about;
	private JMenuItem logIn;
	private JMenuItem logOut;
	private JMenuItem exitProgram;
	
	// Variables for contact dialoog and log in
	private JDialog contactInfo;
	private JButton closeAbout;
	private JDialog loginBox;
	
	// Sending message variables
	private JTextField message;
	private JLabel messageLabel;
	private JButton sendButton;
	
	// Chat box and connection status
	private JTextArea chatBox;
	private JLabel connectionStatus, date;

	// Create an instance of ChatClient and default port
	ChatClient chat;
	static int DEFAULT_PORT = 5555;

	// Function for displaying messages
	public void display(String message) 
  	{
    		if (message.contains("#server")) {
			// Remove #server and display server's maessage
			int index = message.indexOf('#');
			String mes = message.substring(0, index);
			chatBox.append("SERVER MSG> " + mes + "\n");
		} 
		else {
			// Split the message and print: loginID> msg (messages)
			if (message.contains(" has loged on")) {
				String[] mes = message.split(" ", 2);
				chatBox.append(mes[0] + " " + mes[1] + "\n");
			}
			else {
				String[] mes = message.split(" ", 2);
				chatBox.append(mes[0] + "> " + mes[1] + "\n");
			}
		}
    		
  	}
	
	public ClientGUI(String host, int port, String id) {
		// Start client
		chat = new ChatClient(host, port, id, this);

		/* Name the program's frame and set its size */
		mainProgram = new JFrame("SimpleChatClient");
		mainProgram.setLayout(null);
		mainProgram.setSize(630, 500);
		
		// Add a label, text area, and button for sending messages
		message = new JTextField();
		message.setBounds(10,380,240,50);
		
		messageLabel = new JLabel("Type your message here: ");
		messageLabel.setBounds(10,350,200,20);
		
		sendButton = new JButton("Send");
		sendButton.setBounds(300,380,100,20);
		
		mainProgram.add(message);
		mainProgram.add(messageLabel);
		mainProgram.add(sendButton);
		
		// Add event listener for the sendButton
		sendButton.addActionListener(this);
		
		// Add a chat box
		chatBox = new JTextArea("\n");
		chatBox.setBounds(10, 0, 300, 300);
		chatBox.setEditable(true);
		mainProgram.add(chatBox);
		
		// Add labels that indicate current date and connectivity status
		connectionStatus = new JLabel("You're not logged in! To login, go to File->Login");
		connectionStatus.setBounds(320,0,275,50);
		mainProgram.add(connectionStatus);
		
		// Add a menu bar
		mb = new JMenuBar();
			
		// Create menus that appear in menu bar
		file = new JMenu("File");
		help = new JMenu("Help");
			
		// In Help menu, user can get version information and contact details 
		about = new JMenuItem("About");
		help.add(about);
		
		// Add options for logging in, logging out and closing the program
		logIn = new JMenuItem("Login");
		logOut = new JMenuItem("Logout");
		exitProgram = new JMenuItem("Exit");
		file.add(logIn);
		file.add(logOut);
		file.add(exitProgram);
		
		// Create a dialog box for contact information and version history
		contactInfo = new JDialog(mainProgram);
        contactInfo.setTitle("About SimpleChatGUI Client.");
        contactInfo.setSize(new Dimension(850, 100));
		
		// Add elements to the dialog (description and button)
		JPanel pan=new JPanel();
		pan.setLayout(new FlowLayout());
		pan.add(new JLabel("Simple Chat Client GUI is licensed under MIT license."));
		pan.add(new JLabel("Furthermore, OCSF component of Simple Chat was made at University of Ottawa"));
		
		// Add action listener to the close button
		closeAbout = new JButton("Close");
		pan.add(closeAbout);
		closeAbout.addActionListener(this);
		
		// Add the panel to the dialog
		contactInfo.add(pan);
		
		// Location and visibility
        contactInfo.setLocationRelativeTo(mainProgram);
        contactInfo.setModal(false);
        contactInfo.setVisible(false);
			
		// Add action listeners to the menu items
		about.addActionListener(this);
		logIn.addActionListener(this);
		logOut.addActionListener(this);
		exitProgram.addActionListener(this);

		// Add menus to the menu bar. Then, add the menu and panel bar to the frame
		mb.add(file);
		mb.add(help);
		mainProgram.setJMenuBar(mb);
		//mainProgram.add(panel);
		
		// Operations for closing the client and its display
		mainProgram.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainProgram.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==about) {
			// Display the dialog box
			contactInfo.setVisible(true);
		}
		if (e.getSource()==closeAbout) {
			// Close the box
			contactInfo.setVisible(false);
		}
		if (e.getSource()==exitProgram) {
			// Exit the program
			System.exit(0);
		}
		if (e.getSource()==sendButton) {
			// Send the message to the server
			String out = message.getText();
			chat.handleMessageFromClientUI(out);
			// Clean the message box and display the message into the chat box
			message.setText(null);
			chatBox.append(out+"\n");
		}
		if (e.getSource()==logIn) {
			// Send command for login
			chat.handleMessageFromClientUI("#login");
			// Change connection status
			connectionStatus.setText("You're logged in! To logout, go to File->Logout");
		}
		if (e.getSource()==logOut) {
			// Log off the user
			chat.handleMessageFromClientUI("#logoff");
			// Change connection status
			connectionStatus.setText("You're not logged in! To login, go to File->Login");
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String clientID = "";
				String host = "";
				int port = 0;  //The port number

				try
				{
					// Given that only clientID argument is mandatory, check if user entered additional arguments
					if (args.length == 3) {
						clientID = args[0];
						host = args[1];
						port = Integer.parseInt(args[2]);

					} 
					else if (args.length == 2) {
						// Determine the type of argument. Assume that second argument is port argument
						clientID = args[0];
						port = Integer.parseInt(args[1]);
						host = "localhost";
					}
					else
				      		clientID = args[0];
						host = "localhost";
						port = DEFAULT_PORT;
				    }
				    catch(ArrayIndexOutOfBoundsException e)
				    {
				      System.out.println("ERROR - No login ID specified.  Connection aborted.");
					System.exit(1);
				    }
					// The assumption was wrong.
				    	catch (NumberFormatException e) 
				    	{
						// Use the default port
						port = DEFAULT_PORT;
						host = args[1];
					}
				new ClientGUI(host,port, clientID);
			}
		});
	}
}
