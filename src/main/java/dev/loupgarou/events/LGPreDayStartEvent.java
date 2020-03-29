package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class LGPreDayStartEvent extends LGEvent implements Cancellable {
	
	@Getter @Setter private boolean cancelled;
	
	public LGPreDayStartEvent(@NonNull LGGame game) {
		super(game);
	}
}