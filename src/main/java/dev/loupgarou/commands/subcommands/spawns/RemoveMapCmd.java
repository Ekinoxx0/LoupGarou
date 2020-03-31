package dev.loupgarou.commands.subcommands.spawns;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class RemoveMapCmd extends SubCommand {

	public RemoveMapCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("removemap"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		
		Player p = (Player) cs;
		
		if(args.length != 2) {
			p.sendMessage("§c/" + label + " " + args[0] + " <MAP>");
			return;
		}
		
		for(LGMap map : LGMaps.getMapsInfo().getMaps()) {
			if(map.getName().equalsIgnoreCase(args[1])) {
				LGMaps.getMapsInfo().getMaps().remove(map);

				p.sendMessage("§aSuppression de la map : " + args[1]);
				try {
					LGMaps.save(getMain());
					p.sendMessage("§aSauvegarde des maps");
				} catch (IOException e) {
					p.sendMessage("§cImpossible de sauvegarder les maps... " + e.getMessage());
				}
				return;
			}
		}
		

		p.sendMessage("§cNom de map inconnu");
	}

}