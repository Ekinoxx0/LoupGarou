package dev.loupgarou.events;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.NonNull;

/**
 * Called when a LGPlayer join a game
 * Clear all scoreboard and custom packets in this event
 */
public class LGGameJoinEvent extends LGEvent{
	@Getter LGPlayer player;
	
	public LGGameJoinEvent(@NonNull LGGame game, @NonNull LGPlayer player) {
		super(game);
		this.player = player;
	}
}
