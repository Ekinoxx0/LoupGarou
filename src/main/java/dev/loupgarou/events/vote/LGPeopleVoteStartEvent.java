package dev.loupgarou.events.vote;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.events.LGEvent;
import dev.loupgarou.events.daycycle.LGDayEndEvent;
import dev.loupgarou.events.daycycle.LGDayStartEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called when a people vote start
 * 
 * After : {@link LGDayStartEvent}
 * Before : {@link LGDayEndEvent}
 */
public class LGPeopleVoteStartEvent extends LGEvent implements Cancellable {
	@Getter @Setter private boolean cancelled;
	
	public LGPeopleVoteStartEvent(@NonNull LGGame game) {
		super(game);
	}
}
