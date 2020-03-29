package dev.loupgarou.commands.subcommands.discord;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class MuteAllCmd extends SubCommand {

	public MuteAllCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("muteall", "allmute", "mutealldiscord"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		getMain().getDiscord().setMutedChannel(true);
		cs.sendMessage("§aMute all discord");
	}
	
}