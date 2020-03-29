package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class QuickCmd extends SubCommand {

	public QuickCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("quick", "veryquick"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (getMain().getCurrentGame() != null) {
			if (getMain().getCurrentGame().getVote() != null) {
				getMain().getCurrentGame().getVote().quick(20);
				cs.sendMessage("§aQuick timer");
			} else {
				cs.sendMessage("§cNo vote!");
			}
		} else {
			cs.sendMessage("§cNo game");
		}
	}

}