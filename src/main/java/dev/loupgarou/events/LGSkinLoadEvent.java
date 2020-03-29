package dev.loupgarou.events;

import com.comphenix.protocol.wrappers.WrappedGameProfile;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class LGSkinLoadEvent extends LGEvent {

	@Getter private final LGPlayer player, to;
	@Getter @Setter @NonNull private WrappedGameProfile profile;
	
	public LGSkinLoadEvent(@NonNull LGGame game, @NonNull LGPlayer player, @NonNull LGPlayer to, @NonNull WrappedGameProfile profile) {
		super(game);
		this.player = player;
		this.to = to;
		this.profile = profile;
	}

}
