package dev.loupgarou.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dev.loupgarou.MainLg;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author Ekinoxx
 *         <p>
 *         How to use ?
 *         - Create bukkit inventory, cast it to CraftInventory and instance this object with it
 */
public class InteractInventory implements Listener{

	public static abstract class InventoryCall {
		public abstract void click(HumanEntity human, ItemStack item, ClickType clickType);
	}
	
	public static abstract class InventoryPutCall {
		/**
		 * @param humanHuman who click this inventory
		 * @param item Item clicked
		 * @param clickType Type of this click
		 * @return True if we block the action, false otherwise
		 */
		public abstract boolean click(HumanEntity human, ItemStack item, ClickType clickType, InventoryAction action);
	}
	
	public static abstract class InventoryClose {
		public void nextTick(InteractInventory ii, HumanEntity human) {}
		/**
		 * @param inv 
		 * @param human Human who close this inventory
		 * @return Delete on close ?
		 */
		public abstract boolean close(InteractInventory ii, HumanEntity human);
	}

	public static class InventoryMaterial {

		private Material material;
		private InventoryCall caller;
		private boolean cancelClickAction;

		InventoryMaterial(Material material, InventoryCall caller, boolean cancelClickAction) {
			this.material = material;
			this.caller = caller;
			this.cancelClickAction = cancelClickAction;
		}

		public Material getMaterial() {
			return material;
		}

		public InventoryCall getCaller() {
			return caller;
		}

		public boolean isCancelClickAction() {
			return cancelClickAction;
		}
	}
	
	public static abstract class TextCall {

	    public abstract void send(Player p, String input);

	}

	/*
	 * 
	 */
	
	@Getter private Inventory inv;
	private boolean deleteOnClose;
	@Getter @Setter private InventoryClose closeAction;
	@Getter @Setter private InventoryPutCall putOwnItem;
    private Map<Integer, InventoryMaterial> materialMap;

