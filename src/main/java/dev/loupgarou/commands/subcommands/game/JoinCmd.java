package dev.loupgarou.commands.subcommands.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class JoinCmd extends SubCommand {

	public JoinCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("join", "go"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (args.length == 2) {
			if(cs instanceof Player) {
				LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
				LGGame gameTarget = lgp.getGame();
				if (gameTarget != null) {
					cs.sendMessage(PrefixType.PARTIE + "§cVous êtes déjà en partie !");
					return;
				}
				
				gameTarget = MainLg.getInstance().findGame(args[1]);
				
				if(gameTarget == null) {
					if(Bukkit.getPlayer(args[1]) == null) {
						cs.sendMessage(PrefixType.PARTIE + "§cAucune partie avec ce code");
						return;
					}
					
					LGPlayer target = LGPlayer.thePlayer(Bukkit.getPlayer(args[1]));
					if(lgp == target) {
						cs.sendMessage(PrefixType.PARTIE + "§cImpossible sur vous même.");
						return;
					}
					
					if(target.getGame() == null) {
						cs.sendMessage(PrefixType.PARTIE + "§cAucune partie avec ce pseudo");
						return;
					}
					
					if(target.getGame().getConfig().isPrivateGame()) {
						cs.sendMessage(PrefixType.PARTIE + "§cPartie privée ! Demandez le code privé de la partie et faites /join <code>");
						return;
					}

					cs.sendMessage(PrefixType.PARTIE + "§cPartie indisponible... ERREUR#95137551421345");
					return;
				}

				cs.sendMessage(PrefixType.PARTIE + "§7Vous rejoignez la partie §l" + args[1].toUpperCase());
				gameTarget.tryToJoin(lgp);
			} else {
				cs.sendMessage(PrefixType.PARTIE + "§cMerci de donner le nom d'un joueur en argument");
			}
		} else if (args.length == 3 && cs.hasPermission(BASE_PERM + "." + getAliases().get(0))) {
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cJoueur inconnu !");
				return;
			}
			
			LGGame gameTarget = LGPlayer.thePlayer(target).getGame();
			if (gameTarget != null) {
				cs.sendMessage(PrefixType.PARTIE + "§cLe joueur est déjà en partie !");
				return;
			}

			gameTarget = MainLg.getInstance().findGame(args[1]);
			
			if(gameTarget == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cAucune partie avec ce code ou la partie est privée");
				return;
			}

			cs.sendMessage(PrefixType.PARTIE + "§7Vous essayez de faire rejoindre la partie §l" + args[1].toUpperCase());
			gameTarget.tryToJoin(LGPlayer.thePlayer(target));
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§cArgument inconnu...");
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender cs, LoupGarouCommand cmd, String[] args) {
		if(!(cs instanceof Player)) return Collections.emptyList();
		List<String> players = new ArrayList<String>();
		for(LGPlayer lgp : LGPlayer.all())
			if(lgp.getGame() != null)
				players.add(lgp.getName());
		return players;
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}