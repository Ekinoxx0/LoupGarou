package dev.loupgarou.events.game;

import org.bukkit.event.Cancellable;

import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGEvent;
import dev.loupgarou.events.daycycle.LGNightPlayerPreKilledEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Used when a player is killed.
 * Please use {@link LGNightPlayerPreKilledEvent} if you need to verify this during the night
 */
public class LGPlayerKilledEvent extends LGEvent implements Cancellable {

	@Getter @Setter boolean cancelled;
    
    @Getter @Setter @NonNull private LGPlayer killed;
    @Getter @Setter @NonNull private Reason reason;
    
	public LGPlayerKilledEvent(@NonNull LGGame game, @NonNull LGPlayer killed, @NonNull Reason reason) {
		super(game);
		this.killed = killed;
		this.reason = reason;
	}
	
    @RequiredArgsConstructor
	public static enum Reason{
		LOUP_GAROU("§7§l%s§4 est mort pendant la nuit"),
		GM_LOUP_GAROU("§7§l%s§4 est mort pendant la nuit"),
		LOUP_BLANC(LOUP_GAROU.getMessage()),
		SORCIERE(LOUP_GAROU.getMessage()),
		VOTE("§7§l%s§4 a été victime du vote"),
		CHASSEUR("§7§l%s§4 est mort sur le coup"),
		DICTATOR("§7§l%s§4 a été désigné"),
		DICTATOR_SUICIDE("§7§l%s§4 s'est suicidé par culpabilité"),
		DISCONNECTED("§7§l%s§4 est mort d'une déconnexion"),
		LOVE("§7§l%s§4 s'est suicidé par amour"),
		BOUFFON("§7§l%s§4 est mort de peur"),
		ASSASSIN("§7§l%s§4 s'est fait poignarder"),
		PYROMANE("§7§l%s§4 est parti en fumée"),
		PIRATE("§7§l%s§4 était l'otage"),
		FAUCHEUR("§7§l%s§4 a égaré son âme"),
		
		DONT_DIE("§7§l%s§4 est mort pour rien");
		
		@Getter @NonNull private final String message;
	}
	
}
