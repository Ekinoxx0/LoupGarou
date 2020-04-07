package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class DiscordCmd extends SubCommand {

	public DiscordCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("discord", "discordlink", "linkdiscord"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		MainLg.getInstance().getDiscord().getLinkServer().generateLink(lgp);
	}
	
}