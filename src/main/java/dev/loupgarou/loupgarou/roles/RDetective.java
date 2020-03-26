package dev.loupgarou.loupgarou.roles;

import dev.loupgarou.loupgarou.classes.LGGame;
import dev.loupgarou.loupgarou.classes.LGPlayer;
import dev.loupgarou.loupgarou.classes.LGPlayer.LGChooseCallback;

public class RDetective extends Role{
	public RDetective(LGGame game) {
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
	public String getName() {
		return "§a§lDétective";
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
					if(player.getCache().has("detective_first")) {
						LGPlayer first = player.getCache().remove("detective_first");
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
						player.getCache().set("detective_first", choosen);
						player.sendMessage("§9Choisis un joueur avec qui tu souhaites comparer le rôle de §7§l"+choosen.getName());
					}
				}
			}
		});
	}

	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getCache().remove("detective_first");
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez mis personne en couple", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§9Tu n'as pas créé de couple.");
	}
	
	
}
