package dev.loupgarou.commands.subcommands.spawns;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonParseException;

import dev.loupgarou.classes.LGMaps;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class SaveMapCmd extends SubCommand {

	public SaveMapCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("savemap", "savemaps"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		
		Player p = (Player) cs;
		
		try {
			LGMaps.save(getMain());
			p.sendMessage("§aSauvegarde des maps avec succès !");
		} catch (JsonParseException | IOException e) {
			p.sendMessage("§cImpossible de sauvegarder les maps : " + e.getMessage());
		}
	}

}