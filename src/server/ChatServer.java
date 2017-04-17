package server;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;

import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ChatServer extends Thread {

	protected static JFrame frmServer = new JFrame();
	protected static JTextArea onlineUsers = new JTextArea();
	protected static JTextField commandArea = new JTextField();
	protected static JButton btnCommand = new JButton("Command");
	protected static JTextArea chatArea = new JTextArea();
	protected static JLabel lblNewLabel = new JLabel("Online Users");
	
	protected static BufferedReader is = null;
	protected static PrintWriter os = null;
	
	private ServerSocket ss; 
	private Socket socket;
	public static ArrayList<ClientHandler> listUser = new ArrayList<>();
	protected static boolean isNewUser = false;

	
	protected static int userCount = 0;
	
	
	public static void main(String[] args) {
		
				
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatServer window = new ChatServer();
					window.frmServer.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}


	public ChatServer() {
		
		initialize();
		
		try {
			ss = new ServerSocket(5000);	
			this.start();
			
			chatArea.setText("Server Started \n");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void initialize() {
			
		frmServer.setTitle("Chat Room");
		frmServer.setBounds(100, 100, 450, 300);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.setAlwaysOnTop(true);
		
		SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 221, SpringLayout.NORTH, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, onlineUsers, 10, SpringLayout.NORTH, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, onlineUsers, 322, SpringLayout.WEST, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, onlineUsers, -6, SpringLayout.NORTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.EAST, onlineUsers, -10, SpringLayout.EAST, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, chatArea, 0, SpringLayout.SOUTH, onlineUsers);
		springLayout.putConstraint(SpringLayout.EAST, chatArea, -6, SpringLayout.WEST, onlineUsers);
		springLayout.putConstraint(SpringLayout.EAST, commandArea, -6, SpringLayout.WEST, btnCommand);
		frmServer.getContentPane().setLayout(springLayout);
		onlineUsers.setEditable(false);
		onlineUsers.setBorder(UIManager.getBorder("TextField.border"));
		frmServer.getContentPane().add(onlineUsers);
		
		
		springLayout.putConstraint(SpringLayout.WEST, commandArea, 10, SpringLayout.WEST, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, commandArea, -10, SpringLayout.SOUTH, frmServer.getContentPane());
		commandArea.setToolTipText("Command to manage network");
		frmServer.getContentPane().add(commandArea);
		commandArea.setColumns(10);
		
		
		springLayout.putConstraint(SpringLayout.SOUTH, btnCommand, -8, SpringLayout.SOUTH, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnCommand, -118, SpringLayout.EAST, frmServer.getContentPane());
		btnCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	
				for (ClientHandler client : listUser) {
					
					if(commandArea.getText().endsWith(client.name)){
						
						client.os.println("FIRE "+client.name);
						
					}
						
				}
				
	
				commandArea.setText("");
				
			}
		});
		frmServer.getContentPane().add(btnCommand);
		chatArea.setEditable(false);
		springLayout.putConstraint(SpringLayout.WEST, chatArea, 10, SpringLayout.WEST, frmServer.getContentPane());
		chatArea.setBorder(UIManager.getBorder("TextField.border"));
		springLayout.putConstraint(SpringLayout.NORTH, chatArea, 10, SpringLayout.NORTH, frmServer.getContentPane());
		frmServer.getContentPane().add(chatArea);
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 27, SpringLayout.EAST, btnCommand);
		frmServer.getContentPane().add(lblNewLabel);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	
		while(true){

			try {
				socket = ss.accept();
				ClientHandler ch= new ClientHandler(socket);
				ch.name = "Person " + (++userCount);
				listUser.add(ch);
				chatArea.append(ch.name + " connected chat room.\n");
				
				isNewUser = true;	
				liveUsers(onlineUsers);					
				ch.start();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 
		
		
	}
	public static void liveUsers(JTextArea jtext){
		

		if(isNewUser){
						
			jtext.setText("");
						
			for (ClientHandler user : ChatServer.listUser) {
			
				jtext.append(" "+user.name+"\n");
							
							
				}
						
			isNewUser = false;
						
			}
	}
}

class ClientHandler extends Thread{
	
	private Socket socket = null;
	BufferedReader is = null;
	PrintWriter os = null;
	String name ;
	boolean isTerminated = false;
	
	public ClientHandler(Socket socket){
		
		this.socket = socket;
		
		
	}
	
	public void run(){
		
		try {
			
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream(),true);
			String message;
			
			os.println(this.name);
			
			while(!isTerminated){	
														 
					message = is.readLine(); 
					
					if(message.equals("EXIT")){
						
							ChatServer.chatArea.append(name+" "+"left from the chat room."+"\n");
							ChatServer.listUser.remove(this);
							is.close();
							os.close();
							ChatServer.isNewUser = true;
							ChatServer.liveUsers(ChatServer.onlineUsers);
							
		
							isTerminated = true;
							break;
			
					}
						
						
						
						ChatServer.chatArea.append(name+": "+message+"\n");
	
						for (ClientHandler sendMess : ChatServer.listUser) {
														
							sendMess.os.println(name + ":" + message);												
								
						}

						
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}


