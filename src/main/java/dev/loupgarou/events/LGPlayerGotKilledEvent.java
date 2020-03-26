package dev.loupgarou.events;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGPlayerKilledEvent.Reason;
import lombok.Getter;

public class LGPlayerGotKilledEvent extends LGEvent {
	public LGPlayerGotKilledEvent(LGGame game, LGPlayer killed, Reason reason, boolean endGame) {
		super(game);
		this.killed = killed;
		this.reason = reason;
		this.endGame = endGame;
	}
	
	@Getter private final boolean endGame;
    @Getter private final LGPlayer killed;
    @Getter private Reason reason;
	
}
