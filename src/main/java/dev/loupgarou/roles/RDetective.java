package dev.loupgarou.roles;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGPlayer.LGChooseCallback;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache.CacheType;

public class RDetective extends Role{
	public RDetective(LGGame game) {
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
		return getColor() + "§lDétective";
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
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu mènes l'enquête sur deux joueurs pour découvrir s'ils font partie du même camp.";
	}
	@Override
	public String getTask() {
		return "Choisis deux joueurs à étudier.";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 est sur une enquête...";
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
					if(choosen == player) {
						player.sendMessage("§cVous ne pouvez pas vous sélectionner !");
						return;
					}
					if(player.getCache().has(CacheType.DETECTIVE_FIRST)) {
						LGPlayer first = player.getCache().remove(CacheType.DETECTIVE_FIRST);
						if(first == choosen) {
							player.sendMessage("§cVous ne pouvez pas comparer §7§l"+first.getName()+"§c avec lui même !");
						} else {
							if((first.getRoleType() == RoleType.NEUTRAL || choosen.getRoleType() == RoleType.NEUTRAL) ? first.getRole().getClass() == choosen.getRole().getClass() : first.getRoleType() == choosen.getRoleType())
								player.sendMessage("§7§l"+first.getName()+"§6 et §7§l"+choosen.getName()+"§6 sont §adu même camp.");
							else
								player.sendMessage("§7§l"+first.getName()+"§6 et §7§l"+choosen.getName()+"§6 ne sont §cpas du même camp.");

							player.stopChoosing();
							player.hideView();
							callback.run();
						}
					} else {
						player.getCache().set(CacheType.DETECTIVE_FIRST, choosen);
						player.sendMessage("§9Choisis un joueur avec qui tu souhaites comparer le rôle de §7§l"+choosen.getName());
					}
				}
			}
		});
	}

	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getCache().remove(CacheType.DETECTIVE_FIRST);
		player.stopChoosing();
		player.hideView();
	}
	
	
}
