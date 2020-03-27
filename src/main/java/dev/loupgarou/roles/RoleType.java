package dev.loupgarou.roles;

import org.bukkit.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum RoleType {
	VILLAGER("§a", Material.LIME_GLAZED_TERRACOTTA),
	LOUP_GAROU("§c", Material.RED_GLAZED_TERRACOTTA),
	NEUTRAL("§d", Material.PINK_GLAZED_TERRACOTTA);
	
	private final String color;
	private final Material material;
}
