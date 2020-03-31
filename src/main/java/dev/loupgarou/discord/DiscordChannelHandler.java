package dev.loupgarou.discord;

import java.util.concurrent.TimeUnit;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class DiscordChannelHandler {
	
	private final LGGame game;
	private VoiceChannel voice;
	private Invite invite;
	
	public DiscordChannelHandler(LGGame game) {
		this.game = game;
		
		DiscordManager discord = MainLg.getInstance().getDiscord();
		discord.getVoices().createVoiceChannel("LG - " + game.getOwner().getName()).setUserlimit(game.getMaxPlayers())
			.queue((voice) -> {
				this.voice = voice;
				voice.createInvite()
					 .setMaxAge(90L, TimeUnit.MINUTES)
					 .setMaxUses(100)
					 .queue((invite) -> {
						 this.invite = invite;
						 //TODO
						 invite.getUrl();
					 });
			});
	}
	
	public void muteChannel(boolean mute) {
		if(this.voice == null) return;
		
		for(Member member : this.voice.getMembers())
			member.mute(mute).queue();
	}
	
	public void destroy() {
		if(invite != null)
			invite.delete().queue();
		if(voice != null)
			voice.delete().queue();
	}
	
}
