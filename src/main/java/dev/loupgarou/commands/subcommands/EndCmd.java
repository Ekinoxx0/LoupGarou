package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGWinType;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class EndCmd extends SubCommand {

	public EndCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("end", "stop"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (args.length == 1) {
			if(cs instanceof Player) {
				LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
				LGGame gameTarget = lgp.getGame();
				if (gameTarget == null) {
					cs.sendMessage(MainLg.getPrefix() + "§cVous n'êtes pas en partie !");
					return;
				}
				
				if(gameTarget.getOwner() == lgp) {
					stopGame(gameTarget);
				} else {
					cs.sendMessage(MainLg.getPrefix() + "§cVous n'êtes pas propriété...");
				}
			} else {
				cs.sendMessage(MainLg.getPrefix() + "§cMerci de donner le nom d'un joueur en argument");
			}
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

			stopGame(gameTarget);
			cs.sendMessage(MainLg.getPrefix() + "§6Partie arrêtée avec succès !");
		} else {
			cs.sendMessage(MainLg.getPrefix() + "§cArgument inconnu...");
		}
	}
	
	private void stopGame(LGGame gameTarget) {
		gameTarget.cancelWait();
		gameTarget.endGame(LGWinType.EQUAL);
		gameTarget.broadcastMessage("§cLa partie a été arrêtée de force !");
	}

}