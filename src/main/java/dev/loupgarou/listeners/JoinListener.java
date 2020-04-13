package dev.loupgarou.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.subcommands.debug.ResourcePackCmd;
import dev.loupgarou.packetwrapper.WrapperPlayServerPlayerInfo;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam.Mode;
import dev.loupgarou.utils.CommonText.PrefixType;
import dev.loupgarou.utils.CharManager;
import dev.loupgarou.utils.VariousUtils;
import fr.xephi.authme.events.LoginEvent;

public class JoinListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent e) {
		if(e.getResult() != Result.ALLOWED) return;
		
		LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
		lgp.setConnectingHostname(e.getHostname());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		ResourcePackCmd.load(e.getPlayer());
		e.setJoinMessage(null);
	}
	
	@EventHandler
	public void onJoin(LoginEvent e) {
		Player p = e.getPlayer();
		ResourcePackCmd.load(e.getPlayer());
		
		WrapperPlayServerScoreboardTeam myTeam = new WrapperPlayServerScoreboardTeam();
		myTeam.setName(p.getDisplayName());
		myTeam.setPlayers(Arrays.asList(p.getName()));
		myTeam.setMode(Mode.TEAM_CREATED);
		
		WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		team.setMode(Mode.TEAM_CREATED);

		myTeam.sendPacket(p);
		for(Player allPlayer : Bukkit.getOnlinePlayers()) {
			if(allPlayer == p) continue;
			if(allPlayer.getGameMode() != GameMode.SPECTATOR)
				allPlayer.hidePlayer(MainLg.getInstance(), p);
			
			team.setName(allPlayer.getDisplayName());
			team.setPlayers(Arrays.asList(allPlayer.getName()));
			
			team.sendPacket(p);
			myTeam.sendPacket(allPlayer);
		}
		
		if(p.getGameMode() == GameMode.CREATIVE) return;
		
		LGPlayer lgp = LGPlayer.thePlayer(p);
		VariousUtils.setupLobby(lgp);
		
		if(lgp.getConnectingHostname() != null && lgp.getConnectingHostname().contains(".")) {
			String[] hostn = lgp.getConnectingHostname().split("[.]");
			
			if(hostn.length == 4) {
				//Connecting on custom
				LGGame custom = MainLg.getInstance().findGame(hostn[0]);
				
				if(custom == null) {
					lgp.sendMessage(PrefixType.PARTIE + "§cAucune partie avec le code : §4§l" + hostn[0]);
				} else {
					custom.tryToJoin(lgp);
					return;
				}
			}
		}
		
		if(MainLg.getInstance().getDiscord().getLinkServer().getLinked(lgp) < 0)
			MainLg.getInstance().getDiscord().getLinkServer().generateLink(lgp);
	}
	@EventHandler
	public void onResoucePack(PlayerResourcePackStatusEvent e) {
		switch(e.getStatus()) {
		case SUCCESSFULLY_LOADED:
			e.getPlayer().sendMessage(PrefixType.RESOURCEPACK + "§aChargé §l" + CharManager.CHECKED);
			break;
		case ACCEPTED:
			break;
		case DECLINED:
			e.getPlayer().sendMessage(PrefixType.RESOURCEPACK + "§cVous avez refusé le pack de ressources ! §l" + CharManager.CROSS);
			LGPlayer.thePlayer(e.getPlayer()).setLoadedRessourcePack(null);
			break;
		case FAILED_DOWNLOAD:
			e.getPlayer().sendMessage(PrefixType.RESOURCEPACK + "§cLe téléchargement du pack de ressources a échoué ! §l" + CharManager.CROSS);
			LGPlayer.thePlayer(e.getPlayer()).setLoadedRessourcePack(null);
			break;
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		LGPlayer lgp = LGPlayer.thePlayer(p);
		if(lgp.getGame() != null)
			lgp.getGame().leave(lgp);

		WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
		List<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
		info.setAction(PlayerInfoAction.REMOVE_PLAYER);
		infos.add(new PlayerInfoData(new WrappedGameProfile(p.getUniqueId(), p.getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(p.getName())));
		info.setData(infos);
		for(Player ap : Bukkit.getOnlinePlayers())
			info.sendPacket(ap);
		
		lgp.destroy();
	}
	
}
