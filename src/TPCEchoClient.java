import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class TPCEchoClient {
	
	public static void main(String[] args) throws IOException{
		
		String server; 
		InetAddress serverAddress;
		int serverPort;
		Scanner fromKeyboard = new Scanner(System.in);
		
		System.out.println("Creating a client. ");
		System.out.println("Enter computer name or IP address: ");
		server = fromKeyboard.nextLine();
		serverAddress = InetAddress.getByName(server);
		
		System.out.println("Enter port number: ");
		serverPort = Integer.parseInt(fromKeyboard.nextLine());
		
		// check user gave a valid port number
		try {
			if(serverPort <= 0 || serverPort > 65535) {
				throw new NumberFormatException();
			}
		}
		
		catch(NumberFormatException e){
			System.out.println("Illegal port number, " + serverPort);
			return;
		}
		
		String clientInput, name;
		Socket socket = null;
		
		try {
			socket = new Socket(serverAddress, serverPort);
			echoData toSend, toReceive = null;
			
			System.out.println("Enter your name: ");
			name = fromKeyboard.nextLine();
			
			System.out.println("Hi " + name + ", welcome to the guessing game.");
			System.out.println("To play, guess a number between 1 and 50. The game ends when you guess the correct number.");
			
			while (true) {
					
				String guess = fromKeyboard.nextLine();
				
				// we are triggering the reset functionality
				if(guess.contains("yes") || guess.contains("no")) {
					
					toSend = new echoData(name); // create new data object to send to echo server
					toSend.setPlayAgain(true, guess);
					
					sendEchoToServer(socket, toSend);
					
					toReceive = recieveEchoFromServer(socket);
					System.out.println("Server: " + toReceive.getMessage());
					
					if(toReceive.isClosingConnection()) {
						break;
					}
					
				}
				
				else {
					int userGuess = Integer.parseInt(guess);
					toSend = new echoData(name); // create new data object to send to echo server
					toSend.setGuess(userGuess);
					
					sendEchoToServer(socket, toSend);

					// NOW Receive an object back from the server
					toReceive = recieveEchoFromServer(socket);
					
					
					if (toReceive != null) {
						
						System.out.println("Server: " + toReceive.getMessage());
						
						if(toReceive.isClosingConnection()) {
							System.out.println("Server has closed connection. Goodbye!");
							break;
						}
						
					}
				}
				
			}
			
			socket.close(); // Close the client socket and its streams	
		}
		
		catch(ConnectException e) {
			System.out.println("Connection refused, probably no server running ");
		}
		
	}
	
	public static void sendEchoToServer(Socket clntSock, echoData toSend) throws IOException {
		try {
			OutputStream os = clntSock.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			// notice the need to create a new object to send
			oos.writeObject(toSend);

		} catch (EOFException e) { // needed to catch when client is done
			System.out.println("in Send EOFException: goodbye client at " + clntSock.getRemoteSocketAddress()
					+ " with port# " + clntSock.getPort());
			clntSock.close(); // Close the socket. We are done with this client!
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("in Send IOException: goodbye client at " + clntSock.getRemoteSocketAddress()
					+ " with port# " + clntSock.getPort());
			clntSock.close(); // this requires the throws IOException
		}
	}

	public static echoData recieveEchoFromServer(Socket clntSock) throws IOException {
		// client transport and network info
		SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
		int port = clntSock.getPort();

		// client object
		echoData fromClient = null;

		try {
			InputStream is = clntSock.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			fromClient = (echoData) ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EOFException e) { // needed to catch when client is done
			System.out.println("in receive EOF: goodbye client at " + clientAddress + " with port# " + port);
			clntSock.close(); // Close the socket. We are done with this client!
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("in receive IO: goodbye client at " + clientAddress + " with port# " + port);
			clntSock.close(); // this requires the throws IOException
		}
		return fromClient;

	}
	
}
