package dev.loupgarou.commands.subcommands.discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import net.dv8tion.jda.api.entities.Member;

public class CheckDiscordCmd extends SubCommand {

	public CheckDiscordCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("checkdiscord", "onlinediscord", "checksdiscord", "discordcheck"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		cs.sendMessage("§9Check discord :");
		List<Member> knowns = new ArrayList<Member>();
		for (Player target : Bukkit.getOnlinePlayers()) {
			Member m = getMain().getDiscord().getMemberFromName(target.getName());
			if (m == null) {
				cs.sendMessage("§7 *IG* §c" + target.getName());
			} else {
				cs.sendMessage("§7 *IG* §a" + target.getName());
				knowns.add(m);
			}
		}

		for (Member i : getMain().getDiscord().getSelectedChannel().getMembers()) {
			if (!knowns.contains(i)) {
				cs.sendMessage("§7 *DCD* §c" + i.getEffectiveName());
			}
		}
	}

}