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
		cs.sendMessage("§aVous avez bien reload la config !");
		cs.sendMessage("§7§oSi vous avez changé les rôles, écriver §8§o/lg joinall§7§o !");
		getMain().loadConfig();
	}
	
}