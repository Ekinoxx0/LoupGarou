package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class StatusCmd extends SubCommand {

	public StatusCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("status"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		
		if(lgp.getGame() == null) return;//TODO Msg
		
		cs.sendMessage(MainLg.getPrefix() + (lgp.getGame().getConfig().isHideRole() ? "§aHideRole actif" : "§cHideRole inactif"));
		cs.sendMessage(MainLg.getPrefix() + (lgp.getGame().getConfig().isHideVote() ? "§aHideVote actif" : "§cHideVote inactif"));
		cs.sendMessage(MainLg.getPrefix() + (lgp.getGame().getConfig().isHideVoteExtra() ? "§aHideVoteExtra actif" : "§cHideVoteExtra inactif"));
		
	}
	
}