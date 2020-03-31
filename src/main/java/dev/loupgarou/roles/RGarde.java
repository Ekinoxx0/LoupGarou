package dev.loupgarou.roles;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventHandler;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGPlayer.LGChooseCallback;
import dev.loupgarou.events.daycycle.LGNightPlayerPreKilledEvent;
import dev.loupgarou.events.daycycle.LGPreDayStartEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache.CacheType;

public class RGarde extends Role{
	public RGarde(LGGame game) {
		super(game);
	}
	@Override
	public RoleType getType() {
		return RoleType.VILLAGER;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.VILLAGE;
	}
	@Override
	public String getColor() {
		return "§a";
	}
	@Override
	public String getName() {
		return getColor() + "§lGarde";
	}
	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux te protéger toi ou quelqu'un d'autre des attaques §c§lhostiles§f. Tu ne peux pas protéger deux fois d’affilé la même personne.";
	}
	@Override
	public String getTask() {
		return "Choisis un joueur à protéger.";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 choisit un joueur à protéger.";
	}
	
	@Override
	public int getTimeout() {
		return 15;
	}
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		
		player.choose(new LGChooseCallback() {
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null) {
					LGPlayer lastProtected = player.getCache().get(CacheType.GARDE_LASTPROTECTED);
					if(choosen == lastProtected) {
						if(lastProtected == player)
							player.sendMessage("§4§oTu t'es déjà protégé la nuit dernière.");
						else
							player.sendMessage("§4§oTu as déjà protégé §7§l§o"+lastProtected.getName()+"§4§o la nuit dernière.");
					}  else {
						if(choosen == player) {
							player.sendMessage("§6Tu décides de te protéger toi-même cette nuit.");
							player.sendActionBarMessage("§9Tu seras protégé.");
						} else {
							player.sendMessage("§6Tu vas protéger §7§l"+choosen.getName()+"§6 cette nuit.");
							player.sendActionBarMessage("§7§l"+choosen.getName()+"§9 sera protégé.");
						}
						choosen.getCache().set(CacheType.GARDE_PROTECTED, true);
						player.getCache().set(CacheType.GARDE_LASTPROTECTED, choosen);
						player.stopChoosing();
						player.hideView();
						callback.run();
					}
				}
			}
		});
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getCache().remove(CacheType.GARDE_LASTPROTECTED);
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez protégé personne.", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§cVous n'avez protégé personne cette nuit.");
	}
	
	private static List<Reason> reasonsProtected = Arrays.asList(Reason.LOUP_GAROU, Reason.LOUP_BLANC, Reason.GM_LOUP_GAROU, Reason.ASSASSIN);
	
	@EventHandler
	public void onPlayerKill(LGNightPlayerPreKilledEvent e) {
		if(e.getGame() == getGame() && reasonsProtected.contains(e.getReason()) && e.getKilled().getCache().getBoolean(CacheType.GARDE_PROTECTED)) {
			e.getKilled().getCache().remove(CacheType.GARDE_PROTECTED);
			e.setReason(Reason.DONT_DIE);
		}
	}
	@EventHandler
	public void onDayStart(LGPreDayStartEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getGame().getInGame())
				lgp.getCache().remove(CacheType.GARDE_PROTECTED);
	}
}
