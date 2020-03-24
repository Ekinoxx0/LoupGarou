package fr.leomelki.loupgarou;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGWinType;

public class CommandLG implements CommandExecutor, TabCompleter {

	private final MainLg mainLg;
	
	public CommandLG(MainLg mainLg) {
		this.mainLg = mainLg;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("lg")) {
			if(!sender.hasPermission("loupgarou.admin")) {
				sender.sendMessage(MainLg.getPrefix()+"§4Erreur: Vous n'avez pas la permission...");
				return true;
			}
			
			if(args.length >= 1) {
				
				switch(args[0].toLowerCase()) {
				
				case "clearallspawn":
					mainLg.getConfig().set("spawns", new ArrayList<Object>());
					mainLg.saveConfig();
					mainLg.loadConfig();
					sender.sendMessage(MainLg.getPrefix()+"§cTous les spawns ont été supprimés.");
					break;
				
				case "addspawn":
					Player player = (Player)sender;
					Location loc = player.getLocation();
					List<Object> list = (List<Object>) mainLg.getConfig().getList("spawns");
					list.add(Arrays.asList((double)loc.getBlockX(), loc.getY(), (double)loc.getBlockZ(), (double)loc.getYaw(), (double)loc.getPitch()));
					mainLg.saveConfig();
					mainLg.loadConfig();
					sender.sendMessage(MainLg.getPrefix()+"§aLa position a bien été ajoutée !");
					return true;
					
				case "removespawn":
					Player p1 = (Player)sender;
					List<List<Double>> list1 = (List<List<Double>>) mainLg.getConfig().getList("spawns");
					
					boolean finded = false;
					for(List<Double> l : list1) {
						Location sel = new Location(p1.getWorld(), l.get(0), l.get(1), l.get(2));
						if(p1.getLocation().distance(sel) < 1) {
							list1.remove(l);
							finded = true;
						}
					}
					
					if(finded) {
						mainLg.saveConfig();
						mainLg.loadConfig();
						sender.sendMessage(MainLg.getPrefix()+"§aLa position a bien été supprimée !");
					} else {
						sender.sendMessage(MainLg.getPrefix()+"§cAucune position ici.");
					}
					
					break;
					
				case "end":
					LGPlayer.thePlayer(Bukkit.getPlayer(args[1])).getGame().cancelWait();
					LGPlayer.thePlayer(Bukkit.getPlayer(args[1])).getGame().endGame(LGWinType.EQUAL);
					LGPlayer.thePlayer(Bukkit.getPlayer(args[1])).getGame().broadcastMessage("§cLa partie a été arrêtée de force !");
					return true;
					
				case "start":
					if(args.length != 2) {
						sender.sendMessage("§aVous avez bien démarré une nouvelle partie !");
						return true;
					}
					sender.sendMessage("§aVous avez bien démarré une nouvelle partie !");
					LGPlayer.thePlayer(Bukkit.getPlayer(args[1])).getGame().updateStart();
					return true;
				
				case "reloadconfig":
					sender.sendMessage("§aVous avez bien reload la config !");
					sender.sendMessage("§7§oSi vous avez changé les rôles, écriver §8§o/lg joinall§7§o !");
					mainLg.loadConfig();
					return true;
					
				case "deaddiscord":
					mainLg.getDiscord().clearDead();
					break;
					
				case "debugresetpl":
					LGPlayer.reset();
					sender.sendMessage("§aDebug reset player list");
					break;
					
				case "muteall":
					mainLg.getDiscord().setMutedChannel(true);
					break;
					
				case "unmuteall":
					mainLg.getDiscord().setMutedChannel(false);
					break;
					
				case "joinall":
					for(Player p : Bukkit.getOnlinePlayers())
						Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(p, "joinall"));
					for(Player p : Bukkit.getOnlinePlayers())
						Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(p, "joinall"));
					
					int playablePlayers = 0;
					for(Player p : Bukkit.getOnlinePlayers())
						if(p.getGameMode() != GameMode.SPECTATOR) playablePlayers++;
					
					if(playablePlayers > mainLg.getCurrentGame().getMaxPlayers()) {
						sender.sendMessage(MainLg.getPrefix()+"§cPas assez de rôle pour contenir tous les joueurs");
					} else if(playablePlayers < mainLg.getCurrentGame().getMaxPlayers()) {
						sender.sendMessage(MainLg.getPrefix()+"§cTrop de rôle pour le nombre de joueur en ligne...");
					}

					return true;
						
				case "reloadpacks":
					for(Player p : Bukkit.getOnlinePlayers())
						Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(p, "reloadPacks"));
					for(Player p : Bukkit.getOnlinePlayers())
						Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(p, "reloadPacks"));
					return true;
					
				case "nextnight":
					sender.sendMessage("§aVous êtes passé à la prochaine nuit");
					if(mainLg.getCurrentGame() != null) {
						mainLg.getCurrentGame().broadcastMessage("§2§lLe passage à la prochaine nuit a été forcé !");
						for(LGPlayer lgp : mainLg.getCurrentGame().getInGame())
							lgp.stopChoosing();
						mainLg.getCurrentGame().cancelWait();
						mainLg.getCurrentGame().nextNight();
					}
					return true;
					
				case "nextday":
					sender.sendMessage("§aVous êtes passé à la prochaine journée");
					if(mainLg.getCurrentGame() != null) {
						mainLg.getCurrentGame().broadcastMessage("§2§lLe passage à la prochaine journée a été forcé !");
						mainLg.getCurrentGame().cancelWait();
						for(LGPlayer lgp : mainLg.getCurrentGame().getInGame())
							lgp.stopChoosing();
						mainLg.getCurrentGame().endNight();
					}
					return true;
					
				case "quick":
					if(mainLg.getCurrentGame() != null) {
						mainLg.getCurrentGame().setWaitTicks(20 * 20);
						sender.sendMessage("§aQuick timer");
					}
					return true;
					
				case "veryquick":
					if(mainLg.getCurrentGame() != null) {
						mainLg.getCurrentGame().setWaitTicks(20 * 5);
						sender.sendMessage("§aVery Quick timer");
					}
					return true;
					
				case "roles":
					if(args.length == 1 || args[1].equalsIgnoreCase("all")) {
						sender.sendMessage(MainLg.getPrefix()+"§6Voici la liste des rôles:");
						int index = 0;
						for(String role : mainLg.getRoles().keySet())
							sender.sendMessage(MainLg.getPrefix()+"  §e- "+index+++" - §6"+role+" §e> "+MainLg.getInstance().getConfig().getInt("role."+role));
						sender.sendMessage("\n"+MainLg.getPrefix()+" §7Écrivez §8§o/lg role set <role_id/role_name> <nombre>§7 pour définir le nombre de joueurs qui devrons avoir ce rôle.");
					}else if(args.length == 1 || args[1].equalsIgnoreCase("list")) {
						sender.sendMessage(MainLg.getPrefix()+"§6Voici la liste des rôles:");
						int index = 0;
						int total = 0;
						for(String role : mainLg.getRoles().keySet()) {
							if(MainLg.getInstance().getConfig().getInt("role."+role) <= 0) {
								total += MainLg.getInstance().getConfig().getInt("role."+role);
								sender.sendMessage(MainLg.getPrefix()+"  §e- "+ index++ +" - §6"+role+" §e> "+MainLg.getInstance().getConfig().getInt("role."+role));
							}
						}
						sender.sendMessage("\n"+MainLg.getPrefix()+" §7Écrivez §8§o/lg role set <role_id/role_name> <nombre>§7 pour définir le nombre de joueurs qui devrons avoir ce rôle.");
						sender.sendMessage("\n"+MainLg.getPrefix()+" §9Total : " + total);
					} else {
						if(args[1].equalsIgnoreCase("set") && args.length == 4) {
							String role = null;
							if(args[2].length() <= 2)
								try {
									Integer i = Integer.valueOf(args[2]);
									Object[] array = mainLg.getRoles().keySet().toArray();
									if(array.length <= i) {
										sender.sendMessage(MainLg.getPrefix()+"§4Erreur: §cCe rôle n'existe pas.");
										return true;
									}else
										role = (String)array[i];
								}catch(Exception err) {sender.sendMessage(MainLg.getPrefix()+"§4Erreur: §cCeci n'est pas un nombre");}
							else
								role = args[2];
							
							if(role != null) {
								String real_role = null;
								for(String real : mainLg.getRoles().keySet())
									if(real.equalsIgnoreCase(role)) {
										real_role = real;
										break;
									}
								
								if(real_role != null) {
									try {
										MainLg.getInstance().getConfig().set("role."+real_role, Integer.valueOf(args[3]));
										sender.sendMessage(MainLg.getPrefix()+"§6Il y aura §e"+args[3]+" §6"+real_role);
										mainLg.saveConfig();
										mainLg.loadConfig();
										sender.sendMessage("§7§oSi vous avez fini de changer les rôles, écriver §8§o/lg joinall§7§o !");
									}catch(Exception err) {
										sender.sendMessage(MainLg.getPrefix()+"§4Erreur: §c"+args[3]+" n'est pas un nombre");
									}
									return true;
								}
							}
							sender.sendMessage(MainLg.getPrefix()+"§4Erreur: §cLe rôle que vous avez entré est incorrect");
							
						} else {
							sender.sendMessage(MainLg.getPrefix()+"§4Erreur: §cCommande incorrecte.");
							sender.sendMessage(MainLg.getPrefix()+"§4Essayez §c/lg roles set <role_id/role_name> <nombre>§4 ou §c/lg roles list");
						}
					}
					return true;
					
					default:
						sender.sendMessage(MainLg.getPrefix()+"§4Erreur: §cCommande incorrecte.");
						sender.sendMessage(MainLg.getPrefix()+"§4Essayez /lg §caddSpawn/end/start/nextNight/nextDay/reloadConfig/roles/reloadPacks/joinAll");
						return true;
					
				}
				
			}
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(!sender.hasPermission("loupgarou.admin"))
			return new ArrayList<String>(0);
		
		if(args.length > 1) {
			if(args[0].equalsIgnoreCase("roles"))
				if(args.length == 2)
					return getStartingList(args[1], "list", "set", "all");
				else if(args.length == 3 && args[1].equalsIgnoreCase("set"))
					return getStartingList(args[2], mainLg.getRoles().keySet().toArray(new String[mainLg.getRoles().size()]));
				else if(args.length == 4)
					return Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
		}else if(args.length == 1)
			return getStartingList(args[0], "clearallspawn", "addspawn", "removespawn", "quick", "veryquick", "end", "start", "nextnight", "nextday", "reloadconfig", "roles", "joinall", "reloadpacks");
		return new ArrayList<String>(0);
	}
	
	private List<String> getStartingList(String startsWith, String... list){
		startsWith = startsWith.toLowerCase();
		ArrayList<String> returnlist = new ArrayList<String>();
		if(startsWith.length() == 0)
			return Arrays.asList(list);
		for(String s : list)
			if(s.toLowerCase().startsWith(startsWith))
				returnlist.add(s);
		return returnlist;
	}
	
	
	
}
