package dev.loupgarou.roles;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.InteractInventory.InventoryClose;
import dev.loupgarou.utils.ItemBuilder;
import dev.loupgarou.utils.VariableCache.CacheType;

public class RChienLoup extends Role{
	public RChienLoup(LGGame game) {
		super(game);
	}
	
	@Override
	public int getMaxNb() {
		return Integer.MAX_VALUE;
	}

	@Override
	public String getColor() {
		return "§a";
	}
	
	@Override
	public String getName() {
		return getColor() + "§lChien-Loup";
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
		return "Tu gagnes avec le §a§lVillage§f. Au début de la première nuit, tu peux choisir entre rester fidèle aux §a§lVillageois§f ou alors rejoindre le clan des §c§lLoups-Garous§f.";
	}

	@Override
	public String getTask() {
		return "Souhaites-tu devenir un §c§lLoup-Garou§6 ?";
	}

	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 pourrait trouver de nouveaux amis...";
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
	public int getTimeout() {
		return 15;
	}
	
	@Override
	public boolean hasPlayersLeft() {
		for(LGPlayer lgp : getPlayers())
			if(!lgp.getCache().getBoolean(CacheType.HAS_CHOOSEN_CHIEN_LOUP))
				return true;
			
		return false;
	}
	
	public void openInventory(LGPlayer lgp, Runnable callback) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9, "§7Choisis ton camp."));
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.GREEN_ROLE_Q))
				.name("§2Devenir Villageois")
				.lore(Arrays.asList(
						"§7§lVous n'aurez aucun pouvoir mais",
						"§7§lresterez dans le camp du §a§lVillage§7§l."
						))
				.build(), 
				
				3, true, new InventoryCall() {
			
			@Override
			public void click(HumanEntity human, ItemStack item, ClickType clickType) {
				lgp.getCache().set(CacheType.HAS_CHOOSEN_CHIEN_LOUP, true);
				human.closeInventory();
				lgp.sendActionBarMessage("§6Tu resteras fidèle au §a§lVillage§6.");
				lgp.sendMessage("§6Tu resteras fidèle au §a§lVillage§6.");
				lgp.hideView();
				callback.run();
			}
		});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.RED_ROLE_Q))
				.name("§cDevenir Loup-Garou")
				.lore(Arrays.asList(
						"§cVous rejoindrez le camp des §c§lLoups"
						))
				.build(), 
				
				5, true, new InventoryCall() {
			
			@Override
			public void click(HumanEntity human, ItemStack item, ClickType clickType) {
				human.closeInventory();
				lgp.getCache().set(CacheType.HAS_CHOOSEN_CHIEN_LOUP, true);

				lgp.sendActionBarMessage("§6Tu as changé de camp.");
				lgp.sendMessage("§6Tu as changé de camp.");
				
				//On le fait aussi rejoindre le camp des loups pour le tour pendant la nuit.
				RChienLoupLG lgChienLoup = null;
				for(Role role : getGame().getRoles())
					if(role instanceof RChienLoupLG)
						lgChienLoup = (RChienLoupLG)role;
				
				if(lgChienLoup == null)
					getGame().getRoles().add(lgChienLoup = new RChienLoupLG(getGame()));
				
				lgChienLoup.join(lgp, false);
				lgp.updateOwnSkin();
				
				lgp.hideView();
				callback.run();
			}
		});
		
		ii.setCloseAction(new InventoryClose() {
			
			@Override
			public boolean close(InteractInventory ii, HumanEntity human) {
				return lgp.getRole() != RChienLoup.this || getGame().isEnded() || lgp.getCache().getBoolean(CacheType.HAS_CHOOSEN_CHIEN_LOUP);
			}

			@Override
			public void nextTick(InteractInventory ii, HumanEntity human) {
				if(lgp.getRole() != RChienLoup.this) return;
				if(getGame().isEnded()) return;
				
				if(!lgp.getCache().getBoolean(CacheType.HAS_CHOOSEN_CHIEN_LOUP))
					ii.openTo(lgp.getPlayer());
			}
		});
		
		ii.openTo(lgp.getPlayer());
	}
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		openInventory(player, callback);
	}
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getCache().set(CacheType.HAS_CHOOSEN_CHIEN_LOUP, true);
		player.getPlayer().closeInventory();
		player.hideView();
		player.sendActionBarMessage("§6Tu rejoins le §a§lVillage.");
		player.sendMessage("§6Tu rejoins le §a§lVillage.");
	}

}
