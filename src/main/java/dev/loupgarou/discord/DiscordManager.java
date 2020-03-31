package dev.loupgarou.discord;

import dev.loupgarou.MainLg;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter {
	
	private static final String TOKEN = "Njk0NTIwOTE5OTI1NzE5MTMx.XoM1Bw.fdibSCgCjG14YMU50w99LheXpYw";
	private static final long CATEGORY_ID = 694507525206441994L;
	
	@Getter private final JDA jda;
	@Getter private final Category voices;
	
	public DiscordManager(MainLg main) throws Exception {
		this.jda = new JDABuilder(TOKEN).build();
	    this.jda.addEventListener(this);
	    try {
			this.jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    this.voices = this.jda.getCategoryById(CATEGORY_ID);
	    if(this.voices == null) {
	    	throw new Exception("Section id is unknown.");
	    }

		for(VoiceChannel voice : this.voices.getVoiceChannels())
			voice.delete().queue();
	}
	
	@Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		//TODO
    }
	
	@Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		//TODO
    }
	
	/*
	public Member getMemberFromName(String playerName) {
		if(playerName == null) return null;
		if(this.jda == null) return null;
		
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
	}*/

}
