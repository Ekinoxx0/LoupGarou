package dev.loupgarou.classes;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.io.CharStreams;

import dev.loupgarou.MainLg;
import dev.loupgarou.events.other.LGCustomItemChangeEvent;
import dev.loupgarou.roles.utils.Role;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class LGCustomItems {
	public static enum SpecialItems {
		ARROW,
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
		if(!mappings.containsKey(role.getClass().getSimpleName().substring(1).toLowerCase())) {
			MainLg.debug("No material specified in mappings(" + mappings.size() + ") for : '" + role.getClass().getSimpleName().substring(1).toLowerCase() + "'");
			return getSpecialItem(SpecialItems.MID_ROLE_Q);
		}
		return mappings.get(role.getClass().getSimpleName().substring(1).toLowerCase()).get("role");
	}
	
	public static Material getItemMenu(@NonNull Role role) {
		if(!mappings.containsKey(role.getClass().getSimpleName().substring(1).toLowerCase())) {
			MainLg.debug("No material specified in mappings(" + mappings.size() + ") for : '" + role.getClass().getSimpleName().substring(1).toLowerCase() + "'");
			return getSpecialItem(SpecialItems.MID_ROLE_Q);
		}
		return mappings.get(role.getClass().getSimpleName().substring(1).toLowerCase()).get("menu");
	}
	
	private static Material getItem(@NonNull LGPlayer player, @NonNull ArrayList<LGCustomItemsConstraints> constraints) {
		Bukkit.getPluginManager().callEvent(new LGCustomItemChangeEvent(player.getGame(), player, constraints));
		Collections.sort(constraints);
		
		String roleName = player.getRole().getClass().getSimpleName().substring(1).toLowerCase();
		if(!mappings.containsKey(roleName)) {
			MainLg.debug("No material specified in mappings(" + mappings.size() + ") for : '" + roleName + "'");
			return getSpecialItem(SpecialItems.MID_ROLE_Q);
		}

		HashMap<String, Material> mapps = mappings.get(roleName);
		StringJoiner sj = new StringJoiner("_");
		for(LGCustomItemsConstraints constraint : constraints)
			sj.add(constraint.getName());
		
		if(mapps.get(sj.toString()) == null) {
			MainLg.debug("No material specified in mappings for : '" + roleName + "' -> " + sj.toString());
			return getSpecialItem(SpecialItems.MID_ROLE_Q);
		}
			
		return mapps.get(sj.toString());
	}
	
	public static void updateItem(@NonNull LGPlayer lgp) {
		updateItem(lgp, getItem(lgp, new ArrayList<LGCustomItems.LGCustomItemsConstraints>()));
	}

	public static void updateItem(@NonNull LGPlayer lgp, @NonNull ArrayList<LGCustomItemsConstraints> constraints) {
		updateItem(lgp, getItem(lgp, constraints));
	}
	
	public static void updateItem(@NonNull LGPlayer lgp, Material material) {
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
	
}
