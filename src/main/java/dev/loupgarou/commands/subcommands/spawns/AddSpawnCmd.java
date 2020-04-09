package dev.loupgarou.commands.subcommands.spawns;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGMaps.LGLocation;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class AddSpawnCmd extends SubCommand {

	public AddSpawnCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("addspawn"));
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
		
		if(!target.getWorld().equals(p.getWorld().getName())) {
			p.sendMessage("§cImpossible d'ajouter un spawn si vous n'êtes pas dans le monde " + target.getWorld());
			return;
		}
		
		target.getSpawns().add(new LGLocation(p.getLocation()));
		p.sendMessage("§aSpawn ajouté ! (N°" + target.getSpawns().size() + ")");
		try {
			LGMaps.save(getMain());
			p.sendMessage("§aMap sauvegardé");
		} catch (IOException e) {
			p.sendMessage("§cImpossible de sauvegarder la maps... " + e.getMessage());
		}
	}

}