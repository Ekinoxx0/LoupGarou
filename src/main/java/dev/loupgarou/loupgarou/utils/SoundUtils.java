package dev.loupgarou.loupgarou.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
* @author Ekinoxx
 */
public class SoundUtils {
	
	public static Sound parseSound(String sound){
		sound = sound.toUpperCase();
		Sound s = null;
		
		try{
			s = Sound.valueOf(sound);
			return s;
		}catch(Exception ex){}
		
		if (s == null) {
			switch (sound) {
			case "SUCCESSFUL_HIT":
				sound = "ENTITY_ARROW_HIT_PLAYER";
				break;
				
			case "VILLAGER_IDLE":
				sound = "ENTITY_VILLAGER_AMBIENT";
				break;
				
			case "VILLAGER_HAGGLE":
				sound = "ENTITY_VILLAGER_TRADING";
				break;
				
			case "VILLAGER_HIT":
				sound = "ENTITY_VILLAGER_HURT";
				break;
				
			case "LEVEL_UP":
				sound = "ENTITY_PLAYER_LEVELUP";
				break;
				
			case "EXPLODE":
				sound = "ENTITY_GENERIC_EXPLODE";
				break;
				
			case "CLICK":
				sound = "UI_BUTTON_CLICK";
				break;
				
			case "ORB_PICKUP":
				sound = "ENTITY_EXPERIENCE_ORB_PICKUP";
				break;

			}
			
			try{
				s = Sound.valueOf(sound);
				if(s != null){
					return s;
				}
			}catch(Exception ex){}
			
			try{
				s = Sound.valueOf("ENTITY_" + sound);
				if(s != null){
					return s;
				}
			}catch(Exception ex){}
			
			try{
				s = Sound.valueOf("BLOCK_" + sound);
				if(s != null){
					return s;
				}
			}catch(Exception ex){}
		}

		return s;
	}

	/**
	 * Send sound to a player
	 * 
	 * @param player Player to send sound
	 * @param sound Sound to send
	 */
	public static void sendSound(Player player, String sound){
		Sound s = parseSound(sound);
		if(sound != null && player != null && player.isOnline()){
			sendSound(player, s, 1F, 1F);
		}
	}
	
	public static void sendSound(Player player, Sound sound){
		if(sound != null && player != null && player.isOnline()){
			sendSound(player, sound, 1F, 1F);
		}
	}
	
	/**
	 * Send sound to somes players
	 * 
	 * @param players List<Player> to send sound
	 * @param sound Sound in text to send
	 */
	public static void sendSound(List<Player> players, String sound){
		Sound s = parseSound(sound);
		for(Player p : players){
			if(s != null && p != null && p.isOnline()){
				sendSound(p, s, 1F, 1F);
			}
		}
	}
	
	public static void sendSound(List<Player> players, Sound sound){
		for(Player p : players){
			if(sound != null && p != null && p.isOnline()){
				sendSound(p, sound, 1F, 1F);
			}
		}
	}
	
	/**
	 * Send sound to a player
	 * 
	 * @param player Player to send sound
	 * @param sound Sound to send
	 * @param volume Float volume
	 * @param pitch Float pitch
	 */
	@Deprecated
	public static void sendSound(Player player, Sound sound, float volume, float pitch){
		if(sound != null && player != null && player.isOnline()){
			player.playSound(player.getLocation(), sound, volume, pitch);
		}
	}

	/**
	 * Send sound to a player at specific location
	 * 
	 * @param player Player to play sound
	 * @param location Location to play sound
	 * @param sound Sound to send
	 */
	@Deprecated
	public static void sendSound(Player player, Location location, Sound sound){
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
	@Deprecated
	public static void sendSound(Player player, Location location, Sound sound, float volume, float pitch){
		player.playSound(location, sound, volume, pitch);
	}
	
	@Deprecated
	public static void sendSoundForAll(Sound sound){
		for(Player player : Bukkit.getOnlinePlayers())
			sendSound(player, sound);
	}
	
	/**
	 * Send sound for all players
	 * 
	 * @param sound Sound in text to send
	 */
	public static void sendSoundForAll(String sound){
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
	@Deprecated
	public static void sendSoundForAll(Sound sound, float volume, float pitch){
		for(Player player : Bukkit.getOnlinePlayers())
			sendSound(player, sound, volume, pitch);
	}
	
	@Deprecated
	public static void sendSoundAt(Location location, Sound sound){
		location.getWorld().playSound(location, sound, 1F, 1F);
	}

	/**
	 * Send sound at specific location
	 * 
	 * @param location Location to send sound
	 * @param sound Sound in text to send
	 */
	public static void sendSoundAt(Location to, String sound) {
		Sound s = parseSound(sound);
		if(s != null && to != null){
			to.getWorld().playSound(to, s, 1F, 1F);
		}
	}

	/**
	 * Send sound at specific location
	 * 
	 * @param location Location to send sound
	 * @param sound Sound to send
	 * @param volume Float volume
	 * @param pitch Float pitch
	 */
	@Deprecated
	public static void sendSoundAt(Location location, Sound sound, float volume, float pitch){
		location.getWorld().playSound(location, sound, volume, pitch);
	}

}
