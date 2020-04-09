package dev.loupgarou;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.gson.JsonParseException;

import dev.loupgarou.classes.LGChat;
import dev.loupgarou.classes.LGChat.LGChatCallback;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGWinType;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.discord.DiscordManager;
import dev.loupgarou.listeners.CancelListener;
import dev.loupgarou.listeners.ChatListener;
import dev.loupgarou.listeners.GameListener;
import dev.loupgarou.listeners.JoinListener;
import dev.loupgarou.listeners.ProtocolListener;
import dev.loupgarou.listeners.VoteListener;
import dev.loupgarou.roles.RAnge;
import dev.loupgarou.roles.RApprentieVoyante;
import dev.loupgarou.roles.RAssassin;
import dev.loupgarou.roles.RBouffon;
import dev.loupgarou.roles.RChaperonRouge;
import dev.loupgarou.roles.RChasseur;
import dev.loupgarou.roles.RChasseurDeVampire;
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
import dev.loupgarou.roles.RMontreurDOurs;
import dev.loupgarou.roles.RPetiteFille;
import dev.loupgarou.roles.RPirate;
import dev.loupgarou.roles.RPretre;
import dev.loupgarou.roles.RPronostiqueur;
import dev.loupgarou.roles.RPyromane;
import dev.loupgarou.roles.RSorciere;
import dev.loupgarou.roles.RSurvivant;
import dev.loupgarou.roles.RVampire;
import dev.loupgarou.roles.RVillageois;
import dev.loupgarou.roles.RVoleur;
import dev.loupgarou.roles.RVoyante;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.utils.RandomString;
import lombok.Getter;
import lombok.NonNull;

public class MainLg extends JavaPlugin {
	
	@Getter private static MainLg instance;
	@Getter private final static List<Player> DEBUGS = new ArrayList<Player>();
	@Getter private LinkedHashMap<Class<? extends Role>, Constructor<? extends Role>> roles = new LinkedHashMap<Class<? extends Role>, Constructor<? extends Role>>();
	
	@Getter private List<LGGame> games = new ArrayList<LGGame>();
	@Getter private DiscordManager discord;
	@Getter private LGChat lobbyChat = new LGChat(new LGChatCallback() {
		@Override
		public String receive(LGPlayer sender, String message) {
			return "§7"+sender.getName()+" §8» §f"+message;
		}
	}, "LOBBY");
	
	@Override
	public void onEnable() {
		instance = this;
		
		loadRoles();
		
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new CancelListener(), this);
		Bukkit.getPluginManager().registerEvents(new VoteListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new GameListener(), this);

		try {
			LGMaps.loadMaps(this);
		} catch (JsonParseException | IOException e) {
			e.printStackTrace();
			return;
		}

	    try {
			this.discord = new DiscordManager(this);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		new LoupGarouCommand(this);
		
		for(Player player : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, "is connected"));
		
		new ProtocolListener(this);
	}
	
	@Override
	public void onDisable() {
		for(LGGame game : new ArrayList<LGGame>(this.games)) {
			try {
				game.endGame(LGWinType.NONE);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}

		try {
			this.discord.getJda().removeEventListener(this.discord);
			this.discord.getJda().shutdown();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			this.discord.getLinkServer().close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
	}

	public LGGame findGame(@NonNull String key) {
		for(LGGame game : this.games) {
			if(!game.getConfig().isPrivateGame() && game.getOwner().getName().equalsIgnoreCase(key))
				return game;
			
			if(game.getKey().equalsIgnoreCase(key))
				return game;
		}
		
		return null;
	}
	
	public String generateKey() {
		String key = RandomString.simple(5);
		
		for(LGGame game : this.games)
			if(game.getKey() != null && game.getKey().equals(key)) {
				key = generateKey();
				break;
			}
		
		return key;
	}
	
	private void loadRoles() {
		List<Class<? extends Role>> roles = Arrays.asList(
				RLoupGarou.class,
				RLoupGarouNoir.class,
				RLoupGarouBlanc.class,
				RLoupFeutrer.class,
				RGrandMechantLoup.class,

				RPyromane.class,
				RAssassin.class,
				RSurvivant.class,
				RBouffon.class,
				RAnge.class,

				RVillageois.class,
				RVoyante.class,
				RApprentieVoyante.class,
				RDetective.class,
				RSorciere.class,
				RChasseur.class,
				RCupidon.class,
				RCorbeau.class,
				RGarde.class,
				RMedium.class,
				RDictateur.class,
				RPetiteFille.class,
				RChaperonRouge.class,
				RPirate.class,
				RPretre.class,
				RFaucheur.class,
					
				REnfantSauvage.class,
				RChienLoup.class,
				RVoleur.class,
				RVampire.class,
				RPronostiqueur.class,
				RMontreurDOurs.class,
				RChasseurDeVampire.class
			);
			
		for(Class<? extends Role> role : roles)
			registerRole(role);
		
		MainLg.debug("Registered " + this.roles.size() + " (/" + roles.size() + ") roles in total...");
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
	}
	
	private void registerRole(Class<? extends Role> role) {
		try {
			roles.put(role, role.getConstructor(LGGame.class));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static void debug(String key, String s) {
		debug("[" + key + "] " + s);
	}
	
	public static void debug(String s) {
		getInstance().getLogger().log(Level.INFO, s);
		for(Player p : DEBUGS) {
			if(p != null)
				p.sendMessage("§7" + s);
		}
	}
}
