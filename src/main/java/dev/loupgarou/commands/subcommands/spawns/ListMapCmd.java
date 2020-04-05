package dev.loupgarou.commands.subcommands.spawns;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class ListMapCmd extends SubCommand {

	public ListMapCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("listmap"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(args.length != 1) {
			cs.sendMessage("§c/" + label + " " + args[0]);
			return;
		}
		
		for(LGMap map : LGMaps.getMapsInfo().getMaps()) {
			cs.sendMessage("§9" + map.getName() + " : " + map.getMaterial());
		}
		
	}

}