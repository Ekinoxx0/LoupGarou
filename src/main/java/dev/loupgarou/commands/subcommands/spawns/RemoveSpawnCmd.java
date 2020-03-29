package dev.loupgarou.commands.subcommands.spawns;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class RemoveSpawnCmd extends SubCommand {

	public RemoveSpawnCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("removespawn", "delspawn", "deletespawn"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		Player p = (Player) cs;
		List<List<Double>> spawns = (List<List<Double>>) getMain().getConfig().getList("spawns");

		boolean finded = false;
		for (List<Double> loc : spawns) {
			Location sel = new Location(p.getWorld(), loc.get(0), loc.get(1), loc.get(2));
			if (p.getLocation().distance(sel) < 1) {
				spawns.remove(loc);
				finded = true;
			}
		}

		if (finded) {
			getMain().saveConfig();
			getMain().loadConfig();
			cs.sendMessage(MainLg.getPrefix() + "§aLa position a bien été supprimée !");
		} else {
			cs.sendMessage(MainLg.getPrefix() + "§cAucune position ici.");
		}
	}

}