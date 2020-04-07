package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class QuickCmd extends SubCommand {

	public QuickCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("quick", "veryquick"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);

		if(lgp.getGame() == null) {
			lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie...");
			return;
		}
		
		if(lgp.getGame().getOwner() != lgp) {
			lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas le propriétaire de la partie...");
			return;
		}
		
		if (lgp.getGame() != null) {
			if (lgp.getGame().getVote() != null) {//TODO fix quick
				lgp.getGame().getVote().quick(20);
				cs.sendMessage(PrefixType.PARTIE + "§aQuick timer");
			} else {
				cs.sendMessage(PrefixType.PARTIE + "§cNo vote!");
			}
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§cNo game");
		}
	}

}