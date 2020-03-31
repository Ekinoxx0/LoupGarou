package dev.loupgarou;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.discord.DiscordManager;
import dev.loupgarou.listeners.CancelListener;
import dev.loupgarou.listeners.ChatListener;
import dev.loupgarou.listeners.GameListener;
import dev.loupgarou.listeners.JoinListener;
import dev.loupgarou.listeners.ProtocolListener;
import dev.loupgarou.listeners.VoteListener;
import dev.loupgarou.roles.RAnge;
import dev.loupgarou.roles.RAssassin;
import dev.loupgarou.roles.RBouffon;
import dev.loupgarou.roles.RChaperonRouge;
import dev.loupgarou.roles.RChasseur;
import dev.loupgarou.roles.RChienLoup;
import dev.loupgarou.roles.RCorbeau;
import dev.loupgarou.roles.RCupidon;
import dev.loupgarou.roles.RDetective;
import dev.loupgarou.roles.RDictateur;
import dev.loupgarou.roles.REnfantSauvage;
import dev.loupgarou.roles.RFaucheur;
import dev.loupgarou.roles.RGarde;
import dev.loupgarou.roles.RGrandMechantLoup;
import dev.loupgarou.roles.RLoupFeutrer;
import dev.loupgarou.roles.RLoupGarou;
import dev.loupgarou.roles.RLoupGarouBlanc;
import dev.loupgarou.roles.RLoupGarouNoir;
import dev.loupgarou.roles.RMedium;
import dev.loupgarou.roles.RPetiteFille;
import dev.loupgarou.roles.RPirate;
import dev.loupgarou.roles.RPretre;
import dev.loupgarou.roles.RPyromane;
import dev.loupgarou.roles.RSorciere;
import dev.loupgarou.roles.RSurvivant;
import dev.loupgarou.roles.RVillageois;
import dev.loupgarou.roles.RVoleur;
import dev.loupgarou.roles.RVoyante;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.utils.Updater;
import lombok.Getter;

public class MainLg extends JavaPlugin {
	
	@Getter private static MainLg instance;
	@Getter private final static List<Player> DEBUGS = new ArrayList<Player>();
	@Getter private LinkedHashMap<String, Constructor<? extends Role>> roles = new LinkedHashMap<String, Constructor<? extends Role>>();
	@Getter private static String prefix = "";
	
	@Getter private List<LGGame> games = new ArrayList<LGGame>();
	@Getter private DiscordManager discord;
	
	@Override
	public void onEnable() {
		instance = this;
		new Updater(this);
		
		loadRoles();

	    this.discord = new DiscordManager(this);
		
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new CancelListener(), this);
		Bukkit.getPluginManager().registerEvents(new VoteListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new GameListener(), this);
		
		new LoupGarouCommand(this);
		
		for(Player player : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, "is connected"));
		
		new ProtocolListener(this);
	}
	
	@Override
	public void onDisable() {
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
	}
	
	private void loadRoles() {
		try {
			roles.put("LoupGarou", RLoupGarou.class.getConstructor(LGGame.class));
			roles.put("LoupGarouNoir", RLoupGarouNoir.class.getConstructor(LGGame.class));
			roles.put("LoupGarouBlanc", RLoupGarouBlanc.class.getConstructor(LGGame.class));
			roles.put("GrandMechantLoup", RGrandMechantLoup.class.getConstructor(LGGame.class));

			roles.put("Pyromane", RPyromane.class.getConstructor(LGGame.class));
			roles.put("Assassin", RAssassin.class.getConstructor(LGGame.class));
			roles.put("Survivant", RSurvivant.class.getConstructor(LGGame.class));
			roles.put("Bouffon", RBouffon.class.getConstructor(LGGame.class));
			roles.put("Ange", RAnge.class.getConstructor(LGGame.class));

			roles.put("Villageois", RVillageois.class.getConstructor(LGGame.class));
			roles.put("Voyante", RVoyante.class.getConstructor(LGGame.class));
			roles.put("Detective", RDetective.class.getConstructor(LGGame.class));
			roles.put("Sorciere", RSorciere.class.getConstructor(LGGame.class));
			roles.put("Chasseur", RChasseur.class.getConstructor(LGGame.class));
			roles.put("Cupidon", RCupidon.class.getConstructor(LGGame.class));
			roles.put("Corbeau", RCorbeau.class.getConstructor(LGGame.class));
			roles.put("Garde", RGarde.class.getConstructor(LGGame.class));
			roles.put("Medium", RMedium.class.getConstructor(LGGame.class));
			roles.put("Dictateur", RDictateur.class.getConstructor(LGGame.class));
			roles.put("PetiteFille", RPetiteFille.class.getConstructor(LGGame.class));
			roles.put("ChaperonRouge", RChaperonRouge.class.getConstructor(LGGame.class));
			roles.put("Pirate", RPirate.class.getConstructor(LGGame.class));
			roles.put("Pretre", RPretre.class.getConstructor(LGGame.class));
			roles.put("Faucheur", RFaucheur.class.getConstructor(LGGame.class));
			
			roles.put("EnfantSauvage", REnfantSauvage.class.getConstructor(LGGame.class));
			roles.put("ChienLoup", RChienLoup.class.getConstructor(LGGame.class));
			roles.put("Voleur", RVoleur.class.getConstructor(LGGame.class));
			roles.put("LoupFeutrer", RLoupFeutrer.class.getConstructor(LGGame.class));
			
			/*
			 * L'Ancien
			 * Bouc Emissaire
			 * Idiot du village
			 * Joueur de flute
			 * Renard
			 * Servante Dévouée
			 * Deux soeur / Trois frères
			 * Montreur d'ours
			 * Le comédien
			 * Chevalier à l'épée rouillée
			 * L'abominable sectaire
			 * 
			 * Spécial:
			 * Garde champêtre
			 * 
			 * Garde->Salvateur
			 * 
			 */
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Deprecated
	public FileConfiguration getConfig() {return null;}
	
	public static void debug(String s) {
		getInstance().getLogger().log(Level.INFO, s);
		for(Player p : DEBUGS) {
			if(p != null)
				p.sendMessage("§7" + s);
		}
	}
}
