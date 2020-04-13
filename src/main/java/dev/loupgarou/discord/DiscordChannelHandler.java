package dev.loupgarou.discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.daycycle.LGDayStartEvent;
import dev.loupgarou.events.daycycle.LGNightStartEvent;
import dev.loupgarou.events.game.LGGameJoinEvent;
import dev.loupgarou.events.game.LGGameStartEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent;
import dev.loupgarou.utils.CommonText.PrefixType;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class DiscordChannelHandler implements Listener {

	private final DiscordManager discord;
	@Getter private final LGGame game;
	@Getter private VoiceChannel voice;
	@Getter private Invite invite;
	
	@Getter private boolean isChannelMuted;
	
	public DiscordChannelHandler(@NonNull LGGame game) {
		this.game = game;
		this.discord = MainLg.getInstance().getDiscord();
		
		discord.getVoices().createVoiceChannel("LG - " + game.getOwner().getName()).setUserlimit(game.getConfig().getMap().getSpawns().size())
			.queue((voice) -> {
				this.voice = voice;
				discord.register(this);
				
				voice.createInvite()
					 .setMaxAge(90L, TimeUnit.MINUTES)
					 .setMaxUses(100)
					 .queue((invite) -> {
						 this.invite = invite;
						 game.getOwner().sendMessage(PrefixType.DISCORD + "§2Création du salon finalisé, rejoignez le discord via : " + invite.getUrl());
						 Bukkit.getPluginManager().registerEvents(DiscordChannelHandler.this, MainLg.getInstance());
					 },
					 (failure) -> {
						 game.broadcastMessage(PrefixType.DISCORD + "§cSalon créé, mais une erreur est survenue lors de la création de l'invitation...");
						 failure.printStackTrace();
					 });
				
				Member owner = discord.get(game.getOwner());
				if(owner != null) {
					voice.getManager()
						.putPermissionOverride(owner, 
							Arrays.asList(
									Permission.VOICE_DEAF_OTHERS,
									Permission.VOICE_MOVE_OTHERS,
									Permission.VOICE_MUTE_OTHERS,
									Permission.VOICE_USE_VAD,
									Permission.PRIORITY_SPEAKER
									), 
							Collections.emptyList()).queue(
									(success) -> {},
									(failure) -> {
										 game.getOwner().sendMessage(PrefixType.DISCORD + "§cUne erreur est survenue de l'attribution de vos permissions discord.");
									});
				}
				
			},
			(failure) -> {
				game.broadcastMessage(PrefixType.DISCORD + "§cUne erreur est survenue sur lors de la création du salon discord...");
				failure.printStackTrace();
				this.destroy();
			});
	}
	
	@EventHandler
	public void onLGGameStart(LGGameStartEvent e) {
		if(e.getGame() != this.game) return;
		for(LGPlayer lgp : this.game.getInGame())
			move(lgp);
		
		if(this.voice != null)
			this.voice.getManager().setUserLimit(this.game.getInGame().size()).queue();
	}
	
	@EventHandler
	public void onLGGameJoin(LGGameJoinEvent e) {
		if(e.getGame() != this.game) return;
		move(e.getPlayer());
	}
	
	@EventHandler
	public void onLGDayStart(LGDayStartEvent e) {
		if(e.getGame() != this.game) return;
		this.muteChannel(false);
	}
	
	@EventHandler
	public void onLGNightStart(LGNightStartEvent e) {
		if(e.getGame() != this.game) return;
		this.muteChannel(true);
	}
	
	@EventHandler
	public void onLGPlayerKilled(LGPlayerKilledEvent e) {
		if(e.getGame() != this.game) return;
		this.refresh(e.getKilled());
	}
	
	public void move(LGPlayer lgp) {
		MainLg.debug(this.game.getKey(), "Discord.move(" + lgp.getName() + ")");
		
		if(!this.discord.isRecognized(lgp)) {
			lgp.sendMessage(PrefixType.DISCORD + "§cVous n'êtes pas lié à discord !");
			return;
		}
		
		Member m = this.discord.get(lgp);
		
		if(m == null) {
			lgp.sendMessage(PrefixType.DISCORD + "§cVous n'êtes pas connecté à un salon discord.");
			return;
		}
		
		GuildVoiceState voiceState = m.getVoiceState();
		if(!voiceState.inVoiceChannel()) {
			lgp.sendMessage(PrefixType.DISCORD + "§cVous n'êtes pas connecté à un salon discord...");
			return;
		}
		
		if(voiceState.getChannel() == voice)
			return;
			
		discord.getGuild().moveVoiceMember(m, voice).queue(
			(success) -> {
				lgp.sendMessage(PrefixType.DISCORD + "§9Vous avez été déplacé sur discord...");
				if(m.getVoiceState().isGuildMuted())
					m.mute(false).queue();
			},
			(failure) -> {
				lgp.sendMessage("§cEchec pour vous déplacer sur discord !");
			});
	}
	
	public void refresh(LGPlayer lgp) {
		Member member = this.discord.get(lgp);
		if(member == null) return;

		MainLg.debug(this.game.getKey(), "Discord.refresh(" + lgp.getName() + ")");
		member.mute(this.isChannelMuted | lgp.isDead()).queue();
	}
	
	public void muteChannel(boolean mute) {
		if(this.voice == null) return;
		if(this.isChannelMuted == mute) return;
		this.isChannelMuted = mute;
		MainLg.debug(this.game.getKey(), "Discord.muteChannel(" + mute + ")");
		
		if(mute) {
			for(Member member : this.voice.getMembers())
				if(!member.getVoiceState().isGuildMuted())
					member.mute(true).queue();
			return;
		}
		
		List<Member> has = new ArrayList<Member>();
		
		for(LGPlayer lgp : this.game.getDeads()) {
			Member deadMember = this.discord.get(lgp);
			if(deadMember == null) continue;
			if(!deadMember.getVoiceState().isGuildMuted())
				deadMember.mute(true).queue();
			has.add(deadMember);
		}

		for(Member member : this.voice.getMembers())
			if(!has.contains(member))
				if(member.getVoiceState().isGuildMuted())
					member.mute(false).queue();
	}
	
	public boolean isValid() {
		return this.game != null && !this.game.isEnded() && this.voice != null;
	}
	
	public void destroy() {
		if(invite != null)
			invite.delete().queue();

		HandlerList.unregisterAll(this);

		try {
			for (Member m : voice.getMembers()) {
				GuildVoiceState voiceState = m.getVoiceState();
				if (!voiceState.inVoiceChannel())
					continue;

				discord.getGuild().moveVoiceMember(m, discord.getEndGame()).queue();

				if (m.getVoiceState().isGuildMuted())
					m.mute(false).queue();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		final VoiceChannel currentVoice = voice;
		new BukkitRunnable() {
			@Override
			public void run() {
				currentVoice.delete().queue();
			}
		}.runTaskLaterAsynchronously(MainLg.getInstance(), 20 * 10);
		
		if(game != null)
			game.broadcastMessage(PrefixType.DISCORD + "§6Destruction de la liaison Discord...");
		
		this.invite = null;
		this.voice = null;
		MainLg.getInstance().getDiscord().unregister(this);
	}
	
}
