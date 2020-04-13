package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class MaintenanceCmd extends SubCommand {

	public MaintenanceCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("maintenance"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		MainLg.getInstance().setMaintenanceMode(!MainLg.getInstance().isMaintenanceMode());
		cs.sendMessage("Maintenance: " + MainLg.getInstance().isMaintenanceMode());
	}
	
}