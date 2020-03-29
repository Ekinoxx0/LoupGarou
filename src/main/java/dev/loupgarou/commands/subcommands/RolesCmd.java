package dev.loupgarou.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;

public class RolesCmd extends SubCommand {

	public RolesCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("roles"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(args.length == 1 || args[1].equalsIgnoreCase("list")) {
			cs.sendMessage(MainLg.getPrefix()+"§6Voici la liste des rôles:");
			int index = 0;
			int total = 0;
			for(String role : getMain().getRoles().keySet()) {
				if(MainLg.getInstance().getConfig().getInt("role."+role) > 0) {
					total += MainLg.getInstance().getConfig().getInt("role."+role);
					cs.sendMessage(MainLg.getPrefix()+"  §e- "+ index++ +" - §6"+role+" §e> "+MainLg.getInstance().getConfig().getInt("role."+role));
				}
			}
			cs.sendMessage("\n"+MainLg.getPrefix()+" §7Écrivez §8§o/lg role set <role_id/role_name> <nombre>§7 pour définir le nombre de joueurs qui devrons avoir ce rôle.");
			cs.sendMessage("\n"+MainLg.getPrefix()+" §9Total : " + total);
		}else if(args[1].equalsIgnoreCase("all")) {
			cs.sendMessage(MainLg.getPrefix()+"§6Voici la liste des rôles:");
			int index = 0;
			for(String role : getMain().getRoles().keySet()) {
				cs.sendMessage(MainLg.getPrefix()+"  §e- "+index+++" - §6"+role+" §e> "+MainLg.getInstance().getConfig().getInt("role."+role));
			}
			cs.sendMessage("\n"+MainLg.getPrefix()+" §7Écrivez §8§o/lg role set <role_id/role_name> <nombre>§7 pour définir le nombre de joueurs qui devrons avoir ce rôle.");
		} else {
			if(args[1].equalsIgnoreCase("set") && args.length == 4) {
				String role = null;
				if(args[2].length() <= 2)
					try {
						Integer i = Integer.valueOf(args[2]);
						Object[] array = getMain().getRoles().keySet().toArray();
						if(array.length <= i) {
							cs.sendMessage(MainLg.getPrefix()+"§4Erreur: §cCe rôle n'existe pas.");
							return;
						}else
							role = (String)array[i];
					}catch(Exception err) {getMain();
					cs.sendMessage(MainLg.getPrefix()+"§4Erreur: §cCeci n'est pas un nombre");}
				else
					role = args[2];
				
				if(role != null) {
					String real_role = null;
					for(String real : getMain().getRoles().keySet())
						if(real.equalsIgnoreCase(role)) {
							real_role = real;
							break;
						}
					
					if(real_role != null) {
						try {
							MainLg.getInstance().getConfig().set("role."+real_role, Integer.valueOf(args[3]));
							cs.sendMessage(MainLg.getPrefix()+"§6Il y aura §e"+args[3]+" §6"+real_role);
							getMain().saveConfig();
							getMain().loadConfig();
							cs.sendMessage("§7§oSi vous avez fini de changer les rôles, écriver §8§o/lg joinall§7§o !");
						}catch(Exception err) {
							cs.sendMessage(MainLg.getPrefix()+"§4Erreur: §c"+args[3]+" n'est pas un nombre");
						}
						return;
					}
				}
				cs.sendMessage(MainLg.getPrefix()+"§4Erreur: §cLe rôle que vous avez entré est incorrect");
				
			} else {
				cs.sendMessage(MainLg.getPrefix()+"§4Erreur: §cCommande incorrecte.");
				cs.sendMessage(MainLg.getPrefix()+"§4Essayez §c/lg roles set <role_id/role_name> <nombre>§4 ou §c/lg roles list");
			}
		}
	}
	
}