    public InteractInventory(@NonNull Inventory inventory) {
        this.inv = inventory;
        this.deleteOnClose = true;
        this.closeAction = null;
        this.materialMap = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, MainLg.getInstance());
    }

    /**
     * Fill border of an inventory with an ItemStack
     *
     * @param s                 ItemStack to fill
     * @param cancelclickaction Boolean if when click, user can get item
     * @throws Exception Not compatible with InventoryType
     */
    public void fillBorder(ItemStack s, boolean cancelclickaction) {
    	fillBorder(s, cancelclickaction, null);
    }

    /**
     * Fill border of an inventory with an ItemStack
     *
     * @param s                 ItemStack to fill
     * @param cancelclickaction Boolean if when click, user can get item
     * @param caller            InventoryCall caller to call when click
     * @throws Exception Not compatible with InventoryType
     */
    public void fillBorder(ItemStack s, boolean cancelclickaction, InventoryCall caller) {
    	if(s == null){
    		s = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").data((byte)7).build();
    	}
    	
    	if(caller == null){
    		caller = new InventoryCall() {@Override public void click(HumanEntity human, ItemStack item, ClickType clickType) {}};
    	}
    	
    	if(this.inv.getType() == InventoryType.CHEST){
    		int count = this.inv.getSize();
    		int line = 0;
    		
    		int myCounter = count;
    		while(myCounter >= 9){
    			line++;
    			myCounter = myCounter - 9;
    		}
    		
    		if(line <= 2){
    			return;
    		}
    		
			try {
				for (int a = 0; a < 9; a++) {
					this.registerItem(s, a, cancelclickaction, caller);
				}

				for (int b = 0; b <= line; b++) {
					try {
						if (b != 0) {
							if (b != line) {
								this.registerItem(s, 9 * b, cancelclickaction, caller);
							}
							this.registerItem(s, 8 * b + (b - 1), cancelclickaction, caller);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				for (int c = 0; c <= 8; c++) {
					if (c != 0) {
						int calcul = c + (line - 1) * 9;
						this.registerItem(s, calcul, cancelclickaction, caller);
					}
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
    		
            
    	}
    }

    /**
     * Fill all inventory with an ItemStack
     *
     * @param s                 ItemStack to fill
     * @param cancelclickaction Boolean if when click, user can get item
     * @param caller            InventoryCall caller to call when click
     */
    public void fill(ItemStack s, boolean cancelclickaction, InventoryCall caller) {
    	if(s == null){
    		s = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").data((byte)7).build();
    	}
    	if(caller == null){
    		caller = new InventoryCall() {
    			@Override 
    			public void click(HumanEntity human, ItemStack item, ClickType clickType) {
    			}
    		};
    	}
        for (int i = 0; i < this.inv.getSize(); i++)
        	this.registerItem(s, i, cancelclickaction, caller);
    }

    /**
     * Register an item
     *
     * @param item              Itemstack to set
     * @param column            Integer column to set
     * @param line              Integer line to set
     * @param cancelclickaction Boolean if when click, user can get item
     * @param caller            InventoryCall caller to call when click
     */
    public void registerItem(ItemStack item, int column, int line, boolean cancelclickaction, InventoryCall caller) {
    	if(caller == null){
    		caller = new InventoryCall() {
    			@Override 
    			public void click(HumanEntity human, ItemStack item, ClickType clickType) {
    			}
    		};
    	}
    	
        registerItem(item, (line * 9) + column, cancelclickaction, caller);
    }

    /**
     * Register an item
     *
     * @param item              Itemstack to set
     * @param position          Integer position to set
     * @param cancelclickaction Boolean if when click, user can get item
     * @param caller            InventoryCall caller to call when click
     */
    public void registerItem(ItemStack item, int position, boolean cancelclickaction, InventoryCall caller) {
    	if(caller == null){
    		caller = new InventoryCall() {
    			@Override 
    			public void click(HumanEntity human, ItemStack item, ClickType clickType) {
    			}
    		};
    	}
        if (position < 0 || position >= inv.getSize()){
        	throw new IndexOutOfBoundsException("Position " + position + " isn't valid in " + inv.getSize());
        }
        removeItem(position);

        inv.setItem(position, item);
        materialMap.put(position, new InventoryMaterial(item.getType(), caller, cancelclickaction));
    }

    public void registerFromInventory(InteractInventory ii) {
    	this.emptyInv();
    	
    	for(Entry<Integer, InventoryMaterial> i : ii.materialMap.entrySet()){
            if (i.getKey() < 0 || i.getKey() >= inv.getSize()){
            	continue;
            }
            
            this.inv.setItem(i.getKey(), ii.inv.getItem(i.getKey()));
            this.materialMap.put(i.getKey(), i.getValue());
    	}
    }

    /**
     * Request text input to player
     *
     * @param toRequest            Player to request
     * @param requestHeader        String text to send to player before input
     * @param cancelsendingmessage Boolean if input text is send on global or not
     * @param caller               TextCall caller to call when input send
     */
    public static void requestText(Player toRequest, String requestHeader, boolean cancelsendingmessage, TextCall caller) {
    	requestText(toRequest, Arrays.asList(requestHeader), cancelsendingmessage, caller);
    }
    
    /**
     * Request text input to player
     *
     * @param toRequest            Player to request
     * @param requestHeader        String text to send to player before input
     * @param cancelsendingmessage Boolean if input text is send on global or not
     * @param caller               TextCall caller to call when input send
     */
    public static void requestText(Player toRequest, List<String> requestHeader, boolean cancelsendingmessage, TextCall caller) {
    	for(String s : requestHeader){
            toRequest.sendMessage(s);
    	}

        Listener listener = new Listener() {
            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent e) {
                if (e.getPlayer().equals(toRequest)) {
                    if (cancelsendingmessage) e.setCancelled(true);
                    caller.send(toRequest, e.getMessage());
                    HandlerList.unregisterAll(this);
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                if (e.getPlayer().equals(toRequest)) HandlerList.unregisterAll(this);
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, MainLg.getInstance());
    }

    public void removeItem(int position) {
        if (position >= inv.getSize() || position < 0) return;
        inv.setItem(position, new ItemStack(Material.AIR));
        if (materialMap.containsKey(position))
            materialMap.remove(position);
    }
    
    public void removeItems(int start) {
        removeItems(start, inv.getSize() - 1);
    }

    public void removeItems(int start, int end) {
        for (int i = start; i <= end; i++) {
            removeItem(i);
        }
    }

    public void emptyInv() {
    	removeItems(0, inv.getSize()-1);
    }
    
    /* */

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(inv)) {
            InventoryMaterial material = materialMap.get(e.getSlot());
            if(material != null && e.getCurrentItem() != null && material.getMaterial() == e.getCurrentItem().getType()) {
                e.setCancelled(material.isCancelClickAction());
                if (material.getCaller() != null) material.getCaller().click(e.getWhoClicked(), e.getCurrentItem(), e.getClick());
            }else if(putOwnItem == null){
        		e.setCancelled(true);
            }else if(putOwnItem != null){
        		e.setCancelled(putOwnItem.click(e.getWhoClicked(), e.getCurrentItem(), e.getClick(), e.getAction()));
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(inv)) {
        	if (closeAction != null) {
        		deleteOnClose = closeAction.close(this, e.getPlayer());
        		new BukkitRunnable() {
					
					@Override
					public void run() {
						closeAction.nextTick(InteractInventory.this, e.getPlayer());
					}
				}.runTaskLater(MainLg.getInstance(), 1);
        	}
        	
        	if (deleteOnClose) {
        		inv.getViewers().remove(e.getPlayer());
                close();
        	}
        }
    }
    
    /* */

    /**
     * Open inventory on a player
     *
     * @param player Player to open
     */
    public void openTo(Player player) {
        if(this.inv == null) return;
        if(player == null) return;
        
        SoundUtils.sendSound(player, Sound.UI_BUTTON_CLICK);
        player.openInventory(this.inv);
    }
    
    /**
     * Open inventory on a player
     *
     * @param player Player to open
     */
    public void openInTo(InteractInventory ii, Player player) {
    	if(inv == null) return;
    	if(ii == null) return;
    	
        if(inv.getSize() == ii.inv.getSize()){
        	SoundUtils.sendSound(player, Sound.UI_BUTTON_CLICK);
        	deleteOnClose = false;
        	ii.registerFromInventory(this);
        	deleteOnClose = true;
        }
    }
    
    public void close() {
        HandlerList.unregisterAll(this);
    	if(inv == null) return;
    	for(HumanEntity he : new ArrayList<>(inv.getViewers()))
    		he.closeInventory();
    	
        inv.clear();
        inv = null;
    }

}
