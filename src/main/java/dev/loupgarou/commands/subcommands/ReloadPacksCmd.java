package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class ReloadPacksCmd extends SubCommand {

	public ReloadPacksCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("reloadpacks", "reloadpack", "reloadrsscpack"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		for(Player p : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(p, "reloadPacks"));
		for(Player p : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(p, "reloadPacks"));
	}
	
}