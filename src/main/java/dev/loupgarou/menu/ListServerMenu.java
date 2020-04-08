package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;

public class ListServerMenu {

	public static void openMenu(@NonNull LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9 * 6, "Liste des parties"));
		
		int x = 0;
		int y = 0;
		
		for(LGGame game : MainLg.getInstance().getGames()) {
			if(game.isStarted()) continue;
			if(game.isEnded()) continue;
			
			ii.registerItem(
					new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.MID_ROLE))
					.name("§6Partie de " + game.getOwner().getName())
					.lore(Arrays.asList(
							"",
							"Joueurs : " + game.getInGame().size() + "/" + (game.getConfig().getTotalConfiguredRoles() < game.getInGame().size() ? game.getConfig().getMap().getSpawns().size() : game.getConfig().getTotalConfiguredRoles()),
							""
							))
					.build(), 
					x, y, true, 
					new InventoryCall() {
						
						@Override
						public void click(HumanEntity human, ItemStack item, ClickType clickType) {
							//TODO Invite system
							if(game.getConfig().getBanned().contains(human.getName())) return;
							if(game.getConfig().isPrivateGame()) return;//TODO MSG private
							
							game.tryToJoin(lgp);
						}
					});
			
			if(x >= 8) {
				x = 0;
				y++;
			}
		}
		
		ii.openTo(lgp.getPlayer());
	}
	
	
	
}
