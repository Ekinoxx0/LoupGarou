package dev.loupgarou.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.Setter;

public class LGMayorVoteEvent extends LGEvent implements Cancellable{
	public LGMayorVoteEvent(LGGame game) {
		super(game);
	}

	@Getter @Setter private boolean cancelled;
}
