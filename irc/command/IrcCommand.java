package irc.command;

import java.lang.reflect.*;
import java.util.*;

public class IrcCommand
{
	private static HashMap<String, Class<IrcCommand>> commands = new HashMap<String, Class<IrcCommand>>();
	
	private String fullCommand, commandName, nick = "", user = "", hostOrNick = "", paramsStr;
	private String[] parameters;

	//Skal kaldes i en static blok i alle underklasser der tilhoerer en eller flere bestemte kommandoer
	public static void AddCommand(Class<IrcCommand> cmd, String name)
	{
		commands.put(name, cmd);
	}
	
	static {
		AddCommand(IrcCommand.class, "PING");
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
	
	public String[] getParameters()
	{
		return parameters;
	}
	
	public String toString()
	{
		return fullCommand;
	}
	
	protected IrcCommand() //default konstruktoren skal ikke kunne kaldes udefra
	{
	}
	
	//Alle underklasser skal implementere denne
	public IrcCommand(String pFullCommand, String command, String[] prefix, String params)
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
		parameters = parseParams(params);
	}
	
	public static IrcCommand parse(String cmdstr) throws Throwable
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
				return new IrcCommand(cmdstr, "", null, "");
				
			prefixInf = parsePrefix(cmdstr.substring(1, i));
			
			cmdstr = cmdstr.substring(i+1);
		}
		
		//finder kommandonavnet
		i = cmdstr.indexOf(' ');
		if ((i == -1) || (cmdstr.length() == i+1)) //ingen parametre - ingenting der skal parses specifikt
		{
			commandName = cmdstr;
			params = "";
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
		try
		{
			Constructor<IrcCommand> constructor = commandClass.getConstructor(String.class, String.class, String[].class, String.class);
			return constructor.newInstance(fullCommand, commandName, prefixInf, params);
		}
		catch (InvocationTargetException e)
		{ 
			throw e.getCause();
		}
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
	
	public static String[] parseParams(String params)
	{
		int i, nextSpace;
		String S;
		
		if ((params == null) || (params.length() == 0) || (params.charAt(0) == ' '))
			return new String[]{}; //ingen parametre
		
		ArrayList<String> list = new ArrayList<String>();
		i = 0;
		
		while (i != -1)
		{
			if (params.charAt(i) == ':')
			{
				if (i == params.length() -1) //tjekker om der kommer noget efter kolonet
					S = "";
				else
					S = params.substring(i+1);
				i = -1;
			}
			else
			{
				nextSpace = params.indexOf(' ', i);
				if ((nextSpace == -1) ||(nextSpace == params.length() -1))
				{
					S = params.substring(i);
					i = -1;
				}
				else
				{
					S = params.substring(i, nextSpace);
					i = nextSpace+1;
				}
			}
			list.add(S);
		}
		
		return list.toArray(new String[]{});
	}
}