package dev.loupgarou.discord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.io.CharStreams;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.utils.CommonText.PrefixType;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordManager extends ListenerAdapter {
	
	private static String TOKEN;
	public static String SECRET;
	public static final long CLIENT_ID = 690997265384603830L;
	private static final long CATEGORY_GAME = 696410298260914247L;
	private static final long CHANNEL_END_GAME = 696413735631323148L;
	private static final long BASIC_ROLE = 691019100738158613L;
	
	static {
		try {
			SECRET = CharStreams.toString(new InputStreamReader(DiscordManager.class.getResourceAsStream("/secret")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			TOKEN = CharStreams.toString(new InputStreamReader(DiscordManager.class.getResourceAsStream("/token")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Getter private final JDA jda;
	@Getter private final Guild guild;
	@Getter private final Category voices;
	@Getter private final VoiceChannel endGame;
	@Getter private final DiscordLinkServer linkServer;
	private final List<DiscordChannelHandler> handlers;
	
	public DiscordManager(MainLg main) throws Exception {
		this.handlers = new ArrayList<DiscordChannelHandler>();
		this.jda = JDABuilder.createDefault(TOKEN).setActivity(Activity.watching("des loups.")).build();
	    this.jda.addEventListener(this);
	    try {
			this.jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    this.voices = this.jda.getCategoryById(CATEGORY_GAME);
	    if(this.voices == null)
	    	throw new Exception("Section id is unknown.");
	    
	    this.guild = this.voices.getGuild();
	    
	    this.endGame = this.jda.getVoiceChannelById(CHANNEL_END_GAME);
	    if(this.endGame == null)
	    	throw new Exception("EndGame id is unknown.");

		for(VoiceChannel voice : this.voices.getVoiceChannels())
			voice.delete().queue();

		this.linkServer = new DiscordLinkServer();
	}
	
    public void onVoiceUpdateNoLink(GuildVoiceUpdateEvent e) {
    	if(e.getChannelJoined() == null) return;
		if(!this.voices.getChannels().contains(e.getChannelJoined())) return;

		DiscordChannelHandler handler = null;
		for(DiscordChannelHandler h : this.handlers)
			if(h.getVoice().equals(e.getChannelJoined())) handler = h;

		if(handler == null) {
			MainLg.debug("Voice channel not recognize...");
			this.guild.moveVoiceMember(e.getEntity(), this.endGame).queue();
			e.getChannelJoined().delete().queue();
			return;
		}
		
		e.getEntity().mute(handler.isChannelMuted()).queue();
	}
    
	@Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent e) {
		e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(BASIC_ROLE)).queue();
	}
	
	@Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
		LGPlayer lgp = this.get(e.getEntity());
		if(lgp == null) {
			this.onVoiceUpdateNoLink(e);
			return;
		}
		boolean leftGameChannel = this.voices.getChannels().contains(e.getChannelLeft());
		
		if(e.getChannelJoined() == null) {
			//Disconnected
			lgp.sendMessage(PrefixType.DISCORD + "§5Déconnecté du discord...");
			return;
		}
		
		if(e.getChannelLeft() == null)
			lgp.sendMessage(PrefixType.DISCORD + "§2Connecté au discord");
		
		if(this.voices.getChannels().contains(e.getChannelJoined())) {
			DiscordChannelHandler handler = null;
			for(DiscordChannelHandler h : this.handlers)
				if(h.getVoice().equals(e.getChannelJoined())) handler = h;
			
			if(handler == null) {
				MainLg.debug("Voice channel not recognize...");
				lgp.sendMessage(PrefixType.DISCORD + "§cUne erreur est survenue... #544891");
				this.guild.moveVoiceMember(e.getEntity(), this.endGame).queue();
				e.getChannelJoined().delete().queue();
				return;
			}
			
			if(handler.getGame().isStarted() || handler.getGame().isEnded()) {
				if(lgp.getGame() == handler.getGame()) {
					lgp.sendMessage(PrefixType.DISCORD + "§aVous vous êtes reconnecté au salon de votre partie...");
					e.getEntity().mute(lgp.isDead() | !handler.getGame().isDay()).queue();
				} else {
					lgp.sendMessage(PrefixType.DISCORD + "§6Vous êtes connecté sur une partie qui n'est pas la vôtre !");
					e.getEntity().mute(true).queue();
				}
				return;
			} else {
				if(handler.getGame().getOwner() == lgp) {
					lgp.sendMessage(PrefixType.DISCORD + "§aVous êtes connecté à votre partie");
				} else {
					lgp.sendMessage(PrefixType.DISCORD + "§aVous êtes connecté à la partie " + (handler.getGame().getConfig().isPrivateGame() ? "privée" : "publique") + " de " + handler.getGame().getOwner().getName());
				}
				e.getEntity().mute(false).queue();
			}
			
			if(leftGameChannel) {
				//Moved from a game to a game
			} else {
				//Joined a game
			}
		} else if(leftGameChannel) {
			//Moved from game to random
			lgp.sendMessage(PrefixType.DISCORD + "§cVous avez quitté un salon de jeu Loup Garou.");
			e.getEntity().mute(false).queue();
		} else {
			//Joined a random
			//Moved from random to random
			e.getEntity().mute(false).queue();
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
			if(member.getUser().getName().toLowerCase().contains(lgp.getName().toLowerCase()))
				return lgp;
			
			if(member.getEffectiveName().toLowerCase().contains(lgp.getName().toLowerCase()))
				return lgp;
			
			for(Role r : member.getRoles())
				if(r.getName().equals("!" + lgp.getName()))
					return lgp;
		}
		
		return null;
	}
	
	public Member get(@NonNull LGPlayer lgp) {
		if(this.jda == null) return null;
		if(this.voices == null) return null;
		Member m = null;
		
		long id = this.linkServer.getLinked(lgp);
		if(id > 0)
			m = this.guild.getMemberById(id);
		
		if(m == null)
			m = get(lgp.getName());
		
		if(m == null && lgp.getPlayer() == null)
			m = get(lgp.getPlayer().getName());
		
		return m;
	}
	
	private Member get(@NonNull String playerName) {
		if(this.jda == null) return null;
		if(this.voices == null) return null;

		for(Member m : this.guild.getMembers())
			for(Role r : m.getRoles())
				if(r.getName().equals("!" + playerName))
					return m;
		
		Member find = null;
		for(Member m : this.guild.getMembers()) {
			if(m.getEffectiveName().toLowerCase().equals(playerName.toLowerCase()))
				if(find != null)
					return null;
				else
					find = m;
		}
		
		return null;
	}

}
