package dev.loupgarou.commands.subcommands.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.TComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
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
			lgp.sendMessage(new TComponent(PrefixType.PARTIE), new TComponent("§6Cliquez §lICI§6 pour rejoindre !").setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/join " + lgp.getGame().getKey())));
			cs.sendMessage(PrefixType.PARTIE + "§aVous avez invité " + target.getDisplayName() + " à votre partie");
		}else {
			cs.sendMessage(PrefixType.PARTIE + "§c/" + label + " <Joueur>");
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender cs, LoupGarouCommand cmd, String[] args) {
		if(!(cs instanceof Player)) return Collections.emptyList();
		List<String> players = new ArrayList<String>();
		for(Player p : Bukkit.getOnlinePlayers())
			players.add(p.getDisplayName());
		return players;
	}
	
	@Override
	public String getPermission() {
		return null;
	}

}