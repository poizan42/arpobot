package irc.command;

public class MsgCommand extends IrcCommand
{
	public String message = "", receiver = "", sender, ctcpMessage;
	public boolean isCtcp;
	
	public MsgCommand(String pFullCommand, String command, String[] prefix, String params)
	{
		super(pFullCommand, command, prefix, params);
		sender = nickOrServer;
		if (parameters.length >= 1)
			receiver = parameters[0];
		if (parameters.length >= 2)
			message = parameters[1];
		
		//A CTCP message starts and ends with charcode 0x01
		if ((command.equals("PRIVMSG")) && (message.length() > 0) && (message.charAt(0) == 0x01) && (message.charAt(message.length()-1) == 0x01))
		{
			isCtcp = true;
			ctcpMessage = message.substring(1,message.length()-1);
		}
	}
}