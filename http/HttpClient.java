package http;

import java.io.*;
import java.net.*;

public class httpClient
{
	private String host;
	private int port;
	private int i;
	Socket forbindelse;
	OutputStream binærUd;
	InputStream binærInd;
	PrintWriter ud;
	BufferedReader ind;

	private void setHost(String host) throws Exception
	{
		i = host.indexOf("://");
		if (i >= 0)
		{
			host = host.substring(i+3);
		}
		i = host.indexOf(":");
		if (i >= 0)
		{
			this.port = Integer.parseInt(host.substring(i+1));
			host = host.substring(0, i);
		}
		else
		{
			this.port = 80;
		}
		this.host = host;
		connect();
	}

	private void setHost(String host, int port) throws Exception
	{
		i = host.indexOf("://");
		if (i >= 0)
		{
			host = host.substring(i+3);
		}
		this.host = host;
		this.port = port;
		connect();
	}
	private void connect() throws Exception
	{
		forbindelse = new Socket(this.host,this.port);
		binærUd  = forbindelse.getOutputStream();
		binærInd = forbindelse.getInputStream();
		ud  = new PrintWriter(binærUd);
		ind = new BufferedReader(new InputStreamReader(binærInd));
	}
	private void httpGET(String request)
	{
		if (!request.startsWith("/"))
			request = "/"+request;
		ud.println("GET "+ request +" HTTP/1.1");
		ud.println("Host: "+this.host);
		ud.println();
		ud.flush();

	}

	public void getWord() throws Exception
	{
		setHost("word.arvox.dk",80);
		httpGET("/?word&return");

		String s = ind.readLine();
		while (s != null)
		{
			if (s.startsWith(":"))
				System.out.println(s.substring(1));
			s = ind.readLine();
		}
		forbindelse.close();
	}

	public void google(String search) throws Exception
	{
		setHost("www.google.com",80);
		httpGET("/search?q=" + search + "&btnI=true");

		String s;
		do
		{
			s = ind.readLine();
			if (s.startsWith("Location:"))
			{
				System.out.println(s.substring(10));
				forbindelse.close();
				break;
				// dette er gjordt for at forhindre scriptet i at haenge!!!
			}
		}
		while (s != null);
	}
}