package dev.loupgarou.events;

import java.util.List;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGWinType;
import lombok.Getter;
import lombok.Setter;

public class LGGameEndEvent extends LGEvent implements Cancellable{
	@Getter @Setter private boolean cancelled;
	@Getter private final LGWinType winType;
	@Getter private final List<LGPlayer> winners;
	public LGGameEndEvent(LGGame game, LGWinType winType, List<LGPlayer> winners) {
		super(game);
		this.winType = winType;
		this.winners = winners;
	}
}