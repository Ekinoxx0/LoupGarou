package dev.loupgarou.roles.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGGameConfig;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.classes.LGPlayer;

public class FakeRoles {
	
	@SuppressWarnings("deprecation")
	private static final LGGame fakeGame = new LGGame(new LGPlayer("fake"), new LGGameConfig(new LGMap("fake", Bukkit.getWorlds().get(0).getName(), Material.AIR), true));
	private static final HashMap<Class<? extends Role>, Role> roles = new HashMap<Class<? extends Role>, Role>();
	
	static {
		MainLg.getInstance().getGames().remove(fakeGame);
	}
	
	public static List<Role> all() {
		List<Role> all = new ArrayList<Role>();
		
		for(Entry<Class<? extends Role>, Constructor<? extends Role>> entry : MainLg.getInstance().getRoles().entrySet()) {
			all.add(getRole(entry.getKey()));
		}
		
		return all;
	}
	
	public static List<Role> inRoleType(RoleType type) {
		List<Role> inRoleType = new ArrayList<Role>();
		
		for(Role r : all())
			if(r.getType() == type)
				inRoleType.add(r);
		
		return inRoleType;
	}

	public static Role getRole(Class<? extends Role> clazz) {
		for(Entry<Class<? extends Role>, Role> entry : roles.entrySet())
			if(entry.getKey() == clazz)
				return entry.getValue();
		
		try {
			Role r = MainLg.getInstance().getRoles().get(clazz).newInstance(fakeGame);
			roles.put(clazz, r);
			return r;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
