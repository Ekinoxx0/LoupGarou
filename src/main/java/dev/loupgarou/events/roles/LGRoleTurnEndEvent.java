package dev.loupgarou.events.roles;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.events.LGEvent;
import dev.loupgarou.events.daycycle.LGNightEndEvent;
import dev.loupgarou.events.daycycle.LGNightStartEvent;
import dev.loupgarou.roles.utils.Role;
import lombok.Getter;
import lombok.NonNull;

/**
 * Called when the turn of a role is ended
 * The newRole can be null if there are no role after this one
 * 
 * After : {@link LGNightStartEvent}
 * Before : {@link LGNightEndEvent}
 */
public class LGRoleTurnEndEvent extends LGEvent {
	@Getter private final Role newRole;
	@Getter @NonNull private final Role previousRole;
	
	public LGRoleTurnEndEvent(@NonNull LGGame game, Role newRole, @NonNull Role previousRole) {
		super(game);
		this.newRole = newRole;
		this.previousRole = previousRole;
	}
	
}