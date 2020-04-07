package dev.loupgarou.listeners;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.utils.CommonText.PrefixType;

public class JoinListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent e) {
		if(e.getResult() != Result.ALLOWED) return;
		
		LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
		lgp.setConnectingHostname(e.getHostname());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.teleport(p.getWorld().getSpawnLocation());
		
		WrapperPlayServerScoreboardTeam myTeam = new WrapperPlayServerScoreboardTeam();
		myTeam.setName(p.getDisplayName());
		myTeam.setPrefix(WrappedChatComponent.fromText("§7"));
		myTeam.setPlayers(Arrays.asList(p.getDisplayName()));
		myTeam.setMode(0);
		for(Player allPlayer : Bukkit.getOnlinePlayers())
			if(allPlayer != p) {
				if(allPlayer.getGameMode() != GameMode.SPECTATOR)
					allPlayer.hidePlayer(MainLg.getInstance(), p);
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				team.setName(allPlayer.getDisplayName());
				team.setPrefix(WrappedChatComponent.fromText("§7"));
				team.setPlayers(Arrays.asList(allPlayer.getDisplayName()));
				team.setMode(0);
				
				team.sendPacket(p);
				myTeam.sendPacket(allPlayer);
			}
		
		if(e.getJoinMessage() != "is connected")
			p.getPlayer().setResourcePack("https://raw.githubusercontent.com/Ekinoxx0/LoupGarouRessourcePack/master/loup_garou.zip", "");
		
		
		LGPlayer lgp = LGPlayer.thePlayer(p);
		lgp.showView();
		
		if(lgp.getConnectingHostname() != null && lgp.getConnectingHostname().contains(".")) {
			String[] hostn = lgp.getConnectingHostname().split("[.]");
			
			if(hostn.length == 4) {
				//Connecting on custom
				LGGame custom = MainLg.getInstance().findGame(hostn[0]);
				
				if(custom == null) {
					lgp.sendMessage(PrefixType.PARTIE + "§cAucune partie avec le code : §4§l" + hostn[0]);
				} else {
					custom.tryToJoin(lgp);
				}
			}
		}
		
		if(p.getGameMode() == GameMode.SURVIVAL)
			p.setGameMode(GameMode.ADVENTURE);
		
		e.setJoinMessage(null);
		p.removePotionEffect(PotionEffectType.JUMP);
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.setWalkSpeed(0.2f);
		p.setFoodLevel(20);
	}
	@EventHandler
	public void onResoucePack(PlayerResourcePackStatusEvent e) {
		if(e.getStatus() == Status.SUCCESSFULLY_LOADED) {
			LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
			lgp.showView();
			//lgp.join(MainLg.getInstance().getCurrentGame());
		} else if(e.getStatus() == Status.DECLINED) {
			e.getPlayer().sendMessage(PrefixType.RESSOURCEPACK + "§cVous avez refuser le ressource pack !");
		} else if(e.getStatus() == Status.FAILED_DOWNLOAD) {
			e.getPlayer().kickPlayer(PrefixType.RESSOURCEPACK + "§cIl vous faut le resourcepack pour jouer !");
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		LGPlayer lgp = LGPlayer.thePlayer(p);
		if(lgp.getGame() != null)
			lgp.getGame().leave(lgp);
		
		LGPlayer.removePlayer(p);
		lgp.remove();
	}
	
}
