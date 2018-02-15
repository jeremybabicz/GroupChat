import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ChatClient 
{
	public static int portNumber;
	
	Socket clientSocket;
	PrintWriter myOutput;
	BufferedReader in;
	BufferedReader stdIn;
	
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
			System.out.println("\tjava ChatClient <port number>");
            return;
		}
		ChatClient client = new ChatClient(portNumber);
		
		//have threads execute functions
		client.execute();
	}
	//ChatClient constructor
	public ChatClient (int p)
	{
		portNumber = p;
		try
		{
			this.clientSocket = new Socket("localhost", portNumber);
			this.myOutput = new PrintWriter(clientSocket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			this.stdIn = new BufferedReader(new InputStreamReader(System.in));
		}
		catch(Exception e)
		{
			
		}
	}
	
	//execute threads
	public void execute ()
	{
		//create threads
		ExecutorService threads = Executors.newFixedThreadPool(2);
		
		//threads handle reading and writing to server 
		threads.submit(this::read);
		threads.submit(this::write);
		
		//terminate threads
		threads.shutdown();
	}
	
	//reads in data from Server
	public void read ()
	{
		try
		{
			String inputLine;
			while((inputLine = in.readLine()) != null)
			{
				System.out.println(inputLine);	
			}
			close();
		}
	
		catch(Exception e){};
	}

	//writes out to the Server
	public void write ()
	{
		try
		{
			
			String outputLine;
			while((outputLine = stdIn.readLine()) != null)
			{
				myOutput.println(outputLine);
			}
			close();
		}
	
		catch(Exception e){};
	}
	
	//close Client Socket
	public void close ()
	{
		try
		{
			clientSocket.shutdownOutput();
			clientSocket.close();
			System.exit(0);
		}
		catch(Exception e)
		{

		}
	}
}