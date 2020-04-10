package dev.loupgarou.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.packetwrapper.WrapperPlayServerEntityDestroy;
import lombok.Getter;

public class VariousUtils {
	
	@Getter private static final ItemStack LOBBY_ITEM = new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.SERVER_ICON))
															.name("§9Menu")
															.lore(Arrays.asList("§7§oClique droit"))
															.build();
	
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
	
	public static void resetDisplay(LGPlayer lgp) {
		Player p = lgp.getPlayer();
		if(p == null) return;
		
		MainLg.debug("resetDisplay(" + lgp.toString() + ");");
		
		lgp.showView();
		lgp.updateOwnSkin();
		lgp.setScoreboard(null);
		
		VariousUtils.setWarning(p, false);
		VariousUtils.clearVotes(p);
		
		p.setGameMode(GameMode.ADVENTURE);
		p.setWalkSpeed(0.2f);
		p.setExp(0);
		p.setLevel(0);
		p.setFoodLevel(20);
		p.setSaturation(Float.MAX_VALUE);
		p.setHealth(20f);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[] {});
		p.getInventory().setItemInOffHand(null);
		p.updateInventory();
		p.closeInventory();
		for(PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
	}
	
	public static void setupLobby(LGPlayer lgp) {
		Player p = lgp.getPlayer();
		if(p == null) return;
		
		MainLg.debug("setupLobby(" + lgp.toString() + ");");

		lgp.leaveAllChat();
		lgp.reset();
		lgp.joinChat(MainLg.getInstance().getLobbyChat(), null, false);
		
		resetDisplay(lgp);
		
		p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		p.getInventory().setItem(4, LOBBY_ITEM);
	}
	
	public static void clearVotes(Player p) {
		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(new int[] {Integer.MIN_VALUE + p.getEntityId()});
		int[] ids = new int[Bukkit.getOnlinePlayers().size() + 1];
		List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		for(int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
			ids[i] = Integer.MIN_VALUE + players.get(i).getEntityId();
			destroy.sendPacket(players.get(i));
		}

		ids[ids.length-1] = -p.getEntityId();

		destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(ids);
		destroy.sendPacket(p);
	}

}
