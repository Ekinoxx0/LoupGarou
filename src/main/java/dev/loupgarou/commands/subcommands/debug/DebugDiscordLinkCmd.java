package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class DebugDiscordLinkCmd extends SubCommand {

	public DebugDiscordLinkCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("debugdiscordlink"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		for(Entry<String, Object> entry : MainLg.getInstance().getDiscord().getLinkServer().getConfig().getValues(true).entrySet())
			cs.sendMessage(entry.getKey() + " : " + entry.getValue());
		cs.sendMessage("§c" + MainLg.getInstance().getDiscord().getLinkServer().getConfig().getValues(true).size());
	}
	
}