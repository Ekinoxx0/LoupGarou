package dev.loupgarou.events.roles;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGEvent;
import dev.loupgarou.roles.utils.Role;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class LGDiscoverRoleEvent extends LGEvent implements Cancellable {

	@Getter @Setter boolean cancelled;
    
    @Getter @NonNull private LGPlayer source;
    @Getter @NonNull private LGPlayer target;
    @Getter @Setter @NonNull private Role discoveredRole;
    
	public LGDiscoverRoleEvent(@NonNull LGGame game, @NonNull LGPlayer source, @NonNull LGPlayer target) {
		super(game);
		this.source = source;
		this.target = target;
		this.discoveredRole = target.getRole();
	}
	
}
