package dev.loupgarou.events;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGWinType;
import lombok.Getter;
import lombok.Setter;

public class LGEndCheckEvent extends LGEvent{
	public LGEndCheckEvent(LGGame game, LGWinType winType) {
		super(game);
		this.winType = winType;
	}

	@Getter @Setter private LGWinType winType;
}