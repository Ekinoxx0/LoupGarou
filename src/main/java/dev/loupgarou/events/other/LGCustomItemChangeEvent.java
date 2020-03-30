package dev.loupgarou.events.other;

import java.util.List;

import dev.loupgarou.classes.LGCustomItems.LGCustomItemsConstraints;
import dev.loupgarou.events.LGEvent;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.NonNull;

/**
 * Called each time we need special items for a specific player
 * @param game The current game of the player
 * @param player The player who need a special item
 * @param constraints The list of contraints like
 * @see LGCustomItemsConstraints
 */
public class LGCustomItemChangeEvent extends LGEvent {
	@Getter private final LGPlayer player;
	@Getter private final List<LGCustomItemsConstraints> constraints;
	
	public LGCustomItemChangeEvent(@NonNull LGGame game, @NonNull LGPlayer player, @NonNull List<LGCustomItemsConstraints> constraints) {
		super(game);
		this.player = player;
		this.constraints = constraints;
	}
}
