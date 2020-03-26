package dev.loupgarou.classes;

import java.lang.reflect.Constructor;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGCustomItems.LGCustomItemsConstraints;
import dev.loupgarou.classes.chat.LGChat;
import dev.loupgarou.events.LGCustomItemChangeEvent;
import dev.loupgarou.events.LGDayEndEvent;
import dev.loupgarou.events.LGDayStartEvent;
import dev.loupgarou.events.LGEndCheckEvent;
import dev.loupgarou.events.LGGameEndEvent;
import dev.loupgarou.events.LGGameJoinEvent;
import dev.loupgarou.events.LGMayorVoteEvent;
import dev.loupgarou.events.LGNightEndEvent;
import dev.loupgarou.events.LGNightPlayerPreKilledEvent;
import dev.loupgarou.events.LGNightStart;
import dev.loupgarou.events.LGPlayerGotKilledEvent;
import dev.loupgarou.events.LGPlayerKilledEvent;
import dev.loupgarou.events.LGPreDayStartEvent;
import dev.loupgarou.events.LGRoleTurnEndEvent;
import dev.loupgarou.events.LGSkinLoadEvent;
import dev.loupgarou.events.LGVoteEvent;
import dev.loupgarou.events.LGVoteLeaderChange;
import dev.loupgarou.events.LGPlayerKilledEvent.Reason;
import dev.loupgarou.packetwrapper.WrapperPlayServerChat;
import dev.loupgarou.packetwrapper.WrapperPlayServerExperience;
import dev.loupgarou.packetwrapper.WrapperPlayServerPlayerInfo;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardObjective;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.packetwrapper.WrapperPlayServerUpdateHealth;
import dev.loupgarou.packetwrapper.WrapperPlayServerUpdateTime;
import dev.loupgarou.roles.RChienLoupLG;
import dev.loupgarou.roles.REnfantSauvageLG;
import dev.loupgarou.roles.Role;
import dev.loupgarou.roles.RoleType;
import dev.loupgarou.roles.RoleWinType;
import dev.loupgarou.scoreboard.CustomScoreboard;
import dev.loupgarou.utils.MultipleValueMap;
import dev.loupgarou.utils.VariousUtils;

import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings({"deprecation","unchecked"})
public class LGGame implements Listener{
	@Getter private final SecureRandom random = new SecureRandom();
	@Getter private final int maxPlayers;
	@Getter private ArrayList<LGPlayer> inGame = new ArrayList<LGPlayer>();
	@Getter private ArrayList<Role> roles = new ArrayList<Role>();
	
	@Getter private boolean started;
	@Getter private int night = 0;
	private BukkitTask startingTask;
	@Getter @Setter private int waitTicks;
	@Getter private boolean day;
	@Getter public long time = 0;
	@Getter private HashMap<Integer, LGPlayer> placements = new HashMap<Integer, LGPlayer>();
	
	@Getter @Setter public boolean hideRole;
	@Getter @Setter public boolean hideVote;
	@Getter @Setter public boolean hideVoteExtra;
	
	@Getter private LGChat spectatorChat = new LGChat((sender, message) -> {
		return "§7"+sender.getName()+" §6» §f"+message;
	}, "SPEC");
	@Getter private LGChat dayChat = new LGChat((sender, message) -> {
		return "§e"+sender.getName()+" §6» §f"+message;
	}, "DAY");
	
	
	public LGGame(int maxPlayers) {
		this.maxPlayers = maxPlayers;
		Bukkit.getPluginManager().registerEvents(this, MainLg.getInstance());
	}
	
	@Getter
	private MultipleValueMap<LGPlayerKilledEvent.Reason, LGPlayer> deaths = new MultipleValueMap<LGPlayerKilledEvent.Reason, LGPlayer>();

	public void sendActionBarMessage(String msg) {
		WrapperPlayServerChat chat = new WrapperPlayServerChat();
		chat.setPosition((byte)2);
		chat.setMessage(WrappedChatComponent.fromText(msg));
		for(LGPlayer lgp : inGame)
			chat.sendPacket(lgp.getPlayer());
	}
	public void broadcastMessage(String msg) {
		for(LGPlayer lgp : inGame)
			lgp.sendMessage(msg);
	}
	public void broadcastSpacer() {
		for(LGPlayer lgp : inGame)
			lgp.getPlayer().sendMessage("\n");
	}

