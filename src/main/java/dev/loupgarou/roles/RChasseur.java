package dev.loupgarou.roles;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGDayStartEvent;
import dev.loupgarou.events.LGGameEndEvent;
import dev.loupgarou.events.LGNightStart;
import dev.loupgarou.events.LGPlayerKilledEvent;
import dev.loupgarou.events.LGPlayerKilledEvent.Reason;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;

public class RChasseur extends Role{
	public RChasseur(LGGame game) {
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
		return "§a§lChasseur";
	}
	@Override
	public String getFriendlyName() {
		return "du " + getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. À ta mort, tu dois éliminer un joueur en utilisant ta dernière balle.";
	}
	@Override
	public String getTask() {
		return "Tu dois choisir qui va mourir avec toi.";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 choisit qui il va emporter avec lui.";
	}
	@Override
	public int getTimeout() {
		return 15;
	}
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		getGame().wait(getTimeout(), ()->{
			this.onNightTurnTimeout(player);
			callback.run();
		}, (currentPlayer, secondsLeft)->{
			return currentPlayer == player ? "§9§lC'est à ton tour !" : "§6Le Chasseur choisit sa cible (§e"+secondsLeft+" s§6)";
		});
		MainLg.debug("tour de "+player.getName());
		getGame().broadcastMessage("§9"+getBroadcastedTask());
		player.sendMessage("§6"+getTask());
		//player.sendTitle("§6C'est à vous de jouer", "§a"+getTask(), 60);
		player.choose((choosen)->{
			if(choosen != null) {
				player.stopChoosing();
				getGame().cancelWait();
				LGPlayerKilledEvent killEvent = new LGPlayerKilledEvent(getGame(), choosen, Reason.CHASSEUR);
				Bukkit.getPluginManager().callEvent(killEvent);
				if(killEvent.isCancelled())
					return;
				
				if(getGame().kill(killEvent.getKilled(), killEvent.getReason(), true))
					return;
				callback.run();
			}
		}, player);
	}
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		getGame().broadcastMessage("§9Il n'a pas tiré sur la détente...");
		player.stopChoosing();
	}
	
	ArrayList<LGPlayer> needToPlay = new ArrayList<LGPlayer>();
	
	@EventHandler
	public void onPlayerKill(LGPlayerKilledEvent e) {
		MainLg.debug(e.getKilled().getRole()+" "+this);
		if(e.getKilled().getRole() == this && e.getReason() != Reason.DISCONNECTED) {
			needToPlay.add(e.getKilled());
			MainLg.debug("added");
		}
	}
	@EventHandler
	public void onDayStart(LGDayStartEvent e) {
		if(e.getGame() != getGame())return;
		MainLg.debug("day start "+needToPlay.size());
		
		if(needToPlay.size() > 0)
			e.setCancelled(true);
		
		if(!e.isCancelled())return;
		MainLg.debug("cancel");
		new Runnable() {
			public void run() {
				if(needToPlay.size() == 0) {
					MainLg.debug("finish");
					e.getGame().startDay();
					return;
				}
				LGPlayer player = needToPlay.remove(0);
				MainLg.debug("> "+player.getName());
				onNightTurn(player, this);
			}
		}.run();
	}
	
	@EventHandler
	public void onEndGame(LGGameEndEvent e) {
		if(e.getGame() != getGame())return;
		
		if(needToPlay.size() > 0)
			e.setCancelled(true);
		
		if(!e.isCancelled())return;
		
		new Runnable() {
			public void run() {
				if(needToPlay.size() == 0) {
					e.getGame().checkEndGame(true);
					return;
				}
				LGPlayer player = needToPlay.remove(0);
				onNightTurn(player, this);
			}
		}.run();
	}
	
/*	Deprecated by #onDayStart(LGDayStartEvent)
 * 
 * @EventHandler
	public void onVote(LGVoteEvent e) {
		if(e.getGame() == getGame()) {
			if(needToPlay.size() > 0) {
				e.setCancelled(true);
				new Runnable() {
					public void run() {
						if(needToPlay.size() == 0) {
							e.getGame().nextNight();
							return;
						}
						LGPlayer player = needToPlay.remove(0);
						onNightTurn(player, this);
					}
				}.run();
			}
		}
	}*/
	
	@EventHandler
	public void onNight(LGNightStart e) {
		if(e.getGame() == getGame() && !e.isCancelled()) {
			if(needToPlay.size() > 0) {
				e.setCancelled(true);
				new Runnable() {
					public void run() {
						if(needToPlay.size() == 0) {
							e.getGame().nextNight();
							return;
						}
						LGPlayer player = needToPlay.remove(0);
						onNightTurn(player, this);
					}
				}.run();
			}
		}
	}
}
