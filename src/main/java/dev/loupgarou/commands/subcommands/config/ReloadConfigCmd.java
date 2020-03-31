package dev.loupgarou.commands.subcommands.config;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class ReloadConfigCmd extends SubCommand {

	public ReloadConfigCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("reloadconfig", "reload"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		cs.sendMessage("§cAucune config à reload");//TODO
	}
	
}