package dev.loupgarou.classes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import dev.loupgarou.MainLg;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class LGMaps {

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final String fileName = "maps.json";
	
	@Getter private static LGMaps mapsInfo = null;
	
	public static void loadMaps(MainLg main) throws IOException, JsonParseException, JsonSyntaxException {
		File mapsFile = new File(main.getDataFolder(), fileName);
		if (!mapsFile.exists())
			save(main);
		
		String json = Files.toString(mapsFile, Charset.forName("UTF-8"));
		
		mapsInfo = gson.fromJson(json, LGMaps.class);
	}
	
	public static void save(MainLg main) throws IOException {
		if(mapsInfo == null)
			mapsInfo = new LGMaps();

		File mapsFile = new File(main.getDataFolder(), fileName);
		Files.write(gson.toJson(mapsInfo), mapsFile, Charset.forName("UTF-8"));
	}
	
	// DATA
	
	@Getter private List<LGMap> maps = new ArrayList<LGMap>();
	
	@RequiredArgsConstructor
	public static class LGMap {
		@NonNull @Getter private String name;
		@Getter @NonNull private String world;
		@Getter private List<LGLocation> spawns = new ArrayList<LGLocation>();

		public boolean isWorldValid() {
			for(World w : Bukkit.getWorlds())
				if(w.getName().equals(world))
					return true;
			
			return false;
		}
	}
	
	@Getter
	public static class LGLocation {
		private transient final LGMap map;
		@Getter private double x;
		@Getter private double y;
		@Getter private double z;
		@Getter private float yaw;
		@Getter private float pitch;
		
		public LGLocation(Location l, LGMap map) throws IllegalArgumentException {
			if(l.getWorld().getName() != map.getWorld()) throw new IllegalArgumentException();
			this.map = map;
			this.x = l.getBlockX();
			this.y = l.getY();
			this.z = l.getBlockZ();
			this.pitch = l.getPitch();
			this.yaw = l.getYaw();
		}
		
		public Location toLocation() {
			return new Location(Bukkit.getWorld(map.getWorld()), x, y, z, yaw, pitch);
		}
	}
	
}
