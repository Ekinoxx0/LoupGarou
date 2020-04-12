package dev.loupgarou.menu.game;

import java.util.Arrays;

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

public class GameMenu {
	
	private static final String TITLE = "Menu de partie";
	private @NonNull final LGGame game;
	private @NonNull final RoleMenu roleMenu;
	private @NonNull final AutoRoleMenu autoRoleMenu;
	
	public GameMenu(@NonNull LGGame game) {
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
				.lore(Arrays.asList("§7La liste des rôles sera cachée. Mais les rôles seront affichés à la mort."))
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
				.lore(Arrays.asList("§7Les noms des messages de votes seront masqués."))
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
				.lore(Arrays.asList("§7Le nombre de vote pour chaque joueur sera caché."))
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
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(game.getConfig().isHideVoteRole() ? SpecialItems.GREEN_ROLE : SpecialItems.RED_ROLE))
				.name("§9Votes Rôles cachés : " + (game.getConfig().isHideVoteRole() ? "§aOUI" : "§cNON"))
				.lore(Arrays.asList(
						"§7Permet de masquer les votes spécifiques",
						"§7aux rôles tel que Loup Garou ou Vampires.",
						"",
						"§7Nous recommandons l'activation de cette option",
						"§7si la Petite Fille est active car il forcera les Loup Garou",
						"§7à utiliser leur chat textuel"
						))
				.build(), 
				4, 4, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						if(lgp.getGame().getOwner() != lgp) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
							return;
						}

						lgp.getGame().getConfig().setHideVoteRole(!lgp.getGame().getConfig().isHideVoteRole());
						if(lgp.getGame().getConfig().isHideVoteRole()) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVote Rôles cachée");
						} else {
							lgp.sendMessage(PrefixType.PARTIE + "§9Vote Rôles affichés");
						}
						openPartieMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.CHECK))
				.name("§2Démarrer la partie")
				.lore(Arrays.asList("", "§7§oCliquez pour lancer la partie"))
				.build(), 
				ii.getInv().getSize() - 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						if(lgp.getGame().getOwner() != lgp) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
							return;
						}
						
						game.updateStart();
						human.closeInventory();
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
	/*
	 * Others menu
	 */

	public void openRoleMenu(LGPlayer lgp) {
		roleMenu.openMenu(lgp);
	}
	
	public void openAutoRoleMenu(LGPlayer lgp) {
		autoRoleMenu.openMenu(lgp);
	}

	public boolean hasConfiguredAuto() {
		return autoRoleMenu.total() > 0;
	}
	
}
