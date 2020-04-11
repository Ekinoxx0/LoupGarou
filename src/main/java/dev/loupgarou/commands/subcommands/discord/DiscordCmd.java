package dev.loupgarou.commands.subcommands.discord;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class DiscordCmd extends SubCommand {

	public DiscordCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("discord"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(args.length == 2 && args[1].equalsIgnoreCase("link") && cs instanceof Player) {
			MainLg.getInstance().getDiscord().getLinkServer().generateLink(LGPlayer.thePlayer((Player) cs));
			return;
		}
		cs.sendMessage(PrefixType.DISCORD + "§9http://discord.gg/2qh6Xhv");
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}