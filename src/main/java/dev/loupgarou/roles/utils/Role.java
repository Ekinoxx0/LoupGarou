package dev.loupgarou.roles.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.Setter;

public abstract class Role implements Listener{
	@Getter @Setter private int waitedPlayers;
	@Getter private List<LGPlayer> players = new ArrayList<LGPlayer>();
	@Getter private final LGGame game;
	
	public Role(LGGame game) {
		this.game = game;
		Bukkit.getPluginManager().registerEvents(this, MainLg.getInstance());
		waitedPlayers = game.getConfig().getRoles().get(getClass().getSimpleName().substring(1));//TODO Is that really neccessary ?
	}
	

	public abstract String getColor();
	public abstract String getName();
	public abstract String getFriendlyName();
	public abstract String getShortDescription();
	public abstract String getDescription();
	public abstract String getTask();
	public abstract String getBroadcastedTask();
	public RoleType getType(LGPlayer lgp) {
		return getType();
	}
	public RoleWinType getWinType(LGPlayer lgp) {
		return getWinType();
	}
	public RoleType getType() { return null; }
	public RoleWinType getWinType() { return null; }
	/**
	 * @return Timeout in second for this role
	 */
	public abstract int getTimeout();

	public void onNightTurn(Runnable callback) {
		List<LGPlayer> players = new ArrayList<LGPlayer>(getPlayers());
		 new Runnable() {
			
			@Override
			public void run() {
				getGame().cancelWait();
				if(players.size() == 0) {
					onTurnFinish(callback);
					return;
				}
				LGPlayer player = players.remove(0);
				
				if(player.isRoleActive()) {
					getGame().wait(getTimeout(), ()->{
						try {
							Role.this.onNightTurnTimeout(player);
						}catch(Exception err) {
							System.out.println("Error when timeout role");
							err.printStackTrace();
						}
						this.run();
					}, (currentPlayer, secondsLeft)->{
						return currentPlayer == player ? "§9§lC'est à ton tour !" : "§6C'est au tour "+getFriendlyName()+" §6(§e"+secondsLeft+" s§6)";
					});
					player.sendMessage("§6"+getTask());
				//	player.sendTitle("§6C'est à vous de jouer", "§a"+getTask(), 100);
					onNightTurn(player, this);
				} else {
					getGame().wait(getTimeout(), ()->{}, (currentPlayer, secondsLeft)->{
						return currentPlayer == player ? "§c§lTu ne peux pas jouer" : "§6C'est au tour "+getFriendlyName()+" §6(§e"+secondsLeft+" s§6)";
					});
					Runnable run = this;
					new BukkitRunnable() {

						@Override
						public void run() {
							run.run();
						}
					}.runTaskLater(MainLg.getInstance(), 20*(ThreadLocalRandom.current().nextInt(getTimeout()/3*2-4)+4));
				}
				
				/*getGame().wait(getTimeout(), ()->{
					try {
						Role.this.onNightTurnTimeout(player);
					}catch(Exception err) {
						MainLg.debug(getGame().getKey(), "Error when timeout role");
						err.printStackTrace();
					}
					this.run();
				}, (currentPlayer, secondsLeft)->{
					return currentPlayer == player ? "§9§lC'est à ton tour !" : (Role.this.game.getConfig().isHideRole() ? "§6C'est au tour de quelqu'un..." : "§6C'est au tour " + getFriendlyName()) + " §6(§e"+secondsLeft+" s§6)";
				});
				player.sendMessage("§6" + getTask());
				onNightTurn(player, this);*/
			}
		}.run();
	}
	 
	public void join(LGPlayer player, boolean sendMessage) {
		MainLg.debug(getGame().getKey(), player.getName() + " est " + getName());
		players.add(player);
		if(player.getRole() == null)
			player.setRole(this);
		waitedPlayers--;
		if(sendMessage) {
			player.sendTitle("§6Tu es "+getName(), "§e"+getShortDescription(), 200);
			player.sendMessage("§6Tu es "+getName()+"§6.");
			player.sendMessage("§6Description : §f"+getDescription());
		}
	}
	
	public void join(LGPlayer player) {
		join(player, !getGame().isStarted());
		LGCustomItems.updateItem(player);
	}
	public boolean hasPlayersLeft() {
		return getPlayers().size() > 0;
	}
	protected void onNightTurnTimeout(LGPlayer player) {}
	protected void onNightTurn(LGPlayer player, Runnable callback) {}
	protected void onTurnFinish(Runnable callback) {
		callback.run();
	}
	
	/**
	 * @return En combientième ce rôle doit être appellé
	 */
	public int getTurnOrder() {
		try {
			RoleSort role = RoleSort.valueOf(getClass().getSimpleName().substring(1));
			return role == null ? -1 : role.ordinal();
		}catch(Throwable e) {
			return -1;
		}
	}
}
