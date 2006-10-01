import irc.IrcClient;
public class Arpobot
{
	final static String server = "dk.quakenet.org";
	final static int port = 6667;

	final static String nick1 = "ArvoXbot";
	final static String nick2 = "ArvoXbotAlpha";
	final static String kanal = "#ArvoX";

	final static String version = "svn $Revision: 9 $ $Date: 2006-10-01 11:25:22 +0200 (sø, 01 okt 2006) $";
	String nick;

	public static void main(String[] args) throws Exception
	{

		String nick = null;
		String servername = null;
		int code;
		String linje;
		String skrivning;
		int i;
		String sendernick;

		IrcClient bot =  new IrcClient(server , port);
		//bot.setServer(server , port);
		bot.connect();

		System.out.println(nick1 +"@"+server+":"+port);


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
		}
	}
}