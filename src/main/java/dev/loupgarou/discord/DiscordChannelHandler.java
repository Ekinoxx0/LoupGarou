package dev.loupgarou.discord;

import java.util.concurrent.TimeUnit;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class DiscordChannelHandler {

	private final DiscordManager discord;
	private final LGGame game;
	private VoiceChannel voice;
	private Invite invite;
	
	private boolean isChannelMuted;
	
	public DiscordChannelHandler(@NonNull LGGame game) {
		this.game = game;
		this.discord = MainLg.getInstance().getDiscord();
		
		discord.getVoices().createVoiceChannel("LG - " + game.getOwner().getName()).setUserlimit(game.getMaxPlayers())
			.queue((voice) -> {
				this.voice = voice;
				discord.register(this);
				
				voice.createInvite()
					 .setMaxAge(90L, TimeUnit.MINUTES)
					 .setMaxUses(100)
					 .queue((invite) -> {
						 this.invite = invite;
						 game.broadcastMessage("§2Création du salon finalisé, rejoignez le discord via : " + invite.getUrl());
					 },
					 (failure) -> {
						 this.invite = null;
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
	
	public void muteChannel(boolean mute) {
		if(this.voice == null) return;
		if(this.isChannelMuted == mute) return;
		this.isChannelMuted = mute;
		
		for(Member member : this.voice.getMembers())
			member.mute(mute).queue();
	}
	
	public boolean isValid() {
		return this.game != null && !this.game.isEnded() && this.voice != null;
	}
	
	public void destroy() {
		if(invite != null)
			invite.delete().queue();
		if(voice != null)
			voice.delete().queue();
		if(game != null)
			game.broadcastMessage("§6Destruction de la liaison Discord...");
		
		this.invite = null;
		this.voice = null;
		MainLg.getInstance().getDiscord().unregister(this);
	}
	
}
