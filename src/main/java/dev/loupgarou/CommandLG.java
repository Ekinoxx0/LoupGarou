package dev.loupgarou;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandLG implements CommandExecutor, TabCompleter {

	private final MainLg mainLg;
	
	public CommandLG(MainLg mainLg) {
		this.mainLg = mainLg;
	}

	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(!sender.hasPermission("loupgarou.admin"))
			return args.length == 0 ? Arrays.asList("menu") : new ArrayList<String>(0);
		
		if(args.length > 1) {
			if(args[0].equalsIgnoreCase("roles"))
				if(args.length == 2)
					return getStartingList(args[1], "list", "set", "all");
				else if(args.length == 3 && args[1].equalsIgnoreCase("set"))
					return getStartingList(args[2], mainLg.getRoles().keySet().toArray(new String[mainLg.getRoles().size()]));
				else if(args.length == 4)
					return Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
		}else if(args.length == 1)
			return getStartingList(args[0], "muteall", "hidevote", "hidevoteextra", "checkdiscord", "unmuteall", "hidecompo", "deaddiscord", "clearallspawn", "addspawn", "removespawn", 
					"quick", "veryquick", "end", "start", "nextnight", "nextday", "reloadconfig", "roles", "joinall", "reloadpacks", "showspawns", "debug", "debugresetpl", "spec", "status", "menu");
		return new ArrayList<String>(0);
	}
	
	private List<String> getStartingList(String startsWith, String... list){
		startsWith = startsWith.toLowerCase();
		ArrayList<String> returnlist = new ArrayList<String>();
		if(startsWith.length() == 0)
			return Arrays.asList(list);
		for(String s : list)
			if(s.toLowerCase().startsWith(startsWith))
				returnlist.add(s);
		return returnlist;
	}


	@Override
	public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
		return false;
	}
	
	
	
}
