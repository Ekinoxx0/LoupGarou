package dev.loupgarou.commands.subcommands.config;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class HideRoleCmd extends SubCommand {

	public HideRoleCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("hiderole"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		getMain().getCurrentGame().setHideRole(!getMain().getCurrentGame().isHideRole());
		getMain().getConfig().set("hideRole", getMain().getCurrentGame().isHideRole());
		getMain().saveConfig();
		if(getMain().getCurrentGame().isHideRole()) {
			cs.sendMessage("§cComposition cachée");
		} else {
			cs.sendMessage("§9Composition affichée");
		}
	}
	
}