	private BukkitTask waitTask;
	public void wait(int seconds, Runnable callback) {
		wait(seconds, callback, null);
	}
	public void wait(int seconds, Runnable callback, TextGenerator generator) {
		cancelWait();
		waitTicks = seconds*20;
		waitTask = new BukkitRunnable() {
			@Override
			public void run() {
				WrapperPlayServerExperience exp = new WrapperPlayServerExperience();
				exp.setLevel((short)(Math.floorDiv(waitTicks, 20)+1));
				exp.setExperienceBar((float)waitTicks/(seconds*20F));
				for(LGPlayer player : getInGame()) {
					exp.sendPacket(player.getPlayer());
					if(generator != null)
						player.sendActionBarMessage(generator.generate(player, Math.floorDiv(waitTicks, 20)+1));
				}
				if(waitTicks == 0) {
					for(LGPlayer player : getInGame())
						player.sendActionBarMessage("");
					waitTask = null;
					cancel();
					callback.run();
				}
				waitTicks--;
			}
		}.runTaskTimer(MainLg.getInstance(), 0, 1);
	}
	public void wait(int seconds, int initialSeconds, Runnable callback, TextGenerator generator) {
		cancelWait();
		waitTicks = seconds*20;
		waitTask = new BukkitRunnable() {
			@Override
			public void run() {
				WrapperPlayServerExperience exp = new WrapperPlayServerExperience();
				exp.setLevel((short)(Math.floorDiv(waitTicks, 20)+1));
				exp.setExperienceBar((float)waitTicks/(initialSeconds*20F));
				for(LGPlayer player : getInGame()) {
					exp.sendPacket(player.getPlayer());
					if(generator != null)
						player.sendActionBarMessage(generator.generate(player, Math.floorDiv(waitTicks, 20)+1));
				}
				if(waitTicks == 0) {
					for(LGPlayer player : getInGame())
						player.sendActionBarMessage("");
					waitTask = null;
					cancel();
					callback.run();
				}
				waitTicks--;
			}
		}.runTaskTimer(MainLg.getInstance(), 0, 1);
	}
	
	public static interface TextGenerator{
		public String generate(LGPlayer player, int secondsLeft);
	}
	public void cancelWait() {
		if(waitTask != null) {
			waitTask.cancel();
			waitTask = null;
		}
	}
	
