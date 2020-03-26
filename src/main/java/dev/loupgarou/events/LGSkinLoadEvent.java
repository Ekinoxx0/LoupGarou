package dev.loupgarou.events;

import com.comphenix.protocol.wrappers.WrappedGameProfile;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.Setter;

public class LGSkinLoadEvent extends LGEvent {

	@Getter private final LGPlayer player, to;
	@Getter @Setter private WrappedGameProfile profile;
	public LGSkinLoadEvent(LGGame game, LGPlayer player, LGPlayer to, WrappedGameProfile profile) {
		super(game);
		this.player = player;
		this.to = to;
		this.profile = profile;
	}

}
