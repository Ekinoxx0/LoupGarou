package dev.loupgarou.discord;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import dev.loupgarou.MainLg;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter {
	
	private static final String TOKEN = "TOKEN_DISCORD";
	
	private @Getter VoiceChannel selectedChannel;
	private JDA jda;
	
	private final List<String> unknowns;
	private final List<Member> personalMutes;
	private boolean allMuted;

	public DiscordManager(MainLg main) {
		this.unknowns = new ArrayList<String>();
		this.personalMutes = new ArrayList<Member>();
		try {
			this.setup(main);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void clearDead() {
		this.personalMutes.clear();
	}

	private void setup(MainLg main) throws LoginException, IllegalArgumentException {
		this.jda = new JDABuilder(TOKEN).build();
	    this.jda.addEventListener(this);
	    try {
			this.jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for(VoiceChannel voice : this.jda.getVoiceChannels()) {
			if(voice.getIdLong() == main.getConfig().getLong("channel_discord")) {//TODO Discord
				this.selectedChannel = voice;
				break;
			}
		}
		
		if(this.selectedChannel == null) {
			Bukkit.broadcastMessage("§9§lDISCORD > §cAucun salon discord correspondant");
			return;
		} else {
			Bukkit.broadcastMessage("§9§lDISCORD > §aLien prêt");
		}
	}
	
	public void setMutedChannel(boolean allMuted) {
		if(this.jda == null || this.selectedChannel == null) return;
		if(this.allMuted == allMuted) return;
		
		this.allMuted = allMuted;
		
		MainLg.debug("§7[DISCORD] setMutedChannel(" + allMuted + ");");
		
		try {
			for(Member m : this.selectedChannel.getMembers()) {
				boolean b = false;
				for(Member dead : this.personalMutes) {
					if(dead.getIdLong() == m.getIdLong()) {
						b = true;
					}
				}
				m.mute(this.allMuted || b).queue();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setMuted(String playerName, boolean muted) {
		if(playerName == null) return;
		if(this.jda == null || this.selectedChannel == null) return;
		Member m = this.getMemberFromName(playerName);
		
		if(m != null) {
			MainLg.debug("§7[DISCORD] setMuted(" + playerName + "," + muted + ");");
			m.mute(muted);
			if(muted) {
				this.personalMutes.add(m);
			} else {
				this.personalMutes.remove(m);
			}
		} else {
			if(!this.unknowns.contains(playerName)) {
				this.unknowns.add(playerName);
				Bukkit.broadcastMessage("§9§lDISCORD > §cJoueur inconnu sur discord : " + playerName);
			}
		}
		
	}
	
	@Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
    	if(e.getChannelJoined() != null && e.getChannelJoined().getIdLong() == this.selectedChannel.getIdLong()) {
    		boolean b = this.allMuted;
        	for(Member m : this.personalMutes)
        		if(m.getIdLong() == e.getEntity().getIdLong())
        			b = true;
        	
        	e.getEntity().mute(b).queue();
    	} else if(e.getChannelLeft() != null && e.getChannelLeft().getIdLong() == this.selectedChannel.getIdLong()) {
        	e.getEntity().mute(false).queue();
    	}
    }
	
	@Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
    	if(e.getChannelLeft() != null && e.getChannelLeft().getIdLong() != this.selectedChannel.getIdLong()) return;
    	e.getEntity().mute(false).queue();
    }
	
	public Member getMemberFromName(String playerName) {
		if(playerName == null) return null;
		if(this.jda == null || this.selectedChannel == null) return null;
		
		for(Member m : this.selectedChannel.getMembers()) {
			if(m.getEffectiveName().toLowerCase().contains(playerName.toLowerCase())) {
				return m;
			}
		}

		for(Member m : this.selectedChannel.getMembers()) {
			for(Role r : m.getRoles()) {
				if(r.getName().equals(playerName)) {
					return m;
				}
			}
		}
		
		return null;
	}

}
