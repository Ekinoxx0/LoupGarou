package fr.leomelki.loupgarou;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.discord.DiscordManager;
import fr.leomelki.loupgarou.listeners.CancelListener;
import fr.leomelki.loupgarou.listeners.ChatListener;
import fr.leomelki.loupgarou.listeners.JoinListener;
import fr.leomelki.loupgarou.listeners.LoupGarouListener;
import fr.leomelki.loupgarou.listeners.ProtocolListener;
import fr.leomelki.loupgarou.listeners.VoteListener;
import fr.leomelki.loupgarou.roles.RAnge;
import fr.leomelki.loupgarou.roles.RAssassin;
import fr.leomelki.loupgarou.roles.RBouffon;
import fr.leomelki.loupgarou.roles.RChaperonRouge;
import fr.leomelki.loupgarou.roles.RChasseur;
import fr.leomelki.loupgarou.roles.RChienLoup;
import fr.leomelki.loupgarou.roles.RCorbeau;
import fr.leomelki.loupgarou.roles.RCupidon;
import fr.leomelki.loupgarou.roles.RDetective;
import fr.leomelki.loupgarou.roles.RDictateur;
import fr.leomelki.loupgarou.roles.REnfantSauvage;
import fr.leomelki.loupgarou.roles.RFaucheur;
import fr.leomelki.loupgarou.roles.RGarde;
import fr.leomelki.loupgarou.roles.RGrandMechantLoup;
import fr.leomelki.loupgarou.roles.RLoupGarou;
import fr.leomelki.loupgarou.roles.RLoupGarouBlanc;
import fr.leomelki.loupgarou.roles.RLoupGarouNoir;
import fr.leomelki.loupgarou.roles.RMedium;
import fr.leomelki.loupgarou.roles.RPetiteFille;
import fr.leomelki.loupgarou.roles.RPirate;
import fr.leomelki.loupgarou.roles.RPretre;
import fr.leomelki.loupgarou.roles.RPyromane;
import fr.leomelki.loupgarou.roles.RSorciere;
import fr.leomelki.loupgarou.roles.RSurvivant;
import fr.leomelki.loupgarou.roles.RVillageois;
import fr.leomelki.loupgarou.roles.RVoyante;
import fr.leomelki.loupgarou.roles.Role;
import lombok.Getter;
import lombok.Setter;

public class MainLg extends JavaPlugin{
	
	private static MainLg instance;
	@Getter private HashMap<String, Constructor<? extends Role>> roles = new HashMap<String, Constructor<? extends Role>>();
	@Getter private static String prefix = "";
	
	@Getter @Setter private LGGame currentGame;//Because for now, only one game will be playable on one server (flemme)
	@Getter private DiscordManager discord;
	
	@Override
	public void onEnable() {
		instance = this;
		loadRoles();
		if(!new File(getDataFolder(), "config.yml").exists()) {//Créer la config
			FileConfiguration config = getConfig();
			config.set("spawns", new ArrayList<List<Double>>());
			for(String role : roles.keySet())//Nombre de participant pour chaque rôle
				config.set("role."+role, 1);
			saveConfig();
		}
		loadConfig();

	    this.discord = new DiscordManager(this);
		
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new CancelListener(), this);
		Bukkit.getPluginManager().registerEvents(new VoteListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new LoupGarouListener(), this);
		CommandLG cmd = new CommandLG(this);
		Bukkit.getPluginCommand("lg").setExecutor(cmd);
		Bukkit.getPluginCommand("lg").setTabCompleter(cmd);
		
		for(Player player : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, "is connected"));
		
		new ProtocolListener(this);
	
	}
	public void loadConfig() {
		int players = 0;
		for(String role : roles.keySet())
			players += getConfig().getInt("role."+role);
		currentGame = new LGGame(players);
	}
	@Override
	public void onDisable() {
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
	}
	public static MainLg getInstance() {
		return instance;
	}
	private void loadRoles() {
		try {
			roles.put("LoupGarou", RLoupGarou.class.getConstructor(LGGame.class));
			roles.put("LoupGarouNoir", RLoupGarouNoir.class.getConstructor(LGGame.class));
			roles.put("Garde", RGarde.class.getConstructor(LGGame.class));
			roles.put("Sorciere", RSorciere.class.getConstructor(LGGame.class));
			roles.put("Voyante", RVoyante.class.getConstructor(LGGame.class));
			roles.put("Chasseur", RChasseur.class.getConstructor(LGGame.class));
			roles.put("Villageois", RVillageois.class.getConstructor(LGGame.class));
			roles.put("Medium", RMedium.class.getConstructor(LGGame.class));
			roles.put("Dictateur", RDictateur.class.getConstructor(LGGame.class));
			roles.put("Cupidon", RCupidon.class.getConstructor(LGGame.class));
			roles.put("PetiteFille", RPetiteFille.class.getConstructor(LGGame.class));
			roles.put("ChaperonRouge", RChaperonRouge.class.getConstructor(LGGame.class));
			roles.put("LoupGarouBlanc", RLoupGarouBlanc.class.getConstructor(LGGame.class));
			roles.put("Bouffon", RBouffon.class.getConstructor(LGGame.class));
			roles.put("Ange", RAnge.class.getConstructor(LGGame.class));
			roles.put("Survivant", RSurvivant.class.getConstructor(LGGame.class));
			roles.put("Assassin", RAssassin.class.getConstructor(LGGame.class));
			roles.put("GrandMechantLoup", RGrandMechantLoup.class.getConstructor(LGGame.class));
			roles.put("Corbeau", RCorbeau.class.getConstructor(LGGame.class));
			roles.put("Detective", RDetective.class.getConstructor(LGGame.class));
			roles.put("ChienLoup", RChienLoup.class.getConstructor(LGGame.class));
			roles.put("Pirate", RPirate.class.getConstructor(LGGame.class));
			roles.put("Pyromane", RPyromane.class.getConstructor(LGGame.class));
			roles.put("Pretre", RPretre.class.getConstructor(LGGame.class));
			roles.put("Faucheur", RFaucheur.class.getConstructor(LGGame.class));
			roles.put("EnfantSauvage", REnfantSauvage.class.getConstructor(LGGame.class));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
