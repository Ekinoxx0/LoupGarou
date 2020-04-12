package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.utils.CommonText.PrefixType;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;

public class ListServerMenu {

	public static void openMenu(@NonNull LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 5, "Liste des parties"));
		
		int x = 0;
		int y = 0;
		
		for(LGGame game : MainLg.getInstance().getGames()) {
			if(game.isStarted()) continue;
			if(game.isEnded()) continue;
			if(game.getConfig().getBanned().contains(lgp.getName())) continue;
			
			ii.registerItem(
					new ItemBuilder(Material.PLAYER_HEAD)
					.name("§6Partie de " + game.getOwner().getName())
					.lore(Arrays.asList(
							"",
							"§7Joueurs : " + game.getInGame().size() + "/" + (game.getConfig().getTotalConfiguredRoles() < game.getInGame().size() ? game.getConfig().getMap().getSpawns().size() : game.getConfig().getTotalConfiguredRoles()),
							"§7Map : " + game.getConfig().getMap().getName(),
							"§7Communication : " + game.getConfig().getCom()
							))
					.build(), 
					x, y, true, 
					new InventoryCall() {
						
						@Override
						public void click(HumanEntity human, ItemStack item, ClickType clickType) {
							if(game.getConfig().getBanned().contains(human.getName())) {
								lgp.sendMessage(PrefixType.PARTIE + "§cPartie indisponible.");
								lgp.playAudio(Sound.ENTITY_VILLAGER_NO);
								return;
							}
							if(game.getConfig().isPrivateGame() && !game.getConfig().getInvited().contains(human.getName())) {
								lgp.sendMessage(PrefixType.PARTIE + "§cPartie privée.");
								lgp.playAudio(Sound.ENTITY_VILLAGER_NO);
								return;
							}
							
							game.tryToJoin(lgp);
						}
					});
			
			if(x >= 8) {
				x = 0;
				y++;
			}
		}
		
		if(MainLg.getInstance().getGames().isEmpty()) {
			ii.registerItem(
					new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.CROSS))
					.name("§7Aucune partie disponible...")
					.build(), 
					4, 2, true, 
					new InventoryCall() {
						
						@Override
						public void click(HumanEntity human, ItemStack item, ClickType clickType) {
							human.closeInventory();
						}
					});
		}
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.GREY_ROLE_Q))
				.name("§8" + MainLg.getInstance().getGames().size() + " partie")
				.build(), 
				ii.getInv().getSize() - 9, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						human.closeInventory();
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
	
	
}
