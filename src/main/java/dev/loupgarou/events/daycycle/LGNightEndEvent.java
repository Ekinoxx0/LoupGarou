package dev.loupgarou.events.daycycle;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called when the night end.
 * Ambiance of day is already set, but nobody is dead from the night yet.
 * 
 * Used to remove special night skin
 */
public class LGNightEndEvent extends LGEvent implements Cancellable {
	
	@Getter @Setter private boolean cancelled;
	
	public LGNightEndEvent(@NonNull LGGame game) {
		super(game);
	}
}