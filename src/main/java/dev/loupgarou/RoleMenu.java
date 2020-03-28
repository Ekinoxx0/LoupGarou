package dev.loupgarou;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.roles.Role;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;

public class RoleMenu {
	
	private static final LGGame fakeGame = new LGGame(0);
	private static final HashMap<String, Role> roles = new HashMap<String, Role>();
	
	private static Role getRole(String name) {
		for(Entry<String, Role> entry : roles.entrySet())
			if(entry.getKey().equals(name))
				return entry.getValue();
		
		try {
			Role r = MainLg.getInstance().getRoles().get(name).newInstance(fakeGame);
			roles.put(name, r);
			return r;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void openMenu(Player p) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(p, 4 * 9, "Sélection des rôles"));
		
		int i = 0;
		int total = 0;
		for(String role : MainLg.getInstance().getRoles().keySet()) {
				total += MainLg.getInstance().getConfig().getInt("role."+role);
				
				ii.registerItem(
						new ItemBuilder(getRole(role).getType().getMaterial())
							.name(getRole(role).getType().getColor() + role)
							.lore(Arrays.asList(
									"§7" + MainLg.getInstance().getConfig().getInt("role."+role),
									"",
									"§f" + optimizeLines(getRole(role).getDescription())
									))
							.build(), 
						i, true, new InventoryCall() {
							
							@Override
							public void click(HumanEntity human, ItemStack item, ClickType clickType) {
								if(!human.hasPermission("loupgarou.admin")) return;
								
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
								openMenu(p);
							}
						});
				i++;
		}
		
		ii.registerItem(
				new ItemBuilder(Material.GOLD_NUGGET)
					.name("§aTotal : " + total)
					.build(), 
				4*9-1, true, null);
		
		ii.openTo(p);
	}
	
	/*
	 */
	
	public static String optimizeLines(String lore) {
		int maxWord = 5;
		
		int a = 0;
		int b = 0;
		
		for(String s : lore.split(" ")){
			a += s.length();
			b++;
			if(a >= 30){
				maxWord--;
			}
			if(b >= maxWord){
				a = 0;
				b = 0;
			}
		}
		
		return optimizeLines(lore, maxWord);
	}

	public static String optimizeLines(String text, int nbWordPerLines) {
    	int wordCountInLine = 0;
    	String result = "";
    	String[] words = text.split(" ");
    	
    	String currentLine = "";
    	int i = 0;
    	for (String word : words) {
    		currentLine += word + " ";
    		wordCountInLine++;
    		
    		if (wordCountInLine >= nbWordPerLines && i < words.length-1) {
    			result += currentLine + "\n";//Add color
    			char color = 'f';
    			
    			char[] charCurrentLine = currentLine.toCharArray();
    			for (int j = 0; j < charCurrentLine.length; j++)
    				if(charCurrentLine[j] == '§')
    					color = charCurrentLine[j + 1];
    			
    			currentLine = "§" + color;
    			wordCountInLine = 0;
    		}
    		i++;
    	}
    	
    	return result;
	}
	
}
