package irc.command;

import java.util.*;

public class IrcCommand
{
	private static HashMap<String, IrcCommand> commands = new HashMap<String, IrcCommand>();
	
	private String fullCommand, commandName, sender;

	//Skal kaldes i en static blok i alle underklasser der tilhoerer en eller flere bestemte kommandoer
	public static void AddCommand(IrcCommand cmd, String name)
	{
		commands.put(name, cmd);
	}
	
	public String getCommandName()
	{
		return commandName;
	}
	
	public String getFullCommand()
	{
		return fullCommand;
	}

	public String getSender()
	{
		return sender;
	}
	
	public String toString()
	{
		return fullCommand;
	}
	
	public static IrcCommand parse(String cmdstr)
	{
		//TODO: Implementer :)
		return null;
	}

}