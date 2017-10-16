/*
 Program Name: Assigment 5 version 1
 Programmer:   Christopher Roberts
 Written Date: 04/29/2016
 Instructor:   Prof. Ramos
 Course:       Java 
 
 
 1.Create a simple GUI application that allows a client to connect with a server and
exchange messages.


 2.Analysis: Identifier   Type      Default  Min      Max      Description
          -----------  --------  -------  -------  -------  --------------
Constants: Chat History     

Inputs: BufferedReader input          
	   
Outputs: PrintWriter output     
	  
Equations: N/A

Output Layout:
               GUI
 
 

 3.Design (pseudocode):
 
1. Create GUI
2. Open Server Sockets
3. Open Client Socket
4. Setup I/O Streams for Client and Server.
5. Setup while loop to read lines sent from Client to Server, and Server to client.
6. Create Send method to send messages, a Display method for chat history and a chatAllowed method to set chatbox and send button to disabled until Sockets are connected.
7. Display messages from Server and Client on their respective GUI.
8. Server and Client has conversation.
9. END PROGRAM.

____________________________________________________________________________________________________________________________________________	
*/

//imports
   
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;


public class Server extends JFrame
{

// Components, Streams, data members

   private JMenuBar menu;
   private JMenu file;
   
   private JMenuItem Copy, Clear, Help;
   
   private JLabel host = new JLabel("Current Host: ");
   private JLabel port = new JLabel("Current Port: ");
   
   private JTextArea discussionArea = new JTextArea();                                 // Creates text area for conversation.
   private JTextField inputArea = new JTextField();                                      // Creates text area for input. 
      
   private JTextField hostField = new JTextField();
   private JTextField portField = new JTextField();
   
