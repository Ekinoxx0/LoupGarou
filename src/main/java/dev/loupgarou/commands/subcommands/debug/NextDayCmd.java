package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class NextDayCmd extends SubCommand {

	public NextDayCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("nextday", "daynext", "forcenextday"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		
		if(lgp.getGame() == null) return;//TODO Msg
		
		cs.sendMessage("§aVous êtes passé à la prochaine journée");
		if(lgp.getGame() != null) {
			lgp.getGame().broadcastMessage("§2§lLe passage à la prochaine journée a été forcé !");
			lgp.getGame().cancelWait();
			for(LGPlayer l : lgp.getGame().getInGame())
				l.stopChoosing();
			lgp.getGame().endNight();
		}
	}
	
}