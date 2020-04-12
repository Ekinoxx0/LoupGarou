package dev.loupgarou.commands.subcommands.debug;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

public class ResourcePackCmd extends SubCommand {

	private static final ViaAPI<?> api = Via.getAPI();
	
	private static final String url = "https://github.com/Ekinoxx0/LoupGarouRessourcePack/raw/";
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
	
	public static void reset(@NotNull Player p) {
		p.setResourcePack(url + commitIdLGRessource + "/empty.zip", "");
	}

	public static void load(@NotNull Player p) {
		ProtocolVersion v = ProtocolVersion.getProtocol(api.getPlayerVersion(p.getUniqueId()));
		if(v.getId() < ProtocolVersion.v1_13.getId()) {
			p.setResourcePack(url + commitIdLGRessource + "/generated-pre13.zip", "");
		} else {
			p.setResourcePack(url + commitIdLGRessource + "/generated.zip", "");
		}
	}
	
	
}