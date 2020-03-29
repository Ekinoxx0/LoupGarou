package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class SpecCmd extends SubCommand {

	public SpecCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("spec", "spectator"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(args.length == 2) {
			Player spec = Bukkit.getPlayer(args[1]);
			spec.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 255, true));
			LGPlayer lgp = LGPlayer.thePlayer(spec);
			lgp.setDead(true);
			lgp.joinChat(getMain().getCurrentGame().getDayChat());
			lgp.setMuted();
			
			lgp.setGame(getMain().getCurrentGame());
			getMain().getCurrentGame().getInGame().add(lgp);
		} else {
			cs.sendMessage(MainLg.getPrefix() + "§4Essayez /lg §cspec <JOUEUR>");
		}
	}
	
}