package dev.loupgarou.roles;

import org.bukkit.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum RoleType {
	VILLAGER("§9", Material.TUBE_CORAL_BLOCK),
	LOUP_GAROU("§c", Material.NETHER_WART_BLOCK),
	NEUTRAL("§d", Material.BRAIN_CORAL_BLOCK);
	
	private final String color;
	private final Material material;
}
