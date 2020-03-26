package dev.loupgarou.loupgarou;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.loupgarou.utils.InteractInventory;
import dev.loupgarou.loupgarou.utils.ItemBuilder;
import dev.loupgarou.loupgarou.utils.InteractInventory.InventoryCall;

public class RoleMenu {
	
	public void openMenu(Player p) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(p, 3 * 9, ""));
		
		int i = 0;
		int total = 0;
		for(String role : MainLg.getInstance().getRoles().keySet()) {
				total += MainLg.getInstance().getConfig().getInt("role."+role);
				
				ii.registerItem(
						new ItemBuilder(Material.LIGHT_GRAY_WOOL)
							.name("§9" + role)
							.lore(Arrays.asList(
									"§7" + MainLg.getInstance().getConfig().getInt("role."+role)
									))
							.build(), 
						i, true, new InventoryCall() {
							
							@Override
							public void click(HumanEntity human, ItemStack item, ClickType clickType) {
								int nb = MainLg.getInstance().getConfig().getInt("role."+role);
								int modif = 0;
								
								switch(clickType) {
								
								case RIGHT:
								case LEFT:
								case MIDDLE:
									modif = +1;
									break;
									
								case SHIFT_LEFT:
								case SHIFT_RIGHT:
									modif = -1;
									break;
									
								default:
									p.sendMessage("§7Clic inconnu.");
									break;
								}
								
								p.sendMessage(MainLg.getPrefix()+"§6Il y aura §e" + (nb + modif) + " §6"+role);
								MainLg.getInstance().getConfig().set("role."+role, nb + modif);
								MainLg.getInstance().saveConfig();
								MainLg.getInstance().loadConfig();
							}
						});
				i++;
		}
		
		ii.registerItem(
				new ItemBuilder(Material.LIGHT_GRAY_WOOL)
					.name("§9Total : " + total)
					.build(), 
				3*9-1, true, null);
		
		ii.openTo(p);
	}
	
}