	public void kill(LGPlayer player, Reason reason) {
		MainLg.debug("Kill "+player.getName()+" ("+player.getRole()+") for "+reason+" ("+(!deaths.containsValue(player) && !player.isDead())+")");
		if(!deaths.containsValue(player) && !player.isDead()){
			LGNightPlayerPreKilledEvent event = new LGNightPlayerPreKilledEvent(this, player, reason);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled())
				deaths.put(event.getReason(), player);
		}
	}
	
	public boolean tryToJoin(LGPlayer lgp) {
		if(ended)return false;
		if(!started && inGame.size() < maxPlayers) {//Si la partie n'a pas démarrée et qu'il reste de la place
			lgp.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			VariousUtils.setWarning(lgp.getPlayer(), false);
			if(lgp.isMuted())
				lgp.resetMuted();

			lgp.getPlayer().getInventory().clear();
			lgp.getPlayer().updateInventory();
			lgp.getPlayer().closeInventory();
			
			lgp.joinChat(dayChat);
			
			lgp.setGame(this);
			inGame.add(lgp);
			
			lgp.setScoreboard(null);
			
			for(LGPlayer other : this.getInGame()) {
				if(other.getPlayer() == null) {
					Bukkit.broadcastMessage("§cça risque pas de fonctionner si y'a un joueur déco... #55231");
					this.getInGame().remove(other);
				}
					
				other.updatePrefix();
				if(lgp != other) {
					lgp.getPlayer().hidePlayer(other.getPlayer());
					lgp.getPlayer().showPlayer(other.getPlayer());
					
					other.getPlayer().hidePlayer(lgp.getPlayer());
					other.getPlayer().showPlayer(lgp.getPlayer());
				}
			}
			
			lgp.getPlayer().setGameMode(GameMode.ADVENTURE);
			broadcastMessage("§7Le joueur §8"+lgp.getName()+"§7 a rejoint la partie §9(§8"+inGame.size()+"§7/§8"+maxPlayers+"§9)");
			
			//Reset scoreboard
			WrapperPlayServerScoreboardObjective obj = new WrapperPlayServerScoreboardObjective();
			obj.setName("lg_scoreboard");
			obj.setMode(1);
			obj.sendPacket(lgp.getPlayer());
			
			Bukkit.getPluginManager().callEvent(new LGGameJoinEvent(this, lgp));
			//updateStart();
			return true;
		}
		return false;
	}
	public void checkLeave() {
		if(startingTask != null) {
			startingTask.cancel();
			startingTask = null;
			broadcastMessage("§c§oUn joueur s'est déconnecté. Le décompte de lancement a donc été arrêté.");
		}
	}
	public void updateStart() {
		if(!isStarted())
			if(inGame.size() == maxPlayers) {//Il faut que la partie soit totalement remplie pour qu'elle démarre car sinon, tous les rôles ne seraient pas distribués
				for(LGPlayer lgp : getInGame()) {
					CustomScoreboard scoreboard = new CustomScoreboard("§7"/*[§9Loup-Garou§7]*/, lgp);
					scoreboard.getLine(0).setDisplayName("§6La partie va démarrer...");
					lgp.setScoreboard(scoreboard);
				}
				if(startingTask == null) {
					startingTask = new BukkitRunnable() {
						int timeLeft = 5+1;
						@Override
						public void run() {
							if(--timeLeft == 0)//start
								start();
							else
								sendActionBarMessage("§6Démarrage dans §e"+timeLeft+"§6...");
						}
					}.runTaskTimer(MainLg.getInstance(), 20, 20);
				}
			}else if(startingTask != null) {
				startingTask.cancel();
				broadcastMessage("§c§oLe démarrage de la partie a été annulé car une personne l'a quittée !");
			} else if(inGame.size() != maxPlayers) {
				broadcastMessage("§cDémarrage impossible car le nombre de joueur ne correspond pas aux rôles");
			}
	}
	public void start() {
		MainLg.getInstance().getDiscord().clearDead();
		if(startingTask != null) {
			startingTask.cancel();
			startingTask = null;
		}
		MainLg.getInstance().loadConfig();
		started = true;
		MainLg main = MainLg.getInstance();
		
		//Registering roles
		List<?> original = MainLg.getInstance().getConfig().getList("spawns");
		List<Object> list = new ArrayList<Object>(original);
		
		if(list.size() < getInGame().size()) {
			Bukkit.broadcastMessage("§cPas assez de spawn !");
			return;
		}
		
		try {
			int i = 0;
			for(LGPlayer lgp : getInGame()) {
				List<Double> location = (List<Double>) list.remove(i++);
				Player p = lgp.getPlayer();
				p.setWalkSpeed(0);
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 180, false, false));
				lgp.setPlace(original.indexOf(location));
				placements.put(lgp.getPlace(), lgp);
				p.teleport(new Location(p.getWorld(), location.get(0)+0.5, location.get(1), location.get(2)+0.5, location.get(3).floatValue(), location.get(4).floatValue()));
				WrapperPlayServerUpdateHealth update = new WrapperPlayServerUpdateHealth();
				update.setFood(20);
				update.setFoodSaturation(1);
				update.setHealth(20);
				update.sendPacket(p);
				lgp.getScoreboard().getLine(0).setDisplayName("§6Attribution des rôles...");
			}
		} catch(Exception ex) {
			Bukkit.broadcastMessage("§4§lUne erreur est survenue lors de la tp aux spawns... Regardez la console !");
			ex.printStackTrace();
		}
		
		try {
			for(Entry<String, Constructor<? extends Role>> role : main.getRoles().entrySet())
				if(main.getConfig().getInt("role."+role.getKey()) > 0)
					roles.add(role.getValue().newInstance(this));
		}catch(Exception err) {
			Bukkit.broadcastMessage("§4§lUne erreur est survenue lors de la création des roles... Regardez la console !");
			err.printStackTrace();
		}
		
		new BukkitRunnable() {
			int timeLeft = 5*2;
			int actualRole = getRoles().size();
			@Override
			public void run() {
				if(--timeLeft == 0) {
					cancel();
					_start();
					return;
				}
				if(timeLeft == 5*2-1) {
					broadcastMessage("§2Attribution des rôles...");
					for(LGPlayer lgp : getInGame()) {
						lgp.getPlayer().getInventory().clear();
						lgp.getPlayer().updateInventory();
					}
				}
				
				if(--actualRole < 0)
					actualRole = getRoles().size()-1;
				
				ItemStack stack = new ItemStack(LGCustomItems.getItem(getRoles().get(actualRole)));
				for(LGPlayer lgp : getInGame()) {
					lgp.getPlayer().getInventory().setItemInOffHand(stack);
					lgp.getPlayer().updateInventory();
				}
			}
		}.runTaskTimer(MainLg.getInstance(), 0, 4);
	}
	private void _start() {
		broadcastMessage("§8§oDébut de la partie...");
		//Give roles...
		ArrayList<LGPlayer> toGive = (ArrayList<LGPlayer>) inGame.clone();
		started = false;
		for(Role role : getRoles())
			while(role.getWaitedPlayers() > 0) {
				int randomized = random.nextInt(toGive.size());
				LGPlayer player = toGive.remove(randomized);

				role.join(player);
				WrapperPlayServerUpdateHealth update = new WrapperPlayServerUpdateHealth();
				update.setFood(20);
				update.setFoodSaturation(1);
				update.setHealth(20);
				update.sendPacket(player.getPlayer());
			}
		started = true;
		
		updateRoleScoreboard();
		
		//Classe les roles afin de les appeler dans le bon ordre
		roles.sort(new Comparator<Role>() {
			@Override
			public int compare(Role role1, Role role2) {
				return role1.getTurnOrder()-role2.getTurnOrder();
			}
		});
		
		//Start day one
		nextNight(10);
	}
	public void updateRoleScoreboard() {
		if(hideRole) {
			for(LGPlayer lgp : getInGame()) {
				for (int i = 0; i < 15; i++)
					lgp.getScoreboard().getLine(i).delete();
			}
			
			return;
		}
		
		HashMap<Role, IndexedRole> roles_ = new HashMap<>();
		for(LGPlayer lgp : getAlive())
			if(lgp.getRole() != null && roles_.containsKey(lgp.getRole()))
				roles_.get(lgp.getRole()).increase();
			else if(lgp.getRole() != null)
				roles_.put(lgp.getRole(), new IndexedRole(lgp.getRole()));
		ArrayList<IndexedRole> roles = new ArrayList<IndexedRole>(roles_.values());
		roles.sort((a, b)->{
			//TODO fix dégueu juste ici pour le chien loup lg à changer (2x)
			return (b.getNumber()+(b.getRole().getType() != RoleType.LOUP_GAROU || b.getRole() instanceof RChienLoupLG || b.getRole() instanceof REnfantSauvageLG ? b.getRole().getType() == RoleType.NEUTRAL ? 0 : 999 : 200) - a.getNumber()-(a.getRole().getType() != RoleType.LOUP_GAROU || a.getRole() instanceof RChienLoupLG || a.getRole() instanceof REnfantSauvageLG ? a.getRole().getType() == RoleType.NEUTRAL ? 0 : 999 : 200));
		});
		for(int i = 0;i<roles.size();i++) {
			IndexedRole role = roles.get(i);
			if(role.getNumber() == 0) {
				for(LGPlayer lgp : getInGame())
					lgp.getScoreboard().getLine(i).delete();
			}else
				for(LGPlayer lgp : getInGame())
					lgp.getScoreboard().getLine(i).setDisplayName("§e"+role.getNumber()+" §6- §e"+role.getRole().getName().replace("§l", ""));
		}
		for(int i = 15;i>=roles.size();i--)
			for(LGPlayer lgp : getInGame())
				lgp.getScoreboard().getLine(i).delete();
	}
	public List<LGPlayer> getAlive(){
		ArrayList<LGPlayer> alive = new ArrayList<LGPlayer>();
		for(LGPlayer lgp : getInGame())
			if(!lgp.isDead())
				alive.add(lgp);
		return alive;
	}
	
	public void nextNight() {
		nextNight(5);
	}
	public void nextNight(int timeout) {
		if(ended)return;
		LGNightStart event = new LGNightStart(this);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled())
			return;
		
		if(mayorKilled()) {//mort du maire
			broadcastMessage("§9Le §5§lCapitaine§9 est mort, il désigne un joueur en remplaçant.");
			getMayor().sendMessage("§6Choisis un joueur qui deviendra §5§lCapitaine§6 à son tour.");
			LGGame.this.wait(30, ()->{
				mayor.stopChoosing();
				setMayor(getAlive().get(random.nextInt(getAlive().size())));
				broadcastMessage("§7§l"+mayor.getName()+"§9 devient le nouveau §5§lCapitaine§9.");
				nextNight();
			}, (player, secondsLeft)->{
				return "§e"+mayor.getName()+"§6 choisit qui sera le nouveau §5§lCapitaine§6 (§e"+secondsLeft+" s§6)";
			});
			mayor.choose((choosen)->{
				if(choosen != null) {
					mayor.stopChoosing();
					cancelWait();
					setMayor(choosen);
					broadcastMessage("§7§l"+mayor.getName()+"§9 devient le nouveau §5§lCapitaine§9.");
					nextNight();
				}
			}, mayor);
			return;
		}
		
		new BukkitRunnable() {
			int timeoutLeft = timeout*20;
			@Override
			public void run() {
				if(--timeoutLeft <= 20+20*2) {
					if(timeoutLeft == 20)
						cancel();
					WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime();
					time.setAgeOfTheWorld(0);
					time.setTimeOfDay(LGGame.this.time = (long)(18000-(timeoutLeft-20D)/(20*2D)*12000D));
					for(LGPlayer lgp : getInGame())
						time.sendPacket(lgp.getPlayer());
				}
			}
		}.runTaskTimer(MainLg.getInstance(), 1, 1);
		LGGame.this.wait(timeout, this::nextNight_, (player, secondsLeft)->{
			return "§6La nuit va tomber dans §e"+secondsLeft+" seconde"+(secondsLeft > 1 ? "s" : "");
		});
	}
	private void nextNight_() {
		if(ended)return;
		night++;
		broadcastSpacer();
		broadcastMessage("§9----------- §lNuit n°"+night+"§9 -----------");
		broadcastMessage("§8§oLa nuit tombe sur le village...");
		MainLg.getInstance().getDiscord().setMutedChannel(true);
		
		for(LGPlayer player : getAlive())
			player.leaveChat();
		for(LGPlayer player : getInGame()) {
			player.stopAudio(LGSound.AMBIANT_DAY);
			player.playAudio(LGSound.START_NIGHT, 0.5);
			player.playAudio(LGSound.AMBIANT_NIGHT, 0.07);
		}
		day = false;
		Bukkit.getPluginManager().callEvent(new LGDayEndEvent(this));
		for(LGPlayer player : getInGame())
			player.hideView();

		ArrayList<Role> roles = (ArrayList<Role>) getRoles().clone();
		new Runnable() {
			Role lastRole;
			
			public void run() {
				Runnable run = this;
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if(roles.size() == 0) {
							Bukkit.getPluginManager().callEvent(new LGRoleTurnEndEvent(LGGame.this, null, lastRole));
							lastRole = null;
							endNight();
							return;
						}
						Role role = roles.remove(0);
						Bukkit.getPluginManager().callEvent(new LGRoleTurnEndEvent(LGGame.this, role, lastRole));
						lastRole = role;
						if(role.getTurnOrder() == -1 || !role.hasPlayersLeft())
							this.run();
						else {
							if(!hideRole) {
								broadcastMessage("§9"+role.getBroadcastedTask());
							} else {
								broadcastMessage("§9Quelqu'un fait quelque chose...");
							}
							role.onNightTurn(run);
						}
					}
				}.runTaskLater(MainLg.getInstance(), 60);
			}
		}.run();
	}
	public boolean kill(LGPlayer killed, Reason reason, boolean endGame) {
		if(killed.getPlayer() != null){
			killed.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, false, false));
			killed.setMuted();
			
			for(LGPlayer lgp : getInGame())
				if(lgp == killed) {
					WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
					ArrayList<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
					info.setAction(PlayerInfoAction.REMOVE_PLAYER);
					infos.add(new PlayerInfoData(new WrappedGameProfile(lgp.getPlayer().getUniqueId(), lgp.getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(lgp.getName())));
					info.setData(infos);
					info.sendPacket(lgp.getPlayer());
				}else
					lgp.getPlayer().hidePlayer(killed.getPlayer());
			
			if(vote != null)
				vote.remove(killed);
			
			broadcastMessage(String.format(reason.getMessage(), killed.getName())+", il était "+killed.getRole().getName()+(killed.getCache().getBoolean("infected") ? " §c§l(Infecté)" : "")+"§4.");
			
			MainLg.getInstance().getDiscord().setMuted(killed.getName(), true);
			
			//Lightning effect
			killed.getPlayer().getWorld().strikeLightningEffect(killed.getPlayer().getLocation());
			
			for(Role role : getRoles())
				if(role.getPlayers().contains(killed))
					role.getPlayers().remove(killed);
	
			killed.setDead(true);
			
			Bukkit.getPluginManager().callEvent(new LGPlayerGotKilledEvent(this, killed, reason, !checkEndGame(false) && endGame));
			
			VariousUtils.setWarning(killed.getPlayer(), true);
			
			killed.getPlayer().getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
			
			LGCustomItems.updateItem(killed);
			
			//killed.leaveChat();
			killed.joinChat(spectatorChat);
			killed.joinChat(dayChat, true);
		}
		
		//Update scoreboard
		
		updateRoleScoreboard();
		
		//End update scoreboard
		
		if(!checkEndGame(false))
			return false;
		if(endGame)
			checkEndGame();
		return true;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onGameEnd(LGGameEndEvent e) {
		MainLg.getInstance().getDiscord().setMutedChannel(false);
		if(e.getGame() == this && e.getWinType() == LGWinType.VILLAGEOIS)
			for(LGPlayer lgp : getInGame())
				if(lgp.getRoleType() == RoleType.VILLAGER)
					e.getWinners().add(lgp);
	}
	
	@Setter
	boolean ended;
	public void endGame(LGWinType winType) {
		if(ended)return;
		MainLg.getInstance().getDiscord().clearDead();
		MainLg.getInstance().getDiscord().setMutedChannel(false);
		
		ArrayList<LGPlayer> winners = new ArrayList<LGPlayer>();
		LGGameEndEvent event = new LGGameEndEvent(this, winType, winners);
		Bukkit.getPluginManager().callEvent(event);

		if(event.isCancelled())
			return;
		
		ended = true;
		//We unregister every role listener because they are unused after the game's end !
		for(Role role : getRoles())
			HandlerList.unregisterAll(role);
		
		broadcastMessage(winType.getMessage());
		for(LGPlayer lgp : getInGame()) {
			MainLg.getInstance().getDiscord().setMuted(lgp.getName(), false);
			lgp.setDead(false);
			lgp.leaveChat();
			lgp.joinChat(spectatorChat);
			
			lgp.setScoreboard(null);
			
			lgp.sendTitle("§7§lÉgalité", "§8Personne n'a gagné...", 200);
			
			if(winners.contains(lgp))
				lgp.sendTitle("§a§lVictoire !", "§6Vous avez gagné la partie.", 200);
			else
				if(winType == LGWinType.EQUAL || winType == LGWinType.NONE)
					lgp.sendTitle("§7§lÉgalité", "§8Personne n'a gagné...", 200);
				else
					lgp.sendTitle("§c§lDéfaite...", "§4Vous avez perdu la partie.", 200);
			
			
			Player p = lgp.getPlayer();
			lgp.showView();
			p.removePotionEffect(PotionEffectType.JUMP);
			p.setWalkSpeed(0.2f);
		}
		
		for(LGPlayer lgp : getInGame())
			if(lgp.getPlayer().isOnline()) {
				LGPlayer.removePlayer(lgp.getPlayer());
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				team.setMode(1);
				team.setName("you_are");
				team.sendPacket(lgp.getPlayer());
				LGPlayer.thePlayer(lgp.getPlayer()).join(MainLg.getInstance().getCurrentGame());
			}
		//A remettre pour activer le démarrage automatique
	/*	wait(30, ()->{
			for(LGPlayer lgp : getInGame())
				if(lgp.getPlayer().isOnline()) {
					WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
					team.setMode(1);
					team.setName("you_are");
					team.sendPacket(lgp.getPlayer());
					LGPlayer.thePlayer(lgp.getPlayer()).join(MainLg.getInstance().getCurrentGame());
				}
		}, (player, secondsLeft)->{
			return "§6Démarrage d'une nouvelle partie dans §e"+secondsLeft+" seconde"+(secondsLeft > 1 ? "s" : "");
		});*/
	}
	public boolean mayorKilled() {
		return getMayor() != null && getMayor().isDead();
	}
	public void endNight() {
		if(ended)return;
		MainLg.getInstance().getDiscord().setMutedChannel(false);
		broadcastSpacer();
		broadcastMessage("§9----------- §lJour n°"+night+"§9 -----------");
		broadcastMessage("§8§oLe jour se lève sur le village...");
		
		for(LGPlayer p : getInGame()) {
			p.stopAudio(LGSound.AMBIANT_NIGHT);
			p.playAudio(LGSound.START_DAY, 0.5);
			p.playAudio(LGSound.AMBIANT_DAY, 0.07);
		}
		
		LGNightEndEvent eventNightEnd = new LGNightEndEvent(this);
		Bukkit.getPluginManager().callEvent(eventNightEnd);
		if(eventNightEnd.isCancelled())
			return;
		
		int died = 0;
		boolean endGame = false;
		
		
		for(Entry<Reason, LGPlayer> entry : deaths.entrySet()) {
			if(entry.getKey() == Reason.DONT_DIE)
				continue;
			if(entry.getValue().isDead())//On ne fait pas mourir quelqu'un qui est déjà mort (résout le problème du dictateur tué par le chasseur)
				continue;
			if(entry.getValue().getPlayer() != null) {//S'il a deco bah au moins ça crash pas hehe
				LGPlayerKilledEvent event = new LGPlayerKilledEvent(this, entry.getValue(), entry.getKey());
				Bukkit.getPluginManager().callEvent(event);
				if(!event.isCancelled()) {
					endGame |= kill(event.getKilled(), event.getReason(), false);
					died++;
				}
			}
		}
		deaths.clear();
		if(died == 0)
			broadcastMessage("§9Étonnamment, personne n'est mort cette nuit.");
		
		MainLg.getInstance().getDiscord().setMutedChannel(false);

		day = true;
		for(LGPlayer player : getInGame())
			player.showView();

		
		new BukkitRunnable() {
			int timeoutLeft = 20;
			@Override
			public void run() {
				if(timeoutLeft++ > 20) {
					if(timeoutLeft == 20+(2*20))
						cancel();
					WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime();
					time.setAgeOfTheWorld(0);
					time.setTimeOfDay(LGGame.this.time = (long)(18000-(timeoutLeft-20D)/(20*2D)*12000D));
					for(LGPlayer lgp : getInGame())
						time.sendPacket(lgp.getPlayer());
				}
			}
		}.runTaskTimer(MainLg.getInstance(), 1, 1);
		
		LGPreDayStartEvent dayStart = new LGPreDayStartEvent(this);
		Bukkit.getPluginManager().callEvent(dayStart);
		if(!dayStart.isCancelled()) {
			if(endGame)
				checkEndGame();
			else
				startDay();
		}
	}
	public void startDay() {
		for(LGPlayer player : getInGame())
			player.joinChat(dayChat, player.isDead());
		
		LGDayStartEvent dayStart = new LGDayStartEvent(this);
		Bukkit.getPluginManager().callEvent(dayStart);
		if(dayStart.isCancelled())
			return;
		if(mayorKilled()) {//mort du maire
			broadcastMessage("§9Le §5§lCapitaine§9 est mort, il désigne un joueur en remplaçant.");
			getMayor().sendMessage("§6Choisis un joueur qui deviendra §5§lCapitaine§6 à son tour.");
			LGGame.this.wait(30, ()->{
				mayor.stopChoosing();
				setMayor(getAlive().get(random.nextInt(getAlive().size())));
				broadcastMessage("§7§l"+mayor.getName()+"§9 devient le nouveau §5§lCapitaine§9.");
				startDay();
			}, (player, secondsLeft)->{
				return "§e"+mayor.getName()+"§6 choisit qui sera le nouveau §5§lCapitaine§6 (§e"+secondsLeft+" s§6)";
			});
			mayor.choose((choosen)->{
				if(choosen != null) {
					mayor.stopChoosing();
					cancelWait();
					setMayor(choosen);
					broadcastMessage("§7§l"+mayor.getName()+"§9 devient le nouveau §5§lCapitaine§9.");
					startDay();
				}
			}, mayor);
			return;
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(getMayor() == null && getAlive().size() > 2)
					mayorVote();
				else
					peopleVote();
			}
		}.runTaskLater(MainLg.getInstance(), 40);
	
	}
	@Getter private LGPlayer mayor;
	
	public void setMayor(LGPlayer mayor) {
		LGPlayer latestMayor = this.mayor;
		this.mayor = mayor;
		if(mayor != null && mayor.getPlayer().isOnline()) {
			LGCustomItems.updateItem(mayor);
			mayor.updateSkin();
			mayor.updateOwnSkin();
		}
		if(latestMayor != null && latestMayor.getPlayer() != null && latestMayor.getPlayer().isOnline()) {
			LGCustomItems.updateItem(latestMayor);
			latestMayor.updateSkin();
			latestMayor.updateOwnSkin();
		}
	}
	
	@EventHandler
	public void onCustomItemChange(LGCustomItemChangeEvent e) {
		if(e.getGame() == this) {
			if(getMayor() == e.getPlayer())
				e.getConstraints().add(LGCustomItemsConstraints.MAYOR.getName());
			if(e.getPlayer().isDead())
				e.getConstraints().add(LGCustomItemsConstraints.DEAD.getName());
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSkinChange(LGSkinLoadEvent e) {
		if(e.getGame() == this) {
			e.getProfile().getProperties().removeAll("textures");
			if(getMayor() == e.getPlayer())
				e.getProfile().getProperties().put("textures", LGCustomSkin.MAYOR.getProperty());
			else
				e.getProfile().getProperties().put("textures", LGCustomSkin.VILLAGER.getProperty());
		}
	}
	
	private void mayorVote() {
		if(ended)return;
		LGMayorVoteEvent event = new LGMayorVoteEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()) {
			broadcastMessage("§9Il est temps de voter pour élire un §5§lCapitaine§9.");
			vote = new LGVote(180, 20, this, true, true, (player, secondsLeft)-> {
				return player.getCache().has("vote") ? "§6Tu votes pour §7§l"+player.getCache().<LGPlayer>get("vote").getName() : "§6Il te reste §e"+secondsLeft+" seconde"+(secondsLeft > 1 ? "s" : "")+"§6 pour voter";
			});
			vote.start(getAlive(), getInGame(), ()->{
				if(vote.getChoosen() == null)
					setMayor(getAlive().get(random.nextInt(getAlive().size())));
				else
					setMayor(vote.getChoosen());

				broadcastMessage("§7§l"+mayor.getName()+"§6 devient le §5§lCapitaine §6du village.");
				peopleVote();
			});
		}
	}
	@Getter private LGVote vote;
	boolean isPeopleVote = false;
	@EventHandler
	public void leaderChange(LGVoteLeaderChange e) {
		if(isPeopleVote && vote != null && e.getGame() == this) {
			for(LGPlayer player : e.getLatest())
				if(!e.getNow().contains(player))
					VariousUtils.setWarning(player.getPlayer(), false);
			
			for(LGPlayer player : e.getNow())
				if(!e.getLatest().contains(player))
					VariousUtils.setWarning(player.getPlayer(), true);
		}
	}
	private void peopleVote() {
		if(ended)return;
		LGVoteEvent event = new LGVoteEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()) {
			broadcastMessage("§9La phase des votes a commencé.");
			isPeopleVote = true;
			vote = new LGVote(180, 20, this, false, false, (player, secondsLeft)-> {
				return player.getCache().has("vote") ? "§6Tu votes pour §7§l"+player.getCache().<LGPlayer>get("vote").getName() : "§6Il te reste §e"+secondsLeft+" seconde"+(secondsLeft > 1 ? "s" : "")+"§6 pour voter";
			});
			vote.start(getAlive(), getInGame(), ()->{
				isPeopleVote = false;
				if(vote.getChoosen() == null || (vote.isMayorVote() && getMayor() == null))
					broadcastMessage(getMayor() != null ? "§9Le maire a décidé de gracier les accusés." : "§9Personne n'est mort aujourd'hui.");
				else {
					LGPlayerKilledEvent killEvent = new LGPlayerKilledEvent(this, vote.getChoosen(), Reason.VOTE);
					Bukkit.getPluginManager().callEvent(killEvent);
					if(killEvent.isCancelled())//chassou ?
						return;
					if(kill(killEvent.getKilled(), killEvent.getReason(), true))
						return;
				}
				nextNight();
			}, mayor);
		}//Sinon c'est à celui qui a cancel de s'en occuper
	}

	public boolean checkEndGame() {
		return checkEndGame(true);
	}
	public boolean checkEndGame(boolean doEndGame) {
		int goodGuy = 0, badGuy = 0, solo = 0;
		for(LGPlayer lgp : getAlive())
			if(lgp.getRoleWinType() == RoleWinType.LOUP_GAROU)
				badGuy++;
			else if(lgp.getRoleWinType() == RoleWinType.VILLAGE)
				goodGuy++;
			else if(lgp.getRoleWinType() == RoleWinType.SEUL)
				solo++;
		LGEndCheckEvent event = new LGEndCheckEvent(this, goodGuy == 0 || badGuy == 0 ? (goodGuy+badGuy == 0 ? LGWinType.EQUAL : (goodGuy > 0 ? LGWinType.VILLAGEOIS : LGWinType.LOUPGAROU)) : LGWinType.NONE);
		
		if((badGuy+goodGuy > 0 && solo > 0) || solo > 1)
			event.setWinType(LGWinType.NONE);
		
		if(badGuy+goodGuy == 0 && solo == 1)
			event.setWinType(LGWinType.SOLO);
		
		Bukkit.getPluginManager().callEvent(event);
		if(doEndGame && event.getWinType() != LGWinType.NONE)
			endGame(event.getWinType());
		MainLg.debug("Endgame > "+event.getWinType()+" ("+doEndGame+")");
		return event.getWinType() != LGWinType.NONE;
	}
}