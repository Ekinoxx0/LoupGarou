package dev.loupgarou.roles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.daycycle.LGNightEndEvent;
import dev.loupgarou.events.daycycle.LGNightPlayerPreKilledEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import dev.loupgarou.events.roles.LGRoleTurnEndEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache.CacheType;

public class RChaperonRouge extends Role{
	public RChaperonRouge(LGGame game) {
		super(game);
	}
	@Override
	public int getMaxNb() {
		return Integer.MAX_VALUE;
	}
	@Override
	public RoleType getType() {
		return RoleType.VILLAGER;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.VILLAGE;
	}
	@Override
	public String getColor() {
		return "§a";
	}
	@Override
	public String getName() {
		return getColor() + "§lChaperon Rouge";
	}
	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Tant que le §a§lChasseur§f est en vie, tu ne peux pas te faire tuer par les §c§lLoups§f pendant la nuit.";
	}
	@Override
	public String getTask() {
		return "";
	}
	@Override
	public String getBroadcastedTask() {
		return "";
	}
	@Override
	public int getTimeout() {
		return -1;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onKill(LGNightPlayerPreKilledEvent e) {
		if(e.getKilled().getRole() == this && e.getReason() == Reason.LOUP_GAROU || e.getReason() == Reason.GM_LOUP_GAROU && e.getKilled().isRoleActive()) {
			for(Role role : getGame().getRoles())
				if(role instanceof RChasseur)
					if(role.getPlayers().size() > 0){
						e.getKilled().getCache().set(CacheType.CHAPERON_KILL, true);
						e.setReason(Reason.DONT_DIE);
						break;
					}
		}
	}
	@EventHandler
	public void onTour(LGRoleTurnEndEvent e) {
		if(e.getGame() == getGame()) {
			MainLg.debug(getGame().getKey(), "(ChaperonRouge-LGRoleTurnEndEvent)" + e.getPreviousRole().getName());
			if(e.getPreviousRole() instanceof RLoupGarou) {
				for(LGPlayer lgp : getGame().getAlive())
					if(lgp.getCache().getBoolean(CacheType.CHAPERON_KILL) && lgp.isRoleActive()) {
						for(LGPlayer l : getGame().getInGame())
							if(l.getRoleType() == RoleType.LOUP_GAROU)
								l.sendMessage("§cVotre cible est immunisée.");
					}
			}else if(e.getPreviousRole() instanceof RGrandMechantLoup) {
				for(LGPlayer lgp : getGame().getAlive())
					if(lgp.getCache().getBoolean(CacheType.CHAPERON_KILL) && lgp.isRoleActive()) {
						for(LGPlayer l : e.getPreviousRole().getPlayers())
							l.sendMessage("§cVotre cible est immunisée.");
					}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDayStart(LGNightEndEvent e) {
		if(e.getGame() == getGame()) {
			for(LGPlayer lgp : getPlayers())
				if(lgp.getCache().getBoolean(CacheType.CHAPERON_KILL)) {
					lgp.getCache().remove(CacheType.CHAPERON_KILL);
					lgp.sendMessage("§9§oTu as été attaqué cette nuit.");
				}
		}
	}
}
