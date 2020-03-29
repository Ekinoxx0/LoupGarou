package dev.loupgarou.commands.subcommands.discord;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class UnmuteAllCmd extends SubCommand {

	public UnmuteAllCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("unmuteall", "allunmute", "unmutealldiscord"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		getMain().getDiscord().setMutedChannel(false);
		cs.sendMessage("§aUnmute all discord");
	}

}