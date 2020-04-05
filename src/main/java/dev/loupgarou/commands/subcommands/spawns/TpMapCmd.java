package dev.loupgarou.commands.subcommands.spawns;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class TpMapCmd extends SubCommand {

	public TpMapCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("tpmap"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		Player p = (Player) cs;
		
		if(args.length != 2) {
			p.sendMessage("§c/" + label + " " + args[0] + " <MAP>");
			return;
		}
		
		LGMap target = null;
		for(LGMap map : LGMaps.getMapsInfo().getMaps())
			if(map.getName().equalsIgnoreCase(args[1]))
				target = map;
		
		if (target == null) {
			p.sendMessage("§cMap inconnue : " + args[2]);
			return;
		}
		
		if(target.getSpawns().size() == 0) {
			p.sendMessage("§cAucun spawn pour " + args[2]);
			return;
		}

		p.teleport(target.getSpawns().get(0).toLocation());
		p.sendMessage("§aTp spawn");
	}

}