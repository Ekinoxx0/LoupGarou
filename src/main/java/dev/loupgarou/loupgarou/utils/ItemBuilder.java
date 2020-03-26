package dev.loupgarou.loupgarou.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

/**
 * @author Ekinoxx
 */
@SuppressWarnings("deprecation")
public class ItemBuilder {

	private ItemStack item;
	private ItemMeta meta;
	
    /**
     * Instance itembuilder with material
     *
     * @param material Material
     */
    public ItemBuilder(Material material) {
    	Validate.notNull(material);
    	
    	item = new ItemStack(material);
    	meta = item.getItemMeta();
    }
    
    /**
     * Instance itembuilder with ItemStack
     *
     * @param item ItemStack
     */
    public ItemBuilder(ItemStack item) {
    	this.item = item;
    	this.meta = item.getItemMeta();
    }

    /**
     * Set amount of ItemBuilder
     *
     * @param amount Integer amount
     * @return ItemBuilder
     */
    public ItemBuilder amount(int amount) {
    	item.setAmount(amount);
        return this;
    }


    /**
     * Set durability of ItemBuilder
     *
     * @param durability Durability of the item 
     * @return ItemBuilder
     */
	public ItemBuilder durability(short durability) {
    	item.setDurability(durability);
        return this;
    }

    /**
     * Set data of ItemBuilder
     *
     * @param s Short data
     * @return ItemBuilder
     */
	public ItemBuilder data(byte s) {
		item.setDurability(s);
        return this;
    }

    /**
     * Set name of ItemBuilder
     *
     * @param name String name
     * @return ItemBuilder
     */
    public ItemBuilder name(String name) {
    	meta.setDisplayName(name);
        return this;
    }

    /**
     * Set Unbreakable data of ItemBuilder
     *
     * @param value boolean value
     * @return ItemBuilder
     */
    public ItemBuilder unbreakable(boolean value) {
    	try{
        	meta.setUnbreakable(value);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
        return this;
    }

    /**
     * Set lore of ItemBuilder
     *
     * @param lore List of String lore
     * @return ItemBuilder
     */
    public ItemBuilder lore(List<String> lore) {
    	try{
    		if (lore != null && !lore.isEmpty()) {
    			ArrayList<String> lors = new ArrayList<String>();
    			for(String s : lore){
    				if (s.contains("\n")) {
    					String[] arrayOfString;
    					int j = (arrayOfString = s.split("\n")).length;
    					for (int i = 0; i < j; i++) {
    						lors.add(arrayOfString[i]);
    					}
    				} else {
    					lors.add(s);
    				}
    			}
    			
    			meta.setLore(lors);
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
        return this;
    }

    /**
     * Set enchantments of ItemBuilder
     *
     * @param enchants Map with Enchantment and Integer enchants
     * @return ItemBuilder
     */
    public ItemBuilder setEnchants(Map<Enchantment, Integer> enchants) {
    	for(Entry<Enchantment, Integer> i : enchants.entrySet()){
    		meta.addEnchant(i.getKey(), i.getValue(), true);
    	}
        return this;
    }

    /**
     * Add enchantment of ItemBuilder
     *
     * @param enchant Enchantment and Integer enchant
     * @return ItemBuilder
     */
    public ItemBuilder addEnchant(Enchantment ench, Integer power) {
    	try{
    		meta.addEnchant(ench, power, true);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
        return this;
    }

    /**
     * Set glow of ItemBuilder (not compatible with enchants)
     *
     * @param value Boolean if it have a glow
     * @return ItemBuilder
     */
	public ItemBuilder glow(boolean value) {
		if(value){
			if (this.item.getType() != Material.FISHING_ROD) {
				if (!this.meta.hasEnchants()) {
					this.meta.addEnchant(Enchantment.LURE, 1, true);
					this.meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
				}
			} else if (!this.meta.hasEnchants()) {
				this.meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
				this.meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
			}
		}else{
			if(this.meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
				this.meta.getEnchants().clear();
			}
		}
        return this;
	}

	public ItemBuilder hideFlags(boolean value) {
		for (ItemFlag ifl : ItemFlag.values()) {
			meta.addItemFlags(ifl);
		}
		return this;
	}
	
	public ItemBuilder setSkullOwner(String owner) {
		try{
			SkullMeta meta = (SkullMeta) this.meta;
			meta.setOwner(owner);
		}catch(Exception ex){}
		return this;
	}
	
	public ItemBuilder addPotionEffect(PotionEffect effect){
		try{
            PotionMeta meta = (PotionMeta) this.meta;
            meta.addCustomEffect(effect, true);
            this.meta = meta;
		}catch(Exception ex){
			ex.printStackTrace();
		}
        return this;
    }

	public ItemBuilder setLeatherColor(Color color) {
		try{
			LeatherArmorMeta meta = (LeatherArmorMeta) this.meta;
			meta.setColor(color);
			this.meta = meta;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return this;
	}

    /**
     * Build itembuilder
     *
     * @return ItemStack
     */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Get an ItemBuilder by ItemStack
     *
     * @param stack ItemStack to convert
     * @return ItemBuilder
     */
	public static ItemBuilder fromItemStack(ItemStack stack) {
        Validate.notNull(stack, "ItemStack in method fromItemStack cannot be null");
        return new ItemBuilder(stack);
    }

}
