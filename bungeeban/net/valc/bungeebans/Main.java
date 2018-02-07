package net.valc.bungeebans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.valc.bungeebans.commands.BanCommand;
import net.valc.bungeebans.commands.CheckCommand;
import net.valc.bungeebans.commands.MuteCommand;
import net.valc.bungeebans.commands.UnbanCommand;
import net.valc.bungeebans.commands.UnmuteCommand;
import net.valc.bungeebans.handler.ChatEvent;
import net.valc.bungeebans.handler.LoginEvent;
import net.valc.bungeebans.sql.MySQL;
import net.valc.bungeebans.sql.MySQL.MySQLCredentials;
import net.valc.bungeebans.utils.ConfigManager;

public class Main extends Plugin {
	
	private static ConfigManager configManager;
	public static Main sharedInstance = null;
	static MySQL mySQL;
	public static String host = "";
	public static String username = "";
	public static String password = "";
	public static String database = "";
	public static String PREFIX = "", CONSOLE_PREFIX = "";
	public static int port = 3306;
	
	public void onEnable() {
		sharedInstance = this;
		configManager = new ConfigManager();
		configManager.init();
		MySQLCredentials credentials = new MySQLCredentials(host, port, username, password, database);
		mySQL = new MySQL(credentials);
		mySQL.openConnection();
		if(mySQL.isConnected()) {
			logToConsole("�aMySQL connection success, creating tables..");
			mySQL.update("CREATE TABLE IF NOT EXISTS BungeeBan(Playername VARCHAR(16), banEnd LONG, banReason VARCHAR(256), banBy VARCHAR(16))");
			mySQL.update("CREATE TABLE IF NOT EXISTS BungeeMutes(Playername VARCHAR(16), muteEnd LONG, muteReason VARCHAR(256), muteBy VARCHAR(16))");
		}
		PluginManager pm = BungeeCord.getInstance().getPluginManager();
		pm.registerCommand(sharedInstance(), new UnbanCommand("mnunban"));
		pm.registerCommand(sharedInstance(), new UnmuteCommand("mnunmute"));
		pm.registerCommand(sharedInstance(), new MuteCommand("mnmute"));
		pm.registerCommand(sharedInstance(), new BanCommand("mnban"));
		pm.registerCommand(sharedInstance(), new CheckCommand("mncheck"));
		pm.registerListener(this, new ChatEvent());
		pm.registerListener(this, new LoginEvent());
	}
	
	@SuppressWarnings("deprecation")
	private void logToConsole(String string) {
		BungeeCord.getInstance().getConsole().sendMessage(string);
	}

	public static Main sharedInstance() {
		return sharedInstance;
	}
	
	public static MySQL getMySQL() {
		return mySQL;
	}
	
	public static ConfigManager getConfigManager() {
		return configManager;
	}
	
	public enum TimeUnit
	{
	  SECOND(new String[] { "s", "sec", "secs", "second", "seconds" }, 1L),  MINUTE(new String[] { "m", "min", "mins", "minute", "minutes" }, 60L),  HOUR(new String[] { "h", "hs", "hour", "hours" }, 3600L),  DAY(new String[] { "d", "ds", "day", "days" }, 86400L);
	  
	  private String[] names;
	  private long seconds;
	  
	  private TimeUnit(String[] names, long seconds)
	  {
	    this.names = names;
	    this.seconds = seconds;
	  }
	  
	  public long getSeconds()
	  {
	    return this.seconds;
	  }
	  
	  public String[] getNames()
	  {
	    return this.names;
	  }
	  

	  public static TimeUnit getByString(String str)
	  {
	    for (TimeUnit timeUnit : TimeUnit.values()) {
	      for (String name : timeUnit.getNames()) {
	        if (name.equalsIgnoreCase(str)) {
	          return timeUnit;
	        }
	      }
	    }
	    return null;
	  }
	}

	public static String isStaff(ProxiedPlayer player) {
		String content = null;
		try {
			content = getHTML("http://mainapi.minenation.dk:8080/getstaff/"+player.getUniqueId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
	public static String getHTML(String urlToRead) throws Exception {
	    StringBuilder result = new StringBuilder();
	    URL url = new URL(urlToRead);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String line;
	    while ((line = rd.readLine()) != null) {
	       result.append(line);
	    }
	    rd.close();
	    return result.toString();
	}
}
