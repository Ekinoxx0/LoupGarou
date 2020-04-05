package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGGameConfig;
import dev.loupgarou.classes.LGGameConfig.CommunicationType;
import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.InteractInventory.InventoryClose;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;

public class CreateServerMenu {
	
	private static final InventoryClose canceledServerCreation = new InventoryClose() {
		
		@Override
		public boolean close(HumanEntity human) {
			if(human.getOpenInventory() == null && LGPlayer.thePlayer((Player) human).getGame() == null)
				human.sendMessage("§6Annulation de la création de la partie...");
			return true;
		}
	};

	public static void openMenu(@NonNull LGPlayer lgp) {
		if(lgp.getGame() != null) {
			lgp.sendMessage("§cVous êtes déjà en partie...");
			return;
		}
		
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

		ii.setCloseAction(canceledServerCreation);
		
		ii.openTo(lgp.getPlayer());
	}
	
	private static void chooseMap(@NonNull LGPlayer lgp, boolean isPrivate) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 3, "Sélection de la map"));
		
		ii.fill(null, true, null);
		
		int i = 3;
		for(LGMap map : LGMaps.getMapsInfo().getMaps()) {
			if(!map.isValid()) continue;
			ii.registerItem(
					new ItemBuilder(map.getMaterial())
					.name("§6§l" + map.getName())
					.lore(
							Arrays.asList(
									"",
									"§7" + map.getDescription(),
									"",
									"§7" + map.getSpawns().size() + " joueurs maximum"
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
			
			i++;
		}
		
		ii.setCloseAction(canceledServerCreation);
		
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
						validate(lgp, config);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(Material.JUNGLE_SIGN)
				.name("§6Sélection du type de tchat")
				.lore(
						Arrays.asList(
								"§dTextuel",
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
						validate(lgp, config);
					}
				});
		
		ii.setCloseAction(canceledServerCreation);
		
		ii.openTo(lgp.getPlayer());
	}
	
	private static void validate(@NonNull LGPlayer lgp, LGGameConfig config) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9, "Valider votre choix..."));
		
		ii.fill(null, true, null);
		
		ii.registerItem(
				new ItemBuilder(Material.GOLD_NUGGET)
				.name("§2Valider la configuration :")
				.lore(
						Arrays.asList(
								"",
								"§7Map : " + config.getMap().getName(),
								"§7Communication : " + config.getCom()
								)
						)
				.build(), 
				3, 0, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						human.closeInventory();
						LGGame created = new LGGame(lgp, config);
						if(!created.tryToJoin(lgp)) {
							human.sendMessage("§cUne erreur est survenue lors de la création de votre partie ! #8841654");
						}
					}
				});
		
		ii.registerItem(
				new ItemBuilder(Material.IRON_NUGGET)
				.name("§cAnnuler")
				.lore(
						Arrays.asList(
								)
						)
				.build(), 
				5, 0, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						human.closeInventory();
					}
				});
		
		ii.setCloseAction(canceledServerCreation);
		
		ii.openTo(lgp.getPlayer());
	}
	
	
	
	
}
