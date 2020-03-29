package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called when a mayor is elected by the people
 */
public class LGMayorVoteEvent extends LGEvent implements Cancellable {

	@Getter @Setter private boolean cancelled;
	
	public LGMayorVoteEvent(@NonNull LGGame game) {
		super(game);
	}
}
