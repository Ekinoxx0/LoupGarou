package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.menu.MainMenu;

public class MainMenuCmd extends SubCommand {

	public MainMenuCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("mainmenu"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		MainMenu.openMenu(lgp);
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}
