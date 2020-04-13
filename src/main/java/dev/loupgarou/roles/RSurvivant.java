package dev.loupgarou.roles;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGWinType;
import dev.loupgarou.events.daycycle.LGNightPlayerPreKilledEvent;
import dev.loupgarou.events.daycycle.LGPreDayStartEvent;
import dev.loupgarou.events.game.LGGameEndEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import dev.loupgarou.events.roles.LGVampiredEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache;
import dev.loupgarou.utils.VariableCache.CacheType;

public class RSurvivant extends Role{
	public RSurvivant(LGGame game) {
		super(game);
	}
	@Override
	public int getMaxNb() {
		return Integer.MAX_VALUE;
	}
	@Override
	public RoleType getType() {
		return RoleType.NEUTRAL;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.NONE;
	}
	@Override
	public String getColor() {
		return "§d";
	}
	@Override
	public String getName() {
		return getColor() + "§lSurvivant";
	}
	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes si tu remplis ton objectif";
	}
	@Override
	public String getDescription() {
		return "Tu es §d§lNeutre§f et tu gagnes si tu remplis ton objectif. Ton objectif est de survivre. Tu disposes de §l2§f protections. Chaque nuit, tu peux utiliser une protection pour ne pas être tué par les §c§lLoups§f. Tu peux gagner aussi bien avec les §a§lVillageois§f qu'avec les §c§lLoups§f, tu dois juste rester en vie jusqu'à la fin de la partie.";
	}
	@Override
	public String getTask() {
		return "Veux-tu utiliser une protection cette nuit ?";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 décide s'il veut se protéger.";
	}
	@Override
	public int getTimeout() {
		return 15;
	}
	boolean inMenu;
	public void openInventory(Player player) {
		inMenu = true;
		Inventory inventory = Bukkit.createInventory(null, 9, "§7Veux-tu te protéger ?");
		ItemStack[] items = new ItemStack[9];
		VariableCache cache = LGPlayer.thePlayer(player).getCache();
		if(cache.<Integer>get(CacheType.SURVIVANT_LEFT) > 0) {
			items[3] = new ItemStack(LGCustomItems.getSpecialItem(SpecialItems.CROSS));
			ItemMeta meta = items[3].getItemMeta();
			meta.setDisplayName("§7§lNe rien faire");
			meta.setLore(Arrays.asList("§8Passez votre tour"));
			items[3].setItemMeta(meta);
			items[5] = new ItemStack(LGCustomItems.getSpecialItem(SpecialItems.CHECK));
			meta = items[5].getItemMeta();
			meta.setDisplayName("§2§lSe protéger (§6§l"+cache.<Integer>get(CacheType.SURVIVANT_LEFT)+"§2§l restant)");
			meta.setLore(Arrays.asList(
					"§8Tu ne pourras pas être tué par",
					"§8  les §c§lLoups§8 cette nuit."));
			items[5].setItemMeta(meta);
		} else {
			items[4] = new ItemStack(LGCustomItems.getSpecialItem(SpecialItems.CROSS));
			ItemMeta meta = items[4].getItemMeta();
			meta.setDisplayName("§7§lNe rien faire");
			meta.setLore(Arrays.asList("§8Passez votre tour"));
			items[4].setItemMeta(meta);
		}
		player.closeInventory();
		inventory.setContents(items);
		player.openInventory(inventory);
	}
	@Override
	public void join(LGPlayer player, boolean displayMsg, boolean leavePrecedentRole) {
		super.join(player, displayMsg, leavePrecedentRole);
		player.getCache().set(CacheType.SURVIVANT_LEFT, 2);
	}

	Runnable callback;
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		this.callback = callback;
		openInventory(player.getPlayer());
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.hideView();
		closeInventory(player.getPlayer());
		player.sendMessage("§4§oTu es sans défense...");
	}
	
	private void closeInventory(Player p) {
		inMenu = false;
		p.closeInventory();
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player player = (Player)e.getWhoClicked();
		LGPlayer lgp = LGPlayer.thePlayer(player);
			
		if(lgp.getRole() != this || item == null || item.getItemMeta() == null)return;

		if(item.getType() == LGCustomItems.getSpecialItem(SpecialItems.CROSS)) {
			e.setCancelled(true);
			lgp.sendMessage("§4§oTu es sans défense...");
			closeInventory(player);
			lgp.hideView();
			callback.run();
		}else if(item.getType() == LGCustomItems.getSpecialItem(SpecialItems.CHECK)) {
			e.setCancelled(true);
			closeInventory(player);
			lgp.sendActionBarMessage("§9§lTu as décidé de te protéger.");
			lgp.sendMessage("§6Tu as décidé de te protéger.");
			lgp.getCache().set(CacheType.SURVIVANT_LEFT, lgp.getCache().<Integer>get(CacheType.SURVIVANT_LEFT)-1);
			lgp.getCache().set(CacheType.SURVIVANT_PROTECTED, true);
			lgp.hideView();
			callback.run();
		}
	}

	@EventHandler
	public void onPlayerKill(LGNightPlayerPreKilledEvent e) {
		if(e.getGame() == getGame() && Arrays.asList(Reason.LOUP_GAROU, Reason.LOUP_BLANC, Reason.GM_LOUP_GAROU, Reason.ASSASSIN).contains(e.getReason()) && e.getKilled().getCache().getBoolean(CacheType.SURVIVANT_PROTECTED) && e.getKilled().isRoleActive()) {
			e.setReason(Reason.DONT_DIE);
		}
	}
	
	@EventHandler
	public void onVampired(LGVampiredEvent e) {
		if(e.getGame() == getGame() && e.getPlayer().getCache().getBoolean(CacheType.SURVIVANT_PROTECTED))
			e.setProtect(true);
	}
	
	@EventHandler
	public void onDayStart(LGPreDayStartEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getGame().getInGame())
				if(lgp.isRoleActive())
					lgp.getCache().remove(CacheType.SURVIVANT_PROTECTED);
	}
	
	@EventHandler
	public void onQuitInventory(InventoryCloseEvent e) {
		if(e.getInventory().getSize() == 9) {
			LGPlayer player = LGPlayer.thePlayer((Player)e.getPlayer());
			if(player.getRole() == this && inMenu) {
				new BukkitRunnable() {
					
					@Override
					public void run() {
						e.getPlayer().openInventory(e.getInventory());
					}
				}.runTaskLater(MainLg.getInstance(), 1);
			}
		}
	}
	
	
	
	
	
	@EventHandler
	public void onWin(LGGameEndEvent e) {
		if(e.getGame() == getGame() && getPlayers().size() > 0 && e.getWinType() != LGWinType.ANGE) {
			for(LGPlayer lgp : getPlayers())
				e.getWinners().add(lgp);
			new BukkitRunnable() {
					
				@Override
				public void run() {
					getGame().broadcastMessage("§6§oLe "+getName()+"§6§o a rempli son objectif.");
				}
			}.runTaskAsynchronously(MainLg.getInstance());
		}
	}
}
