import javax.xml.parsers.*;
import org.w3c.dom.*;
import irc.*;
import irc.command.*;

public class Arpobot
{
	final static String version = "svn $Revision$ $Date$";

	String server;
	int port;

	String username,realname,channel,nick1,nick2;
	String onConnectCmd;
	Document settingsdoc;
	Element settingselem;
	NodeList nicks;

	public static void main(String[] args) throws Throwable
	{
		(new Arpobot()).run("arpobot.conf");
	}

	public Arpobot()
	{
	}

	private void loadConfig(String filename) throws Throwable
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		settingsdoc = builder.parse(filename);
		settingselem = settingsdoc.getDocumentElement();
		Element consettings = (Element)settingselem.getElementsByTagName("connection").item(0);
		NodeList servers = ((Element)consettings.getElementsByTagName("network").item(0)).getElementsByTagName("server");
		Element serverElem = ((Element)servers.item(0));
		server = serverElem.getAttributeNode("host").getValue();
		port = Integer.parseInt(serverElem.getAttributeNode("port").getValue());
		nicks = consettings.getElementsByTagName("nick");
		nick1 = nicks.item(0).getTextContent();
		nick2 = nicks.item(1).getTextContent();
		username = consettings.getElementsByTagName("username").item(0).getTextContent();
		realname = consettings.getElementsByTagName("realname").item(0).getTextContent();
		channel = consettings.getElementsByTagName("channel").item(0).getTextContent();
		onConnectCmd = consettings.getElementsByTagName("onConnectCmd").item(0).getTextContent();
	}

	public void run(String configfilepath) throws Throwable
	{
		String nick = "";
		String servername = null;
		int code;
		IrcCommand cmd;
		String skrivning;
		int i;
		String sendernick;
		IrcClient.LogLevel inll;

		loadConfig(configfilepath);

		IrcClient bot =  new IrcClient(server, port);
		bot.connect();

		System.out.println(nick1 +"@"+server+":"+port);

		while (true)
		{
			cmd = bot.getCommand();
			if (cmd == null)
				continue;
			String cmdName = cmd.commandName;
			if (cmdName.equals(IrcNumerics.RPL_MOTD))
				continue; //ignore the MOTD for now

			/*System.out.println("kommando: "+cmd.getCommandName());
			System.out.println("nick el. server: "+cmd.getNickOrServer());
			System.out.println("user: "+cmd.getUser());
			System.out.println("host: "+cmd.getHost());
			System.out.println("params: "+cmd.getParamsStr());
			for (String s : cmd.getParameters())
			{
				System.out.println("parameter: "+s);
			}
			System.out.println();*/

			if (cmdName.equals("NOTICE") && (((MsgCommand)cmd).receiver.equals("AUTH")) && (((MsgCommand)cmd).message.equals("*** Checking Ident")))
			{
				// Set nick and username
				inll = IrcClient.LogLevel.CONN;
				bot.nick(nick1);
				bot.user(username,realname);
			}
			else if (cmdName.equals(IrcNumerics.ERR_NICKNAMEINUSE))
			{
				inll = IrcClient.LogLevel.CONN;
				bot.nick(nick2);
				nick = nick2;
			}
			else if (cmdName.equals("PING"))
			{
				inll = IrcClient.LogLevel.DEBUG;
				bot.pong(cmd.parameters[0]);
			}
			else if (cmdName.equals(IrcNumerics.RPL_WELCOME))
			{
				inll = IrcClient.LogLevel.CONN;
				bot.execute(onConnectCmd+"\r\n", IrcClient.LogLevel.CONN);
				bot.join(channel);
			}
			else if (cmd instanceof MsgCommand)
			{
				MsgCommand msgcmd = (MsgCommand)cmd;
				if (msgcmd.receiver.equals(nick))
					inll = IrcClient.LogLevel.PRIV;
				else
					inll = IrcClient.LogLevel.CHAN;
				if ((msgcmd.message.length() > 0) && (msgcmd.message.charAt(0) == '!'))
				{
					if (msgcmd.message.toLowerCase().equals("!version"))
						bot.notice(msgcmd.sender, "Arpobot "+version);
				}
				else if (msgcmd.isCtcp)
				{
					if (msgcmd.ctcpMessage.equals("VERSION"))
						bot.ctcpReply(msgcmd.sender, "VERSION Arpobot "+version);
				}
			}
			else
				 inll = IrcClient.LogLevel.UNK;

			System.out.println("--> ("+inll.toString()+") "+cmd.fullCommand);
		}
	}
}