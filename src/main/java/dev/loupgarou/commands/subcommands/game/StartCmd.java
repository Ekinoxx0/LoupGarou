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

public class StartCmd extends SubCommand {

	public StartCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("start"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (args.length == 1) {
			if(cs instanceof Player) {
				LGGame gameTarget = LGPlayer.thePlayer((Player) cs).getGame();
				if (gameTarget == null) {
					cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie !");
					return;
				}

				gameTarget.updateStart();
			} else {
				cs.sendMessage(PrefixType.PARTIE + "§cMerci de donner le nom d'un joueur en argument");
			}
			return;
		} else if (args.length == 2) {
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

			gameTarget.updateStart();
			cs.sendMessage(PrefixType.PARTIE + "§6Partie arrêtée avec succès !");
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