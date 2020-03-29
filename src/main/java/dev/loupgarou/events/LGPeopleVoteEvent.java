package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called when a vote start
 * 
 * After : {@link LGDayStartEvent}
 * Before : {@link LGDayEndEvent}
 */
public class LGPeopleVoteEvent extends LGEvent implements Cancellable {
	@Getter @Setter private boolean cancelled;
	
	public LGPeopleVoteEvent(@NonNull LGGame game) {
		super(game);
	}
}
