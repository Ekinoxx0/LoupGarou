package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class JoinAllCmd extends SubCommand {
	//TODO Delete this shit.
	public JoinAllCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("joinall", "alljoin"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		
		if(lgp.getGame() == null) return;//TODO Msg
		
		for(Player p : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(p, "joinall"));
		for(Player p : Bukkit.getOnlinePlayers())
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(p, "joinall"));
		
		int playablePlayers = 0;
		for(Player p : Bukkit.getOnlinePlayers())
			if(p.getGameMode() != GameMode.SPECTATOR) playablePlayers++;
		
		if(playablePlayers > lgp.getGame().getMaxPlayers()) {
			cs.sendMessage(MainLg.getPrefix()+"§cPas assez de rôle pour contenir tous les joueurs");
		} else if(playablePlayers < lgp.getGame().getMaxPlayers()) {
			cs.sendMessage(MainLg.getPrefix()+"§cTrop de rôle pour le nombre de joueur en ligne...");
		}
	}
	
}
