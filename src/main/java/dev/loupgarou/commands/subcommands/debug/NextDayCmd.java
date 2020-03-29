package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class NextDayCmd extends SubCommand {

	public NextDayCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("nextday", "daynext", "forcenextday"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		cs.sendMessage("§aVous êtes passé à la prochaine journée");
		if(getMain().getCurrentGame() != null) {
			getMain().getCurrentGame().broadcastMessage("§2§lLe passage à la prochaine journée a été forcé !");
			getMain().getCurrentGame().cancelWait();
			for(LGPlayer lgp : getMain().getCurrentGame().getInGame())
				lgp.stopChoosing();
			getMain().getCurrentGame().endNight();
		}
	}
	
}