   private JScrollPane discussionScroll = new JScrollPane(discussionArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private JScrollPane inputScroll = new JScrollPane(inputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   
   private JButton Send, Stop;
   
   private PrintWriter output;
   private BufferedReader input;
   
   private ServerSocket serverSocket;
   private Socket socket;

   private static int portNumber;
   private String inputLine, message, copiedText;


//-----------------------------------------------------------------
//  Main Function
//-----------------------------------------------------------------
   public static void main(String[] args)
   {
      	
      if (args.length != 1) 
      {																					
         System.err.println("To access file type: java Server <port number>");
         System.exit(1);
      }
      else
      {
         System.out.println("Running Server.java");
      }
     
      portNumber = Integer.parseInt(args[0]);
   
      Server s = new Server();
      s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                          // Closes GUI.
      s.setVisible(true); 																		   // Allows Component to be visible.
      s.ServerSocket();
      
   }
      
//-----------------------------------------------------------------
//  GUI
//-----------------------------------------------------------------

   public Server()
   { 
      super("Server Messaging Pad");
      Frame(this);
      Menu(); 
      DiscussionBox();
      InputBox();
      HostBox();
      PortBox();
      Buttons();
      Actions();
      Appearance();
   }
   
//-----------------------------------------------------------------
//  Frame
//-----------------------------------------------------------------   
   private void Frame(Server s)
   {
      s.setSize(1000,572);                                                      // Sets dimensions of GUI.
      s.setResizable(false);                                                    // Sets GUI to not be resizeable.
      s.setLocationRelativeTo(null);                                            // Sets GUI to always open in center of screen.
      s.setLayout(null);                                                        // Allows for Absolute Positioning.
   }
 
//-----------------------------------------------------------------
//  Menu Bar
//-----------------------------------------------------------------  
   private void Menu()
   {
      menu = new JMenuBar();
      file = new JMenu(" File ");
      setJMenuBar(menu);
      
      Copy = new JMenuItem("Copy");
      Clear = new JMenuItem("Clear");
      Help = new JMenuItem("Help");
      
      menu.add(file);
      
      file.add(Copy);
      file.add(Clear);
      file.add(Help);
      
   }
   
//-----------------------------------------------------------------
//  Discussion Area
//-----------------------------------------------------------------
   private void DiscussionBox()
   {                           
      discussionArea.setEditable(false);
      discussionArea.setFont(new Font("Arial",Font.BOLD,20));
      discussionArea.setLineWrap(true);
      discussionScroll.setBounds(5,5,984,350);
      add(discussionScroll);
      
   }

//-----------------------------------------------------------------
//  Input Area
//-----------------------------------------------------------------		
   private void InputBox()
   {                           
      inputArea.setEditable(false);
      inputArea.setFont(new Font("Arial",Font.BOLD,20));
      inputScroll.setBounds(5,360,800,70);
      add(inputScroll);
      
   }
   
//-----------------------------------------------------------------
//  Host
//-----------------------------------------------------------------
   private void HostBox()
   {
      host.setFont(new Font("Arial",Font.BOLD,20));
      host.setBounds(5,440,150,30);
      add(host);
   
      hostField.setEditable(false);
      hostField.setText("N/A");
      hostField.setFont(new Font("Arial",Font.BOLD,20));
      hostField.setBounds(140,440,664,30);
      add(hostField);
   }

//-----------------------------------------------------------------
//  Port
//-----------------------------------------------------------------   
   private void PortBox()
   {
      port.setFont(new Font("Arial",Font.BOLD,20));
      port.setBounds(5,480,150,30);
      add(port);
      
      portField.setEditable(false);
      portField.setText("" + portNumber);
      portField.setFont(new Font("Arial",Font.BOLD,20));
      portField.setBounds(140,480,664,30);
      add(portField);
      
   }
//-----------------------------------------------------------------
//  Buttons
//-----------------------------------------------------------------      
   private void Buttons()
   {
   
   // Send Button                         
      Send = new JButton("Send");                                     // Initializes buttons.
      Send.setBounds(810,360,178,70);                                 // Sets button size and placement.
      Send.setFont(new Font("Arial",Font.BOLD,12));                   // Sets font size of button
      Send.setEnabled(false);                                         // Set button to be non clickable. 
      add(Send);                                                   	 // Adds button to messenger frame.
   	
   // Stop Button	
      Stop = new JButton("Stop");
      Stop.setBounds(810,440,178,70);                                	
      Stop.setFont(new Font("Arial",Font.BOLD,12));                 
      add(Stop); 
      Stop.setEnabled(false);
   }
   
//-----------------------------------------------------------------
//  Button and Menu Item Actions
//----------------------------------------------------------------- 
   private void Actions()
   {
   
   // Enter Button Action
      inputArea.addActionListener(                                    
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {  
                     sendMessage(e.getActionCommand());                                      // Sends with the enter button.
                     inputArea.setText("");                                                  // Clears input box afterwards.
                  }
               });
   
   // Copy Menu Item            
      Copy.addActionListener(                                    
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {  
                  //Copies Chat History to a clipboard for pasting elsewhere.
                  
                     copiedText = discussionArea.getText();
                     StringSelection stringSelection = new StringSelection(copiedText);
                     Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                     clip.setContents(stringSelection, null);
                  }
               });
   
   
   // Clear Menu Item            
      Clear.addActionListener(                                    
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {  
                     discussionArea.setText("");                                          // Clears Chat history.
                  }
               });
               
   // Help Menu Item            
      Help.addActionListener(                                    
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {  
                  //Brings up help window with button functions.
                     JOptionPane.showMessageDialog(null,"Send Button - Send messages to Client.\nStop Button - Stops conversation and closes socket connection.\nCurrent Host - Shows the host the message pad is connected to.\nCurrent Port - Shows the current port the message pad is connected to.\nCopy Button - Copies chat window to a clipboard\nClear Button - Clears the chat window.","Button Functions",JOptionPane.OK_OPTION); 
                  }
               });
               
   // Stop Button          
      Stop.addActionListener(                                      
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {
                     System.exit(0);
                     System.out.println("The connection has been closed.");
                  }
               });
                           
   // Send Button 
      Send.addActionListener(                                    
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {  
                     sendMessage(inputArea.getText());                                    // Grabs inputArea and then sends it as a message when clicked.
                     inputArea.setText("");
                  }
               });
   
   }
   
