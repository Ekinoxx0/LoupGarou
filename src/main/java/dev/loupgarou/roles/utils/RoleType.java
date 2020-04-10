package dev.loupgarou.roles.utils;

import org.bukkit.Material;

import dev.loupgarou.classes.LGCustomItems;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleType {
	VILLAGER("Villageois", LGCustomItems.getSpecialItem(LGCustomItems.SpecialItems.GREEN_ROLE_Q)),
	LOUP_GAROU("Loup Garou", LGCustomItems.getSpecialItem(LGCustomItems.SpecialItems.RED_ROLE_Q)),
	VAMPIRE("Vampire", LGCustomItems.getSpecialItem(LGCustomItems.SpecialItems.GREY_ROLE_Q)),
	NEUTRAL("Neutre", LGCustomItems.getSpecialItem(LGCustomItems.SpecialItems.MID_ROLE_Q));
	
	@Getter @NonNull private String beautifulName;
	@Getter @NonNull private Material item;
}
