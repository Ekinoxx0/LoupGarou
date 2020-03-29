package dev.loupgarou.events;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.roles.utils.Role;
import lombok.Getter;

public class LGRoleTurnEndEvent extends LGEvent{
	public LGRoleTurnEndEvent(LGGame game, Role newRole, Role previousRole) {
		super(game);
		this.newRole = newRole;
		this.previousRole = previousRole;
	}
	
	@Getter private final Role newRole, previousRole;
}