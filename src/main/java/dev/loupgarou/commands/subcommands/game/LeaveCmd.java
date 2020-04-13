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
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.VariousUtils;
import dev.loupgarou.utils.CommonText.PrefixType;

public class LeaveCmd extends SubCommand {

	public LeaveCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("leave", "quit", "hub", "spawn", "lobby"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (args.length == 1) {
			if(cs instanceof Player) {
				LGGame gameTarget = LGPlayer.thePlayer((Player) cs).getGame();
				if (gameTarget == null) {
					VariousUtils.setupLobby(LGPlayer.thePlayer((Player) cs));
					return;
				}

				gameTarget.leave(LGPlayer.thePlayer((Player) cs));
			} else {
				cs.sendMessage(PrefixType.PARTIE + "§cMerci de donner le nom d'un joueur en argument");
			}
			return;
		} else if (args.length == 2 && cs.hasPermission(BASE_PERM + "." + getAliases().get(0))) {
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cJoueur inconnu !");
				return;
			}
			
			LGGame gameTarget = LGPlayer.thePlayer(target).getGame();
			if (gameTarget == null) {
				VariousUtils.setupLobby(LGPlayer.thePlayer(target));
				cs.sendMessage(PrefixType.PARTIE + "§2Téléportation au lobby du joueur");
				return;
			}
			
			gameTarget.leave(LGPlayer.thePlayer(target));
			target.sendMessage(PrefixType.PARTIE + "§6Quelqu'un vous a forcer a quitter votre partie.");
			cs.sendMessage(PrefixType.PARTIE + "§2Le joueur a quitté sa partie de force.");
			return;
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§cArgument inconnu...");
		}
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