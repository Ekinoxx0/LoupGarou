package dev.loupgarou.roles.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
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
	private static final HashMap<String, Role> roles = new HashMap<String, Role>();
	
	public static Role getRole(String name) {
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
	
}
