package dev.loupgarou.commands.subcommands.game;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
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
					((Player) cs).teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					cs.sendMessage(PrefixType.PARTIE + "§2Téléportation au lobby");
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
				target.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				cs.sendMessage(PrefixType.PARTIE + "§2Téléportation au lobby du joueur");
				return;
			}
			
			gameTarget.leave(LGPlayer.thePlayer(target));
			return;
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§cArgument inconnu...");
		}
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}