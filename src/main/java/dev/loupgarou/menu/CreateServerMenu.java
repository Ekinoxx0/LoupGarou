package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGGameConfig;
import dev.loupgarou.classes.LGGameConfig.CommunicationType;
import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.utils.CommonText.PrefixType;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;

public class CreateServerMenu {
	
	public static void openMenu(@NonNull LGPlayer lgp) {
		if(MainLg.getInstance().isMaintenanceMode()) {
			lgp.sendMessage(PrefixType.PARTIE + "§4§lDémarrage impossible car une maintenance serveur va bientôt démarrer.");
			lgp.sendMessage(PrefixType.PARTIE + "§4§lPendant cette période, les parties sont suspendues et nous vous demandons de patienter.");
			return;
		}
		if(lgp.getGame() != null) {
			lgp.sendMessage(PrefixType.PARTIE + "§cVous êtes déjà en partie...");
			return;
		}
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 3, "Création d'une partie"));
		
		ii.fill(null, true, null);
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.GREEN_ROLE))
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
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.RED_ROLE))
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
		
		int i;
		switch(LGMaps.getMapsInfo().getMaps().size()) {
		case 4:
		case 5:
			i = 2;
			break;
		case 6:
		case 7:
			i = 1;
			break;
		case 8:
		case 9:
			i = 0;
			break;
		default:
			i = 0;
			break;
		}
		
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

			if(LGMaps.getMapsInfo().getMaps().size()%2 == 0 && i == 3)
				i++; //Add space in the middle
			
			i++;
		}
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.MID_ROLE_Q))
				.name("§9§lBientôt votre map ?")
				.lore(
						Arrays.asList(
								"",
								"§7Nous acceptons les propositions de maps certaines conditions :",
								"§7 - La map doit être petite 150x150 maximum",
								"§7 - La map doit être votre création ou être disponible gratuitement",
								"§7 - La map doit contenir 12/24/36 places déjà placées",
								"§7 - La map doit être jouable sans pack de ressources supplémentaire",
								"§7 - La map ne doit pas contenir de redstone ou des commandes blocs",
								"",
								"§8Autre détails :",
								"§8 - Nous acceptons les maps toutes versions (1.7 - 1.16)",
								"§8 - La physique de la map sera désactivée (sable/échelle/feuille/etc..)",
								""
								)
						)
				.build(), 
				ii.getInv().getSize() - 1, true, null);
		
		ii.openTo(lgp.getPlayer());
	}
	
	private static void chooseType(@NonNull LGPlayer lgp, LGGameConfig config) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 3, "Sélection du type de tchat"));
		
		ii.fill(null, true, null);
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.DISCORD))
				.name("§6Sélection du type de tchat")
				.lore(
						Arrays.asList(
								"§9Vocal sur Discord",
								"",
								"§7Si vous choississez ce mode, nous allons vous créer un",
								"§7salon vocal sur Discord qui sera automatisé pour les nuits",
								"§7et les morts de joueurs.",
								"",
								MainLg.getInstance().getDiscord().getLinkServer().isLinked(lgp) ? "§cVous n'êtes pas lié à Discord" : "§2Vous êtes lié à Discord"
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
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.TEXTUAL))
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
		
		ii.openTo(lgp.getPlayer());
	}
	
	private static void validate(@NonNull LGPlayer lgp, LGGameConfig config) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9, "Valider votre choix..."));
		
		ii.fill(null, true, null);
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.CHECK))
				.name("§2Valider la configuration :")
				.lore(
						Arrays.asList(
								"",
								"§7Map : " + config.getMap().getName(),
								"§7Communication : " + config.getCom(),
								"§7Places maximum : " + config.getMap().getSpawns().size()
								)
						)
				.build(), 
				3, 0, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						human.sendMessage(PrefixType.PARTIE + "§7Création de la partie en cours...");
						human.closeInventory();
						LGGame created = new LGGame(lgp, config);
						new BukkitRunnable() {
							
							@Override
							public void run() {
								if(!created.tryToJoin(lgp))
									human.sendMessage(PrefixType.PARTIE + "§cUne erreur est survenue lors de la création de votre partie ! #8841654");
							}
						}.runTaskLater(MainLg.getInstance(), 20);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.CROSS))
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
		
		ii.openTo(lgp.getPlayer());
	}
	
	
	
	
}
