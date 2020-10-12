import java.io.Serializable;

public class echoData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String message;
	private int guess;
	private Boolean isClosingConnection;
	private Boolean playAgain = false;
	private String userWantsToPlay;
	
	public echoData() { // default constuctor
		
	}
	
	public echoData(String n)
	{
		this.name = n;
	}
	
	public String getName()
	{
		return name;	
	}
	
	public void setMessage(String messageToShow) {
		message = messageToShow;
	}
	
	public void setGuess(int guess)
	{
		this.guess = guess;
	}
	
	public void setName(String n)
	{
		this.name = n;	
	}
	
	public void setClosingConnection(Boolean connection) 
	{
		isClosingConnection = connection;
	}
	
	public void setPlayAgain(Boolean again, String wantsToPlay) 
	{
		playAgain = again;
		userWantsToPlay = wantsToPlay;
	}
	
	public Boolean isClosingConnection() 
	{
		return isClosingConnection;
	}
	
	public Boolean getPlayAgain()
	{
		return playAgain;
	}
	
	public String getUserWantsToPlay() {
		return userWantsToPlay;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public int getGuess() 
	{
		return guess;
	}
	
}
