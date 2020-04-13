package dev.loupgarou.classes;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.io.CharStreams;

import dev.loupgarou.MainLg;
import dev.loupgarou.events.other.LGCustomItemChangeEvent;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.roles.utils.Role;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class LGCustomItems {
	public static enum SpecialItems {
		ARROW,
		BACKARROW,
		CHECK,
		CROSS,
		DEATH_POTION,
		DISCORD,
		HEART,
		LIFE_POTION,
		NODISCORD,
		NOROBOT,
		OPTIONS,
		ROBOT,
		SERVER_ICON,
		TEXTUAL,
		GREEN_ROLE,
		GREEN_ROLE_Q,
		GREY_ROLE,
		GREY_ROLE_Q,
		MID_ROLE,
		MID_ROLE_Q,
		RED_ROLE,
		RED_ROLE_Q,
	}
	
	public static HashMap<String, HashMap<String, Material>> mappings = new HashMap<String, HashMap<String,Material>>();
	static {
		JSONParser parser = new JSONParser();
		try {
			String json = CharStreams.toString(new InputStreamReader(LGCustomItems.class.getResourceAsStream("/mapping_resource_pack.json")));
			
			JSONObject mappingsRoot = (JSONObject)parser.parse(json);
			for(Object roleObject : mappingsRoot.keySet()) {
				HashMap<String, Material> roleMaterials = new HashMap<String, Material>();
				JSONObject roleMaterialsJson = (JSONObject) mappingsRoot.get(roleObject);
				for(Object roleType : roleMaterialsJson.keySet())
					roleMaterials.put((String)roleType, Material.valueOf((String)roleMaterialsJson.get(roleType)));
				
				LGCustomItems.mappings.put((String) roleObject, roleMaterials);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Material getSpecialItem(@NonNull SpecialItems special) {
		return mappings.get("_specials").get(special.toString().toLowerCase());
	}
	
	public static Material getItem(@NonNull Role role) {
		return getItem(role, new ArrayList<LGCustomItems.LGCustomItemsConstraints>());
	}
	
	public static Material getItemMenu(@NonNull Role role) {
		if(!mappings.containsKey(role.getClass().getSimpleName().substring(1).toLowerCase())) {
			MainLg.debug("No material specified in mappings(" + mappings.size() + ") for : '" + role.getClass().getSimpleName().substring(1).toLowerCase() + "'");
			return getSpecialItem(SpecialItems.MID_ROLE_Q);
		}
		return mappings.get(role.getClass().getSimpleName().substring(1).toLowerCase()).get("menu");
	}
	
	public static Material getItem(@NonNull Role r, @NonNull List<LGCustomItemsConstraints> constraints) {
		if(constraints.isEmpty())
			return mappings.get(r.getClass().getSimpleName().substring(1).toLowerCase()).get("role");
		
		Collections.sort(constraints);
		
		String roleName = r.getClass().getSimpleName().substring(1).toLowerCase();
		if(!mappings.containsKey(roleName)) {
			MainLg.debug("No material specified in mappings(" + mappings.size() + ") for : '" + roleName + "'");
			return getSpecialItem(SpecialItems.MID_ROLE_Q);
		}

		StringJoiner sj = new StringJoiner("_");
		for(LGCustomItemsConstraints constraint : constraints)
			sj.add(constraint.getName());
		
		if(!mappings.get(roleName).containsKey(sj.toString())) {
			MainLg.debug("No material specified in mappings for : '" + roleName + "' -> " + sj.toString());
			return getSpecialItem(SpecialItems.MID_ROLE_Q);
		}
			
		return mappings.get(roleName).get(sj.toString());
	}
	
	public static void updateItem(@NonNull LGPlayer lgp) {
		lgp.getPlayer().getInventory().setHeldItemSlot(0);
		Material menuItem = getItemMenu(lgp.getRole());
		for (int i = 1; i < 9; i++)
			lgp.getPlayer().getInventory().setItem(i, new ItemStack(menuItem));
		
		List<LGCustomItemsConstraints> list = new ArrayList<LGCustomItems.LGCustomItemsConstraints>();
		Bukkit.getPluginManager().callEvent(new LGCustomItemChangeEvent(lgp.getGame(), lgp, list));
		updateItem(lgp, getItem(lgp.getRole(), list));
	}

	@Deprecated
	public static void updateItem(@NonNull LGPlayer lgp, Material material) {
		if(lgp.getPlayer() == null) return;
		
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(material == null ? Material.AIR : material));
		lgp.getPlayer().updateInventory();
	}
	
	@RequiredArgsConstructor
	public static enum LGCustomItemsConstraints{
		INFECTED("infecte"),
		MAYOR("maire"),
		DEAD("mort");
		@Getter private final String name;
	}

	public static void checkRessourcePack(CommandSender cs) {
		List<Material> specials = new ArrayList<Material>();
		for(SpecialItems si : SpecialItems.values()) {
			Material siMat = LGCustomItems.getSpecialItem(si);
			if(siMat == null) {
				cs.sendMessage("§7" + si + " - " + siMat);
				cs.sendMessage("§cSpecial item is null, aborting...");
				return;
			}
			specials.add(siMat);
		}
		
		for(Role r : FakeRoles.all()) {
			Material roleMat = LGCustomItems.getItem(r);
			if(roleMat == null || specials.contains(roleMat) || roleMat.isAir()) {
				cs.sendMessage("" + r.getName() + " (roleMat) is null or special, aborting...");
				return;
			}
			Material roleMenuMat = LGCustomItems.getItemMenu(r);
			if(roleMenuMat == null || specials.contains(roleMenuMat) || roleMenuMat.isAir()) {
				cs.sendMessage("" + r.getName() + " (roleMenuMat) is null or special, aborting...");
				return;
			}
			
			HashMap<List<LGCustomItemsConstraints>, Material> roleContrainsMat = new HashMap<List<LGCustomItemsConstraints>, Material>();
			for(LGCustomItemsConstraints c1 : LGCustomItemsConstraints.values()) {
				List<LGCustomItemsConstraints> co1 = new ArrayList<LGCustomItemsConstraints>(Arrays.asList(c1));
				Material roleConstrainedMat1 = LGCustomItems.getItem(r, co1);
				roleContrainsMat.put(co1, roleConstrainedMat1);
				for(LGCustomItemsConstraints c2 : LGCustomItemsConstraints.values()) {
					if(c1 == c2) continue;
					List<LGCustomItemsConstraints> co2 = new ArrayList<LGCustomItemsConstraints>(Arrays.asList(c1, c2));
					Material roleConstrainedMat2 = LGCustomItems.getItem(r, co2);
					roleContrainsMat.put(co2, roleConstrainedMat2);
					for(LGCustomItemsConstraints c3 : LGCustomItemsConstraints.values()) {
						if(c2 == c3 || c1 == c3) continue;
						List<LGCustomItemsConstraints> co3 = new ArrayList<LGCustomItemsConstraints>(Arrays.asList(c1, c2, c3));
						Material roleConstrainedMat3 = LGCustomItems.getItem(r, co3);
						roleContrainsMat.put(co3, roleConstrainedMat3);
					}
				}
			}
			
			for(Entry<List<LGCustomItemsConstraints>, Material> entry : roleContrainsMat.entrySet()) {
				if(entry.getValue() == null || specials.contains(entry.getValue()) || entry.getValue().isAir()) {
					cs.sendMessage("" + r.getName() + " (LGCustomItemsConstraints) is null or special, aborting...");
					cs.sendMessage("§7" + Arrays.toString(entry.getKey().toArray()));
					return;
				}
			}
		}
		
		cs.sendMessage("§2Sanity checked all ressources in mapping.");
	}
	
}
