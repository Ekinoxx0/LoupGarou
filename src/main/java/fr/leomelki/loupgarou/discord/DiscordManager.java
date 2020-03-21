package fr.leomelki.loupgarou.discord;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import fr.leomelki.loupgarou.MainLg;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DiscordManager extends ListenerAdapter {
	private static final String DEFAULT_VALUE_CONFIG = "TOKEN_DISCORD";
	private static final long DEFAULT_CHANNEL_CONFIG = -1L;
	
	private VoiceChannel selectedChannel;
	private ShardManager shard;
	
	private boolean allMuted;

	public DiscordManager(MainLg main) {
		try {
			this.setup(main);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void setup(MainLg main) throws LoginException, IllegalArgumentException {
    	if(!main.getConfig().contains("token")) {
    		main.getConfig().set("token", DEFAULT_VALUE_CONFIG);
    		main.saveConfig();
    	}
    	
    	if(!main.getConfig().contains("channel_discord") || !main.getConfig().isLong("channel_discord")) {
    		main.getConfig().set("channel_discord", DEFAULT_CHANNEL_CONFIG);
    		main.saveConfig();
    	}
    	
    	if(main.getConfig().getString("token").equals(DEFAULT_VALUE_CONFIG)) {
			Bukkit.broadcastMessage("§9§lDISCORD > §cAucune config de token discord.");
    		return;
    	}
    	
    	if(main.getConfig().getLong("channel_discord") == DEFAULT_CHANNEL_CONFIG) {
			Bukkit.broadcastMessage("§9§lDISCORD > §cAucune config de channel discord.");
    		return;
    	}
    	
	    DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
	    builder.setStatus(OnlineStatus.ONLINE);
	    builder.setToken(main.getConfig().getString("token"));
	    builder.addEventListeners(this);
		this.shard = builder.build();

		Bukkit.broadcastMessage("List : ");
		for(VoiceChannel voice : this.shard.getVoiceChannels()) {
			Bukkit.broadcastMessage(voice.getId() + " -> " + voice.getName());
			if(voice.getIdLong() == main.getConfig().getLong("channel_discord")) {
				this.selectedChannel = voice;
				break;
			}
		}
		
		if(this.selectedChannel == null) {
			Bukkit.broadcastMessage("§9§lDISCORD > §cAucun salon discord correspondant");
			return;
		}
	}
	
	public void setMutedChannel(boolean allMuted) {
		if(this.shard == null || this.selectedChannel == null) return;
		if(this.allMuted == allMuted) return;
		
		this.allMuted = allMuted;
		
		try {
			for(Member m : this.selectedChannel.getMembers())
				m.mute(this.allMuted).submit();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
    	if(e.getChannelJoined().getIdLong() != this.selectedChannel.getIdLong()) return;
    	
    	e.getEntity().mute(this.allMuted).submit();
    }
	
	@Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
    	if(e.getChannelLeft().getIdLong() != this.selectedChannel.getIdLong()) return;
    	e.getEntity().mute(false).submit();
    }
}
