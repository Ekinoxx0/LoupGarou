package dev.loupgarou.roles.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.NonNull;

public abstract class Role implements Listener{
	@Getter private List<LGPlayer> players = new ArrayList<LGPlayer>();
	@Getter private final LGGame game;
	
	public Role(@NonNull LGGame game) {
		this.game = game;
		Bukkit.getPluginManager().registerEvents(this, MainLg.getInstance());
	}
	

	public abstract int getMaxNb();
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
	public abstract RoleType getType();
	public abstract RoleWinType getWinType();
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
					getGame().waitRole(getTimeout(), ()->{
						try {
							Role.this.onNightTurnTimeout(player);
						}catch(Exception err) {
							System.out.println("Error when timeout role");
							System.err.println("Error related to game : " + game.getKey());
							err.printStackTrace();
						}
						this.run();
					}, player, Role.this);
					player.sendMessage("§6"+getTask());
				//	player.sendTitle("§6C'est à vous de jouer", "§a"+getTask(), 100);
					onNightTurn(player, this);
				} else {
					getGame().waitRole(getTimeout(), ()->{}, player, Role.this);
					Runnable run = this;
					new BukkitRunnable() {

						@Override
						public void run() {
							run.run();
						}
					}.runTaskLater(MainLg.getInstance(), 20*(ThreadLocalRandom.current().nextInt(getTimeout()/3*2-4)+4));
				}
				
			}
		}.run();
	}
	 
	public void join(@NonNull LGPlayer player, boolean sendMessage, boolean leavePrecedentRole) {
		MainLg.debug(getGame().getKey(), getName() + "§7.join(" + player.getName() + ", " + sendMessage + ")");
		players.add(player);
		if(player.getRole() != null && leavePrecedentRole)
			player.getRole().getPlayers().remove(player);
		
		if(player.getRole() == null || leavePrecedentRole)
			player.setRole(this);
		
		if(sendMessage) {
			player.sendTitle("§6Tu es "+getName(), "§e"+getShortDescription(), 200);
			player.sendMessage("§6Tu es "+getName()+"§6.");
			player.sendMessage("§6Description : §f"+getDescription());
			
			switch(this.getWinType()) {
			case LOUP_GAROU:
			case VAMPIRE:
				player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0, false, false));
				break;
			case SEUL:
				player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 0, false, false));
				break;
			default:
				break;
			}
		}
		
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
