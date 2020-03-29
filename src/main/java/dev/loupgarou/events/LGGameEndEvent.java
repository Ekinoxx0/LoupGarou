package dev.loupgarou.events;

import java.util.List;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGWinType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called when the game is ending, after LGEndCheckEvent.
 * If winType is a custom role, you must clear then add your custom winners in your Role class
 *
 * Cancelling this event can have side effects !
 * @see LGEndCheckEvent
 */
public class LGGameEndEvent extends LGEvent implements Cancellable {
	@Getter @Setter private boolean cancelled;
	@Getter private final LGWinType winType;
	@Getter private final List<LGPlayer> winners;
	
	public LGGameEndEvent(@NonNull LGGame game, @NonNull LGWinType winType, @NonNull List<LGPlayer> winners) {
		super(game);
		this.winType = winType;
		this.winners = winners;
	}
}