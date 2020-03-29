package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class DebugResetPlCmd extends SubCommand {

	public DebugResetPlCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("debugresetpl", "debugreset"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		LGPlayer.reset();
		cs.sendMessage("§aDebug reset player list");
	}
	
}