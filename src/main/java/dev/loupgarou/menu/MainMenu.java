package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.roles.RGarde;
import dev.loupgarou.roles.RLoupGarou;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;

public class MainMenu {
	
	public static void openMenu(@NonNull LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 5, "Menu principal"));
		
		ii.fillBorder(null, true);
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getItemMenu(FakeRoles.getRole(RGarde.class)))
				.name("§9Création d'une partie")
				.lore(
						Arrays.asList(
								"",
								"§7§oCliquez pour créer votre partie"
								)
						)
				.build(), 
				3, 2, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						CreateServerMenu.openMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.HEART))
				.name("§9Liste des parties")
				.lore(
						Arrays.asList(
								"",
								"§7§oCliquez pour afficher la liste"
								)
						)
				.build(), 
				5, 2, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						ListServerMenu.openMenu(lgp);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.OPTIONS))
				.name("§7Options")
				.lore(
						Arrays.asList(
								"",
								"§7§oCliquez pour afficher les options"
								)
						)
				.build(), 
				ii.getInv().getSize() - 9, true,
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						lgp.playAudio(Sound.ENTITY_VILLAGER_NO);
						lgp.sendMessage("§cIndisponible pour le moment...");
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getItemMenu(FakeRoles.getRole(RLoupGarou.class)))
				.name("§9Les Rôles")
				.lore(
						Arrays.asList(
								"",
								"§7§oCliquez pour afficher les rôles disponibles"
								)
						)
				.build(), 
				ii.getInv().getSize() - 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						DisplayRoleMenu.openMenu(lgp);
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
}
