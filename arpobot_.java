package arpobot;

import java.io.*;
import java.net.*;

class irc
{
	private String commando;
	private String server;
	private int port;
	private Socket socket;
	private BufferedWriter skriv;
	private BufferedReader laes;
	
	public enum LogLevel {DEBUG, USERINF, CTCP, CHAN, PRIV};

	public void pass (String pass) throws Exception
	{
		commando = "PASS " + pass + "\r\n";
		execute(commando, LogLevel.USERINF);
	}
	public void nick (String nickname) throws Exception
	{
		commando = "NICK " + nickname + "\r\n";
		execute(commando, LogLevel.USERINF);
	}
	public void user (String username, String realname) throws Exception
	{
		String mode = "8";
		commando = "USER " + username + " " + mode + " * " + " : " + realname + "\r\n";
		execute(commando, LogLevel.USERINF);
	}
	public void pong (String pong) throws Exception
	{
		commando = "PONG " + pong.substring(5)+ "\r\n";
		execute(commando, LogLevel.DEBUG);
	}
	public void join (String join, String key) throws Exception
	{
		if (!join.startsWith("#"))
			join = "#" + join;
		commando = "JOIN " + join + " " + key + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void join (String join) throws Exception
	{
		if (!join.startsWith("#"))
			join = "#" + join;
		commando = "JOIN " + join + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void msg (String receiver, String text) throws Exception
	{
		msg(receiver, text, LogLevel.PRIV);
	}
	
	public void ctcpRequest (String receiver, String text) throws Exception
	{
		msg(receiver, '\u0001'+text+'\u0001', LogLevel.CTCP);
	}
	
	private void msg (String receiver, String text, LogLevel logLevel) throws Exception
	{
		commando = "PRIVMSG " + receiver + " :" + text + "\r\n";
		execute(commando, logLevel);
	}
	
	public void notice (String receiver, String text) throws Exception
	{
		notice(receiver, text, LogLevel.PRIV);
	}
	
	public void ctcpReply (String receiver, String text) throws Exception
	{
		notice(receiver, '\u0001'+text+'\u0001', LogLevel.CTCP);
	}	
	
	private void notice (String receiver, String text, LogLevel logLevel) throws Exception
	{
		commando = "NOTICE " + receiver + " :" + text + "\r\n";
		execute(commando, logLevel);
	}



/******/
	private void execute(String commando, LogLevel logLevel) throws Exception
	{
		this.skriv.write(commando);
		this.skriv.flush();
		
		System.out.print("<-- "+commando);
	}
	public void connect() throws Exception
	{
		// Connecter til IRC serveren.
		this.socket = new Socket(server, port);
		this.skriv = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		this.laes = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	}
	public void setServer(String server)
	{
		this.server = server;
		this.port = 6667;
	}
	public void setServer(String server,int port)
	{
		this.server = server;
		this.port = port;
	}
	public String getLine() throws Exception
	{
		return this.laes.readLine();
	}
}

public class Arpobot
{
	public static void main(String[] args) throws Exception
	{
		irc bot =  new irc();
		String nick,altnick;
		String server,servername = null;
		int port;
		int code;
		String linje;
		String skrivning;
		String kanal;
		int i;
		String sendernick;
		String version = "arpobot svn $Revision$ $Date$";

		nick = "ArvoXbot";
		altnick = "ArvoXbotAlpha";
		kanal = "#ArvoX";

		server = "dk.quakenet.org";
		port = 6667;

		bot.setServer(server , port);
		bot.connect();

		System.out.println(nick +"@"+server+":"+port);


		while ((linje = bot.getLine()) != null)
		{
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
*/
			if (linje.indexOf("372 "+nick) < 0)
				System.out.println("--> "+linje);
			if (linje.indexOf("AUTH :*** Checking Ident") >= 0)
			{
	// Saetter nick and username
				bot.nick(nick);
				bot.user(nick,nick);
			}
	// nick er i brug proever et andet
			if (linje.indexOf("433 * "+nick) >= 0)
			{
				bot.nick(altnick);
				bot.user(nick,nick);
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
			if ((linje.indexOf("PRIVMSG "+nick+" :") >= 0) || (linje.indexOf("PRIVMSG "+altnick+" :") >= 0))
			{
				i = linje.indexOf("!");
				sendernick = linje.substring(1,i);
			//version
				if (linje.toLowerCase().indexOf("version") >= 0)
				{
					bot.notice(sendernick,"Jeg er i "+ version +" version");
					//skrivning = "NOTICE "+sendernick+" :Jeg er i "+ version +" version\r\n";
					//skriv.write(skrivning);
					//System.out.print("<-- "+skrivning);
					//skriv.flush();
				}
			}
		}
	}
}