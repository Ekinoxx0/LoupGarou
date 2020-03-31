package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;

public class MainMenu {
	
	public static void openMenu(@NonNull LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 5, "Menu principal"));
		
		ii.fillBorder(null, true);
		
		ii.registerItem(
				new ItemBuilder(Material.DIAMOND_BLOCK)
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
				new ItemBuilder(Material.JUKEBOX)
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
		
		ii.openTo(lgp.getPlayer());
	}
	
}
