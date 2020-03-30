package dev.loupgarou.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
* @author Ekinoxx
 */
public class SoundUtils {
	
	@AllArgsConstructor
	public static enum LGSound {
		KILL(Sound.ENTITY_BLAZE_DEATH),
		START_NIGHT(Sound.ENTITY_SKELETON_DEATH),
		START_DAY(Sound.ENTITY_ZOMBIE_DEATH),
		AMBIANT_NIGHT(Sound.MUSIC_DISC_MALL),
		AMBIANT_DAY(Sound.MUSIC_DISC_MELLOHI);
	
		@Getter Sound sound;
	}


	public static void sendSound(Player player, LGSound sound, float volume){
		sendSound(player, sound.getSound(), volume);
	}
	
	public static void sendSound(Player player, LGSound sound){
		sendSound(player, sound.getSound());
	}

	/**
	 * Send sound to a player
	 * 
	 * @param player Player to send sound
	 * @param sound Sound to send
	 */
	public static void sendSound(Player player, Sound sound){
		if(sound == null) return;
		if(player == null) return;
		sendSound(player, sound, 1F, 1F);
	}
	
	/**
	 * Send sound to a player
	 * 
	 * @param player Player to send sound
	 * @param sound Sound to send
	 */
	public static void sendSound(Player player, Sound sound, float volume){
		if(sound == null) return;
		if(player == null) return;
		sendSound(player, sound, volume, 1F);
	}

	/**
	 * Send sound to a player
	 * 
	 * @param player Player to send sound
	 * @param sound Sound to send
	 */
	public static void sendSound(List<Player> players, Sound sound){
		if(players == null) return;
		if(sound == null) return;
		for(Player p : players)
			sendSound(p, sound, 1F, 1F);
	}
	
	/**
	 * Send sound to a player
	 * 
	 * @param player Player to send sound
	 * @param sound Sound to send
	 * @param volume Float volume
	 * @param pitch Float pitch
	 */
	public static void sendSound(Player player, Sound sound, float volume, float pitch){
		if(sound == null) return;
		if(player == null) return;
		player.playSound(player.getLocation(), sound, volume, pitch);
	}

	/**
	 * Send sound to a player at specific location
	 * 
	 * @param player Player to play sound
	 * @param location Location to play sound
	 * @param sound Sound to send
	 */
	public static void sendSound(Player player, Location location, Sound sound){
		if(player == null) return;
		if(location == null) return;
		if(sound == null) return;
		location.getWorld().playSound(location, sound, 1F, 1F);
	}

	/**
	 * Send sound to a player at specific location
	 * 
	 * @param player Player to send sound
	 * @param location Location to play sound
	 * @param sound Sound to send
	 * @param volume Float volume
	 * @param pitch Float pitch
	 */
	public static void sendSound(Player player, Location location, Sound sound, float volume, float pitch){
		if(player == null) return;
		if(location == null) return;
		if(sound == null) return;
		player.playSound(location, sound, volume, pitch);
	}
	
	public static void sendSoundForAll(Sound sound){
		if(sound == null) return;
		for(Player player : Bukkit.getOnlinePlayers())
			sendSound(player, sound);
	}
	
	/**
	 * Send sound for all players
	 * 
	 * @param sound Sound to send
	 * @param volume Float volume
	 * @param pitch Float pitch
	 */
	public static void sendSoundForAll(Sound sound, float volume, float pitch){
		if(sound == null) return;
		for(Player player : Bukkit.getOnlinePlayers())
			sendSound(player, sound, volume, pitch);
	}
	
	public static void sendSoundAt(Location location, Sound sound){
		if(location == null) return;
		if(sound == null) return;
		location.getWorld().playSound(location, sound, 1F, 1F);
	}

	/**
	 * Send sound at specific location
	 * 
	 * @param location Location to send sound
	 * @param sound Sound to send
	 * @param volume Float volume
	 * @param pitch Float pitch
	 */
	public static void sendSoundAt(Location location, Sound sound, float volume, float pitch){
		if(location == null) return;
		if(sound == null) return;
		location.getWorld().playSound(location, sound, volume, pitch);
	}

	public static void stopSound(Player player, LGSound sound){
		player.stopSound(sound.getSound());
	}

	public static void stopSound(Player player, Sound sound){
		if(sound == null) return;
		if(player == null) return;
		player.stopSound(sound);
	}

	public static void stopAllSound(Player player){
		if(player == null) return;
		for(Sound s : Sound.values())
			player.stopSound(s);
	}

}
