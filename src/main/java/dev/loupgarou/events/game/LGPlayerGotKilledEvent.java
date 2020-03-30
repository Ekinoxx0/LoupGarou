package dev.loupgarou.events.game;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import lombok.Getter;
import lombok.NonNull;

/**
 * Called when is player is killed
 * Use this event if the death of a role has repercution on the game
 */
public class LGPlayerGotKilledEvent extends LGEvent {
	
	@Getter private final boolean endGame;
    @Getter @NonNull private final LGPlayer killed;
    @Getter @NonNull private Reason reason;
    
	public LGPlayerGotKilledEvent(@NonNull LGGame game, @NonNull LGPlayer killed, @NonNull Reason reason, boolean endGame) {
		super(game);
		this.killed = killed;
		this.reason = reason;
		this.endGame = endGame;
	}
	
}
