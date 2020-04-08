package dev.loupgarou.roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.daycycle.LGNightStartEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import dev.loupgarou.events.vote.LGMayorVoteStartEvent;
import dev.loupgarou.events.vote.LGPeopleVoteStartEvent;
import dev.loupgarou.packetwrapper.WrapperPlayServerHeldItemSlot;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache.CacheType;

public class RDictateur extends Role{
	//TODO Replace with InteractInventory
	static private ItemStack[] items = new ItemStack[9];
	static private Inventory inventory;
	static private String inventoryTitle = "§7Veux-tu faire un coup d'état ?";
	static {
		items[3] = new ItemStack(LGCustomItems.getSpecialItem(SpecialItems.CROSS));
		ItemMeta meta = items[3].getItemMeta();
		meta.setDisplayName("§7§lNe rien faire");
		meta.setLore(Arrays.asList("§8Passez votre tour"));
		items[3].setItemMeta(meta);
		items[5] = new ItemStack(Material./*DIAMOND_SWORD*/GUNPOWDER);
		meta = items[5].getItemMeta();
		meta.setDisplayName("§e§lCoup d'État");
		meta.setLore(Arrays.asList(
				"§8Prends le contrôle du village",
				"§8et choisis seul qui mourra demain.",
				"",
				"§8Si tu tues un §a§lVillageois§8, tu",
				"§8l'auras sur la conscience."));
		items[5].setItemMeta(meta);
		inventory = Bukkit.createInventory(null, 9, inventoryTitle);
		inventory.setContents(items.clone());
	}
	public RDictateur(LGGame game) {
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
		return getColor() + "§lDictateur";
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
		return "Tu gagnes avec le §a§lVillage§f. Une fois dans la partie, tu peux choisir d'effectuer un §e§o§lcoup d'état§f, tu choisiras alors seul qui mourra au jour suivant. Si tu fais le bon choix, tu deviendras §5§lCapitaine§f mais si tu tues un §a§lVillageois§f, tu te suicideras la nuit qui suit.";
	}
	@Override
	public String getTask() {
		return "Veux-tu réaliser un coup d'état ?";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 décide s'il veut se dévoiler.";
	}
	
	@Override
	public int getTimeout() {
		return 15;
	}
	
