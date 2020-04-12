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
import dev.loupgarou.utils.CommonText.PrefixType;

public class KickCmd extends SubCommand {

	public KickCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("kick", "expulser", "expulse"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		
		if (args.length == 2) {
			if(lgp.getGame() == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie !");
				return;
			}
			if(lgp.getGame().getOwner() != lgp) {
				cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas propriétaire...");
				return;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cJoueur inconnu !");
				return;
			}
			
			LGGame gameTarget = LGPlayer.thePlayer(target).getGame();
			if (gameTarget == null || gameTarget != lgp.getGame()) {
				cs.sendMessage(PrefixType.PARTIE + "§cJoueur inconnu !");
				return;
			}

			cs.sendMessage(PrefixType.PARTIE + "§aJoueur expulsé !");
			gameTarget.leave(LGPlayer.thePlayer(target));
			target.sendMessage(PrefixType.PARTIE + "§cVous avez été expulsé !");
			return;
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§cArgument inconnu...");
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender cs, LoupGarouCommand cmd, String[] args) {
		if(!(cs instanceof Player)) return Collections.emptyList();
		List<String> players = new ArrayList<String>();
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		
		if(lgp.getGame() == null)
			return Collections.emptyList();
		
		for(LGPlayer lgpA : lgp.getGame().getInGame())
			players.add(lgpA.getName());
		return players;
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}