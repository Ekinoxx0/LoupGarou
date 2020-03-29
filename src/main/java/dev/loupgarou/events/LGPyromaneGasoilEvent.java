package dev.loupgarou.events;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class LGPyromaneGasoilEvent extends LGEvent implements Cancellable {
	@Getter @Setter private boolean cancelled;
	@Getter @Setter @NonNull private LGPlayer player;
	
	public LGPyromaneGasoilEvent(@NonNull LGGame game, @NonNull LGPlayer player) {
		super(game);
		this.player = player;
	}
}