	public void openInventory(Player player) {
		inMenu = true;
		player.closeInventory();
		player.openInventory(inventory);
	}
	Runnable callback, run;
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
		/*player.sendTitle("§cVous ne faites pas votre coup d'état.", "§4Vous avez mis trop de temps à vous décider...", 80);
		player.sendMessage("§cVous ne faites pas votre coup d'état.");
		player.sendMessage("§7§oVous aurez de nouveau le choix lors de la prochaine nuit.");*/
	}

	boolean inMenu = false;
	
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

		if(item.getItemMeta().getDisplayName().equals(items[3].getItemMeta().getDisplayName())) {
			e.setCancelled(true);
			closeInventory(player);
			/*lgp.sendMessage("§cVous ne faites pas votre coup d'état.");
			lgp.sendMessage("§7§oVous aurez de nouveau le choix lors de la prochaine nuit.");*/
			lgp.hideView();
			callback.run();
		}else if(item.getItemMeta().getDisplayName().equals(items[5].getItemMeta().getDisplayName())) {
			e.setCancelled(true);
			closeInventory(player);
			lgp.sendActionBarMessage("§9§lTu effectueras un coup d'état");
			lgp.sendMessage("§6Tu as décidé de faire un coup d'état.");
			lgp.getCache().set(CacheType.COUP_D_ETAT, true);
			lgp.getCache().set(CacheType.JUST_COUP_D_ETAT, true);
			lgp.hideView();
			callback.run();
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		LGPlayer player = LGPlayer.thePlayer(p);
		if(e.getItem() != null && e.getItem().getType() == LGCustomItems.getSpecialItem(SpecialItems.CROSS) && player.getRole() == this) {
			getGame().cancelWait();
			player.stopChoosing();
			p.getInventory().setItem(8, null);
			p.updateInventory();
			getGame().broadcastMessage("§7§l"+player.getName()+"§9 n'a tué personne.");
			run.run();
		}
	}
	
	@EventHandler
	public void onQuitInventory(InventoryCloseEvent e) {
		if(e.getInventory().equals(inventory)) {//TODO Verify
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
	public void onMayorVote(LGMayorVoteStartEvent e) {
		if(e.getGame() == getGame())
			onTurn(e);
	}

	@EventHandler
	public void onVote(LGPeopleVoteStartEvent e) {
		if(e.getGame() == getGame())
			onTurn(e);
	}
	public void onTurn(Cancellable e) {
		for(LGPlayer lgp : getPlayers())
			if(lgp.getCache().getBoolean(CacheType.JUST_COUP_D_ETAT) && lgp.isRoleActive())
				e.setCancelled(true);
		
		if(!e.isCancelled())
			return;
		
		Iterator<LGPlayer> ite = new ArrayList<LGPlayer>(getPlayers()).iterator();
		new Runnable() {
			public void run() {
				run = this;
				if(ite.hasNext()) {
					LGPlayer lgp = ite.next();
					if(lgp.getCache().getBoolean(CacheType.JUST_COUP_D_ETAT)) {
						getPlayers().remove(lgp);
						lgp.getCache().remove(CacheType.JUST_COUP_D_ETAT);
						getGame().broadcastMessage("§7§l"+lgp.getName()+" §9réalise un coup d'état.");
						//lgp.sendTitle("§6Vous faites votre coup d'état", "§aChoisissez qui tuer", 60);
						
						//On le met sur le slot 0 pour éviter un missclick sur la croix
						WrapperPlayServerHeldItemSlot hold = new WrapperPlayServerHeldItemSlot();
						hold.setSlot(0);
						hold.sendPacket(lgp.getPlayer());
						
						lgp.sendMessage("§6Choisis un joueur à exécuter.");
						getGame().wait(60, ()->{
							lgp.stopChoosing();
							getGame().broadcastMessage("§7§l"+lgp.getName()+"§9 n'a tué personne.");
							lgp.getPlayer().getInventory().setItem(8, null);
							lgp.getPlayer().updateInventory();
							this.run();
						}, (player, secondsLeft)->{
							return lgp == player ? "§9§lC'est à ton tour !" : "§6Le Dictateur choisit sa victime (§e"+secondsLeft+" s§6)";
						});
						lgp.choose((choosen)->{
							if(choosen != null) {
								getGame().cancelWait();
								lgp.stopChoosing();
								lgp.getPlayer().getInventory().setItem(8, null);
								lgp.getPlayer().updateInventory();
								kill(choosen, lgp, this);
							}
						});
						lgp.getPlayer().getInventory().setItem(8, items[3]);
						lgp.getPlayer().updateInventory();
					}
				}else
					getGame().nextPreNight();
			}
		}.run();
	}
	protected void kill(LGPlayer choosen, LGPlayer dicta, Runnable callback) {
		RoleType roleType = choosen.getRoleType();
		
		LGPlayerKilledEvent killEvent = new LGPlayerKilledEvent(getGame(), choosen, Reason.DICTATOR);
		Bukkit.getPluginManager().callEvent(killEvent);
		if(killEvent.isCancelled())return;
		if(getGame().kill(killEvent.getKilled(), killEvent.getReason(), true))
			return;
		
		if(roleType != RoleType.VILLAGER) {
			getGame().broadcastMessage("§7§l"+dicta.getName()+" §9devient le §5§lCapitaine§9 du village.");
			getGame().setMayor(dicta);
		} else {
			getGame().kill(dicta, Reason.DICTATOR_SUICIDE);
			for(LGPlayer lgp : getGame().getInGame()) {
				if(lgp == dicta)
					lgp.sendMessage("§9§oÇa ne s'est pas passé comme prévu...");
				else
					lgp.sendMessage("§9Le "+getName()+"§9 s'est trompé, il mourra la nuit suivante.");
			}
		}
		callback.run();
	}
	
	@EventHandler
	public void onNightStart(LGNightStartEvent e) {
		if(e.getGame() == getGame()) {
			LGPlayer lgp = getGame().getDeaths().get(Reason.DICTATOR_SUICIDE);
			if(lgp != null)
				lgp.sendMessage("§8§oDes pensées sombres hantent ton esprit...");
		}
	}
}
