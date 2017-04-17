package server;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class ChatClient {

	protected static JFrame frame = new JFrame();
	protected static JTextField messageArea = new JTextField(); 
	protected static JTextArea onlineUsers = new JTextArea();
	protected static JLabel label = new JLabel("Online Users");
	protected static JButton btnSend = new JButton("Send");
	protected static JTextArea chatArea = new JTextArea();
	
	
	private static BufferedReader in = null;
	private static PrintWriter os = null;
	private static Socket socket = null;
	
	private String clientId = null;


	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					ChatClient window = new ChatClient();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ChatClient() {
		
		initialize();
		
		try {
			this.startChat();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void initialize() {
	
		frame.setBounds(100, 100, 450, 300);
		frame.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent windowEvent){
				
				os.println("EXIT");
				
				
			}
			
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		springLayout.putConstraint(SpringLayout.NORTH, onlineUsers, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, onlineUsers, -112, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, onlineUsers, 215, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, onlineUsers, -10, SpringLayout.EAST, frame.getContentPane());
		onlineUsers.setEditable(false);
		onlineUsers.setBorder(UIManager.getBorder("TextField.border"));
		frame.getContentPane().add(onlineUsers);
		
		
		springLayout.putConstraint(SpringLayout.NORTH, label, 6, SpringLayout.SOUTH, onlineUsers);
		springLayout.putConstraint(SpringLayout.EAST, label, -29, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(label);
		
		
		springLayout.putConstraint(SpringLayout.WEST, messageArea, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, messageArea, 231, SpringLayout.WEST, frame.getContentPane());
		messageArea.setToolTipText("Send to messages Users");
		messageArea.setColumns(10);
		frame.getContentPane().add(messageArea);
		
		
		springLayout.putConstraint(SpringLayout.WEST, btnSend, 10, SpringLayout.EAST, messageArea);
		springLayout.putConstraint(SpringLayout.SOUTH, btnSend, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, messageArea, 1, SpringLayout.NORTH, btnSend);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				os.println(messageArea.getText());			
				messageArea.setText("");
				
			}
		});
		frame.getContentPane().add(btnSend);
		
		
		springLayout.putConstraint(SpringLayout.NORTH, chatArea, 0, SpringLayout.NORTH, onlineUsers);
		springLayout.putConstraint(SpringLayout.WEST, chatArea, 0, SpringLayout.WEST, messageArea);
		springLayout.putConstraint(SpringLayout.SOUTH, chatArea, 0, SpringLayout.SOUTH, onlineUsers);
		springLayout.putConstraint(SpringLayout.EAST, chatArea, -6, SpringLayout.WEST, onlineUsers);
		chatArea.setEditable(false);
		chatArea.setBorder(UIManager.getBorder("TextField.border"));
		frame.getContentPane().add(chatArea);
	}

	
	public void startChat() {
			
		try {
			this.socket = new Socket("localhost",5000);
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream())); frame.dispose();
			this.os = new PrintWriter(this.socket.getOutputStream(),true); 	
			
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					String str;
					try {
						
						str = in.readLine();
						clientId = str;
						frame.setTitle(clientId);
						
						while(true){
							
							str = in.readLine();

							if(str.equals("FIRE "+clientId)){
								
								os.println("EXIT");			
								os.close();
								in.close();
								frame.dispose();
								
							}else{
								
								chatArea.append(str + "\n");
															
							}
							
							
				}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						chatArea.append("Server Crashed or Connection Lost");
					}
					
					
					
				}
				
				
			}).start();		

			
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			chatArea.append("Server Crashed or Connection Lost");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			chatArea.append("Server Crashed or Connection Lost");
		}
		

		
		
	}
	
}
