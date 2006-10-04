import irc.*;
import irc.command.*;

public class Arpobot
{
	final static String server = "irc.hyggenet.org";
	final static int port = 6668;

	final static String username = "Arpobot";
	final static String realname = "Arpobot";
	final static String nick1 = "Arpobot";
	final static String nick2 = "Arpobot2";
	final static String kanal = "#Arpobot";
	final static String topic = "Arpobot udviklingskanal";

	final static String version = "svn $Revision$ $Date$";

	public static void main(String[] args) throws Throwable
	{

		String nick = nick1;
		String servername = null;
		int code;
		IrcCommand cmd;
		String skrivning;
		int i;
		String sendernick;

		IrcClient bot =  new IrcClient(server , port);
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

			System.out.println("--> "+cmd.fullCommand);
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
				bot.nick(nick1);
				bot.user(username,realname);
			}
			else if (cmdName.equals(IrcNumerics.ERR_NICKNAMEINUSE))
			{
				bot.nick(nick2);
				nick = nick2;
			}
			else if (cmdName.equals("PING"))
			{
				bot.pong(cmd.parameters[0]);
			}
			else if (cmdName.equals(IrcNumerics.RPL_WELCOME))
			{
				bot.join(kanal);
				bot.msg("NickServ", "IDENTIFY QtD6JXt8");
			}
			else if (cmd instanceof MsgCommand)
			{
				MsgCommand msgcmd = (MsgCommand)cmd;
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
		}
	}
}