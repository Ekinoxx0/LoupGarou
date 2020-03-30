package dev.loupgarou.roles;

import org.bukkit.event.EventHandler;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGPlayer.LGChooseCallback;
import dev.loupgarou.events.game.LGPlayerKilledEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;

public class RGrandMechantLoup extends Role{

	public RGrandMechantLoup(LGGame game) {
		super(game);
	}

	@Override
	public String getColor() {
		return "§c";
	}

	@Override
	public String getName() {
		return getColor() + "§lGrand Méchant Loup";
	}

	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes avec les §c§lLoups-Garous";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes avec les §c§lLoups-Garous§f. Chaque nuit, tu te réunis avec tes compères pour décider d'une victime à éliminer... Tant qu'aucun autre §c§lLoup§f n'est mort, tu peux, chaque nuit, dévorer une victime supplémentaire.";
	}

	@Override
	public String getTask() {
		return "Choisis un joueur à dévorer.";
	}

	@Override
	public String getBroadcastedTask() {
		return "Le §c§lGrand Méchant Loup§9 n'en a pas terminé...";
	}
	@Override
	public RoleType getType() {
		return RoleType.LOUP_GAROU;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.LOUP_GAROU;
	}

	@Override
	public int getTimeout() {
		return 15;
	}
	
	@Override
	public boolean hasPlayersLeft() {
		return super.hasPlayersLeft() && !lgDied;
	}
	boolean lgDied;
	Runnable callback;
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		this.callback = callback;
		
		player.showView();
		player.choose(new LGChooseCallback() {
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null && choosen != player) {
					player.sendActionBarMessage("§e§l"+choosen.getName()+"§6 va mourir cette nuit");
					player.sendMessage("§6Tu as choisi de manger §7§l"+choosen.getName()+"§6.");
					getGame().kill(choosen, getGame().getDeaths().containsKey(Reason.LOUP_GAROU) ? Reason.GM_LOUP_GAROU : Reason.LOUP_GAROU);
					player.stopChoosing();
					player.hideView();
					callback.run();
				}
			}
		});
	}
	
	@EventHandler
	public void onPlayerDie(LGPlayerKilledEvent e) {//Quand un Loup-Garou meurt, les grands méchants loups ne peuvent plus jouer.
		if(e.getGame() == getGame())
			if(e.getKilled().getRoleType() == RoleType.LOUP_GAROU)
				lgDied = true;
	}
	
	
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
		player.hideView();
		player.sendMessage("§6Tu n'as tué personne.");
	}
	
	@Override
	public void join(LGPlayer player, boolean sendMessage) {
		super.join(player, sendMessage);
		for(Role role : getGame().getRoles())
			if(role instanceof RLoupGarou)
				role.join(player, false);
	}
	
}
