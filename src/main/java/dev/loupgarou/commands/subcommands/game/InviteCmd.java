package dev.loupgarou.commands.subcommands.game;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;

public class InviteCmd extends SubCommand {

	public InviteCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("invite", "inviter", "invite", "propose"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if (args.length == 2 && cs instanceof Player) {
			Player p = (Player) cs;
			LGPlayer lgp = LGPlayer.thePlayer(p);

			if(lgp.getGame() == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas en partie !");
				return;
			}
			
			if(lgp.getGame().getOwner() != lgp) {
				cs.sendMessage(PrefixType.PARTIE + "§cVous n'êtes pas propriétaire...");
				return;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				cs.sendMessage(PrefixType.PARTIE + "§cJoueur inconnu !");
				return;
			}
			
			if (lgp.getGame().getInGame().contains(LGPlayer.thePlayer(target))) {
				cs.sendMessage(PrefixType.PARTIE + "§cLe joueur est déjà dans votre partie !");
				return;
			}
			
			lgp.getGame().getConfig().getInvited().add(target.getName());
			lgp.getGame().getConfig().getInvited().add(target.getDisplayName());
			target.sendMessage(PrefixType.PARTIE + "§6Vous avez été invité par " + lgp.getName());
			target.sendMessage(PrefixType.PARTIE + "§6Faites /join " + lgp.getName() + " pour le rejoindre");
			cs.sendMessage(PrefixType.PARTIE + "§aVous avez invité " + target.getDisplayName() + " à votre partie");
		}else {
			cs.sendMessage(PrefixType.PARTIE + "§c/" + label + " <Joueur>");
		}
	}
	
	@Override
	public String getPermission() {
		return null;
	}

}