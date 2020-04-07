package dev.loupgarou.events.roles;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.Setter;

public class LGVampiredEvent extends LGEvent{
	public LGVampiredEvent(LGGame game, LGPlayer player) {
		super(game);
		this.player = player;
	}

	@Getter @Setter private boolean immuned, protect;
	@Getter @Setter private LGPlayer player;
} 