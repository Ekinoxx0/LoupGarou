package dev.loupgarou.roles;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.daycycle.LGDayStartEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;

public class RMontreurDOurs extends Role{
	public RMontreurDOurs(LGGame game) {
		super(game);
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
	public int getMaxNb() {
		return Integer.MAX_VALUE;
	}
	@Override
	public String getColor() {
		return "§a";
	}
	@Override
	public String getName() {
		return getColor() + "§lMontreur d'Ours";
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
		return "Tu gagnes avec le §a§lVillage§f. Chaque matin, ton Ours va renifler tes voisins et grognera si l'un d'eux est hostile aux Villageois.";
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

	private int lastNight = -1;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDay(LGDayStartEvent e) {
		if (e.getGame() == getGame() && getPlayers().size() > 0) {
			if(lastNight == getGame().getNight())
				return;
			lastNight = getGame().getNight();
			List<?> original = MainLg.getInstance().getConfig().getList("spawns");
			for(LGPlayer target : getPlayers()) {
				if(!target.isRoleActive())
					continue;
				int size = original.size();
				int killedPlace = target.getPlace();

				for (int i = killedPlace + 1;; i++) {
					if (i == size)
						i = 0;
					LGPlayer lgp = getGame().getPlacements().get(i);
					if (lgp != null && !lgp.isDead()) {
						if(lgp.getRoleWinType() == RoleWinType.VILLAGE || lgp.getRoleWinType() == RoleWinType.NONE)
							break;
						else{
							getGame().broadcastMessage("§6La bête du "+getName()+"§6 grogne...");
							return;
						}
					}
					if (lgp == target)// Fait un tour complet
						break;
				}
				for (int i = killedPlace - 1;; i--) {
					if (i == -1)
						i = size - 1;
					LGPlayer lgp = getGame().getPlacements().get(i);
					if (lgp != null && !lgp.isDead()) {
						if(lgp.getRoleWinType() == RoleWinType.VILLAGE || lgp.getRoleWinType() == RoleWinType.NONE)
							break;
						else{
							getGame().broadcastMessage("§6La bête du "+getName()+"§6 grogne...");
							return;
						}
					}
					if (lgp == target)// Fait un tour complet
						break;
				}
			}
		}
	}
}