package dev.loupgarou.commands.subcommands.spawns;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class ClearAllSpawnCmd extends SubCommand {

	public ClearAllSpawnCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("clearall"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		getMain().getConfig().set("spawns", new ArrayList<Object>());
		getMain().saveConfig();
		getMain().loadConfig();
		cs.sendMessage(MainLg.getPrefix() + "§cTous les spawns ont été supprimés.");
	}
	
}