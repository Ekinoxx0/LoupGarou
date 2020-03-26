package dev.loupgarou.loupgarou.events;

import dev.loupgarou.loupgarou.classes.LGGame;
import dev.loupgarou.loupgarou.classes.LGWinType;
import lombok.Getter;
import lombok.Setter;

public class LGEndCheckEvent extends LGEvent{
	public LGEndCheckEvent(LGGame game, LGWinType winType) {
		super(game);
		this.winType = winType;
	}

	@Getter @Setter private LGWinType winType;
}