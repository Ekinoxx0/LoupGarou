package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGPlayerKilledEvent.Reason;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class LGNightPlayerPreKilledEvent extends LGEvent implements Cancellable {

	@Getter @Setter private boolean cancelled;
    
    @Getter private final LGPlayer killed;
    @Getter @Setter @NonNull private Reason reason;
    
	public LGNightPlayerPreKilledEvent(@NonNull LGGame game, @NonNull LGPlayer killed, @NonNull Reason reason) {
		super(game);
		this.killed = killed;
		this.reason = reason;
	}
	
}
