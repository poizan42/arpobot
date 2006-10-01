package irc.command;

import java.util;

public class IrcCommand
{
	private static HashTable<String, IrcCommand> commands = new HashSet<IrcCommand>;
	
	private String fullCommand, commandName, sender;

	//Skal kaldes i en static blok i alle underklasser der tilhører en eller flere bestemte kommandoer
	public static AddCommand(IrcCommand cmd, String name)
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
		
	}

}