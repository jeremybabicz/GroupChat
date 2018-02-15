import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class ChatServer 
{
	//global variables
	public static int portNumber;
	boolean keepRunning = true;
	
	ServerSocket serverSocket;
	Map<Socket, PrintWriter> socketMap;
	ExecutorService threads;
	
	public static void main (String [] args)
	{
		try
		{
			// Gets the port number from the command line
			portNumber = Integer.parseInt(args[0]);
		} 
		catch (Exception e) 
		{
			//User didn't enter the expected input
			System.out.println("Usage:");
			System.out.println("\tjava ChatServer <port number>");
            return;
		}

		ChatServer server = new ChatServer(portNumber);
		
		//have server continuously wait and accept clients
		server.acceptClient();
			
		//terminates threads
		server.endThreads();
			
	}

	//server constructor
	public ChatServer(int p)
	{
		try
		{
			//create HashMap containing clientSockets and their names
			this.socketMap = new HashMap<>();
			
			//create threads to handle clients
			this.threads = Executors.newFixedThreadPool(10);
			
			this.serverSocket = new ServerSocket(p);
		}
		catch(Exception e)
		{
			
		}
		
	}
	
	//server continuously waits and connects to clients
	public void acceptClient ()
	{
		try
		{
			System.out.println("Listening on port " + portNumber + "...");
			while(keepRunning)
			{
				Socket clientSocket = serverSocket.accept();
				
				PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
				
				//add clientSocket and PrintWriter info to HashMap
			    socketMap.put(clientSocket, output);
				//execute thread to handle client
				threads.submit(() -> handleClient(clientSocket));
			}
		}
		catch(Exception e)
		{
			
		}
	}
		 
	//reads data from clients and writes out data
	public void handleClient (Socket cSocket)
	{
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
			
			String inputLine, name = null;
			
			//prompt client to enter their username
			socketMap.get(cSocket).println("Please enter your name: ");
			
			while((inputLine = in.readLine()) != null)
			{
				//get name of client 
				if(name == null)
				{
					name = inputLine;
					
					//notifies server that a new client has connected
					System.out.println("Connected to " + name + "...");
					
					//notifies existing chat members that a new user has joined
					for(Map.Entry<Socket, PrintWriter> entry : socketMap.entrySet())
					{	
						/*checks to ensure the sender's message 
						isn't sent back to themselves*/
						if(!(entry.getKey() == cSocket))
							entry.getValue().println(name + " has joined chat...");	
					}
				}
				else
				{
					//sends sender's message to all clients in chat
					for(Map.Entry<Socket, PrintWriter> entry : socketMap.entrySet())
					{
						if(!(entry.getKey() == cSocket))
							entry.getValue().println(name + ": " + inputLine);	
					}
				}
			
		    }
			
			//notifies server that a client has disconnected
			System.out.println(name + " has disconnected ...");
			
			//notifies existing chat members that the client has left
			for(Map.Entry<Socket, PrintWriter> entry : socketMap.entrySet())
			{
					if(!(entry.getKey() == cSocket))
						entry.getValue().println(name + " has left chat...");	
			}
			
			//removes socket from map once standard input closes
			socketMap.remove(cSocket);
		   }
	
		catch(Exception e){};
	}
	
	//terminates threads 
	public void endThreads ()
	{
		threads.shutdownNow();
	}
}