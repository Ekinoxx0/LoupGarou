package dev.loupgarou.roles;

import org.bukkit.Bukkit;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGPlayer.LGChooseCallback;
import dev.loupgarou.events.roles.LGDiscoverRoleEvent;
import dev.loupgarou.roles.utils.RoleWinType;

public class RApprentieVoyante extends RVoyante{
	public RApprentieVoyante(LGGame game) {
		super(game);
	}
	@Override
	public String getName() {
		return super.getName() + " Apprentie";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux espionner un joueur et découvrir son camp...";
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
						String gentilMechant = e.getWinType() == RoleWinType.VILLAGE || choosen.getRoleWinType() == RoleWinType.NONE ? "§a§lgentil" : "§c§lméchant";
						player.sendActionBarMessage("§e§l" + choosen.getName() + "§6 est §e§l" + gentilMechant);
						player.sendMessage("§6Tu découvres que §7§l" + choosen.getName() + "§6 est " + gentilMechant + "§6.");
					}
					player.stopChoosing();
					player.hideView();
					callback.run();
				}
			}
		});
	}
}
