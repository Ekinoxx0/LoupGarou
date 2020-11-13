package dev.loupgarou.classes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import dev.loupgarou.MainLg;
import lombok.NonNull;

/**
 * TODO Auto invite prec game
 * FIXME Saved
 */
public class LGSavedPlayer {

	private static final Map<LGPlayer, LGSavedPlayer> saved = new HashMap<LGPlayer, LGSavedPlayer>();
	public static LGSavedPlayer get(@NonNull LGPlayer lgp) {
		if(saved.containsKey(lgp)) return saved.get(lgp);
		try {
			return load(lgp);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final File folder = new File(MainLg.getInstance().getDataFolder(), "players/");
	static {
		folder.mkdirs();
	}
	
	private static File getFile(@NonNull String name) {
		return new File(folder, name + ".yml");
	}
	
	private static LGSavedPlayer load(@NonNull LGPlayer lgp) throws IOException {
		if(!getFile(lgp.getRealName()).exists()) getFile(lgp.getRealName()).createNewFile();
		return new LGSavedPlayer(lgp, YamlConfiguration.loadConfiguration(getFile(lgp.getRealName())));
	}
	
	//------------------
	
	@NonNull private final LGPlayer lgp;
	@NonNull private final YamlConfiguration yaml;
	
	public LGSavedPlayer(@NonNull LGPlayer lgp, @NonNull YamlConfiguration yaml) {
		saved.put(lgp, this);
		this.lgp = lgp;
		this.yaml = yaml;
		this.yaml.addDefault("name", lgp.getRealName());
		this.yaml.addDefault("uuid", lgp.getPlayer().getUniqueId());
		this.yaml.addDefault("discord", -1);
		this.yaml.addDefault("options.invitePrecGame", true);
	}
	
	public long getDiscordId() {
		return this.yaml.getLong("discord");
	}
	
	public void setInvitePrecGame(boolean invitePrecGame) {
		this.yaml.set("options.invitePrecGame", invitePrecGame);
		this.save();
	}
	
	public boolean getInvitePrecGame() {
		return this.yaml.getBoolean("options.invitePrecGame");
	}
	
	public void save() {
		try {
			this.yaml.save(getFile(lgp.getRealName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
