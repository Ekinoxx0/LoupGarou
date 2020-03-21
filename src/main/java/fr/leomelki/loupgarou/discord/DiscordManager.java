package fr.leomelki.loupgarou.discord;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import fr.leomelki.loupgarou.MainLg;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter {
	private static final String DEFAULT_VALUE_CONFIG = "TOKEN_DISCORD";
	private static final long DEFAULT_CHANNEL_CONFIG = -1L;
	
	private VoiceChannel selectedChannel;
	private JDA jda;
	
	private final List<Member> deads;
	private boolean allMuted;

	public DiscordManager(MainLg main) {
		this.deads = new ArrayList<Member>();
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
    	
		this.jda = new JDABuilder(main.getConfig().getString("token")).build();
	    this.jda.addEventListener(this);
	    try {
			this.jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for(VoiceChannel voice : this.jda.getVoiceChannels()) {
			if(voice.getIdLong() == main.getConfig().getLong("channel_discord")) {
				this.selectedChannel = voice;
				break;
			}
		}
		
		if(this.selectedChannel == null) {
			Bukkit.broadcastMessage("§9§lDISCORD > §cAucun salon discord correspondant");
			return;
		} else {
			Bukkit.broadcastMessage("§9§lDISCORD > §4Lien prêt : " + this.selectedChannel.getName());
		}
	}
	
	public void setMutedChannel(boolean allMuted) {
		if(this.jda == null || this.selectedChannel == null) return;
		if(this.allMuted == allMuted) return;
		
		this.allMuted = allMuted;
		
		try {
			for(Member m : this.selectedChannel.getMembers())
				m.mute(this.allMuted).submit();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setMuted(String playerName, boolean muted) {
		for(Member m : this.selectedChannel.getMembers()) {
			if(m.getNickname().toLowerCase().contains(playerName.toLowerCase())) {
				m.mute(muted);
				if(muted)
					this.deads.add(m);
				else
					this.deads.remove(m);
				return;
			}
		}

		for(Member m : this.selectedChannel.getMembers()) {
			for(Role r : m.getRoles()) {
				if(r.getName().equals(playerName)) {
					m.mute(muted);
					if(muted)
						this.deads.add(m);
					else
						this.deads.remove(m);
					return;
				}
			}
		}
	}
	
	@Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
    	if(e.getChannelJoined() != null && e.getChannelJoined().getIdLong() == this.selectedChannel.getIdLong()) {
        	e.getEntity().mute(this.allMuted).submit();
        	for(Member m : this.deads)
        		if(m.getIdLong() == e.getEntity().getIdLong())
        			m.mute(true).submit();
    	} else if(e.getChannelLeft() != null && e.getChannelLeft().getIdLong() == this.selectedChannel.getIdLong()) {
        	e.getEntity().mute(false).submit();
    	}
    }
	
	@Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
    	if(e.getChannelLeft() != null && e.getChannelLeft().getIdLong() != this.selectedChannel.getIdLong()) return;
    	e.getEntity().mute(false).submit();
    }
}
