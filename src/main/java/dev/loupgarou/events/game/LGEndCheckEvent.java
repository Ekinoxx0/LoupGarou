package dev.loupgarou.events.game;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGWinType;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called each time we check if the game is ending.
 * If winType is different from LGWinType.NONE, the game will end just after this event
 */
public class LGEndCheckEvent extends LGEvent {

	@Getter @Setter @NonNull private LGWinType winType;
	
	public LGEndCheckEvent(@NonNull LGGame game, @NonNull LGWinType winType) {
		super(game);
		this.winType = winType;
	}
}