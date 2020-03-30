package dev.loupgarou.events.daycycle;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.events.LGEvent;
import lombok.NonNull;

/**
 * Called when a day ended, after a people vote.
 * This is just before each role has onNightTurn().
 * Ambiance is already set to night time and nobody has day chat activated
 */
public class LGNightStartEvent extends LGEvent {
	public LGNightStartEvent(@NonNull LGGame game) {
		super(game);
	}
}