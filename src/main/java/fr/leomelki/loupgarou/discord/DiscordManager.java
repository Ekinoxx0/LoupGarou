package fr.leomelki.loupgarou.discord;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import fr.leomelki.loupgarou.MainLg;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DiscordManager extends ListenerAdapter {
	private static final String DEFAULT_VALUE_CONFIG = "TOKEN_DISCORD";
	
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
    	if(!main.getConfig().contains("token"))
    		main.getConfig().set("token", DEFAULT_VALUE_CONFIG);
    			
    	if(main.getConfig().getString("token").equals(DEFAULT_VALUE_CONFIG)) {
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
