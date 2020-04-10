package dev.loupgarou.commands.subcommands.config;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class RoleCmd extends SubCommand {

	public RoleCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("role", "roles", "rolemenu", "rolesmenu", "menuroles"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		LGPlayer lgp = LGPlayer.thePlayer((Player) cs);

		if(lgp.getGame() == null) {
			lgp.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie...");
			return;
		}
		
		lgp.getGame().getPartieMenu().openRoleMenu(lgp);
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
}
