package dev.loupgarou.commands.subcommands.spawns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class AddSpawnCmd extends SubCommand {

	public AddSpawnCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("addspawn"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(CommandSender cs, String label, String[] args) {
		Player player = (Player) cs;
		Location loc = player.getLocation();
		List<Object> list = (List<Object>) getMain().getConfig().getList("spawns", new ArrayList<List<Double>>());
		list.add(
				Arrays.asList(
						loc.getBlockX(), 
						loc.getY(),
						loc.getBlockZ(), 
						loc.getYaw(),
						loc.getPitch()
						)
				);
		getMain().saveConfig();
		getMain().loadConfig();
		cs.sendMessage(MainLg.getPrefix() + "§aLa position a bien été ajoutée !");
	}

}