package dev.loupgarou.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.roles.RSorciere;
import dev.loupgarou.roles.RVoyante;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.utils.CommonText.PrefixType;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;

public class PartieMenu {
	
	private static final String TITLE = "Menu de partie";
	private @NonNull final LGGame game;
	private @NonNull final RoleMenu roleMenu;
	private @NonNull final AutoRoleMenu autoRoleMenu;
	
	public PartieMenu(@NonNull LGGame game) {
		this.game = game;
		this.roleMenu = new RoleMenu(game);
		this.autoRoleMenu = new AutoRoleMenu(game);
	}
	
	public void openPartieMenu(LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 5 * 9, TITLE));
		ii.fillBorder(null, true);
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getItemMenu(FakeRoles.getRole(RSorciere.class)))
				.name("§9Gestion des rôles")
				.build(), 
				3, 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						openRoleMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getItemMenu(FakeRoles.getRole(RVoyante.class)))
				.name("§9Gestion automatique des rôles")
				.build(), 
				5, 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						openAutoRoleMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(game.getConfig().isHideRole() ? SpecialItems.GREEN_ROLE : SpecialItems.RED_ROLE))
				.name("§9Rôles cachés : " + (game.getConfig().isHideRole() ? "§aOUI" : "§cNON"))
				.build(), 
				3, 3, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						if(lgp.getGame().getOwner() != lgp) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
							return;
						}
						
						lgp.getGame().getConfig().setHideRole(!lgp.getGame().getConfig().isHideRole());
						if(lgp.getGame().getConfig().isHideRole()) {
							lgp.sendMessage(PrefixType.PARTIE + "§cComposition cachée");
						} else {
							lgp.sendMessage(PrefixType.PARTIE + "§9Composition affichée");
						}
						openPartieMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(game.getConfig().isHideVote() ? SpecialItems.GREEN_ROLE : SpecialItems.RED_ROLE))
				.name("§9Votes cachés : " + (game.getConfig().isHideVote() ? "§aOUI" : "§cNON"))
				.build(), 
				4, 3, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						if(lgp.getGame().getOwner() != lgp) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
							return;
						}

						lgp.getGame().getConfig().setHideVote(!lgp.getGame().getConfig().isHideVote());
						if(lgp.getGame().getConfig().isHideVote()) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVote cachée");
						} else {
							lgp.sendMessage(PrefixType.PARTIE + "§9Vote affichés");
						}
						openPartieMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(game.getConfig().isHideVoteExtra() ? SpecialItems.GREEN_ROLE : SpecialItems.RED_ROLE))
				.name("§9Votes complètement cachés : " + (game.getConfig().isHideVoteExtra() ? "§aOUI" : "§cNON"))
				.build(), 
				5, 3, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						if(lgp.getGame().getOwner() != lgp) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
							return;
						}
						
						lgp.getGame().getConfig().setHideVoteExtra(!lgp.getGame().getConfig().isHideVoteExtra());
						if(lgp.getGame().getConfig().isHideVoteExtra()) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVote extra cachée");
						} else {
							lgp.sendMessage(PrefixType.PARTIE + "§9Vote extra affichée");
						}
						openPartieMenu(lgp);
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
	/*
	 * Others menu
	 */

	public void openRoleMenu(LGPlayer lgp) {
		if(game.getConfig().isHideRole()) {
			lgp.sendMessage(PrefixType.PARTIE + "§cLes rôles sont cachés durant cette partie...");
			return;
		}
		
		roleMenu.openMenu(lgp);
	}
	
	public void openAutoRoleMenu(LGPlayer lgp) {
		autoRoleMenu.openMenu(lgp);
	}
	
}
