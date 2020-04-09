package dev.loupgarou.classes;

import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;

import dev.loupgarou.MainLg;
import dev.loupgarou.packetwrapper.WrapperPlayServerChat;
import dev.loupgarou.packetwrapper.WrapperPlayServerTitle;
import dev.loupgarou.scoreboard.CustomScoreboard;
import dev.loupgarou.utils.SoundUtils;
import dev.loupgarou.utils.SoundUtils.LGSound;
import lombok.Getter;
import lombok.NonNull;

public class LGPlayerSimple {

	@Getter private Player player;
	@NonNull private String name;
	@Getter private CustomScoreboard scoreboard;
	
	public LGPlayerSimple(@NonNull Player player) {
		this.player = player;
		this.name = player.getDisplayName();
	}
	@Deprecated
	public LGPlayerSimple(String name) {
		this.name = name;
	}
	
	public String getName() {
		return player != null ? name = getPlayer().getDisplayName() : name;
	}

	public void sendActionBarMessage(String msg) {
		if(this.getPlayer() != null) {
			WrapperPlayServerChat chat = new WrapperPlayServerChat();
			chat.setChatType(ChatType.GAME_INFO);
			chat.setMessage(WrappedChatComponent.fromText(msg));
			chat.sendPacket(getPlayer());
		}
	}
	
	public void sendMessage(String msg) {
		if(this.getPlayer() != null)
			getPlayer().sendMessage(msg);
	}
	
	public void sendTitle(String title, String subTitle, int stay) {
		if(this.getPlayer() == null) return;
		WrapperPlayServerTitle titlePacket = new WrapperPlayServerTitle();
		titlePacket.setAction(TitleAction.TIMES);
		titlePacket.setFadeIn(10);
		titlePacket.setStay(stay);
		titlePacket.setFadeOut(10);
		titlePacket.sendPacket(getPlayer());
			
		titlePacket = new WrapperPlayServerTitle();
		titlePacket.setAction(TitleAction.TITLE);
		titlePacket.setTitle(WrappedChatComponent.fromText(title));
		titlePacket.sendPacket(getPlayer());
			
		titlePacket = new WrapperPlayServerTitle();
		titlePacket.setAction(TitleAction.SUBTITLE);
		titlePacket.setTitle(WrappedChatComponent.fromText(subTitle));
		titlePacket.sendPacket(getPlayer());
	}
	
	public void setScoreboard(CustomScoreboard scoreboard) {
		if(getPlayer() == null) return;
		
		if(this.scoreboard != null)
			this.scoreboard.hide();
			
		this.scoreboard = scoreboard;
			
		if(scoreboard != null)
			scoreboard.show();
	}

	public void playAudio(LGSound sound, float volume) {
		if(getPlayer() != null) return;
		SoundUtils.sendSound(getPlayer(), sound, volume);
	}
	
	public void stopAudio(LGSound sound) {
		if(getPlayer() == null) return;
		getPlayer().stopSound(sound.getSound());
	}
	
	public void hidePlayer(LGPlayer lgp) {
		if(getPlayer() == null || lgp.getPlayer() == null) return;
		getPlayer().hidePlayer(MainLg.getInstance(), lgp.getPlayer());
	}
	
	public void showPlayer(LGPlayer lgp) {
		if(getPlayer() == null || lgp.getPlayer() == null) return;
		getPlayer().showPlayer(MainLg.getInstance(), lgp.getPlayer());
	}
	
	public void destroy() {
		this.player = null;
	}

}
