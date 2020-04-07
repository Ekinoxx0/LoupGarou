package dev.loupgarou.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * TODO add item in waiting
 * TODO add tip to shift clic
 * TODO Auto role
 * TODO Save compo
 */
@RequiredArgsConstructor
public class RoleMenu {
	
	private static final String TITLE = "Sélection des rôles";
	private @NonNull final LGGame game;

	public void openMenu(LGPlayer lgp) {
		if(game.getConfig().isHideRole()) {
			validate(lgp);
		} else {
			openRealMenu(lgp);
		}
	}
	
	private void openRealMenu(LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 4 * 9, TITLE));
		
		int i = 0;
		int total = 0;
		for(String roleName : MainLg.getInstance().getRoles().keySet()) {
			int nbRole = game.getConfig().getRoles().get(roleName);
			total += nbRole;
			ii.registerItem(
					new ItemBuilder(LGCustomItems.getItemMenu(FakeRoles.getRole(roleName)))
						.name(FakeRoles.getRole(roleName).getColor() + roleName)
						.lore(Arrays.asList(
								"§7" + nbRole,
								"",
								"§f" + optimizeLines(FakeRoles.getRole(roleName).getDescription())
								))
						.build(), 
					i, true, new InventoryCall() {
						
						@Override
						public void click(HumanEntity human, ItemStack item, ClickType clickType) {
							if(game.getOwner() != lgp) {
								lgp.sendMessage("§cVous n'êtes pas le propriétaire de la partie...");
								return;
							}
							
							int modif = 0;
							
							switch(clickType) {
							
							case RIGHT:
							case SHIFT_RIGHT:
								modif = -1;
								if(nbRole <= 0)
									modif = 0;
								break;
								
							case LEFT:
							case SHIFT_LEFT:
								modif = +1;
								break;
									
							default:
								return;
							}
							
							if(modif == 0) return;
							
							human.sendMessage(MainLg.getPrefix()+"§6Il y aura §e" + (nbRole + modif) + " §6" + roleName);
							game.getConfig().getRoles().replace(roleName, nbRole + modif);
							
							//Update all opened inventory
							for(LGPlayer lInGame : game.getInGame()) {
								if(lInGame.getPlayer() != null && lInGame.getPlayer().getOpenInventory() != null && lInGame.getPlayer().getOpenInventory().getTitle().equals(TITLE))
									openRealMenu(lInGame);
							}
						}
			});
			i++;
		}
		
		ii.registerItem(
				new ItemBuilder(Material.GOLD_NUGGET)
					.name("§aTotal : " + total)
					.build(), 
				4*9-1, true, null);
		
		ii.openTo(lgp.getPlayer());
	}
	
	private void validate(@NonNull LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9, "Valider votre choix..."));
		
		ii.fill(null, true, null);
		
		ii.registerItem(
				new ItemBuilder(Material.GOLD_NUGGET)
				.name("§6Voulez-vous vraiment dévoiler la configuration ?")
				.lore(
						Arrays.asList(
								"En accèdant à ce menu vous vous dévoilerez la composition de la partie..."
								)
						)
				.build(), 
				3, 0, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						openMenu(lgp);
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
		
		ii.openTo(lgp.getPlayer());
	}
	
	/*
	 */
	
	private static String optimizeLines(String text) {
		int nbWordPerLines = 5;
		
		int a = 0;
		int b = 0;
		
		for(String s : text.split(" ")){
			a += s.length();
			b++;
			if(a >= 40){
				nbWordPerLines--;
			}
			if(b >= nbWordPerLines){
				a = 0;
				b = 0;
			}
		}
		
    	String result = "";
    	String[] words = text.split(" ");
    	
    	String currentLine = "";
    	int wordCountInLine = 0;
    	for (String word : words) {
    		currentLine += word + " ";
    		
    		if (wordCountInLine >= nbWordPerLines) {
    			result += currentLine + "\n";
    			char color = 'f';
    			
    			char[] charCurrentLine = currentLine.toCharArray();
    			for (int j = 0; j < charCurrentLine.length; j++)
    				if(charCurrentLine[j] == '§')
    					color = charCurrentLine[j + 1];
    			
    			currentLine = "§" + color;
    			wordCountInLine = 0;
    		}
    		wordCountInLine++;
    	}
		result += currentLine + "\n";//Add final line
    	
    	return result;
	}
	
}
