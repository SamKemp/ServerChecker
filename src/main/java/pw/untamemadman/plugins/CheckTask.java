package pw.untamemadman.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CheckTask {

	static ServerChecker instance;
	static Map<ServerInfo, Boolean> servers = new HashMap<ServerInfo, Boolean>();
	
	public CheckTask(ServerChecker instance)
	{
		CheckTask.instance = instance;
	}
	
	public static void checkServers()
	{
		
		for (Entry<String, ServerInfo> entry : instance.getProxy().getServers().entrySet())
		{
			
			final ServerInfo si = entry.getValue();
			
			if (ServerChecker.typeList.equals("WHITELIST")) {
				if (!ServerChecker.serverList.contains(si.getName()))
					continue;
			} else {
				if (ServerChecker.serverList.contains(si.getName()))
					continue;
			}
			
			Callback<ServerPing> callback = new Callback<ServerPing>() {

				@Override
				public void done(ServerPing sp, Throwable ex) {
					
					boolean bl;
					
					if (ex == null) {
						bl = true;
					} else {
						bl = false;
					}
					
					if (!servers.containsKey(si)) {
						servers.put(si, bl);
					} else if (servers.get(si).booleanValue() != bl) {
						BaseComponent[] msg;
						
						if (bl) {
							msg = TextComponent.fromLegacyText(ServerChecker.online_msg.replaceAll("%server%", si.getName()));
						} else {
							msg = TextComponent.fromLegacyText(ServerChecker.offline_msg.replaceAll("%server%", si.getName()));
						}
						
						for (ProxiedPlayer p : instance.getProxy().getPlayers())
						{
							if (p.getServer().toString() == "Hub")
							{
								if (p.hasPermission("serverchecker.notify"))
								{
									p.sendMessage(msg);
								}
							}
						}
						
						servers.put(si, bl);
					}
					
				}
			};
			
			entry.getValue().ping(callback);
		}
	}
	
}
