//imports
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;


public class Client extends JFrame
{

   private JMenuBar menu;
   private JMenu file;
   
   private JMenuItem Copy, Clear, Help;
   
   private JLabel host = new JLabel("Current Host: ");
   private JLabel port = new JLabel("Current Port: ");
   
   private JTextArea discussionArea = new JTextArea();                                 // Creates text area for conversation.
   private JTextField inputArea = new JTextField();                                    // Creates text area for input. 
      
   private JTextField hostField = new JTextField();
   private JTextField portField = new JTextField();
   
   private JScrollPane discussionScroll = new JScrollPane(discussionArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                                             ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private JScrollPane inputScroll = new JScrollPane(inputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                                             ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   
   private JButton Send, Stop;
   
   private PrintWriter output;
   private BufferedReader input;
   
   private Socket socket;

   private static int portNumber;
   private static String hostName;
   
   private String inputLine, message, copiedText;



//-----------------------------------------------------------------
//  Main Function
//-----------------------------------------------------------------
   public static void main(String[] args)
   {
         
      if (args.length != 2) 
      {
         System.err.println("To access, type: java Client <host name> <port number>");
         System.exit(1);
      }
      
      else
      {
         System.out.println("Running Client.java");
      }
      
      hostName = args[0];
      portNumber = Integer.parseInt(args[1]);
   
      
      Client c = new Client();
      c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                          // Closes GUI.
      c.setVisible(true); 																		   // Allows Component to be visible.
      c.ClientSocket();
      
      
   }
      
//-----------------------------------------------------------------
//  GUI
//-----------------------------------------------------------------

   public Client()
   { 
      super(" Client Messaging Pad");
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
   private void Frame(Client c)
   {
      c.setSize(1000,572);                                                      // Sets dimensions of GUI.
      c.setResizable(false);                                                    // Sets GUI to not be resizeable.
      c.setLocationRelativeTo(null);                                            // Sets GUI to always open in center of screen.
      c.setLayout(null);                                                        // Allows for Absolute Positioning.
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
      file.add(Help);      }
   
//-----------------------------------------------------------------
//  Discussion Area
//-----------------------------------------------------------------
   private void DiscussionBox()
   {                           
      discussionArea.setEditable(false);
      discussionArea.setFont(new Font("Arial",Font.BOLD,20));
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
      hostField.setText(hostName);
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
      Send.setBounds(810,360,178,70);                                	 // Sets button size and placement.
      Send.setFont(new Font("Arial",Font.BOLD,12));                   // Sets font size of button
      Send.setEnabled(false);
      add(Send);                                                   	// Adds button to messenger frame.
   	
   // Stop Button	
      Stop = new JButton("Stop");
      Stop.setBounds(810,440,178,70);                                	
      Stop.setFont(new Font("Arial",Font.BOLD,12));
      Stop.setEnabled(false);                 
      add(Stop); 
   
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
                     sendMessage(e.getActionCommand());
                     inputArea.setText("");
                  }
               });
   
   // Copy Menu Item            
      Copy.addActionListener(                                    
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {  
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
                     discussionArea.setText("");
                  }
               });
               
   // Help Menu Item            
      Help.addActionListener(                                    
               new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {  
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
                     sendMessage(inputArea.getText());
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
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());                    
      }
      catch(UnsupportedLookAndFeelException e){e.printStackTrace();}
      catch(ClassNotFoundException e){e.printStackTrace();}
      catch(InstantiationException e){e.printStackTrace();}
      catch(IllegalAccessException e){e.printStackTrace();}
   }


//-----------------------------------------------------------------
//  Client
//-----------------------------------------------------------------   	
   private void ClientSocket()
   {
   
      try       
      {
         displayMessage("Attempting to connect to port: " + portNumber + ". . .");
         socket = new Socket(hostName, portNumber);
         displayMessage("\nThe Client has connected to port: " + portNumber);
         displayMessage("\nLooking for Server. . . ");
         displayMessage("\nConnected to host name " + socket.getInetAddress().getHostName()); 
         
         
         output = new PrintWriter(socket.getOutputStream(), true);
         input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         displayMessage("\nTo send a message, type the message in the input box and click 'SEND' or press enter.");
         displayMessage("\nIf you need help with a conversation, press the 'File' menu button." );
         
         
         chatAllowed(true);
         
         do 
         {
            inputLine = (String) input.readLine();
            displayMessage("\n" + inputLine);
         }
         while(inputLine != null);
         
          
      }        
      
      catch (EOFException e)
      {
         displayMessage("The Client has terminated!");
         e.printStackTrace();
         System.exit(-1);
      }
      catch (UnknownHostException e) 
      {
         displayMessage("Client could not recognize host name: " + hostName);
         e.printStackTrace();
         System.exit(-1);
      } 
      catch (IOException e) 
      {
         displayMessage("Couldn't get I/O for the connection to " + hostName);
         System.exit(-1);
      }
   
   
   }

//-----------------------------------------------------------------
//  Send Messages
//-----------------------------------------------------------------
   private void sendMessage(String userMessage)
   {
      output.println("CLIENT: " + userMessage);
      displayMessage("\nCLIENT: " + userMessage);
   }
   
//-----------------------------------------------------------------
//  Show Messages on Chat Window
//-----------------------------------------------------------------
   private void displayMessage(final String chatHistory)
   {
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