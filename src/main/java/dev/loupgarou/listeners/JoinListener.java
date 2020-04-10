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
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.packetwrapper.WrapperPlayServerPlayerInfo;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam.Mode;
import dev.loupgarou.utils.CommonText.PrefixType;
import dev.loupgarou.utils.VariousUtils;
import fr.xephi.authme.events.LoginEvent;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

public class JoinListener implements Listener {

	private static final String url = "https://github.com/Ekinoxx0/LoupGarouRessourcePack/raw/";
	private static final String commitIdLGRessource = "f97c20d90e9e1ca9c0ed018841a8cb22d9bcbc9d";
	private final ViaAPI<?> api = Via.getAPI();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent e) {
		if(e.getResult() != Result.ALLOWED) return;
		
		LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
		lgp.setConnectingHostname(e.getHostname());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		ProtocolVersion v = ProtocolVersion.getProtocol(api.getPlayerVersion(e.getPlayer().getUniqueId()));
		if(v.getId() < ProtocolVersion.v1_13.getId()) {
			e.getPlayer().setResourcePack(url + commitIdLGRessource + "/generated-pre13.zip", "");
		} else {
			e.getPlayer().setResourcePack(url + commitIdLGRessource + "/generated.zip", "");
		}
		e.setJoinMessage(null);
	}
	
	@EventHandler
	public void onJoin(LoginEvent e) {
		Player p = e.getPlayer();
		p.teleport(p.getWorld().getSpawnLocation());
		
		WrapperPlayServerScoreboardTeam myTeam = new WrapperPlayServerScoreboardTeam();
		myTeam.setName(p.getDisplayName());
		myTeam.setPrefix(WrappedChatComponent.fromText(""));
		myTeam.setPlayers(Arrays.asList(p.getDisplayName()));
		myTeam.setMode(Mode.TEAM_CREATED);
		for(Player allPlayer : Bukkit.getOnlinePlayers())
			if(allPlayer != p) {
				if(allPlayer.getGameMode() != GameMode.SPECTATOR)
					allPlayer.hidePlayer(MainLg.getInstance(), p);
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				team.setName(allPlayer.getDisplayName());
				team.setPrefix(WrappedChatComponent.fromText(""));
				team.setPlayers(Arrays.asList(allPlayer.getDisplayName()));
				team.setMode(Mode.TEAM_CREATED);
				
				team.sendPacket(p);
				myTeam.sendPacket(allPlayer);
			}
		
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
	}
	@EventHandler
	public void onResoucePack(PlayerResourcePackStatusEvent e) {
		if(e.getStatus() == Status.SUCCESSFULLY_LOADED) {
			LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
			lgp.showView();
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
