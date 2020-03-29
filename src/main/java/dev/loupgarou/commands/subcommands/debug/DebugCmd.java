package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class DebugCmd extends SubCommand {

	public DebugCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("debug", "debugpl"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		Player d = (Player) cs;
		if(MainLg.getDEBUGS().contains(d)) {
			MainLg.getDEBUGS().remove(d);
		} else {
			MainLg.getDEBUGS().add(d);
		}
	}
	
}