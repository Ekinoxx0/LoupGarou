package dev.loupgarou.roles;

import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGPlayer.LGChooseCallback;
import dev.loupgarou.events.daycycle.LGNightStartEvent;
import dev.loupgarou.events.vote.LGPeopleVoteStartEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache.CacheType;

public class RCorbeau extends Role{
	public RCorbeau(LGGame game) {
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
		return getColor() + "§lCorbeau";
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
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux désigner un joueur qui se retrouvera le lendemain avec deux voix contre lui au vote.";
	}
	@Override
	public String getTask() {
		return "Tu peux choisir un joueur qui aura deux votes contre lui.";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 s'apprête à diffamer quelqu'un...";
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
					choosen.getCache().set(CacheType.CORBEAU_SELECTED, true);
					
					player.sendActionBarMessage("§e§l"+choosen.getName()+"§6 aura deux votes contre lui");
					player.sendMessage("§6Tu nuis à la réputation de §7§l"+choosen.getName()+"§6.");
					player.stopChoosing();
					player.hideView();
					callback.run();
				}
			}
		});
	}
	
	@EventHandler
	public void onNightStart(LGNightStartEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getGame().getAlive())
				lgp.getCache().remove(CacheType.CORBEAU_SELECTED);
	}
	
	@EventHandler
	public void onVoteStart(LGPeopleVoteStartEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getGame().getAlive())
				if(lgp.getCache().getBoolean(CacheType.CORBEAU_SELECTED)) {
					lgp.getCache().remove(CacheType.CORBEAU_SELECTED);
					new BukkitRunnable() {
						
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							//TODO Remake LGPlayer bad impl
							getGame().getVote().vote(new LGPlayer("§a§lLe corbeau"), lgp);
							getGame().getVote().vote(new LGPlayer("§a§lLe corbeau"), lgp);
							if(!e.getGame().getConfig().isHideVoteExtra()) {
								getGame().broadcastMessage("§7§l"+lgp.getName()+"§6 a reçu la visite du "+getName()+"§6.");
							}
						}
					}.runTask(MainLg.getInstance());
					
				}
	}
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez regardé aucun rôle", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§cVous n'avez pas utilisé votre pouvoir cette nuit.");
	}
}
