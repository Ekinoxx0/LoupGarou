package dev.loupgarou.commands.subcommands.config;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class HideVoteExtraCmd extends SubCommand {

	public HideVoteExtraCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("hidevoteextra", "voteextra"));
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
		
		lgp.getGame().getConfig().setHideVoteExtra(!lgp.getGame().getConfig().isHideVoteExtra());
		if(lgp.getGame().getConfig().isHideVoteExtra()) {
			cs.sendMessage(PrefixType.PARTIE + "§cVote extra cachée");
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§9Vote extra affichée");
		}
	}
	
	@Override
	public String getPermission() { return null; }
	
}