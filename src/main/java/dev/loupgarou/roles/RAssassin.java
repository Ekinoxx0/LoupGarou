package dev.loupgarou.roles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGWinType;
import dev.loupgarou.classes.LGPlayer.LGChooseCallback;
import dev.loupgarou.events.LGEndCheckEvent;
import dev.loupgarou.events.LGGameEndEvent;
import dev.loupgarou.events.LGNightEndEvent;
import dev.loupgarou.events.LGNightPlayerPreKilledEvent;
import dev.loupgarou.events.LGPyromaneGasoilEvent;
import dev.loupgarou.events.LGRoleTurnEndEvent;
import dev.loupgarou.events.LGPlayerKilledEvent.Reason;

public class RAssassin extends Role{
	public RAssassin(LGGame game) {
		super(game);
	}
	@Override
	public RoleType getType() {
		return RoleType.NEUTRAL;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.SEUL;
	}
	@Override
	public String getName() {
		return "§1§lAssassin";
	}
	@Override
	public String getFriendlyName() {
		return "de l'"+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes §7§lSEUL";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes §7§lSEUL§f. Chaque nuit, tu peux choisir un joueur à éliminer. Tu es immunisé contre l'attaque des §c§lLoups§f.";
	}
	@Override
	public String getTask() {
		return "Choisis un joueur à éliminer.";
	}
	@Override
	public String getBroadcastedTask() {
		return "L'"+getName()+"§9 ne controle plus ses pulsions...";
	}
	@Override
	public int getTimeout() {
		return 15;
	}
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		
		player.choose(new LGChooseCallback() {
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null && choosen != player) {
					getGame().kill(choosen, Reason.ASSASSIN);
					player.sendActionBarMessage("§e§l"+choosen.getName()+"§6 va mourir");
					player.sendMessage("§6Tu as choisi de tuer §7§l"+choosen.getName()+"§6.");
					player.stopChoosing();
					player.hideView();
					callback.run();
				}
			}
		});
	}
	
	@EventHandler
	public void onKill(LGNightPlayerPreKilledEvent e) {
		if(e.getKilled().getRole() == this && e.getReason() == Reason.LOUP_GAROU || e.getReason() == Reason.GM_LOUP_GAROU) {//Les assassins ne peuvent pas mourir la nuit !
			e.setReason(Reason.DONT_DIE);
			e.getKilled().getCache().set("assassin_protected", true);
		}
	}
	
	@EventHandler
	public void onTour(LGRoleTurnEndEvent e) {
		if(e.getGame() == getGame()) {
			MainLg.debug("(Assassin-LGRoleTurnEndEvent)" + e.getPreviousRole().getName());
			if(e.getPreviousRole() instanceof RLoupGarou) {
				for(LGPlayer lgp : getGame().getAlive())
					if(lgp.getCache().getBoolean("assassin_protected")) {
						for(LGPlayer l : getGame().getInGame())
							if(l.getRoleType() == RoleType.LOUP_GAROU)
								l.sendMessage("§cVotre cible est immunisée.");
					}
			}else if(e.getPreviousRole() instanceof RGrandMechantLoup) {
				for(LGPlayer lgp : getGame().getAlive())
					if(lgp.getCache().getBoolean("assassin_protected")) {
						for(LGPlayer l : e.getPreviousRole().getPlayers())
							l.sendMessage("§cVotre cible est immunisée.");
					}
			}
		}
	}
	
	@EventHandler
	public void onPyroGasoil(LGPyromaneGasoilEvent e) {
		if(e.getPlayer().getRole() == this)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onDayStart(LGNightEndEvent e) {
		if(e.getGame() == getGame()) {
			for(LGPlayer lgp : getGame().getAlive())
				if(lgp.getCache().getBoolean("assassin_protected"))
					lgp.getCache().remove("assassin_protected");
		}
	}
	
	@EventHandler
	public void onEndgameCheck(LGEndCheckEvent e) {
		if(e.getGame() == getGame() && e.getWinType() == LGWinType.SOLO) {
			if(getPlayers().size() > 0)
				e.setWinType(LGWinType.ASSASSIN);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEndGame(LGGameEndEvent e) {
		if(e.getWinType() == LGWinType.ASSASSIN) {
			e.getWinners().clear();
			e.getWinners().addAll(getPlayers());
		}
	}
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez regardé aucun rôle", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§cVous n'avez pas utilisé votre pouvoir cette nuit.");
	}
}
