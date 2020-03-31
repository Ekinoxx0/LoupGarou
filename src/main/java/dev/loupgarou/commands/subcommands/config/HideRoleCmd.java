package dev.loupgarou.commands.subcommands.config;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class HideRoleCmd extends SubCommand {

	public HideRoleCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("hiderole"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		
		if(lgp.getGame() == null) return;//TODO Msg
		
		lgp.getGame().getConfig().setHideRole(!lgp.getGame().getConfig().isHideRole());
		if(lgp.getGame().getConfig().isHideRole()) {
			cs.sendMessage("§cComposition cachée");
		} else {
			cs.sendMessage("§9Composition affichée");
		}
	}
	
}