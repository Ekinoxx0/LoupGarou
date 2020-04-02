package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.classes.LGGameConfig;
import dev.loupgarou.classes.LGGameConfig.CommunicationType;
import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;

public class CreateServerMenu {

	public static void openMenu(@NonNull LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 3, "Création d'une partie"));
		
		ii.fill(null, true, null);
		
		ii.registerItem(
				new ItemBuilder(Material.OBSIDIAN)
				.name("§aPartie publique")
				.build(), 
				3, 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						chooseMap(lgp, false);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(Material.CACTUS)
				.name("§cPartie privée")
				.build(), 
				5, 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						chooseMap(lgp, true);
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
	private static void chooseMap(@NonNull LGPlayer lgp, boolean isPrivate) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 3, "Sélection de la map"));
		
		ii.fill(null, true, null);
		
		Material[] mat = new Material[] {Material.POLISHED_GRANITE, Material.POLISHED_DIORITE};
		
		int i = 3;
		for(LGMap map : LGMaps.getMapsInfo().getMaps()) {
			ii.registerItem(
					new ItemBuilder(mat[LGMaps.getMapsInfo().getMaps().indexOf(map)])
					.name("§6" + map.getName())
					.lore(
							Arrays.asList(
									)
							)
					.build(), 
					i, 1, true, 
					new InventoryCall() {
						
						@Override
						public void click(HumanEntity human, ItemStack item, ClickType clickType) {
							chooseType(lgp, new LGGameConfig(map, isPrivate));
						}
					});
			
			i += 2;
		}
		
		ii.openTo(lgp.getPlayer());
	}
	
	private static void chooseType(@NonNull LGPlayer lgp, LGGameConfig config) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 3, "Sélection du type de tchat"));
		
		ii.fill(null, true, null);
		
		ii.registerItem(
				new ItemBuilder(Material.HAY_BLOCK)
				.name("§6Sélection du type de tchat")
				.lore(
						Arrays.asList(
								"§9Vocal sur Discord",
								"",
								"§7Si vous choississez ce mode, nous allons vous créer un",
								"§7salon vocal sur Discord qui sera automatisé pour les nuits",
								"§7et les morts de joueurs."
								)
						)
				.build(), 
				3, 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						config.setCom(CommunicationType.DISCORD);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(Material.JUNGLE_SIGN)
				.name("§6Sélection du type de tchat")
				.lore(
						Arrays.asList(
								"§6Textuel",
								"",
								"§7Si vous choississez ce mode, vous pourrez discuter",
								"§7avec les autres joueurs via le tchat textuel."
								)
						)
				.build(), 
				5, 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
	
	
	
}