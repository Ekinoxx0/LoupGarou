package dev.loupgarou.commands.subcommands.config;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class HideVoteExtraCmd extends SubCommand {

	public HideVoteExtraCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("hidevoteextra", "voteextra"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		getMain().getCurrentGame().setHideVoteExtra(!getMain().getCurrentGame().isHideVoteExtra());
		getMain().getConfig().set("hideVoteExtra", getMain().getCurrentGame().isHideVoteExtra());
		getMain().saveConfig();
		if(getMain().getCurrentGame().isHideVoteExtra()) {
			cs.sendMessage("§cVote extra cachée");
		} else {
			cs.sendMessage("§9Vote extra affichée");
		}
	}
	
}