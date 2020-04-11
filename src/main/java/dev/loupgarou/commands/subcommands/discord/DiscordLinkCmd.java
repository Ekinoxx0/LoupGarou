package dev.loupgarou.commands.subcommands.discord;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class DiscordLinkCmd extends SubCommand {

	public DiscordLinkCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("discordlink", "linkdiscord"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		MainLg.getInstance().getDiscord().getLinkServer().generateLink(lgp);
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}