package dev.loupgarou.events;

import java.util.List;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;

public class LGCustomItemChangeEvent extends LGEvent {
	@Getter private final LGPlayer player;
	@Getter private final List<String> constraints;
	
	public LGCustomItemChangeEvent(LGGame game, LGPlayer player, List<String> constraints) {
		super(game);
		this.player = player;
		this.constraints = constraints;
	}
}
