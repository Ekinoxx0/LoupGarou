package dev.loupgarou.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGMaps.LGMap;
import dev.loupgarou.roles.RChaperonRouge;
import dev.loupgarou.roles.RChasseur;
import dev.loupgarou.roles.RChasseurDeVampire;
import dev.loupgarou.roles.RFaucheur;
import dev.loupgarou.roles.RGrandMechantLoup;
import dev.loupgarou.roles.RLoupFeutrer;
import dev.loupgarou.roles.RLoupGarou;
import dev.loupgarou.roles.RLoupGarouBlanc;
import dev.loupgarou.roles.RLoupGarouNoir;
import dev.loupgarou.roles.RMontreurDOurs;
import dev.loupgarou.roles.RPetiteFille;
import dev.loupgarou.roles.RPretre;
import dev.loupgarou.roles.RSurvivant;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor @ToString
public class LGGameConfig {
	
	@Getter @ToString.Exclude private final Map<Class<? extends Role>, Integer> roles = new HashMap<Class<? extends Role>, Integer>();
	@Getter @Setter private boolean hideRole = false;
	@Getter @Setter private boolean hideVote = false;
	@Getter @Setter private boolean hideVoteExtra = false;
	@Getter @Setter private int timerDayPerPlayer = 15;
	@Getter @Setter @NonNull private CommunicationType com = CommunicationType.TEXTUEL;
	
	@Getter @NonNull @ToString.Exclude private final LGMap map;
	@Getter private final boolean privateGame;
	@Getter private final List<String> invited = new ArrayList<String>();//TODO Add invite system
	@Getter private final List<String> banned = new ArrayList<String>();//TODO Add ban system
	
	{//Init map
		for(Class<? extends Role> roleClazz : MainLg.getInstance().getRoles().keySet())
			this.roles.put(roleClazz, 0);
	}
	
	/*
	 * Methods
	 */
	
	public int getTotalConfiguredRoles(){
		int total = 0;
		for(Entry<Class<? extends Role>, Integer> entry : roles.entrySet())
			total += entry.getValue();
		return total;
	}
	
	/*
	 * Custom Types
	 */
	
	public enum CommunicationType {
		TEXTUEL,
		DISCORD;
	}

	public Role verifyRoles() {
		Map<RoleType, Integer> rolesPerType = new HashMap<RoleType, Integer>();

		for(RoleType type : RoleType.values())
			rolesPerType.put(type, 0);
		
		for(Entry<Class<? extends Role>, Integer> entry : this.roles.entrySet()) {
			RoleType rt = FakeRoles.getRole(entry.getKey()).getType();
			rolesPerType.replace(rt, rolesPerType.get(rt) + entry.getValue());
		}
		
		for (Entry<Class<? extends Role>, Integer> entry : getRoles().entrySet()) {
			if (entry.getValue() == 0)
				continue;
			Role fakeRole = FakeRoles.getRole(entry.getKey());

			if (fakeRole instanceof RChaperonRouge && getRoles().get(RChasseur.class) == 0)
				return fakeRole;

			if (fakeRole instanceof RLoupFeutrer && getRoles().get(RLoupGarou.class) == 0)
				return fakeRole;

			if (fakeRole instanceof RLoupGarouNoir && getRoles().get(RLoupGarou.class) == 0)
				return fakeRole;

			if (fakeRole instanceof RLoupGarouBlanc && getRoles().get(RLoupGarou.class) == 0)
				return fakeRole;

			if (fakeRole instanceof RGrandMechantLoup && getRoles().get(RLoupGarou.class) == 0)
				return fakeRole;

			if (fakeRole instanceof RSurvivant && rolesPerType.get(RoleType.LOUP_GAROU) == 0)
				return fakeRole;

			if (fakeRole instanceof RMontreurDOurs && getTotalConfiguredRoles() <= 2)
				return fakeRole;

			if (fakeRole instanceof RFaucheur && rolesPerType.get(RoleType.LOUP_GAROU) == 0 && getTotalConfiguredRoles() <= 2)
				return fakeRole;

			if (fakeRole instanceof RPetiteFille && rolesPerType.get(RoleType.LOUP_GAROU) == 0)
				return fakeRole;

			if (fakeRole instanceof RPretre && rolesPerType.get(RoleType.VILLAGER) <= 2)
				return fakeRole;

			if (fakeRole instanceof RChasseurDeVampire && rolesPerType.get(RoleType.VAMPIRE) == 0)
				return fakeRole;
		}

		return null;
	}
}
