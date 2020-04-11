package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class DiscordCmd extends SubCommand {

	public DiscordCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("discord"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		cs.sendMessage(PrefixType.DISCORD + "§9http://discord.gg/2qh6Xhv");
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}