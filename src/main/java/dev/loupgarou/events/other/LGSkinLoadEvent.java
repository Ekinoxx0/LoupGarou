package dev.loupgarou.events.other;

import com.comphenix.protocol.wrappers.WrappedGameProfile;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called to define a special skin to a certain player from a certain player point of view
 */
public class LGSkinLoadEvent extends LGEvent {

	@Getter private final LGPlayer player, to;
	@Getter @Setter @NonNull private WrappedGameProfile profile;
	
	public LGSkinLoadEvent(LGGame game, @NonNull LGPlayer player, @NonNull LGPlayer to, @NonNull WrappedGameProfile profile) {
		super(game);
		this.player = player;
		this.to = to;
		this.profile = profile;
	}

}
