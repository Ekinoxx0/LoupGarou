package dev.loupgarou.roles;

import org.bukkit.Bukkit;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGPlayer.LGChooseCallback;
import dev.loupgarou.events.roles.LGDiscoverRoleEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;

public class RVoyante extends Role{
	public RVoyante(LGGame game) {
		super(game);
	}
	@Override
	public int getMaxNb() {
		return Integer.MAX_VALUE;
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
		return getColor() + "§lVoyante";
	}
	@Override
	public String getFriendlyName() {
		return "de la "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux espionner un joueur et découvrir sa véritable identité...";
	}
	@Override
	public String getTask() {
		return "Choisis un joueur dont tu veux connaître l'identité.";
	}
	@Override
	public String getBroadcastedTask() {
		return "La "+getName()+"§9 s'apprête à sonder un joueur...";
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
				if(choosen != null && choosen != player) {
					LGDiscoverRoleEvent e = new LGDiscoverRoleEvent(getGame(), player, choosen);
					Bukkit.getPluginManager().callEvent(e);
					if (e.isCancelled()) {
						player.sendActionBarMessage("§e§l" + choosen.getName() + "§6 est §e§limmunisé §6 à ton sort !");
						player.sendMessage("§6Tu découvres que §7§l" + choosen.getName() + "§6 est §e§limmunisé§6 à ton sort !");
					} else {
						player.sendActionBarMessage("§e§l" + choosen.getName() + "§6 est §e§l" + e.getDiscoveredRole().getName());
						player.sendMessage("§6Tu découvres que §7§l" + choosen.getName() + "§6 est " + e.getDiscoveredRole().getName() + "§6.");
					}
					player.stopChoosing();
					player.hideView();
					callback.run();
				}
			}
		});
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez regardé aucun rôle", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§cVous n'avez pas utilisé votre pouvoir cette nuit.");
	}
}
