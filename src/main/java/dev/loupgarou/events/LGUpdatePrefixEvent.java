package dev.loupgarou.events;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
