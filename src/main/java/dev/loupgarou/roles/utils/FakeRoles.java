package dev.loupgarou.roles.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGGameConfig;
import dev.loupgarou.classes.LGMaps.LGMap;

public class FakeRoles {
	
	private static final LGGame fakeGame = new LGGame(0, new LGGameConfig(), new LGMap("fake", Bukkit.getWorlds().get(0).getName()));
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