package dev.loupgarou.events.game;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.events.LGEvent;
import lombok.NonNull;

/**
 * Called each time a game start
 */
public class LGGameStartEvent extends LGEvent {

	public LGGameStartEvent(@NonNull LGGame game) {
		super(game);
	}
}