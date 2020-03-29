package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.RoleMenu;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class MenuCmd extends SubCommand {

	public MenuCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("menu"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		RoleMenu.openMenu((Player) cs);
	}
	
	@Override
	public String getPermission() {
		return this.getMain().getCurrentGame().hideRole ? super.getPermission() : null;
	}
	
}
