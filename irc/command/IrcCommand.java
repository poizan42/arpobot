package irc.command;

import java.util.*;

public class IrcCommand
{
	private static HashMap<String, Class<IrcCommand>> commands = new HashMap<String, Class<IrcCommand>>();
	
	private String fullCommand, commandName, nick, user, hostOrNick, parameterStr;

	//Skal kaldes i en static blok i alle underklasser der tilhoerer en eller flere bestemte kommandoer
	public static void AddCommand(Class<IrcCommand> cmd, String name)
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

	public String getNick()
	{
		return nick;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public String getHostOrNick()
	{
		return hostOrNick;
	}
	
	public String getParameterStr()
	{
		return parameterStr;
	}
	
	public String toString()
	{
		return fullCommand;
	}
	
	protected void doParse(String params)
	{
	}
	
	public static IrcCommand parse(String cmdstr)
	{
		//TODO: Implementer :)
		return null;
	}

	/*Parser prefix. Output: 
	0: host/server/nick
	1: nick
	2: user
	*/
	public static String[] parsePrefix(String prefix)
	{
		//prefix     =  servername / ( nickname [ [ "!" user ] "@" host ] )
		int i;
		String nick, user, host;
		
		i = prefix.indexOf('@');
		if (i == -1) // intet @ saa er det enten kun server eller kun nick
			return new String[]{prefix, null, null};
		
		host = prefix.substring(i+1);
		nick = prefix.substring(0, i-1); //nick indeholder enten nickname eller nickname!user
		i = nick.indexOf('!');
		if (i == -1)
			return new String[]{host, nick, null};
		user = nick.substring(i+1);
		nick = nick.substring(0, i-1);
		
		return new String[]{host, nick, user};
	}
}