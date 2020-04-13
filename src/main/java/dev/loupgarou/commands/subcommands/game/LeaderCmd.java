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

public class LeaderCmd extends SubCommand {

	public LeaderCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("leader", "chef", "chief", "owner"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (args.length == 2 && cs instanceof Player) {
			Player p = (Player) cs;
			LGPlayer lgp = LGPlayer.thePlayer(p);

			if(lgp.getGame() == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie !");
				return;
			}
			
			if(lgp.getGame().getOwner() != lgp) {
				cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas propriétaire...");
				return;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			if (target == cs) {
				cs.sendMessage(PrefixType.PARTIE + "§cImpossible sur vous même.");
				return;
			}
			
			if (target == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cJoueur inconnu !");
				return;
			}
			
			if (!lgp.getGame().getInGame().contains(LGPlayer.thePlayer(target))) {
				cs.sendMessage(PrefixType.PARTIE + "§cLe joueur n'est pas dans votre partie !");
				return;
			}
			
			lgp.getGame().setOwner(LGPlayer.thePlayer(target));
			target.sendMessage(PrefixType.PARTIE + "§6Vous êtes maintenant le propriétaire de la partie !");
			cs.sendMessage(PrefixType.PARTIE + "§a" + target.getDisplayName() + " est maintenant le propriétaire.");
		} else if (args.length == 3 && cs.hasPermission(BASE_PERM + "." + getAliases().get(0))) {
			LGGame gameTarget = MainLg.getInstance().findGame(args[1], false);
			if (gameTarget == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cPartie inconnue !");
				return;
			}
			
			Player normalTarget = Bukkit.getPlayer(args[2]);
			if (normalTarget == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cJoueur 2 inconnu !");
				return;
			}
			
			if (gameTarget.getInGame().contains(LGPlayer.thePlayer(normalTarget))) {
				cs.sendMessage(PrefixType.PARTIE + "§cLe joueur 2 n'est pas dans la partie du leader");
				return;
			}
			
			cs.sendMessage(PrefixType.PARTIE + "§6Le leader de la partie de " + gameTarget.getOwner().getName() + " est maintenant " + normalTarget.getDisplayName());
			gameTarget.setOwner(LGPlayer.thePlayer(normalTarget));
			normalTarget.sendMessage(PrefixType.PARTIE + "§6Vous êtes maintenant le propriétaire de la partie !");
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§c/" + label + " <Nouveau-Propriétaire>");
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender cs, LoupGarouCommand cmd, String[] args) {
		if(!(cs instanceof Player)) return Collections.emptyList();
		List<String> players = new ArrayList<String>();
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);

		if (args.length == 2) {
			if(lgp.getGame() == null)
				return Collections.emptyList();
			
			for(LGPlayer lgpA : lgp.getGame().getInGame())
				players.add(lgpA.getName());
		}
		
		if (args.length == 3) {
			for(LGPlayer lgpA : lgp.getGame().getInGame())
				players.add(lgpA.getName());
		}
		return players;
	}
	
	@Override
	public String getPermission() {
		return null;
	}

}