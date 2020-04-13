package dev.loupgarou.menu.game;

import java.util.Arrays;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGGameConfig.InvalidCompo;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.utils.CommonText;
import dev.loupgarou.utils.CommonText.PrefixType;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
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
		InvalidCompo invalidCompo = game.getConfig().verifyRoles();
		
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 5 * 9, TITLE));
		
		int i = 0;
		int total = game.getConfig().getTotalConfiguredRoles();
		for(Class<? extends Role> roleClazz : MainLg.getInstance().getRoles().keySet()) {
			int nbRole = game.getConfig().getRoles().get(roleClazz);
			Role fakeRole = FakeRoles.getRole(roleClazz);
			ii.registerItem(
					new ItemBuilder(LGCustomItems.getItemMenu(fakeRole))
						.name(fakeRole.getColor() + fakeRole.getName())
						.lore(Arrays.asList(
								"§7" + nbRole,
								"",
								"§f" + CommonText.optimizeLines(fakeRole.getDescription()),
								"",
								"§7§oClique gauche pour en ajouter, droit pour en retirer"
								))
						.amount(nbRole > 1 ? nbRole : 1)
						.glow(invalidCompo != null && (invalidCompo.getRole() == fakeRole.getClass() || (invalidCompo.getRole() == null && invalidCompo.getRoleType() == fakeRole.getType())))
						.build(), 
					i, true, new InventoryCall() {
						
						@Override
						public void click(HumanEntity human, ItemStack item, ClickType clickType) {
							if(game.getOwner() != lgp) {
								lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
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
							
							if(modif > 0 && total >= game.getConfig().getMap().getSpawns().size()) {
								human.sendMessage(PrefixType.PARTIE + "§cVous avez configurer le nombre maximum de rôle !");
								return;
							}
							if(nbRole + modif > fakeRole.getMaxNb()) {
								human.sendMessage(PrefixType.PARTIE + "§cImpossible de définir plus de " + fakeRole.getMaxNb() + " " + fakeRole.getName());
								return;
							}
							if(modif == 0) return;
							
							human.sendMessage(PrefixType.PARTIE + "§6Il y aura §e" + (nbRole + modif) + " " + fakeRole.getName());
							game.getConfig().getRoles().replace(roleClazz, nbRole + modif);
							
							//Update all opened inventory
							for(LGPlayer lInGame : game.getInGame())
								if(lInGame.getPlayer() != null && lInGame.getPlayer().getOpenInventory() != null && lInGame.getPlayer().getOpenInventory().getTitle().equals(TITLE))
									openRealMenu(lInGame);
						}
			});
			i++;
		}
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.GREY_ROLE_Q))
					.name("§7Remise à zéro")
					.build(), 
				ii.getInv().getSize() - 3, true, new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						if(game.getOwner() != lgp) {
							lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
							return;
						}
						
						for(Entry<Class<? extends Role>, Integer> entry : game.getConfig().getRoles().entrySet())
							entry.setValue(0);
						lgp.playAudio(Sound.ENTITY_VILLAGER_YES);
						//Update all opened inventory
						for(LGPlayer lInGame : game.getInGame())
							if(lInGame.getPlayer() != null && lInGame.getPlayer().getOpenInventory() != null && lInGame.getPlayer().getOpenInventory().getTitle().equals(TITLE))
								openRealMenu(lInGame);
					}
				});
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(invalidCompo == null ? SpecialItems.GREEN_ROLE_Q : SpecialItems.RED_ROLE_Q))
					.name("§aTotal : " + total)
					.lore(Arrays.asList("", invalidCompo != null ? "§7" + invalidCompo : "§aComposition valide."))
					.amount(total > 1 ? total : 1)
					.glow(invalidCompo == null)
					.build(), 
				ii.getInv().getSize() - 2, true, new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						if(invalidCompo != null) {
							lgp.sendMessage(PrefixType.PARTIE + "§cComposition des rôles impossible... " + invalidCompo);
							lgp.playAudio(Sound.ENTITY_VILLAGER_NO);
							return;
						}
						
						lgp.playAudio(Sound.ENTITY_VILLAGER_YES);
						game.getGameMenu().openGameMenu(lgp);
					}
				});
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.BACKARROW))
				.name("§7Retour")
				.build(), 
				ii.getInv().getSize() - 1, true, 
				new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						game.getGameMenu().openGameMenu(lgp);
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
	private void validate(@NonNull LGPlayer lgp) {
		if(game.getOwner() != lgp) {
			lgp.sendMessage(PrefixType.PARTIE + "§cLes rôles sont cachés durant cette partie...");
			return;
		}
		
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 9, "Valider votre choix..."));
		
		ii.fill(null, true, null);
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.CHECK))
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
