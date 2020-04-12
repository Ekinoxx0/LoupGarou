package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class ResourcePackAllCmd extends SubCommand {

	public ResourcePackAllCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("resourcepackall"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		for(Player p : Bukkit.getOnlinePlayers())
			ResourcePackCmd.load(p);
	}
	
}