package dev.loupgarou.loupgarou.events;

import dev.loupgarou.loupgarou.classes.LGGame;
import dev.loupgarou.loupgarou.classes.LGPlayer;
import lombok.Getter;

public class LGGameJoinEvent extends LGEvent{
	public LGGameJoinEvent(LGGame game, LGPlayer player) {
		super(game);
		this.player = player;
	}

	@Getter LGPlayer player;
}
