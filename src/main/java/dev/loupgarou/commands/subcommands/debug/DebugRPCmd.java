package dev.loupgarou.commands.subcommands.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGCustomItems.LGCustomItemsConstraints;
import dev.loupgarou.classes.LGCustomItems.SpecialItems;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.commands.LoupGarouCommand;
import dev.loupgarou.commands.SubCommand;
import dev.loupgarou.roles.utils.FakeRoles;
import dev.loupgarou.roles.utils.Role;

public class DebugRPCmd extends SubCommand {

	private HashMap<Player, BukkitTask> displaying = new HashMap<Player, BukkitTask>();
	public DebugRPCmd(LoupGarouCommand cmd) {
		super(cmd, Arrays.asList("debugrp"));
	}

	@Override
	public void execute(CommandSender cs, String label, String[] args) {
		if(!(cs instanceof Player)) return;
		
		Player p = (Player) cs;
		LGPlayer lgp = LGPlayer.thePlayer(p);
		
		if(displaying.containsKey(p)) {
			displaying.get(p).cancel();
			displaying.remove(p);
			p.sendMessage("§cCancelled.");
			p.getInventory().clear();
			lgp.getPlayer().getInventory().setItemInOffHand(null);
			return;
		}
		
		LGCustomItems.checkRessourcePack(cs);

		final TreeMap<String, Material> materials = new TreeMap<String, Material>();
		
		for(SpecialItems si : SpecialItems.values())
			materials.put("!" + si, LGCustomItems.getSpecialItem(si));
		
		for(Role r : FakeRoles.all())
			materials.put("!" + r.getName() + "Menu", LGCustomItems.getItemMenu(r));
		
		for(Role r : FakeRoles.all()) {
			materials.put("" + r.getName(), LGCustomItems.getItem(r));
			
			for(LGCustomItemsConstraints c1 : LGCustomItemsConstraints.values()) {
				List<LGCustomItemsConstraints> co1 = new ArrayList<LGCustomItemsConstraints>(Arrays.asList(c1));
				Material roleConstrainedMat1 = LGCustomItems.getItem(r, co1);
				materials.put(r.getName() + Arrays.toString(co1.toArray()), roleConstrainedMat1);
				for(LGCustomItemsConstraints c2 : LGCustomItemsConstraints.values()) {
					if(c1 == c2) continue;
					List<LGCustomItemsConstraints> co2 = new ArrayList<LGCustomItemsConstraints>(Arrays.asList(c1, c2));
					Material roleConstrainedMat2 = LGCustomItems.getItem(r, co2);
					materials.put(r.getName() + Arrays.toString(co2.toArray()), roleConstrainedMat2);
					for(LGCustomItemsConstraints c3 : LGCustomItemsConstraints.values()) {
						if(c2 == c3 || c1 == c3) continue;
						List<LGCustomItemsConstraints> co3 = new ArrayList<LGCustomItemsConstraints>(Arrays.asList(c1, c2, c3));
						Material roleConstrainedMat3 = LGCustomItems.getItem(r, co3);
						materials.put(r.getName() + Arrays.toString(co3.toArray()), roleConstrainedMat3);
					}
				}
			}
		}
		
		BukkitTask task = new BukkitRunnable() {
			Iterator<Entry<String, Material>> iterator = materials.entrySet().iterator();
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if(!p.isOnline()) {
					displaying.remove(p);
					cancel();
					return;
				}
				if(!iterator.hasNext()) {
					lgp.sendTitle("", "§2§lENDED!", 20 * 2);
					displaying.remove(p);
					cancel();
					return;
				}

				lgp.getPlayer().getInventory().clear();
				lgp.getPlayer().getInventory().setItemInOffHand(null);
				Entry<String, Material> entry = iterator.next();
				if(entry.getKey().startsWith("!")) {
					lgp.getPlayer().getInventory().setHeldItemSlot(0);
					lgp.getPlayer().getInventory().setItemInMainHand(new ItemStack(entry.getValue()));
				} else {
					LGCustomItems.updateItem(lgp, entry.getValue());
				}
				lgp.sendTitle("", "§9" + entry.getKey(), 20 * 2);
			}
		}.runTaskTimer(getMain(), 0, 20 * 2);
		displaying.put(p, task);
	}
	
}