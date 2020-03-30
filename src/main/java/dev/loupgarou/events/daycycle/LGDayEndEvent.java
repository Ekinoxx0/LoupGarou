package dev.loupgarou.events.daycycle;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called before the night start
 * After this event will we do a transition before the night start of a duration of 'duration'
 */
public class LGDayEndEvent extends LGEvent implements Cancellable {

	@Getter @Setter boolean cancelled;
	@Getter @Setter int duration;

	public LGDayEndEvent(@NonNull LGGame game, int duration) {
		super(game);
		this.duration = duration;
	}

}
