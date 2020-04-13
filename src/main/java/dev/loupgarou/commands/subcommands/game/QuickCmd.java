package dev.loupgarou.commands.subcommands.game;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class QuickCmd extends SubCommand {

	public QuickCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("quick", "veryquick", "rapide", "voterapide", "quickvote", "votequick", "rapidevote"));
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
		
		if (lgp.getGame().getVote() == null) {
			cs.sendMessage(PrefixType.PARTIE + "§cAucun vote en cours...");
			return;
		}
		
		lgp.getGame().getVote().quick(20);
		cs.sendMessage(PrefixType.PARTIE + "§aVote accéléré");
	}
	
	@Override
	public String getPermission() {
		return null;
	}

}