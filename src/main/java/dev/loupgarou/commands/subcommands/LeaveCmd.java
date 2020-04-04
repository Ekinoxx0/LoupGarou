package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class LeaveCmd extends SubCommand {

	public LeaveCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("leave", "quit"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (args.length == 1) {
			if(cs instanceof Player) {
				LGGame gameTarget = LGPlayer.thePlayer((Player) cs).getGame();
				if (gameTarget == null) {
					cs.sendMessage(MainLg.getPrefix() + "§cVous n'êtes pas en partie !");
					return;
				}

				gameTarget.leave(LGPlayer.thePlayer((Player) cs));
			} else {
				cs.sendMessage(MainLg.getPrefix() + "§cMerci de donner le nom d'un joueur en argument");
			}
			return;
		} else if (args.length == 2 && cs.hasPermission(BASE_PERM + "." + getAliases().get(0))) {
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				cs.sendMessage(MainLg.getPrefix() + "§cJoueur inconnu !");
				return;
			}
			
			LGGame gameTarget = LGPlayer.thePlayer(target).getGame();
			if (gameTarget == null) {
				cs.sendMessage(MainLg.getPrefix() + "§cLe joueur n'est pas en partie !");
				return;
			}
			
			gameTarget.leave(LGPlayer.thePlayer(target));
			return;
		} else {
			cs.sendMessage(MainLg.getPrefix() + "§cArgument inconnu...");
		}
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}