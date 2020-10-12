import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;
import java.net.*; // for Socket, ServerSocket, and InetAddress
import java.io.*; // for IOException and Input/OutputStream

public class TCPEchoServer {
	
	public static void main(String[] args) throws IOException {
		
		int serverPort;
		Scanner fromKeyboard = new Scanner(System.in);
		System.out.println("Creating a server.");
		System.out.println("Enter port number: ");
		serverPort = Integer.parseInt(fromKeyboard.nextLine());
		
		// create a server socket to accept client connection requests
		ServerSocket serverSock = new ServerSocket(serverPort);
		SocketAddress serverAddress = serverSock.getLocalSocketAddress();
		
		SocketAddress clientAddress; 
		int clientPort;
		echoData fromClient;
		
		System.out.println("Server running at address " + serverAddress + " waiting for connection from client...");
		
		// get a random number for user to guess and keep track number of tires 
		int guesses = 0;
		int TARGET_NUM = (int)(Math.random() * 50) + 1;
		
		System.out.println("Target number (only known by server): " + TARGET_NUM);
		
		// persistent connection on the part of the server 
		while(true) {
			
			// accept connection from a client 
			Socket clientSock = serverSock.accept();
			clientAddress = clientSock.getRemoteSocketAddress();
			clientPort = clientSock.getPort();
			
			System.out.println("Handling client at " + clientAddress + " with port #" + clientPort);
			
			String clientPrompt = "Welcome to the guessing game. Please guess the number 1-50.";
			
			while(true) {
	
				fromClient = receiveEchoFromClient(clientSock);
				
				if(fromClient == null) {
					break;
				}
				
				if(fromClient.getPlayAgain()) {
					
					String wantsToPlay = fromClient.getUserWantsToPlay();
					System.out.println("Wants to play: " + wantsToPlay);
					
					if(wantsToPlay.contains("yes")) {
						
						System.out.println("Triggering game reset on server side.");
						
						guesses = 0;
						TARGET_NUM = (int)(Math.random() * 50) + 1;
						System.out.println("Target number (only known by server): " + TARGET_NUM);
						
						clientPrompt = "Welcome to the guessing game. Please guess the number 1-50.";
						sendEchoBackToClient(clientSock, fromClient, clientPrompt, false);
						continue;
						
					}
					
					// user has ended the game
					else if(wantsToPlay.contains("no")) {
						clientPrompt = "Thanks for playing the game! Closing client connection";
						sendEchoBackToClient(clientSock, fromClient, clientPrompt, true);
						break;
					}
					
				}
				
				int guess = fromClient.getGuess();
				System.out.println("Guess from client: " + guess);
				
				// logic to evaluate number guessed by user 
				if(guess < TARGET_NUM) {
					guesses += 1;
					clientPrompt = "Guess is too low. Please guess again \nNumber of tries: " + guesses + "\nEnter Guess: ";
				}
				
				else if (guess > TARGET_NUM) {
					guesses += 1;
					clientPrompt = "Guess is too high. Please guess again \nNumber of tries: " + guesses + "\nEnter Guess: ";
				}
				
				// user has guessed the number
				else {
					guesses += 1;
					clientPrompt = "Correct! It took you " + guesses + " tries to guess the number. Would you like to play again? (please type 'yes' or 'no')";	
				}
			
				
				sendEchoBackToClient(clientSock, fromClient, clientPrompt, false);
				
			}
			System.out.println("Game has ended. Closing client connection and closing server.");
			serverSock.close();
			return;
		}
	}
	
	public static void sendEchoBackToClient(Socket clientSock, echoData fromClient, String toClientMessage, Boolean isClosing) throws IOException{
		echoData toClient;
		try {
			OutputStream os = clientSock.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			//notice the need to create a new object to send
			toClient = new echoData(fromClient.getName());
			toClient.setMessage(toClientMessage);
			toClient.setClosingConnection(isClosing);
			
			oos.writeObject(toClient);

		}  catch (EOFException e) { // needed to catch when client is done
			System.out.println("in Send EOFException: goodbye client at " + clientSock.getRemoteSocketAddress() + " with port # " + clientSock.getPort());
			clientSock.close(); // Close the socket. We are done with this client!
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("in Send IOException: goodbye client at " + clientSock.getRemoteSocketAddress() + " with port # " + clientSock.getPort());
			clientSock.close(); //this requires the throws IOException
		}
	}
	
	
	public static echoData receiveEchoFromClient(Socket clientSock) throws IOException{
		
		SocketAddress clientAddress = clientSock.getRemoteSocketAddress();
		int port = clientSock.getPort();
		
		// client object
		echoData fromClient = null;
		
		try {
			InputStream is = clientSock.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			fromClient = (echoData)ois.readObject();			
		}
		
		catch(ClassNotFoundException e){
			
		}
		
	    catch (EOFException e) { // needed to catch when client is done
			System.out.println("in receive EOF: goodbye client at " + clientAddress + " with port # " + port);
			clientSock.close(); // Close the socket. We are done with this client!
	    }

		catch (IOException e) {
			System.out.println("in receive IO: goodbye client at " + clientAddress + " with port # " + port);
			clientSock.close(); //this requires the throws IOException
		}
		
		return fromClient;
		
	}
	
	
}
