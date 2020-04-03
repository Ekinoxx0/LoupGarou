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
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;

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
		Bukkit.broadcastMessage("§2+ §7" + p.getName());
		p.teleport(p.getWorld().getSpawnLocation());
		
		WrapperPlayServerScoreboardTeam myTeam = new WrapperPlayServerScoreboardTeam();
		myTeam.setName(p.getName());
		myTeam.setPrefix(WrappedChatComponent.fromText(""));
		myTeam.setPlayers(Arrays.asList(p.getName()));
		myTeam.setMode(0);
		for(Player allPlayer : Bukkit.getOnlinePlayers())
			if(allPlayer != p) {
				if(allPlayer.getGameMode() != GameMode.SPECTATOR)
					allPlayer.hidePlayer(MainLg.getInstance(), p);
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				team.setName(allPlayer.getName());
				team.setPrefix(WrappedChatComponent.fromText(""));
				team.setPlayers(Arrays.asList(allPlayer.getName()));
				team.setMode(0);
				
				team.sendPacket(p);
				myTeam.sendPacket(allPlayer);
			}
		p.setFoodLevel(20);
		if(e.getJoinMessage() == null) {
			p.sendMessage("SetRessourcepack");//TODO Rm
			p.getPlayer().setResourcePack("https://raw.githubusercontent.com/Ekinoxx0/LoupGarouRessourcePack/master/loup_garou.zip", "");
		} else {
			p.sendMessage("setDead false");//TODO Rm
			LGPlayer lgp = LGPlayer.thePlayer(p);
			lgp.setDead(false);
			lgp.showView();
			
			//lgp.join(MainLg.getInstance().getCurrentGame());
		}
		
		if(p.getGameMode() != GameMode.SPECTATOR)
			p.setGameMode(GameMode.ADVENTURE);
		e.setJoinMessage("");
		p.removePotionEffect(PotionEffectType.JUMP);
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.setWalkSpeed(0.2f);
	}
	@EventHandler
	public void onResoucePack(PlayerResourcePackStatusEvent e) {
		if(e.getStatus() == Status.SUCCESSFULLY_LOADED) {
			Player p = e.getPlayer();
			LGPlayer lgp = LGPlayer.thePlayer(p);
			lgp.showView();
			//lgp.join(MainLg.getInstance().getCurrentGame());
		} else if(e.getStatus() == Status.DECLINED) {
			Bukkit.broadcastMessage(MainLg.getPrefix()+"§c" + e.getPlayer().getName() + " a refusé le ressources pack. ("+e.getStatus()+")");
		} else if(e.getStatus() == Status.FAILED_DOWNLOAD) {
			e.getPlayer().kickPlayer(MainLg.getPrefix()+"§cIl vous faut le resourcepack pour jouer ! ("+e.getStatus()+")");
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage("§c- §7" + p.getName());
		LGPlayer lgp = LGPlayer.thePlayer(p);
		if(lgp.getGame() != null) {
			lgp.leaveChat();
			if(lgp.getRole() != null && !lgp.isDead())
				lgp.getGame().kill(lgp, Reason.DISCONNECTED, true);
			lgp.getGame().getInGame().remove(lgp);
			lgp.getGame().checkLeave();
		}
		LGPlayer.removePlayer(p);
		lgp.remove();
	}
	
}
