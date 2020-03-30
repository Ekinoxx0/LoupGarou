package dev.loupgarou.events.vote;

import java.util.List;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGVote;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;

/**
 * Called when the most voted person from this people vote change
 */
public class LGVoteLeaderChange extends LGEvent {
	
	@Getter @NonNull List<LGPlayer> latest, now;
	@Getter @NonNull LGVote vote;

	public LGVoteLeaderChange(@NonNull LGGame game, @NonNull LGVote vote, @NonNull List<LGPlayer> latest, @NonNull List<LGPlayer> now) {
		super(game);
		this.latest = latest;
		this.now = now;
		this.vote = vote;
	}

}
