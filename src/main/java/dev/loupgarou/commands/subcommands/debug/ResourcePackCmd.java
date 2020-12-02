package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.utils.CommonText.PrefixType;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

public class ResourcePackCmd extends SubCommand {

	private static final ViaAPI<?> api = Via.getAPI();
	
	private static final String githubRawSuffix = "?raw=true";
	private static final String url = "https://github.com/Ekinoxx0/LoupGarouRessourcePack/blob/";
	private static final String commitIdLGRessource = "ce6bd2814b04b84665a23dcc2829b5e24eab86b8";

	public ResourcePackCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("resourcepack", "ressourcepack", "ressourcespack", "resourcespack", "resourcepacks", "ressourcepacks", "ressourcespacks", "resourcespacks", "reloadrp"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		Player p = (Player) cs;
		load(p);;
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
	public static void reset(Player p) {
		if(p == null || !p.isOnline()) return;
		LGPlayer lgp = LGPlayer.thePlayer(p);
		if(lgp.getLoadedRessourcePack() == null) return;
		
		p.sendMessage(PrefixType.RESOURCEPACK + "§7Remise à zéro du pack de ressources...");
		p.setResourcePack(url + commitIdLGRessource + "/empty.zip" + githubRawSuffix, "");
		lgp.setLoadedRessourcePack(null);
	}

	public static void load(Player p) {
		if(p == null || !p.isOnline()) return;
		LGPlayer lgp = LGPlayer.thePlayer(p);
		if(lgp.getLoadedRessourcePack() != null) return;
		
		ProtocolVersion v = ProtocolVersion.getProtocol(api.getPlayerVersion(p.getUniqueId()));
		p.sendMessage(PrefixType.RESOURCEPACK + "§7Chargement du pack de ressources " + v.getName());
		if(v.getId() < ProtocolVersion.v1_13.getId()) {
			p.setResourcePack(url + commitIdLGRessource + "/generated-pre13.zip" + githubRawSuffix, "");
			lgp.setLoadedRessourcePack(url + commitIdLGRessource + "/generated.zip" + githubRawSuffix);
		} else {
			p.setResourcePack(url + commitIdLGRessource + "/generated.zip" + githubRawSuffix, "");
			lgp.setLoadedRessourcePack(url + commitIdLGRessource + "/generated.zip" + githubRawSuffix);
		}
	}
	
	
}
