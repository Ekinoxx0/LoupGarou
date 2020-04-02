package dev.loupgarou.discord;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter {
	
	private static String TOKEN;
	private static final long CATEGORY_ID = 694507525206441994L;
	
	static {
		try {
			InputStream inputStream = DiscordManager.class.getResourceAsStream("/token");
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
			TOKEN = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
		
    }
	
	public LGPlayer get(Member member) {
		if(member == null) return null;
		if(this.jda == null) return null;
		if(this.voices == null) return null;
		
		for(LGPlayer lgp : LGPlayer.all()) {
			if(member.getEffectiveName().toLowerCase().contains(lgp.getName().toLowerCase())) {
				return lgp;
			}
			
			for(Role r : member.getRoles()) {
				if(r.getName().equals("!" + lgp.getName())) {
					return lgp;
				}
			}
		}
		
		return null;
	}
	
	public Member get(String playerName) {
		if(playerName == null) return null;
		if(this.jda == null) return null;
		if(this.voices == null) return null;
		
		for(Member m : this.voices.getMembers()) {
			if(m.getEffectiveName().toLowerCase().contains(playerName.toLowerCase())) {
				return m;
			}
		}

		for(Member m : this.voices.getMembers()) {
			for(Role r : m.getRoles()) {
				if(r.getName().equals("!" + playerName)) {
					return m;
				}
			}
		}
		
		return null;
	}

}
