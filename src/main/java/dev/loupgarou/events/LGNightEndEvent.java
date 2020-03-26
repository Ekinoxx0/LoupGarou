package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.Setter;

public class LGNightEndEvent extends LGEvent implements Cancellable{
	public LGNightEndEvent(LGGame game) {
		super(game);
	}
	
	@Getter @Setter private boolean cancelled;
}