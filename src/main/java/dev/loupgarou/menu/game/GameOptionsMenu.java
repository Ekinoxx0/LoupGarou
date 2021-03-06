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
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import dev.loupgarou.utils.CommonText.PrefixType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * TODO Ninja vote
 */
@RequiredArgsConstructor
public class GameOptionsMenu {
	
	private static final String TITLE = "Options de partie";
	private @NonNull final LGGame game;

	public void openMenu(LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 4 * 9, TITLE));

		ii.fillBorder(null, true);
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(game.getConfig().isHideRole() ? SpecialItems.GREEN_ROLE : SpecialItems.RED_ROLE))
				.name("§9Rôles cachés : " + (game.getConfig().isHideRole() ? "§aOUI" : "§cNON"))
				.lore(Arrays.asList("§7La liste des rôles sera cachée. Mais les rôles seront affichés à la mort."))
				.build(), 
				3, 1, true, 
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
						openMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(game.getConfig().isHideVote() ? SpecialItems.GREEN_ROLE : SpecialItems.RED_ROLE))
				.name("§9Votes cachés : " + (game.getConfig().isHideVote() ? "§aOUI" : "§cNON"))
				.lore(Arrays.asList("§7Les noms des messages de votes seront masqués."))
				.build(), 
				4, 1, true, 
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
						
						reloadMenu();
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(game.getConfig().isHideVoteExtra() ? SpecialItems.GREEN_ROLE : SpecialItems.RED_ROLE))
				.name("§9Votes complètement cachés : " + (game.getConfig().isHideVoteExtra() ? "§aOUI" : "§cNON"))
				.lore(Arrays.asList("§7Le nombre de vote pour chaque joueur sera caché."))
				.build(), 
				5, 1, true, 
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
						
						reloadMenu();
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
						"§7si une Petite Fille est présente car elle forcera les Loups Garous",
						"§7à utiliser leur chat textuel"
						))
				.build(), 
				4, 2, true, 
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
						
						reloadMenu();
					}
				});
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.GREY_ROLE_Q))
				.name("§9Temps de vote par personne : " + this.game.getConfig().getTimerDayPerPlayer() + " secondes")
				.amount(this.game.getConfig().getTimerDayPerPlayer() <= LGCustomItems.getSpecialItem(SpecialItems.GREY_ROLE_Q).getMaxStackSize() ? this.game.getConfig().getTimerDayPerPlayer() : 1)
				.lore(Arrays.asList(
						"§7Permet d'augmenter ou de diminuer le temps",
						"§7de vote par personne durant le vote de jour.",
						"§7Par défaut : 15 secondes x 12 personnes = 3 minutes",
						"",
						"§8Clique gauche pour augmenter, droit pour diminuer"
						))
				.build(), 
				5, 2, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						if(lgp.getGame().getOwner() != lgp) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
							return;
						}
						
						int modif = 0;
						
						switch(clickType) {
						
						case RIGHT:
						case SHIFT_RIGHT:
							modif = -5;
							if(lgp.getGame().getConfig().getTimerDayPerPlayer() <= 5)
								modif = 0;
							break;
							
						case LEFT:
						case SHIFT_LEFT:
							modif = 5;
							if(lgp.getGame().getConfig().getTimerDayPerPlayer() >= 60)
								modif = 0;
							break;
								
						default:
							return;
						}
						
						if(modif == 0) {
							lgp.sendMessage(PrefixType.PARTIE + "§cLimite atteinte");
							return;
						}

						lgp.getGame().getConfig().setTimerDayPerPlayer(lgp.getGame().getConfig().getTimerDayPerPlayer() + modif);
						lgp.sendMessage(PrefixType.PARTIE + "§9Temps de vote : " + lgp.getGame().getConfig().getTimerDayPerPlayer() + " secondes");
						reloadMenu();
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.BACKARROW))
				.name("§7Retour")
				.build(), 
				ii.getInv().getSize() - 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						game.getGameMenu().openGameMenu(lgp);
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
	public void reloadMenu() {
		for(LGPlayer lInGame : game.getInGame())
			if(lInGame.getPlayer() != null && lInGame.getPlayer().getOpenInventory() != null && lInGame.getPlayer().getOpenInventory().getTitle().equals(TITLE))
				openMenu(lInGame);
	}
	
}
