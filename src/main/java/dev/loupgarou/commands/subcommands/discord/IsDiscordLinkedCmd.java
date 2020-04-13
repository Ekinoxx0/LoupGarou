package dev.loupgarou.commands.subcommands.discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class IsDiscordLinkedCmd extends SubCommand {

	public IsDiscordLinkedCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("isdiscordlinked", "isdiscordlink", "isdiscordlinks", "verifydiscordlink"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(args.length == 2) {
			Player target = Bukkit.getPlayer(args[1]);
			
			if(target == null) {
				cs.sendMessage(PrefixType.DISCORD + "§cJoueur inconnu");
				return;
			}
			
			cs.sendMessage(PrefixType.DISCORD + "§9Reconnu : " + (MainLg.getInstance().getDiscord().isRecognized(LGPlayer.thePlayer(target))));
			cs.sendMessage(PrefixType.DISCORD + "§9Liaison : " + (MainLg.getInstance().getDiscord().getLinkServer().isLinked(LGPlayer.thePlayer(target))));
			cs.sendMessage(PrefixType.DISCORD + "§9get() : " + (MainLg.getInstance().getDiscord().get(LGPlayer.thePlayer(target)) != null));
			return;
		}
		
		cs.sendMessage(PrefixType.DISCORD + "§c/" + label + " <JOUEUR>");
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, LoupGarouCommand cmd, String[] args) {
		if(!(cs instanceof Player)) return Collections.emptyList();
		List<String> players = new ArrayList<String>();
		for(Player p : Bukkit.getOnlinePlayers())
			players.add(p.getDisplayName());
		return players;
	}
}