package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class NextNightCmd extends SubCommand {

	public NextNightCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("nextnight", "nightnext", "forcenextday"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		cs.sendMessage("§aVous êtes passé à la prochaine nuit");
		if(getMain().getCurrentGame() != null) {
			getMain().getCurrentGame().broadcastMessage("§2§lLe passage à la prochaine nuit a été forcé !");
			for(LGPlayer lgp : getMain().getCurrentGame().getInGame())
				lgp.stopChoosing();
			getMain().getCurrentGame().cancelWait();
			getMain().getCurrentGame().nextPreNight();
		}
	}
	
}