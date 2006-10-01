package irc.command;

import java.lang.reflect.*;
import java.util.*;

public class IrcCommand
{
	private static HashMap<String, Class<IrcCommand>> commands = new HashMap<String, Class<IrcCommand>>();
	
	private String fullCommand, commandName, nick, user, hostOrNick, paramsStr;

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
	
	public String getParamsStr()
	{
		return paramsStr;
	}
	
	public String toString()
	{
		return fullCommand;
	}
	
	protected IrcCommand() //default konstruktoren skal ikke kunne kaldes udefra
	{
	}
	
	//Alle underklasser skal implementere denne
	protected IrcCommand(String pFullCommand, String command, String[] prefix, String params)
	{
		fullCommand = pFullCommand;
		commandName = command;
		if ((prefix != null) && (prefix.length == 3))
		{
			hostOrNick = prefix[0];
			nick = prefix[1];
			user = prefix[2];
		}
		paramsStr = params;
	}
	
	public static IrcCommand parse(String cmdstr) throws 
		NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
	/*
    message    =  [ ":" prefix SPACE ] command [ params ] crlf
    prefix     =  servername / ( nickname [ [ "!" user ] "@" host ] )
    command    =  1*letter / 3digit
    params     =  *14( SPACE middle ) [ SPACE ":" trailing ]
               =/ 14( SPACE middle ) [ SPACE [ ":" ] trailing ]

    nospcrlfcl =  %x01-09 / %x0B-0C / %x0E-1F / %x21-39 / %x3B-FF
                    ; any octet except NUL, CR, LF, " " and ":"
    middle     =  nospcrlfcl *( ":" / nospcrlfcl )
    trailing   =  *( ":" / " " / nospcrlfcl )

    SPACE      =  %x20        ; space character
    crlf       =  %x0D %x0A   ; "carriage return" "linefeed"
	*/
		int i;
		String[] prefixInf = null;
		String fullCommand, commandName, params;
		
		if ((cmdstr == null) || (cmdstr.length() == 0))
			return null;
		
		fullCommand = cmdstr;
		
		if (cmdstr.charAt(0) == ':') //der er et praefiks
		{
			i = cmdstr.indexOf(' ');
			
			if ((i == -1) || (cmdstr.length() == i+1)) //burde være falsk - kommandonavnet skal staa efter praefikset
				return new IrcCommand(cmdstr, null, null, null);
				
			prefixInf = parsePrefix(cmdstr.substring(1, i));
			
			cmdstr = cmdstr.substring(i+1);
		}
		
		//finder kommandonavnet
		i = cmdstr.indexOf(' ');
		if ((i == -1) || (cmdstr.length() == i+1)) //ingen parametre - ingenting der skal parses specifikt
		{
			commandName = cmdstr;
			params = null;
		}
		else
		{
			commandName = cmdstr.substring(0, i);
			params = cmdstr.substring(i+1);
		}
		
		//laver en instans af den rigtige klasse
		Class<IrcCommand> commandClass = commands.get(commandName);
		if (commandClass == null) //der er ikke registreret nogen klasse
			return new IrcCommand(cmdstr, commandName, prefixInf, params);
		//finder konstruktoren
		//String pFullCommand, String command, String[] prefix, String params
		Constructor<IrcCommand> constructor = commandClass.getConstructor(String.class, String.class, String[].class, String.class);
		return constructor.newInstance(fullCommand, commandName, prefixInf, params);
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
		nick = prefix.substring(0, i); //nick indeholder enten nickname eller nickname!user
		i = nick.indexOf('!');
		if (i == -1)
			return new String[]{host, nick, null};
		user = nick.substring(i+1);
		nick = nick.substring(0, i);
		
		return new String[]{host, nick, user};
	}
}