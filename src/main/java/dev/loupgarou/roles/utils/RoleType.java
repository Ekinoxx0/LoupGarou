package dev.loupgarou.roles.utils;

import org.bukkit.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum RoleType {
	VILLAGER("§a", Material.LIME_WOOL, Material.LIME_CARPET),
	LOUP_GAROU("§c", Material.RED_WOOL, Material.RED_CARPET),
	NEUTRAL("§d", Material.PINK_WOOL, Material.PINK_CARPET);
	
	private final String color;
	private final Material noBodyMaterial;
	private final Material material;
	
}
