import irc.IrcClient;
import irc.command.*;
import http.HttpClient;
public class Arpobot
{
	final static String server = "dk.quakenet.org";
	final static int port = 6667;

	final static String username = "ArvoXbot";
	final static String realname = "ArvoXbot";
	final static String nick1 = "ArvoXbot-inputparse";
	final static String nick2 = "ArvoXbot-inputparse2";
	final static String kanal = "#ArvoX";
	final static String topic = "ArvoX private channel: #ArvoX -- Nu med egen bot fra dnttah -- http://word.arvox.dk :)";

	final static String version = "svn $Revision: 31 $ $Date: 2006-10-01 22:02:59 +0200 (sø, 01 okt 2006) $";
	String nick;

	public static void main(String[] args) throws Throwable
	{

		String nick = null;
		String servername = null;
		int code;
		IrcCommand kommando;
		String skrivning;
		int i;
		String sendernick;

		HttpClient http = new HttpClient();
		IrcClient bot =  new IrcClient(server , port);
		//bot.setServer(server , port);
		bot.connect();

		System.out.println(nick1 +"@"+server+":"+port);


		while (true)
		{
		kommando = bot.getCommand();
		if (kommando == null)
			continue;
		if (kommando.getCommandName().equals("372"))
			continue; //gider vi ikke se på
		
		System.out.println("kommando: "+kommando.getCommandName());
		System.out.println("nick: "+kommando.getNick());
		System.out.println("user: "+kommando.getUser());
		System.out.println("host: "+kommando.getHostOrNick());
		System.out.println("params: "+kommando.getParamsStr());
		for (String s : kommando.getParameters())
		{
			System.out.println("parameter: "+s);
		}
		System.out.println();
		
		if (kommando.getCommandName().equals("NOTICE") && kommando.getParamsStr().equals("AUTH :*** Checking Ident"))
		{
			// Saetter nick and username
			bot.nick(nick1);
			bot.user(username,realname);
			nick = nick1;
		}
		
		if (kommando.getCommandName().equals("PING"))
		{
			bot.pong(kommando.getParamsStr());
		}
		
		if (kommando.getCommandName().equals("001"))
		{
			bot.join(kanal);
			bot.msg("Q@CServe.quakenet.org", "AUTH ArvoXbot QtD6JXt8");
		}
		
/*	//finder servernavn
		if (linje.startsWith(":") && servername == null)
			{
				i=linje.indexOf(" ");
				servername = linje.substring( 1 , i );
			}
			if (linje.startsWith(":"+servername) && servername != null)
			{
				try
				{
					i = linje.indexOf(" ") + 1;
					String tmp = linje.substring(i,i+3);
					code = Integer.parseInt(tmp);
					System.out.println(code);
				}
				catch (Exception error)
				{}
			}
*//*
			if (linje.indexOf("372 "+nick) < 0)
				System.out.println("--> "+linje);
			if (linje.indexOf("AUTH :*** Checking Ident") >= 0)
			{
	// Saetter nick and username
				bot.nick(nick1);
				bot.user(nick1,nick1);
				nick = nick1;
			}
	// nick er i brug proever et andet
			if (linje.indexOf("433 * "+nick1) >= 0)
			{
				bot.nick(nick2);
				bot.user(nick1,nick1);
				nick = nick2;
			}
	//ponger paa ping
			if (linje.toUpperCase().startsWith("PING "))
			{
				bot.pong(linje);
			}
	//Joiner kanal + auth'er
			if (linje.indexOf("376 "+nick) >= 0)
			{
				bot.join(kanal);
				bot.msg("Q@CServe.quakenet.org", "AUTH ArvoXbot QtD6JXt8");
			}
	//svare paa beskeder
			if (linje.indexOf("PRIVMSG "+nick+" :") >= 0)
			{
				i = linje.indexOf("!");
				sendernick = linje.substring(1,i);
			//version
				if (linje.toLowerCase().indexOf("version") >= 0)
				{
					bot.notice(sendernick,"Jeg er i "+ version);
				}
			}
			if (linje.indexOf("TOPIC "+kanal+" :") >= 0)
			{
				i = linje.indexOf(" :");
				String _topic = linje.substring(i+2);
				if (!_topic.equals(topic))
				{
					bot.topic(kanal, topic);
				}
			}
			if (linje.indexOf("!word") >= 0)
			{
				String sender;
				i = linje.indexOf("!");
				if (i >= 0)
				{
					sender = linje.substring(1,i);
					bot.notice(sender,http.getWord());
				}
			}
			if (linje.indexOf("!google") >= 0)
			{
				String sender,search;
				i = linje.indexOf("!google");
				search = linje.substring(i+8);
				i = linje.indexOf("!");
				if (i >= 0)
				{
					sender = linje.substring(1,i);
					bot.notice(sender,http.google(search));
				}
			}*/

		}
	}
}