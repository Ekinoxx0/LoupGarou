package dev.loupgarou.events.other;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called to defined a prefix to a player from a player view
 */
public class LGUpdatePrefixEvent extends LGEvent {
	@Getter @Setter @NonNull private String prefix;
	@Getter @NonNull private final LGPlayer player, to;
	
	public LGUpdatePrefixEvent(@NonNull LGGame game, @NonNull LGPlayer player, @NonNull LGPlayer to, @NonNull String prefix) {
		super(game);
		this.player = player;
		this.prefix = prefix;
		this.to = to;
	}
}
