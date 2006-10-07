package irc;

import java.io.*;
import java.net.*;
import irc.command.*;

public class IrcClient
{
	private String commando;
	private String server;
	private int port;
	private Socket socket;
	private BufferedWriter skriv;
	private BufferedReader laes;

	public enum LogLevel {DEBUG, MOTD, UNK, CONN, USERINF, CTCP, CHAN, PRIV};

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
	public void oper (String user,String password) throws Exception
	{
		commando = "OPER " + user + " " + password + "\r\n";
		execute(commando, LogLevel.USERINF);
	}
	public void QUIT () throws Exception
	{
		commando = "QUIT\r\n";
		execute(commando, LogLevel.CONN);
	}
	public void QUIT (String message) throws Exception
	{
		commando = "QUIT " + message + "\r\n";
		execute(commando, LogLevel.CONN);
	}
	public void join (String channel, String key) throws Exception
	{
		channel = checkChannel(channel);
		commando = "JOIN " + channel + " " + key + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void join (String channel) throws Exception
	{
		channel = checkChannel(channel);
		commando = "JOIN " + channel + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void part (String channel) throws Exception
	{
		channel = checkChannel(channel);
		commando = "PART " + channel + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void mode (String channel, String mode) throws Exception
	{
		channel = checkChannel(channel);
		commando = "MODE " + channel + " " + mode + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void mode (String channel, String mode, int limet) throws Exception
	{
		channel = checkChannel(channel);
		commando = "MODE " + channel + " " + mode + " " + limet + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void mode (String channel, String mode, String userOrMaskOrPass) throws Exception
	{
		channel = checkChannel(channel);
		commando = "MODE " + channel + " " + mode + " " + userOrMaskOrPass + "\r\n";
		execute(commando, LogLevel.CHAN);
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
//names skal vaere her
//list skal vaere her
	public void invite (String nick, String channel) throws Exception
	{
		channel = checkChannel(channel);
		commando = "INVITE " + nick + " " + channel + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void kick (String user, String channel) throws Exception
	{
		channel = checkChannel(channel);
		commando = "KICK " + channel + " " + user + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
	public void kick (String user, String channel, String comment) throws Exception
	{
		channel = checkChannel(channel);
		commando = "KICK " + channel + " " + user + " :" + comment + "\r\n";
		execute(commando, LogLevel.CHAN);
	}
/*version
  time
  trace
  admin
  info
skal vaere her*/
	//PRIVMSG:
	public void msg (String receiver, String text) throws Exception
	{
		msg(receiver, text, LogLevel.PRIV);
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
	private void notice (String receiver, String text, LogLevel logLevel) throws Exception
	{
		commando = "NOTICE " + receiver + " :" + text + "\r\n";
		execute(commando, logLevel);
	}
/*who
  whois
  whowas
  kill
  ping
skal vaere her*/
	public void pong (String daemon) throws Exception
	{
		commando = "PONG :" + daemon + "\r\n";
		execute(commando, LogLevel.DEBUG);
	}
/*away
  rehash
  restart
  users
  userhost
  ison
skal vaere her*/

/**ctcp**/
	public void ctcpRequest (String receiver, String text) throws Exception
	{
		msg(receiver, '\u0001'+text+'\u0001', LogLevel.CTCP);
	}
	public void ctcpReply (String receiver, String text) throws Exception
	{
		notice(receiver, '\u0001'+text+'\u0001', LogLevel.CTCP);
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
	public void disconnect() throws Exception
	{
		this.socket.close();
	}
	public IrcClient (String server)
	{
		this.server = server;
		this.port = 6667;
	}
	public IrcClient (String server,int port)
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

	public IrcCommand getCommand() throws Throwable
	{
		return IrcCommand.parse(getLine());
	}

	private String checkChannel(String channel)
	{
		if (!channel.startsWith("#"))
			channel = "#" + channel;
		return channel;
	}
}
