package dev.loupgarou.commands.subcommands.spawns;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import dev.loupgarou.MainLg;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.packetwrapper.WrapperPlayServerEntityDestroy;
import dev.loupgarou.packetwrapper.WrapperPlayServerEntityEquipment;
import dev.loupgarou.packetwrapper.WrapperPlayServerEntityMetadata;
import dev.loupgarou.packetwrapper.WrapperPlayServerSpawnEntityLiving;

public class ShowSpawnsCmd extends SubCommand {

	public ShowSpawnsCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("showspawns", "showspawn"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(CommandSender cs, String label, String[] args) {
		Player pSP = (Player) cs;
		List<List<Double>> listPos = (List<List<Double>>) getMain().getConfig().getList("spawns");

		int n = 0;
		for (List<Double> l : listPos) {
			Location sel = new Location(pSP.getWorld(), l.get(0), l.get(1), l.get(2));
			showArrow(pSP, sel, 10, n);
			n++;
		}
	}

	WrappedDataWatcherObject invisible = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)),
			noGravity = new WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class));

	private void showArrow(Player p, Location loc, int time, int n) {
		final int entityId = new Random().nextInt(500000) + 10000;
		if (loc != null) {
			WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
			spawn.setEntityID(entityId);
			spawn.setType(EntityType.DROPPED_ITEM);
			spawn.setX(loc.getX());
			spawn.setY(loc.getY() + 1.3);
			spawn.setZ(loc.getZ());
			spawn.setHeadPitch(0);
			Location toLoc = p.getLocation();
			double diffX = loc.getX() - toLoc.getX(), diffZ = loc.getZ() - toLoc.getZ();
			float yaw = 180 - ((float) Math.toDegrees(Math.atan2(diffX, diffZ)));

			spawn.setYaw(yaw);
			spawn.sendPacket(p);

			WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
			meta.setEntityID(entityId);
			meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte) 0x20),
					new WrappedWatchableObject(noGravity, true)));
			meta.sendPacket(p);

			new BukkitRunnable() {

				@Override
				public void run() {
					WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment();
					equip.setEntityID(entityId);
					equip.setSlot(ItemSlot.HEAD);
					ItemStack skull = new ItemStack(Material.EMERALD);
					equip.setItem(skull);
					equip.sendPacket(p);
				}
			}.runTaskLater(MainLg.getInstance(), 2);

			new BukkitRunnable() {

				@Override
				public void run() {
					WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
					destroy.setEntityIds(new int[] { entityId });
					destroy.sendPacket(p);
				}
			}.runTaskLater(MainLg.getInstance(), time * 20);
		}
	}

}