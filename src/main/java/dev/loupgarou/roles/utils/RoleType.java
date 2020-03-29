package dev.loupgarou.roles.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum RoleType {
	VILLAGER("§a"),
	LOUP_GAROU("§c"),
	NEUTRAL("§d");
	private final String color;
}
