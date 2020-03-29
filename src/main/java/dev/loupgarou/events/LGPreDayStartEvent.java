package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called before a day start, ambiance is already set.
 * 
 * After : {@link LGNightEndEvent}
 * Before : {@link LGDayStartEvent}
 * Or Before {@link LGEndCheckEvent}
 */
public class LGPreDayStartEvent extends LGEvent implements Cancellable {
	
	@Getter @Setter private boolean cancelled;
	
	public LGPreDayStartEvent(@NonNull LGGame game) {
		super(game);
	}
}