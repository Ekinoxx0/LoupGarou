package dev.loupgarou.discord;

import javax.security.auth.login.LoginException;

import dev.loupgarou.MainLg;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter {
	
	private static final String TOKEN = "TOKEN_DISCORD";
	
	private JDA jda;
	
	public DiscordManager(MainLg main) {
		try {
			this.setup(main);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void setup(MainLg main) throws LoginException, IllegalArgumentException {
		this.jda = new JDABuilder(TOKEN).build();
	    this.jda.addEventListener(this);
	    try {
			this.jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/*for(VoiceChannel voice : this.jda.getVoiceChannels()) {
		}*/
		
	}
	
	@Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		//TODO
    }
	
	@Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		//TODO
    }
	
	public Member getMemberFromName(String playerName) {
		if(playerName == null) return null;
		if(this.jda == null) return null;
		
		/*for(Member m : this.selectedChannel.getMembers()) {
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
		}*/
		
		return null;
	}

}
