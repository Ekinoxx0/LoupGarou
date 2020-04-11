package dev.loupgarou.commands.subcommands.discord;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGGameConfig.CommunicationType;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class DiscordMoveCmd extends SubCommand {

	public DiscordMoveCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("discordmove", "move", "movediscord"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);
		if (lgp.getGame() == null) {
			cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie !");
			return;
		}
		
		if(lgp.getGame().getConfig().getCom() == CommunicationType.DISCORD) {
			lgp.getGame().getDiscord().move(lgp);
		} else {
			cs.sendMessage(PrefixType.PARTIE + "§cPartie sans liaison discord");
		}
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}