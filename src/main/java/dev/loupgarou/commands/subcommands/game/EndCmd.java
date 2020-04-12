package dev.loupgarou.commands.subcommands.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGWinType;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class EndCmd extends SubCommand {

	public EndCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("end", "stop", "destroy"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (args.length == 1) {
			if(cs instanceof Player) {
				LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
				LGGame gameTarget = lgp.getGame();
				if (gameTarget == null) {
					cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie !");
					return;
				}
				
				if(gameTarget.getOwner() == lgp) {
					stopGame(gameTarget);
				} else {
					cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas propriétaire...");
				}
			} else {
				cs.sendMessage(PrefixType.PARTIE + "§cMerci de donner le nom d'un joueur en argument");
			}
		} else if (args.length == 2 && cs.hasPermission(BASE_PERM + "." + getAliases().get(0))) {
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cJoueur inconnu !");
				return;
			}
			
			LGGame gameTarget = LGPlayer.thePlayer(target).getGame();
			if (gameTarget == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cLe joueur n'est pas en partie !");
				return;
			}

			stopGame(gameTarget);
			cs.sendMessage(PrefixType.PARTIE + "§6Partie arrêtée avec succès !");
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§cArgument inconnu...");
		}
	}
	
	private void stopGame(LGGame gameTarget) {
		gameTarget.cancelWait();
		gameTarget.endGame(LGWinType.EQUAL);
		gameTarget.broadcastMessage(PrefixType.PARTIE + "§cLa partie a été arrêtée de force !");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender cs, LoupGarouCommand cmd, String[] args) {
		if(!(cs instanceof Player)) return Collections.emptyList();
		if(!cs.hasPermission(BASE_PERM + "." + getAliases().get(0))) return Collections.emptyList();
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