package dev.loupgarou.commands.subcommands.discord;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class DeadDiscordCmd extends SubCommand {

	public DeadDiscordCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("deaddiscord", "cleardeads", "cleardead", "cleardeaddiscord", "cleardeadsdiscord"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		getMain().getDiscord().clearDead();
	}
	
}
