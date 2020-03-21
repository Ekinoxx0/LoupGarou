package fr.leomelki.loupgarou.discord;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import fr.leomelki.loupgarou.MainLg;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DiscordManager extends ListenerAdapter {
	public static final String DEFAULT_VALUE_CONFIG = "TOKEN_DISCORD";
	
	private VoiceChannel selectedChannel;
	private ShardManager shard;

	public DiscordManager(MainLg main) {
		try {
			this.setup(main);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void setup(MainLg main) throws LoginException, IllegalArgumentException {
    	if(!main.getConfig().contains("token") || main.getConfig().getString("token").equals(DEFAULT_VALUE_CONFIG)) {
			Bukkit.broadcastMessage("§9§lDISCORD > §cAucune config de token discord.");
    		return;
    	}
    	
	    DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
	    builder.setToken(main.getConfig().getString("token"));
	    builder.addEventListeners(this);
		this.shard = builder.build();
		
		for(VoiceChannel voice : this.shard.getVoiceChannels()) {
			if(voice.getName().contains("Loup")) {
				this.selectedChannel = voice;
				break;
			}
		}
		
		if(this.selectedChannel == null) {
			Bukkit.broadcastMessage("§9§lDISCORD > §cAucun salon discord contenant le mot : \'Loup\'");
			this.shard.shutdown();
			this.shard = null;
			return;
		}
	}
	
	@Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
    	if(e.getChannelJoined() == this.selectedChannel) {
    		
    	}
    }
}
