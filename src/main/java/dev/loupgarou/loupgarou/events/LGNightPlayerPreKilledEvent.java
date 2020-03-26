package dev.loupgarou.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.loupgarou.classes.LGGame;
import dev.loupgarou.loupgarou.classes.LGPlayer;
import dev.loupgarou.loupgarou.events.LGPlayerKilledEvent.Reason;
import lombok.Getter;
import lombok.Setter;

public class LGNightPlayerPreKilledEvent extends LGEvent implements Cancellable{
	public LGNightPlayerPreKilledEvent(LGGame game, LGPlayer killed, Reason reason) {
		super(game);
		this.killed = killed;
		this.reason = reason;
	}

	@Getter @Setter boolean cancelled;
    
    @Getter private final LGPlayer killed;
    @Getter @Setter private Reason reason;
	
}
