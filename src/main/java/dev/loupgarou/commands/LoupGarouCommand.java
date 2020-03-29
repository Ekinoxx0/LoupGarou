package dev.loupgarou.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.subcommands.JoinAllCmd;
import lombok.Getter;
import lombok.NonNull;

public class LoupGarouCommand implements CommandExecutor, TabExecutor {

	private final List<SubCommand> subs = new ArrayList<>();
	@Getter private final MainLg main;

	public LoupGarouCommand(@NonNull MainLg main) {
		this.main = main;
		this.subs.add(new JoinAllCmd(this));
		
		for(SubCommand cmd : this.subs)
			if(cmd.getAliases().isEmpty()) {
				this.subs.remove(cmd);
				MainLg.debug("SubCommand : " + cmd.getClass().getSimpleName() + " is invalid !");
			}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> list = new ArrayList<>();

		if (!(sender instanceof Player)) {
			return list;
		}

		final CommandSender p = sender;

		if (args.length == 1) {
			subloop: for (SubCommand sub : subs) {
				for (String s : sub.getAliases()) {
					if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
						if (sub.getPermission() == null || p.hasPermission(sub.getPermission())) {
							list.add(sub.getAliases().get(0));
							continue subloop;
						}
					}
				}
			}
		} else if (args.length >= 2) {
			for (SubCommand sub : subs) {
				for (String s : sub.getAliases()) {
					if (s.equalsIgnoreCase(args[0])) {
						list.addAll(sub.onTabComplete(p, this, args));
					}
				}
			}

			if (list.isEmpty()) {
				String lastarg = args[args.length - 1].toLowerCase();

				for (Player pa : Bukkit.getOnlinePlayers()) {
					if (pa.getName().toLowerCase().startsWith(lastarg)) {
						list.add(pa.getName());
					}
				}
			}
		}

		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final CommandSender p = sender;

		if (args.length >= 1) {
			for (SubCommand sub : subs) {
				for (String s : sub.getAliases()) {
					if (s.equalsIgnoreCase(args[0])) {
						if (sub.getPermission() != null && !p.hasPermission(sub.getPermission())) {
							p.sendMessage("§cVous n'avez pas la permission ...");
							return true;
						}
						sub.execute(p, label, args);
						return true;
					}
				}
			}

			p.sendMessage("§cUsage : /" + label + " " + getArgs(p));
		} else {
			p.sendMessage("§cUsage : /" + label + " " + getArgs(p));
		}

		return false;
	}

	public String getArgs(CommandSender cs) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<");

		for (SubCommand sub : subs) {
			if (sub.getPermission() == null || cs.hasPermission(sub.getPermission()) || cs.hasPermission("*") || cs.hasPermission("loupgarou.*")) {
				String s = sb.toString();
				if (!s.contains(sub.getAliases().get(0))) {
					sb.append(sub.getAliases().get(0) + ",");
				}
			}
		}

		if (sb.length() > 2) {
			sb.setLength(sb.length() - 2);
		}
		sb.append(">");
		return sb.toString();
	}

}
