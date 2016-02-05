package pw.untamemadman.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ServerChecker extends Plugin {
 	
	private int interval;
	public static String online_msg;
	public static String offline_msg;
    public static String server;
	public static String typeList;
	public static List<String> serverList;
	
	@Override
	public void onEnable() {
		
		initConfig();
		initServerFile();
		
		new CheckTask(this);
			
		getProxy().getScheduler().schedule(this, new Runnable() {
				
			@Override
			public void run() {
					
				CheckTask.checkServers();
					
			}
		}, 5, interval, TimeUnit.SECONDS);
		
	}
	
	private void initConfig() {
		
		if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try {
				Files.copy(getResourceAsStream("config.yml"), file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        Configuration cfg = null;
        
        try {
        	cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        interval = cfg.getInt("interval");
        online_msg = ChatColor.translateAlternateColorCodes('&', cfg.getString("online_msg"));
        offline_msg = ChatColor.translateAlternateColorCodes('&', cfg.getString("offline_msg"));
	}
	
	private void initServerFile() {
		
		File file = new File(getDataFolder(), "servers.yml");

        if (!file.exists()) {
            try {
				Files.copy(getResourceAsStream("servers.yml"), file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        Configuration cfg = null;
        
        try {
        	cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "servers.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        if (cfg.getString("type").equalsIgnoreCase("whitelist")) {
        	typeList = "WHITELIST";
        } else {
        	typeList = "BLACKLIST";
        }
        
        serverList = cfg.getStringList("servers");
	}
	
	
}
