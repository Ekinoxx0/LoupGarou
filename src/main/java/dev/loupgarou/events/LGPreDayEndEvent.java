package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called before the night start
 * After this event will we do a transition before the night start of a duration of 'duration'
 */
public class LGPreDayEndEvent extends LGEvent implements Cancellable {

	@Getter @Setter boolean cancelled;
	@Getter @Setter int duration;

	public LGPreDayEndEvent(@NonNull LGGame game, int duration) {
		super(game);
		this.duration = duration;
	}

}
