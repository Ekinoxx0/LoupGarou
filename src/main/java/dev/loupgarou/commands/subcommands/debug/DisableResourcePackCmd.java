package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class DisableResourcePackCmd extends SubCommand {

	public DisableResourcePackCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("disableresourcepack", "disableressourcepack", "disablerp", "removerp", "emptyressourcepack", "noresourcepack"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		Player p = (Player) cs;
		ResourcePackCmd.reset(p);
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}