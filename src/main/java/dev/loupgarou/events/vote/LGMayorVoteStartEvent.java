package dev.loupgarou.events.vote;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called when a mayor is being elected by the people
 */
public class LGMayorVoteStartEvent extends LGEvent implements Cancellable {

	@Getter @Setter private boolean cancelled;
	
	public LGMayorVoteStartEvent(@NonNull LGGame game) {
		super(game);
	}
}
