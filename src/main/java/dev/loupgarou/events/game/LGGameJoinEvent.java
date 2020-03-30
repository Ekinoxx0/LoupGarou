package dev.loupgarou.events.game;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;

/**
 * Called when a LGPlayer join a game
 */
public class LGGameJoinEvent extends LGEvent {
	@Getter private LGPlayer player;
	
	public LGGameJoinEvent(@NonNull LGGame game, @NonNull LGPlayer player) {
		super(game);
		this.player = player;
	}
}
