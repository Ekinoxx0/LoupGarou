package dev.loupgarou.events.other;

import org.bukkit.ChatColor;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Called to defined a prefix to a player from a player view
 */
public class LGUpdatePrefixEvent extends LGEvent {
	@Getter @Setter @NonNull private String prefix;
	@Getter @Setter @NonNull private ChatColor color;
	@Getter @NonNull private final LGPlayer player, to;
	
	public LGUpdatePrefixEvent(LGGame game, @NonNull LGPlayer player, @NonNull LGPlayer to) {
		super(game);
		this.player = player;
		this.color = ChatColor.GRAY;
		this.prefix = "";
		this.to = to;
	}
}
