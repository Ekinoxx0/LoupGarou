package dev.loupgarou.loupgarou.events;

import java.util.ArrayList;

import dev.loupgarou.loupgarou.classes.LGGame;
import dev.loupgarou.loupgarou.classes.LGPlayer;
import dev.loupgarou.loupgarou.classes.LGVote;
import lombok.Getter;

public class LGVoteLeaderChange extends LGEvent{

	public LGVoteLeaderChange(LGGame game, LGVote vote, ArrayList<LGPlayer> latest, ArrayList<LGPlayer> now) {
		super(game);
		this.latest = latest;
		this.now = now;
		this.vote = vote;
	}
	
	@Getter ArrayList<LGPlayer> latest, now;
	@Getter LGVote vote;

}
