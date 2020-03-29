package dev.loupgarou.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import dev.loupgarou.MainLg;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SubCommand {

	@Getter @NonNull private final LoupGarouCommand cmd;
	@Getter @NonNull private final List<String> aliases;
	
	public abstract void execute(CommandSender cs, String label, String[] args);

	public List<String> onTabComplete(CommandSender cs, LoupGarouCommand cmd, String[] args) {
		return Collections.emptyList();
	}
	
	public String getPermission() {
		return "loupgarou.cmd." + aliases.get(0).toLowerCase();
	}
	
	protected MainLg getMain() {
		return this.cmd.getMain();
	}
	
}
