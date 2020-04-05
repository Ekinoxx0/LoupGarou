package dev.loupgarou.discord;

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
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class DiscordChannelHandler implements Listener {

	private final DiscordManager discord;
	private final LGGame game;
	private VoiceChannel voice;
	@Getter private Invite invite;
	
	private boolean isChannelMuted;
	
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
						 game.broadcastMessage("§2Création du salon finalisé, rejoignez le discord via : " + invite.getUrl());
						 Bukkit.getPluginManager().registerEvents(DiscordChannelHandler.this, MainLg.getInstance());
					 },
					 (failure) -> {
						 game.broadcastMessage("§cSalon créé, mais une erreur est survenue lors de la création de l'invitation...");
						 failure.printStackTrace();
					 });
			},
			(failure) -> {
				game.broadcastMessage("§cUne erreur est survenue sur lors de la création du salon discord...");
				failure.printStackTrace();
				this.destroy();
			});
	}
	
	@EventHandler
	public void onLGGameStart(LGGameStartEvent e) {
		for(LGPlayer lgp : this.game.getInGame())
			move(lgp);
	}
	
	@EventHandler
	public void onLGGameJoin(LGGameJoinEvent e) {
		move(e.getPlayer());
	}
	
	@EventHandler
	public void onLGDayStart(LGDayStartEvent e) {
		this.muteChannel(false);
	}
	
	@EventHandler
	public void onLGNightStart(LGNightStartEvent e) {
		this.muteChannel(true);
	}
	
	@EventHandler
	public void onLGPlayerKilled(LGPlayerKilledEvent e) {
		this.refresh(e.getKilled());
	}
	
	public void move(LGPlayer lgp) {
		MainLg.debug(this.game.getKey(), "Discord.move(" + lgp.getName() + ")");
		Member m = this.discord.get(lgp);
		
		if(m == null) {
			lgp.sendMessage("§cVous n'êtes pas lié sur discord !");
			return;
		}
		
		final Member member = m;
		discord.getGuild().moveVoiceMember(member, voice).queue(
				(success) -> {
					lgp.sendMessage("§9Vous avez été déplacé sur discord...");
					member.mute(false).queue();
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
		
		for(Member member : this.voice.getMembers()) {
			LGPlayer lgp = this.discord.get(member);
			if(lgp == null) {
				member.mute(mute).queue();
				continue;
			}

			member.mute(mute | lgp.isDead()).queue();
		}
	}
	
	public boolean isValid() {
		return this.game != null && !this.game.isEnded() && this.voice != null;
	}
	
	public void destroy() {
		if(invite != null)
			invite.delete().queue();

		HandlerList.unregisterAll(this);
		
		new BukkitRunnable() {
			
			final VoiceChannel currentVoice = voice;
			
			@Override
			public void run() {
				for(Member m : currentVoice.getMembers()) {
					m.mute(false).queue();
					discord.getGuild().moveVoiceMember(m, discord.getEndGame()).complete();
				}
				
				if(currentVoice != null)
					currentVoice.delete().queue();
			}
		}.runTaskAsynchronously(MainLg.getInstance());
		
		if(game != null)
			game.broadcastMessage("§6Destruction de la liaison Discord...");
		
		this.invite = null;
		this.voice = null;
		MainLg.getInstance().getDiscord().unregister(this);
	}
	
}
