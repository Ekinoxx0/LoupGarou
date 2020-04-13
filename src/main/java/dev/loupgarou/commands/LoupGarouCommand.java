package dev.loupgarou.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.subcommands.MainMenuCmd;
import dev.loupgarou.commands.subcommands.config.AutoRoleCmd;
import dev.loupgarou.commands.subcommands.config.HideRoleCmd;
import dev.loupgarou.commands.subcommands.config.HideVoteCmd;
import dev.loupgarou.commands.subcommands.config.HideVoteExtraCmd;
import dev.loupgarou.commands.subcommands.config.RoleCmd;
import dev.loupgarou.commands.subcommands.debug.CurrentRolesCmd;
import dev.loupgarou.commands.subcommands.debug.DebugCmd;
import dev.loupgarou.commands.subcommands.debug.DebugDiscordLinkCmd;
import dev.loupgarou.commands.subcommands.debug.DebugRPCmd;
import dev.loupgarou.commands.subcommands.debug.DisableResourcePackCmd;
import dev.loupgarou.commands.subcommands.debug.MaintenanceCmd;
import dev.loupgarou.commands.subcommands.debug.NextDayCmd;
import dev.loupgarou.commands.subcommands.debug.NextNightCmd;
import dev.loupgarou.commands.subcommands.debug.ResourcePackAllCmd;
import dev.loupgarou.commands.subcommands.debug.ResourcePackCmd;
import dev.loupgarou.commands.subcommands.discord.DiscordCmd;
import dev.loupgarou.commands.subcommands.discord.DiscordLinkCmd;
import dev.loupgarou.commands.subcommands.discord.DiscordMoveCmd;
import dev.loupgarou.commands.subcommands.discord.IsDiscordLinkedCmd;
import dev.loupgarou.commands.subcommands.game.BanCmd;
import dev.loupgarou.commands.subcommands.game.EndCmd;
import dev.loupgarou.commands.subcommands.game.InviteCmd;
import dev.loupgarou.commands.subcommands.game.JoinCmd;
import dev.loupgarou.commands.subcommands.game.KickCmd;
import dev.loupgarou.commands.subcommands.game.LeaderCmd;
import dev.loupgarou.commands.subcommands.game.LeaveCmd;
import dev.loupgarou.commands.subcommands.game.QuickCmd;
import dev.loupgarou.commands.subcommands.game.StartCmd;
import dev.loupgarou.commands.subcommands.spawns.AddMapCmd;
import dev.loupgarou.commands.subcommands.spawns.AddSpawnCmd;
import dev.loupgarou.commands.subcommands.spawns.ListMapCmd;
import dev.loupgarou.commands.subcommands.spawns.LoadMapCmd;
import dev.loupgarou.commands.subcommands.spawns.RemoveMapCmd;
import dev.loupgarou.commands.subcommands.spawns.RemoveSpawnCmd;
import dev.loupgarou.commands.subcommands.spawns.SaveMapCmd;
import dev.loupgarou.commands.subcommands.spawns.ShowSpawnsCmd;
import dev.loupgarou.commands.subcommands.spawns.TpMapCmd;
import lombok.Getter;
import lombok.NonNull;

public class LoupGarouCommand implements CommandExecutor, TabExecutor {

	private final List<SubCommand> subs = new ArrayList<>();
	@Getter private final MainLg main;

	public LoupGarouCommand(@NonNull MainLg main) {
		this.main = main;
		Bukkit.getPluginCommand("lg").setExecutor(this);
		Bukkit.getPluginCommand("lg").setTabCompleter(this);
		this.subs.add(new AutoRoleCmd(this));
		this.subs.add(new HideRoleCmd(this));
		this.subs.add(new HideVoteCmd(this));
		this.subs.add(new HideVoteExtraCmd(this));
		this.subs.add(new RoleCmd(this));

		this.subs.add(new ResourcePackCmd(this));
		this.subs.add(new ResourcePackAllCmd(this));
		this.subs.add(new DisableResourcePackCmd(this));
		this.subs.add(new MaintenanceCmd(this));
		this.subs.add(new CurrentRolesCmd(this));
		this.subs.add(new DebugCmd(this));
		this.subs.add(new DebugRPCmd(this));
		this.subs.add(new DebugDiscordLinkCmd(this));
		this.subs.add(new NextDayCmd(this));
		this.subs.add(new NextNightCmd(this));
		this.subs.add(new QuickCmd(this));

		this.subs.add(new TpMapCmd(this));
		this.subs.add(new AddMapCmd(this));
		this.subs.add(new ListMapCmd(this));
		this.subs.add(new AddSpawnCmd(this));
		this.subs.add(new LoadMapCmd(this));
		this.subs.add(new RemoveMapCmd(this));
		this.subs.add(new RemoveSpawnCmd(this));
		this.subs.add(new SaveMapCmd(this));
		this.subs.add(new ShowSpawnsCmd(this));

		this.subs.add(new DiscordCmd(this));
		this.subs.add(new DiscordLinkCmd(this));
		this.subs.add(new IsDiscordLinkedCmd(this));
		this.subs.add(new DiscordMoveCmd(this));
		this.subs.add(new MainMenuCmd(this));

		this.subs.add(new BanCmd(this));
		this.subs.add(new KickCmd(this));
		this.subs.add(new LeaderCmd(this));
		this.subs.add(new EndCmd(this));
		this.subs.add(new InviteCmd(this));
		this.subs.add(new JoinCmd(this));
		this.subs.add(new LeaveCmd(this));
		this.subs.add(new StartCmd(this));
		
		for(SubCommand cmd : this.subs)
			if(cmd.getAliases().isEmpty()) {
				this.subs.remove(cmd);
				MainLg.debug("SubCommand : " + cmd.getClass().getSimpleName() + " is invalid !");
			}
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();
		String lastArg = args[args.length - 1].toLowerCase();

		if (args.length == 1) {
			subloop: for (SubCommand sub : subs) {
				for (String s : sub.getAliases()) {
					if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
						if (sub.getPermission() == null || cs.hasPermission(sub.getPermission())) {
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
						list.addAll(sub.onTabComplete(cs, this, args));
					}
				}
			}
		}

		for(String s : new ArrayList<String>(list))
			if(!s.toLowerCase().startsWith(lastArg))
				list.remove(s);
		
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
					sb.append(sub.getAliases().get(0) + ", ");
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
