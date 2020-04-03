package dev.loupgarou.discord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.CharStreams;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter {
	
	private static String TOKEN;
	private static final long CATEGORY_ID = 694507525206441994L;
	
	static {
		try {
			TOKEN = CharStreams.toString(new InputStreamReader(DiscordManager.class.getResourceAsStream("/token")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Getter private final JDA jda;
	@Getter private final Category voices;
	private final List<DiscordChannelHandler> handlers;
	
	public DiscordManager(MainLg main) throws Exception {
		this.handlers = new ArrayList<DiscordChannelHandler>();
		this.jda = new JDABuilder(TOKEN).build();
	    this.jda.addEventListener(this);
	    try {
			this.jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    this.voices = this.jda.getCategoryById(CATEGORY_ID);
	    if(this.voices == null)
	    	throw new Exception("Section id is unknown.");

		for(VoiceChannel voice : this.voices.getVoiceChannels())
			voice.delete().queue();
	}
	
	@Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
		LGPlayer lgp = this.get(e.getEntity());
		boolean leftGameChannel = this.voices.getChannels().contains(e.getChannelLeft());
		
		if(e.getChannelJoined() == null) {
			//Disconnected
			if(lgp == null) return;
			lgp.sendMessage(MainLg.getPrefix() + "§cDéconnecté du discord...");
			return;
		}
		
		if(this.voices.getChannels().contains(e.getChannelJoined())) {
			if(leftGameChannel) {
				//Moved from a game to a game
			} else {
				//Joined a game
			}
		} else if(leftGameChannel) {
			//Moved from game to random
			if(lgp == null) return;
			lgp.sendMessage(MainLg.getPrefix() + "§cVous avez quitté les salons de jeux Loup Garou.");
		} else {
			//Moved from random to random
		}
    }
	
	//Methods
	
	public void register(@NonNull DiscordChannelHandler handler) {
		if(handler.isValid())
			this.handlers.add(handler);
		else
			MainLg.debug("Tried to register an invalid discord handler");
	}
	
	public void unregister(@NonNull DiscordChannelHandler handler) {
		this.handlers.remove(handler);
		if(handler.isValid())
			handler.destroy();
	}
	
	public LGPlayer get(@NonNull Member member) {
		if(this.jda == null) return null;
		if(this.voices == null) return null;
		
		for(LGPlayer lgp : LGPlayer.all()) {
			if(member.getEffectiveName().toLowerCase().contains(lgp.getName().toLowerCase()))
				return lgp;
			
			for(Role r : member.getRoles())
				if(r.getName().equals("!" + lgp.getName()))
					return lgp;
		}
		
		return null;
	}
	
	public Member get(@NonNull String playerName) {
		if(this.jda == null) return null;
		if(this.voices == null) return null;
		
		for(Member m : this.voices.getMembers())
			if(m.getEffectiveName().toLowerCase().contains(playerName.toLowerCase()))
				return m;

		for(Member m : this.voices.getMembers())
			for(Role r : m.getRoles())
				if(r.getName().equals("!" + playerName))
					return m;
		
		return null;
	}

}
