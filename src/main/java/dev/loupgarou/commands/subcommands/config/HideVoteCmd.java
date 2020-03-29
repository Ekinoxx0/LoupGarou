package dev.loupgarou.commands.subcommands.config;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class HideVoteCmd extends SubCommand {

	public HideVoteCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("hidevote"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		getMain().getCurrentGame().setHideVote(!getMain().getCurrentGame().isHideVote());
		getMain().getConfig().set("hideVote", getMain().getCurrentGame().isHideVote());
		getMain().saveConfig();
		if(getMain().getCurrentGame().isHideVote()) {
			cs.sendMessage("§cVote cachée");
		} else {
			cs.sendMessage("§9Vote affichés");
		}
	}
	
}