//-----------------------------------------------------------------
//  Look and Feel
//-----------------------------------------------------------------
   public static void Appearance()                                        
   {
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());                     // Makes the messenger look like the System's Theme.
      }
      catch(UnsupportedLookAndFeelException e){e.printStackTrace();}
      catch(ClassNotFoundException e){e.printStackTrace();}
      catch(InstantiationException e){e.printStackTrace();}
      catch(IllegalAccessException e){e.printStackTrace();}
   }

//-----------------------------------------------------------------
//  Server 
//-----------------------------------------------------------------  	
   private void ServerSocket()
   {
      try
      {   
         displayMessage("Attempting to connect to port: " + portNumber + ". . .");
         serverSocket = new ServerSocket(portNumber);                                                                            // Creates a server socket, bound to the specified port.
         displayMessage("\nThe Server has connected to port: " + portNumber);
      
         while(true)
         {
            try
            {
               displayMessage("\nWaiting for Client to connect. . .");
               socket = serverSocket.accept();                                                                                   // Listens for a connection to be made to this socket and accepts it.
               displayMessage("\nClient has connected to Server.");
            
            
            
               output = new PrintWriter(socket.getOutputStream(), true);                                                         // Opens Output Stream for Sending messages and auto flushes.
               input = new BufferedReader(new InputStreamReader(socket.getInputStream()));                                       // Opens Input Stream for Recieving messages.
               displayMessage("\nTo send a message, type the message in the input box and click 'SEND' or press enter.");
               displayMessage("\nIf you need help with a conversation, press the 'File' menu button." );
                
               
               chatAllowed(true);                                                                                                // Typing and sending is now enabled.
               
               do 
               {
                  inputLine = (String) input.readLine();                                                                         // Read input from Client.
                  displayMessage("\n" + inputLine);                                                                              // Display that input on the Server's Chat History.
               }
               while(inputLine != null);
                
               
                  
            }
            
            catch(EOFException ex)
            {
               displayMessage("Server has ended the connection!");
               inputArea.setText("");
               chatAllowed(false);    
            }
         }   
      
      }
      
      catch (IOException e)
      {
         displayMessage("\nMESSAGE: The Server/Client has ended the connection or the Server could not connect to that port.\nRestart Server and Client!");
         inputArea.setText("");
         chatAllowed(false); 
      }
      
   }

//-----------------------------------------------------------------
//  Send Messages
//-----------------------------------------------------------------
   private void sendMessage(String userMessage)
   {
      output.println("SERVER: " + userMessage);                                              // Sends whatever the Server sent through the Output Stream.
      displayMessage("\nSERVER: " + userMessage);                                            // Displays whatever the Server typed to the chat history.
   }
   
//-----------------------------------------------------------------
//  Show Messages on Chat Window
//-----------------------------------------------------------------   
   private void displayMessage(final String chatHistory)
   {
      //Displays everything sent and recieved on the chat history.
      SwingUtilities.invokeLater
         (
         new Runnable()
         {
            public void run()
            {
               discussionArea.append(chatHistory);
            }
         }
         );
   }

//-----------------------------------------------------------------
//  Chat box permission to type
//-----------------------------------------------------------------
   private void chatAllowed(final boolean permission)
   {
   //Enables typing, sending and stoping after sockets are safely connected.
      SwingUtilities.invokeLater
         (
         new Runnable()
         {
            public void run()
            {
               inputArea.setEditable(permission);
               Send.setEnabled(permission); 
               Stop.setEnabled(permission);
            }
         }
         );
   }

}