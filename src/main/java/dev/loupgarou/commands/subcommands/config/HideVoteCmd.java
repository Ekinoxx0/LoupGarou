package dev.loupgarou.commands.subcommands.config;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class HideVoteCmd extends SubCommand {

	public HideVoteCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("hidevote"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		
		if(lgp.getGame() == null) return;//TODO Msg
		
		lgp.getGame().getConfig().setHideVote(!lgp.getGame().getConfig().isHideVote());
		if(lgp.getGame().getConfig().isHideVote()) {
			cs.sendMessage("§cVote cachée");
		} else {
			cs.sendMessage("§9Vote affichés");
		}
	}
	
}