package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class StatusCmd extends SubCommand {

	public StatusCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("status"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		cs.sendMessage(MainLg.getPrefix() + (getMain().getCurrentGame().isHideRole() ? "§aHideRole actif" : "§cHideRole inactif"));
		cs.sendMessage(MainLg.getPrefix() + (getMain().getCurrentGame().isHideVote() ? "§aHideVote actif" : "§cHideVote inactif"));
		cs.sendMessage(MainLg.getPrefix() + (getMain().getCurrentGame().isHideVoteExtra() ? "§aHideVoteExtra actif" : "§cHideVoteExtra inactif"));
		
	}
	
}