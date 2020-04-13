package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.utils.CommonText.PrefixType;

public class CurrentRolesCmd extends SubCommand {

	public CurrentRolesCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("currentroles"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);

		if(lgp.getGame() == null) {
			lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie...");
			return;
		}

		cs.sendMessage(PrefixType.PARTIE + "§7§lRoles:");
		for(Role r : lgp.getGame().getRoles()) {
			String players = "";
			for(LGPlayer rLgp : r.getPlayers())
				players += rLgp.getName() + ",";
			cs.sendMessage(PrefixType.PARTIE + "§7" + r.getPlayers().size() + " " + r.getName() + "§7 : " + players);
		}
	}
	
}