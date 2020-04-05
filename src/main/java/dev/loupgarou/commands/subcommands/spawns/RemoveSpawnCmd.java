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

public class RemoveSpawnCmd extends SubCommand {

	public RemoveSpawnCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("removespawn", "delspawn", "deletespawn"));
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
		
		if(target.getWorld() != p.getWorld().getName()) {
			p.sendMessage("§cImpossible de supprimer un spawn si vous n'êtes pas dans le monde " + target.getWorld());
			return;
		}
		
		LGLocation selectedLoc = null;
		for(LGLocation lgl : target.getSpawns())
			if(p.getLocation().distance(lgl.toLocation(target)) < 1)
				selectedLoc = lgl;

		if (selectedLoc == null) {
			p.sendMessage("§cVous n'êtes à coté d'aucun spawn...");
			return;
		}
		
		target.getSpawns().remove(selectedLoc);
		p.sendMessage("§aSpawn supprimé ! (N°" + target.getSpawns().size() + ")");
		try {
			LGMaps.save(getMain());
			p.sendMessage("§aMap sauvegardé");
		} catch (IOException e) {
			p.sendMessage("§cImpossible de sauvegarder la maps... " + e.getMessage());
		}
	}

}