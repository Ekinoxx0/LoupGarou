package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.menu.RoleMenu;

public class MenuCmd extends SubCommand {

	public MenuCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("menu"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		
		if(lgp.getGame() == null) return;//TODO Msg
		if(lgp.getGame().getConfig().isHideRole()) return;//TODO Msg
		RoleMenu.openMenu(lgp);
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}
