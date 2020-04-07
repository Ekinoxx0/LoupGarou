package dev.loupgarou.commands.subcommands.spawns;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class AddMapCmd extends SubCommand {

	public AddMapCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("addmap"));
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
		
		if (target != null) {
			p.sendMessage(PrefixType.PARTIE + "§cMap déjà existante : " + args[2]);
			return;
		}
		
		LGMaps.getMapsInfo().getMaps().add(new LGMap(args[1], p.getWorld().getName(), Material.BEDROCK));
		p.sendMessage(PrefixType.PARTIE + "§aAjout de la map : " + args[1]);
		try {
			LGMaps.save(getMain());
			p.sendMessage(PrefixType.PARTIE + "§aSauvegarde des maps");
		} catch (IOException e) {
			p.sendMessage(PrefixType.PARTIE + "§cImpossible de sauvegarder les maps... " + e.getMessage());
		}
	}

}