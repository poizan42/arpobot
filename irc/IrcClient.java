package irc;

import java.io.*;
import java.net.*;

public class IrcClient
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
		join = checkChannel(join);
		commando = "JOIN " + join + " " + key + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void join (String join) throws Exception
	{
		join = checkChannel(join);
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

	public void topic (String channel) throws Exception
	{
		channel = checkChannel(channel);
		commando = "TOPIC "+channel+"\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void topic (String channel,String topic) throws Exception
	{
		channel = checkChannel(channel);
		commando = "TOPIC "+channel+" :"+topic+"\r\n";
		execute(commando, LogLevel.CHAN);
	}



/**Non irc command**/
	private void execute(String commando, LogLevel logLevel) throws Exception
	{
		this.skriv.write(commando);
		this.skriv.flush();

		System.out.print("<-- ("+logLevel.toString()+") "+commando);
	}
	public void connect() throws Exception
	{
		// Connecter til IRC serveren.
		this.socket = new Socket(server, port);
		this.skriv = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		this.laes = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	}

	public IrcClient (String server,int port)
	{
		this.server = server;
		this.port = 6667;
	}
	public IrcClient (String server)
	{
		this.server = server;
		this.port = port;
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

	private String checkChannel(String channel)
	{
		if (!channel.startsWith("#"))
			channel = "#" + channel;
		return channel;
	}
}
