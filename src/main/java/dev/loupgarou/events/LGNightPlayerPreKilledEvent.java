package dev.loupgarou.events;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGPlayerKilledEvent.Reason;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called when we need to verify if the player can be killed.
 * If Reason is set to DONT_DIE, then the player will not be killed
 */
public class LGNightPlayerPreKilledEvent extends LGEvent {

    @Getter private final LGPlayer killed;
    @Getter @Setter @NonNull private Reason reason;
    
	public LGNightPlayerPreKilledEvent(@NonNull LGGame game, @NonNull LGPlayer killed, @NonNull Reason reason) {
		super(game);
		this.killed = killed;
		this.reason = reason;
	}

}
