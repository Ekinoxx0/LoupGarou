package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class LGNightStart extends LGEvent implements Cancellable {

	@Getter @Setter boolean cancelled;

	public LGNightStart(@NonNull LGGame game) {
		super(game);
	}

}
