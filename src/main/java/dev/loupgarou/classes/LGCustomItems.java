package dev.loupgarou.classes;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.io.Resources;

import dev.loupgarou.MainLg;
import dev.loupgarou.events.other.LGCustomItemChangeEvent;
import dev.loupgarou.roles.utils.Role;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class LGCustomItems {
	public static HashMap<String, HashMap<String, Material>> mappings = new HashMap<String, HashMap<String,Material>>();
	static {
		JSONParser parser = new JSONParser();
		try {
			String json = Resources.toString(Resources.getResource("/mapping_resource_pack.json"), Charset.forName("UTF-8"));
			
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
	
	public static Material getItem(@NonNull Role role) {
		return mappings.get(role.getClass().getSimpleName().substring(1)).get("");
	}
	
	public static Material getItemMenu(@NonNull Role role) {
		return mappings.get(role.getClass().getSimpleName().substring(1)).get("menu");
	}
	
	public static Material getItem(@NonNull LGPlayer player, @NonNull List<LGCustomItemsConstraints> constraints) {
		Bukkit.getPluginManager().callEvent(new LGCustomItemChangeEvent(player.getGame(), player, constraints));
		Collections.sort(constraints);
		
		String roleName = player.getRole().getClass().getSimpleName().substring(1);
		if(!mappings.containsKey(roleName)) {
			MainLg.debug("No material specified in mappings(" + mappings.size() + ") for : '" + roleName + "'");
			return Material.AIR;
		}
		
		HashMap<String, Material> mapps = mappings.get(roleName);
		StringJoiner sj = new StringJoiner("_");
		for(LGCustomItemsConstraints constraint : constraints)
			sj.add(constraint.getName());
		return mapps.get(sj.toString());
	}
	
	public static Material getItem(@NonNull LGPlayer player) {
		return getItem(player, new ArrayList<LGCustomItemsConstraints>());
	}
	
	public static void updateItem(@NonNull LGPlayer lgp) {
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(getItem(lgp)));
		lgp.getPlayer().updateInventory();
	}

	public static void updateItem(@NonNull LGPlayer lgp, @NonNull List<LGCustomItemsConstraints> constraints) {
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(getItem(lgp, constraints)));
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
