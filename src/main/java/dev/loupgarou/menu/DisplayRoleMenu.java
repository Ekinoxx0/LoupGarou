package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.utils.CommonText;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;

public class DisplayRoleMenu {
	
	public static void openMenu(LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 4 * 9, "Les Rôles"));
		
		int i = 0;
		for(Class<? extends Role> roleClazz : MainLg.getInstance().getRoles().keySet()) {
			Role fakeRole = FakeRoles.getRole(roleClazz);
			ii.registerItem(
					new ItemBuilder(LGCustomItems.getItemMenu(fakeRole))
						.name(fakeRole.getColor() + fakeRole.getName())
						.lore(Arrays.asList(
								"§f" + CommonText.optimizeLines(fakeRole.getDescription())
								))
						.build(), 
					i, true, null);
			i++;
		}
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.CHECK))
					.name("§a" + MainLg.getInstance().getRoles().size() + " rôles au total")
					.amount(MainLg.getInstance().getRoles().size())
					.build(), 
				ii.getInv().getSize() - 1, true, new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						MainMenu.openMenu(lgp);
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
}
