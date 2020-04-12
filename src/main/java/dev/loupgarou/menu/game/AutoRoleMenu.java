package dev.loupgarou.menu.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGGameConfig.InvalidCompo;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.roles.RAnge;
import dev.loupgarou.roles.RChasseur;
import dev.loupgarou.roles.RCupidon;
import dev.loupgarou.roles.RGarde;
import dev.loupgarou.roles.RLoupGarou;
import dev.loupgarou.roles.RSorciere;
import dev.loupgarou.roles.RVillageois;
import dev.loupgarou.roles.RVoyante;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.utils.CommonText.PrefixType;
import dev.loupgarou.utils.InteractInventory;
import dev.loupgarou.utils.InteractInventory.InventoryCall;
import dev.loupgarou.utils.ItemBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoRoleMenu {
	
	private static final Random random = new Random();
	private static final String TITLE = "Sélection des rôles automatique";
	private @NonNull final LGGame game;
	private Map<RoleType, Integer> configAuto = new HashMap<RoleType, Integer>();
	
	{
		for(RoleType type : RoleType.values())
			configAuto.put(type, 0);
	}
	
	public int total() {
		int total = 0;
		for(Entry<RoleType, Integer> entry : this.configAuto.entrySet())
			total += entry.getValue();
		return total;
	}

	private void generateRandom(LGPlayer lgp) {
		if(configAuto.get(RoleType.VILLAGER) == 0 && configAuto.get(RoleType.NEUTRAL) == 0) {
			lgp.sendMessage(PrefixType.PARTIE + "§cImpossible de faire une génération sans villageois ou neutre...");
			return;
		}
		
		lgp.sendMessage(PrefixType.PARTIE + "Génération des rôles en cours...");
		for(Entry<RoleType, Integer> entry : configAuto.entrySet()) {
			generate(entry.getKey(), entry.getValue());
		}

		int tried = 0;
		InvalidCompo invalid;
		while((invalid = game.getConfig().verifyRoles()) != null && tried <= 20) {
			generate(invalid.getRoleType(), configAuto.get(invalid.getRoleType()));
			tried++;
		}
		
		if(tried >= 20 && invalid != null) {
			lgp.sendMessage(PrefixType.PARTIE + "§6Composition créée invalide...(" + invalid + ")");
			lgp.sendMessage(PrefixType.PARTIE + "§cImpossible de créer une composition avec ces paramètres !");
			return;
		}
		
		lgp.sendMessage(PrefixType.PARTIE + "§a" + total() + " rôles générés aléatoirement");
	}
	
	private List<Role> biasedRolesList(RoleType roleType){
		List<Role> rolesInType = new ArrayList<Role>(FakeRoles.inRoleType(roleType));
		
		//Force somes stats here...
		switch(roleType) {
		case LOUP_GAROU:
			rolesInType.add(FakeRoles.getRole(RLoupGarou.class));
			rolesInType.add(FakeRoles.getRole(RLoupGarou.class));
			rolesInType.add(FakeRoles.getRole(RLoupGarou.class));
			rolesInType.add(FakeRoles.getRole(RLoupGarou.class));
			break;
		case NEUTRAL:
			rolesInType.add(FakeRoles.getRole(RAnge.class));
			break;
		case VILLAGER:
			rolesInType.add(FakeRoles.getRole(RVillageois.class));
			rolesInType.add(FakeRoles.getRole(RVillageois.class));
			rolesInType.add(FakeRoles.getRole(RVoyante.class));
			rolesInType.add(FakeRoles.getRole(RChasseur.class));
			rolesInType.add(FakeRoles.getRole(RGarde.class));
			rolesInType.add(FakeRoles.getRole(RSorciere.class));
			rolesInType.add(FakeRoles.getRole(RCupidon.class));
			break;
		default:
			break;
		}
		
		return rolesInType;
	}
	
	private Map<Role, Float> statisticsRoles(RoleType roleType) {
		Map<Role, Float> stats = new LinkedHashMap<Role, Float>();
		
		for(Role role : this.biasedRolesList(roleType))
			stats.put(role, (stats.get(role) == null ? 0 : stats.get(role)) + 1F);

		for(Entry<Role, Float> entry : stats.entrySet())
			entry.setValue(entry.getValue() / this.biasedRolesList(roleType).size());
		
		return stats.entrySet().stream() //Sort by float
	            .sorted((e1, e2) -> (int) (e2.getValue()*100 - e1.getValue()*100))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
	}
	
	private void generate(RoleType roleType, int temp) {
		if(temp < 0) return;
		if(roleType == null) return;
		List<Role> rolesInType = biasedRolesList(roleType);
		
		//Reset compo
		for(Role role : rolesInType)
			game.getConfig().getRoles().put(role.getClass(), 0);
		
		//Try generate
		while(temp > 0) {
			Role selected = rolesInType.get(random.nextInt(rolesInType.size()));
			game.getConfig().getRoles().put(selected.getClass(), game.getConfig().getRoles().get(selected.getClass()) + 1);
			temp--;
		}
	}
	
	public void openMenu(LGPlayer lgp) {
		InteractInventory ii = new InteractInventory(Bukkit.createInventory(null, 3 * 9, TITLE));
		
		int i = 0;
		for(RoleType roleType : RoleType.values()) {
			String color = "";
			String roles = "";
			for(Entry<Role, Float> entry : this.statisticsRoles(roleType).entrySet()) {
				color = entry.getKey().getColor();
				roles += "§7 - " + entry.getKey().getName() + " §7(" + (int) (entry.getValue()*100) + "%)\n";
			}
			
			int nbRole = configAuto.get(roleType);
			
			ii.registerItem(
					new ItemBuilder(roleType.getItem())
						.name(color + roleType.getBeautifulName())
						.lore(Arrays.asList(
									"",
									"§7" + nbRole + " joueur" + (nbRole > 1 ? "s" : ""),
									"§oClique gauche pour en ajouter, droit pour en retirer",
									"",
									"§9§lRôles :",
									roles
									
								))
						.amount(nbRole > 1 ? nbRole : 1)
						.build(), 
					2 + i, 1, true, new InventoryCall() {
						
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
							
							if(modif > 0 && total() >= game.getConfig().getMap().getSpawns().size()) {
								human.sendMessage(PrefixType.PARTIE + "§cVous avez configurer le nombre maximum de rôle !");
								return;
							}
							if(modif == 0) return;
							
							human.sendMessage(PrefixType.PARTIE + "§6Il y aura §e" + (nbRole + modif) + " " + roleType.getBeautifulName());
							configAuto.put(roleType, modif + nbRole);
							
							//Update all opened inventory
							for(LGPlayer lInGame : game.getInGame())
								if(lInGame.getPlayer() != null && lInGame.getPlayer().getOpenInventory() != null && lInGame.getPlayer().getOpenInventory().getTitle().equals(TITLE))
									openMenu(lInGame);
						}
					});
			
			if(RoleType.values().length == 4 && i == 1)
				i++; //Add space in the middle
			
			i++;
		}
		
		ii.registerItem(
				new ItemBuilder(LGCustomItems.getSpecialItem(SpecialItems.CHECK))
					.name("§aTotal : " + total())
					.lore(Arrays.asList(
							"",
							"§9§oCliquez pour générer la composition..."
							))
					.amount(total() > 1 ? total() : 1)
					.build(), 
				ii.getInv().getSize() - 1, true, new InventoryCall() {
					
					@Override
					public void click(HumanEntity human, ItemStack item, ClickType clickType) {
						human.closeInventory();
						generateRandom(lgp);
					}
				});
		
		ii.openTo(lgp.getPlayer());
	}
	
}
