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
import dev.loupgarou.roles.RPetiteFille;
import dev.loupgarou.roles.RPretre;
import dev.loupgarou.roles.RSurvivant;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import lombok.AllArgsConstructor;
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
	
	public InvalidCompo verifyRoles() {
		if(getTotalConfiguredRoles() < 3)
			return InvalidCompo.TOO_FEW_PLAYERS;
		if(getTotalConfiguredRoles() > this.getMap().getSpawns().size())
			return InvalidCompo.TOO_MANY_PLAYERS;
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
				return InvalidCompo.NO_CHASSEUR_TO_BE_PROTECTED;

			if (fakeRole instanceof RLoupFeutrer && getRoles().get(RLoupGarou.class) == 0)
				return InvalidCompo.NO_LP_OTHER_LP;

			if (fakeRole instanceof RLoupGarouNoir && getRoles().get(RLoupGarou.class) == 0)
				return InvalidCompo.NO_LP_OTHER_LP;

			if (fakeRole instanceof RLoupGarouBlanc && getRoles().get(RLoupGarou.class) == 0)
				return InvalidCompo.NO_LP_OTHER_LP;

			if (fakeRole instanceof RGrandMechantLoup && getRoles().get(RLoupGarou.class) == 0)
				return InvalidCompo.NO_LP_OTHER_LP;

			if (fakeRole instanceof RSurvivant && rolesPerType.get(RoleType.LOUP_GAROU) == 0)
				return InvalidCompo.NO_ONE_TO_SURVIVE_AGAINST;

			if (fakeRole instanceof RFaucheur && rolesPerType.get(RoleType.LOUP_GAROU) == 0)
				return InvalidCompo.NO_BODY_FAUCHEUR;

			if (fakeRole instanceof RPetiteFille && rolesPerType.get(RoleType.LOUP_GAROU) == 0)
				return InvalidCompo.NO_ONE_TO_SPY_GIRL;

			if (fakeRole instanceof RPretre && rolesPerType.get(RoleType.VILLAGER) <= 2)
				return InvalidCompo.NO_ONE_TO_RESPAWN;

			if (fakeRole instanceof RChasseurDeVampire && rolesPerType.get(RoleType.VAMPIRE) == 0)
				return InvalidCompo.NO_VAMPIRE;
		}

		return null;
	}
	
	/*
	 * Custom Types
	 */
	
	@AllArgsConstructor
	public enum InvalidCompo {
		TOO_FEW_PLAYERS("Trop peu de joueurs", null),
		TOO_MANY_PLAYERS("Trop de joueurs", null),
		NO_VAMPIRE("Aucun vampire pour pour le chasseur", RoleType.VAMPIRE),
		NO_ONE_TO_RESPAWN("Trop peu de membre du village à réapparaitre", RoleType.VILLAGER),
		NO_ONE_TO_SPY_GIRL("Pas de Loup Garou face à la Petite Fille", RoleType.VILLAGER),
		NO_BODY_FAUCHEUR("Pas de Loup Garou pour le Faucheur", RoleType.VILLAGER),
		NO_ONE_TO_SURVIVE_AGAINST("Pas de Loup Garou face au survivant", RoleType.NEUTRAL),
		NO_LP_OTHER_LP("Pas de Loup Garou normal", RoleType.LOUP_GAROU),
		NO_CHASSEUR_TO_BE_PROTECTED("Pas de Chasseur pour aider le Chaperon Rouge", RoleType.VILLAGER);
		
		@NonNull private String msg;
		@Getter private RoleType roleType;
		@Override
		public String toString() {
			return msg;
		}
	}
	
	public enum CommunicationType {
		TEXTUEL,
		DISCORD;
	}

}
