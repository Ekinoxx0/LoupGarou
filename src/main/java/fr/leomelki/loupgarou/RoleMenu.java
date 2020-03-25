package fr.leomelki.loupgarou;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.leomelki.loupgarou.utils.InteractInventory;
import fr.leomelki.loupgarou.utils.InteractInventory.InventoryCall;
import fr.leomelki.loupgarou.utils.ItemBuilder;

public class RoleMenu {
	
	public void openMenu(Player p) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(p, 3 * 9, ""));
		
		int total = 0;
		for(String role : MainLg.getInstance().getRoles().keySet()) {
			if(MainLg.getInstance().getConfig().getInt("role."+role) > 0) {
				total += MainLg.getInstance().getConfig().getInt("role."+role);
				
				ii.registerItem(
						new ItemBuilder(Material.LIGHT_GRAY_WOOL)
							.name("§9" + role)
							.lore(Arrays.asList(
									"§7" + MainLg.getInstance().getConfig().getInt("role."+role)
									))
							.build(), 
						3*9-1, true, new InventoryCall() {
							
							@Override
							public void click(HumanEntity human, ItemStack item, ClickType clickType) {
								switch(clickType) {
								case RIGHT:
								case LEFT:
								case MIDDLE:
									break;
								case SHIFT_LEFT:
								case SHIFT_RIGHT:
									break;
								default:
									p.sendMessage("§7Clic inconnu.");
									break;
								
								}
							}
						});
			}
		}
		
		ii.registerItem(
				new ItemBuilder(Material.LIGHT_GRAY_WOOL)
					.name("§9Total : " + total)
					.build(), 
				3*9-1, true, null);
		
		ii.openTo(p);
	}
	
}
