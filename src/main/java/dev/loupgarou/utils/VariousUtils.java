package dev.loupgarou.utils;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam.Mode;
import dev.loupgarou.utils.SoundUtils.LGSound;

public class VariousUtils {
	public static double distanceSquaredXZ(Location from, Location to) {
		return Math.pow(from.getX()-to.getX(), 2)+Math.pow(from.getZ()-to.getZ(), 2);
	}
	public static void setWarning(Player p, boolean warning) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.WORLD_BORDER);
		WorldBorder wb = p.getWorld().getWorldBorder();

		container.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.INITIALIZE);

		container.getIntegers().write(0, 29999984);

		container.getDoubles().write(0, p.getLocation().getX());
		container.getDoubles().write(1, p.getLocation().getZ());

		container.getDoubles().write(3, wb.getSize());
		container.getDoubles().write(2, wb.getSize());

		container.getIntegers().write(2, (int) (warning ? wb.getSize() : wb.getWarningDistance()));
		container.getIntegers().write(1, 0);
		
		container.getLongs().write(0, (long) 0);

		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, container);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private static char[] hex = "0123456789abcdef".toCharArray();
	public static char toHex(int i) {
		return hex[i];
	}
	
	public static void setupLobby(LGPlayer lgp) {
		Player p = lgp.getPlayer();
		if(p == null) return;

		for(LGSound sound : LGSound.values())
			lgp.stopAudio(sound);
		
		lgp.setDead(false);
		lgp.setScoreboard(null);
		lgp.setLatestObjective(null);
		lgp.setGame(null);
		lgp.setPlace(0);
		lgp.setRole(null);
		
		VariousUtils.setWarning(p, false);
		if(p.getGameMode() == GameMode.SURVIVAL)
			p.setGameMode(GameMode.ADVENTURE);
		p.setWalkSpeed(0.2f);
		p.setExp(0);
		p.setLevel(0);
		p.setFoodLevel(20);
		p.setHealth(20f);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[] {});
		p.getInventory().setItemInOffHand(null);
		p.closeInventory();
		p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		for(PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());

		lgp.setDead(false);
		lgp.leaveChat();
		
		WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		team.setMode(Mode.TEAM_REMOVED);
		team.setName("you_are");
		team.sendPacket(p);
	}

}
