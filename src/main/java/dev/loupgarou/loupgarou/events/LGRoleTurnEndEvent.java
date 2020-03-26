package dev.loupgarou.loupgarou.events;

import dev.loupgarou.loupgarou.classes.LGGame;
import dev.loupgarou.loupgarou.roles.Role;
import lombok.Getter;

public class LGRoleTurnEndEvent extends LGEvent{
	public LGRoleTurnEndEvent(LGGame game, Role newRole, Role previousRole) {
		super(game);
		this.newRole = newRole;
		this.previousRole = previousRole;
	}
	
	@Getter private final Role newRole, previousRole;
}