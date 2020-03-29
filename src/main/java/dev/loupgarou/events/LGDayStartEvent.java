package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called at a start of a day.
 * People are already in chat, but are not in vote.
 * If mayor is dead, he is not replaced yet.
 * 
 * Cancelling this event can have side effects !
 */
public class LGDayStartEvent extends LGEvent implements Cancellable{
	public LGDayStartEvent(@NonNull LGGame game) {
		super(game);
	}
	
	@Getter @Setter private boolean cancelled;
}