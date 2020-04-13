package dev.loupgarou.menu.game;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGGameConfig.CommunicationType;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.roles.RPronostiqueur;
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
	private @NonNull final GameOptionsMenu gameOptions;
	
	public GameMenu(@NonNull LGGame game) {
		this.game = game;
		this.roleMenu = new RoleMenu(game);
		this.autoRoleMenu = new AutoRoleMenu(game);
		this.gameOptions = new GameOptionsMenu(game);
	}
	
	public void openGameMenu(LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 5 * 9, TITLE));
		ii.fillBorder(null, true);
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getItemMenu(FakeRoles.getRole(RSorciere.class)))
				.name("§9Gestion des rôles")
				.build(), 
				2, 2, true, 
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
				3, 2, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						openAutoRoleMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getItemMenu(FakeRoles.getRole(RPronostiqueur.class)))
				.name("§9§mSauvegarde des configurations")
				.lore(Arrays.asList("§cIndisponible."))
				.build(), 
				5, 2, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						lgp.playAudio(Sound.ENTITY_VILLAGER_HURT);
						lgp.sendMessage("§cIndisponible pour le moment...");
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.OPTIONS))
				.name("§6Options")
				.build(), 
				6, 2, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						openGameOptionsMenu(lgp);
					}
				});
		
		String discordLink = "";
		if(this.game.getConfig().getCom() == CommunicationType.DISCORD && this.game.getDiscord().getVoice().getMembers().size() > 0) {
			discordLink += "\n§9Salon Discord : " + this.game.getDiscord().getVoice().getMembers().size() + "/" + this.game.getInGame().size();
			
			String notLinked = "\nNon lié à discord :";
			boolean hasAny = false;
			
			for(LGPlayer allLgp : this.game.getInGame()) {
				boolean m = MainLg.getInstance().getDiscord().isRecognized(lgp);
				
				if(!m) {
					hasAny = true;
					notLinked += "§7 - " + allLgp.getName() + "\n";
				}
			}
			
			if(hasAny)
				discordLink += notLinked;
		}
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(game.getConfig().verifyRoles() == null ? SpecialItems.CHECK : SpecialItems.CROSS))
				.name("§2Démarrer la partie")
				.glow(true)
				.lore(Arrays.asList(discordLink, "§7§oCliquez pour lancer la partie"))
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
	
	public void openGameOptionsMenu(LGPlayer lgp) {
		gameOptions.openMenu(lgp);
	}

	public boolean hasConfiguredAuto() {
		return autoRoleMenu.total() > 0;
	}
	
}
