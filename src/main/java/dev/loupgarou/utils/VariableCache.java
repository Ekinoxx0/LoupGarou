package dev.loupgarou.utils;

import java.util.HashMap;

import lombok.NonNull;

@SuppressWarnings("unchecked")
public class VariableCache {
	
	public static enum CacheType {
		
		INLOVE,
		ASSASSIN_PROTECTED,
		BOUFFON_WIN,
		HAS_INFECTED,
		JUST_INFECTED,
		INFECTED,
		VOTE,
		LAST_VOTE_TIME,
		SURVIVANT_LEFT,
		SURVIVANT_PROTECTED,
		WITCH_USED_LIFE,
		WITCH_USED_DEATH,
		PYROMANE_ESSENCE,
		PIRATE_OTAGE,
		PIRATE_OTAGE_D,
		LOUP_FEUTRER,
		GARDE_LASTPROTECTED,
		GARDE_PROTECTED,
		FAUCHEUR_DID,
		ENFANT_SAUVAGE,
		ENFANT_SAUVAGE_D,
		COUP_D_ETAT,
		JUST_COUP_D_ETAT,
		DETECTIVE_FIRST,
		CUPIDON_FIRST,
		CORBEAU_SELECTED,
		CHAPERON_KILL,
		HAS_CHOOSEN_CHIEN_LOUP, 
		JUST_VAMPIRE,
		
	}
	
	private HashMap<CacheType, Object> cache = new HashMap<CacheType, Object>();
	public boolean getBoolean(CacheType key) {
		Object object = get(key);
		return object == null ? false : (boolean)object;
	}
	
	public void init(@NonNull CacheType key, Object value) {
		if(!cache.containsKey(key))
			cache.put(key, value);
	}
	
	
	public void set(@NonNull CacheType key, Object value) {
		cache.put(key, value);
	}
	
	public boolean has(@NonNull CacheType key) {
		return cache.containsKey(key);
	}
	
	public <T> T get(@NonNull CacheType key) {
		return (T)cache.get(key);
	}
	
	public <T> T remove(@NonNull CacheType key) {
		return (T)cache.remove(key);
	}
	
	public void reset() {
		cache.clear();
	}
}
