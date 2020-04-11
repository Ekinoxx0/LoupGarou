package dev.loupgarou.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import dev.loupgarou.MainLg;
import lombok.Getter;
import lombok.NonNull;

public abstract class SubCommand implements TabExecutor {
	
	public static final String BASE_PERM = "loupgarou.cmd";

	@Getter @NonNull private final LoupGarouCommand cmd;
	@Getter @NonNull private final List<String> aliases;
	
	public SubCommand(LoupGarouCommand cmd, List<String> aliases){
		this.cmd = cmd;
		this.aliases = aliases;


		for(String alias : aliases) {
			if(alias == null) continue;
			
			if(Bukkit.getPluginCommand(alias) != null) {
				Bukkit.getPluginCommand(alias).setExecutor(this);
				Bukkit.getPluginCommand(alias).setTabCompleter(this);
				Bukkit.getPluginCommand(alias).setPermission(getPermission());
			}
		}
	}
	
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    	String[] newArgs = new String[args.length + 1];
    	for (int i = 0; i < args.length; i++)
			newArgs[i+1] = args[i];
    	newArgs[0] = label;
    	this.execute(cs, label, newArgs);
    	return true;
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender cs, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    	String[] newArgs = new String[args.length + 1];
    	for (int i = 0; i < args.length; i++)
			newArgs[i+1] = args[i];
    	newArgs[0] = label;
    	return this.onTabComplete(cs, cmd, newArgs);
    }
	
	public abstract void execute(CommandSender cs, String label, String[] args);

	public List<String> onTabComplete(CommandSender cs, LoupGarouCommand cmd, String[] args) {
		return Collections.emptyList();
	}
	
	public String getPermission() {
		return BASE_PERM + "." + aliases.get(0).toLowerCase();
	}
	
	protected MainLg getMain() {
		return this.cmd.getMain();
	}
	
}
