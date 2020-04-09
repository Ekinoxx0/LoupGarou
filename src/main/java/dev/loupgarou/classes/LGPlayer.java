package dev.loupgarou.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import dev.loupgarou.classes.LGChat.LGChatCallback;
import dev.loupgarou.packetwrapper.WrapperPlayServerPlayerInfo;
import dev.loupgarou.packetwrapper.WrapperPlayServerRespawn;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam.Mode;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache;
import dev.loupgarou.utils.VariableCache.CacheType;
import dev.loupgarou.utils.VariousUtils;
import dev.loupgarou.utils.SoundUtils.LGSound;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
public class LGPlayer extends LGPlayerSimple {
	private static HashMap<Player, LGPlayer> cachedPlayers = new HashMap<Player, LGPlayer>();
	public static Collection<LGPlayer> all(){
		return cachedPlayers.values();
	}
	
	public static LGPlayer thePlayer(Player player) {
		LGPlayer lgp = cachedPlayers.get(player);
		if(lgp == null) {
			lgp = new LGPlayer(player);
			cachedPlayers.put(player, lgp);
		}
		return lgp;
	}
	
	public static LGPlayer removePlayer(Player player) {
		return cachedPlayers.remove(player);
	}
	
	@Getter @Setter private int place;
	@Getter @Setter private boolean dead;
	@Getter @Setter private Role role;
	private LGChooseCallback chooseCallback;
	private final List<LGPlayer> blacklistedChoice = new ArrayList<>(0);
	@Getter private final VariableCache cache = new VariableCache();
	@Getter @Setter private LGGame game;
	@Getter @Setter private String latestObjective;
	@Getter @Setter private String connectingHostname;
	@Getter private LGChat chat;
	
	public LGPlayer(@NonNull Player player) {
		super(player);
	}
	@Deprecated
	public LGPlayer(String name) {
		super(name);
	}

	public void choose(LGChooseCallback callback, LGPlayer... blacklisted) {
		blacklistedChoice.clear();
		if(blacklisted != null) 
			this.blacklistedChoice.addAll(Arrays.asList(blacklisted));
		this.chooseCallback = callback;
	}
	public void stopChoosing() {
		this.blacklistedChoice.clear();
		this.chooseCallback = null;
	}
	
	public static interface LGChooseCallback{
		public void callback(LGPlayer choosen);
	}

	public void showView() {
		getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);

		if(getGame() == null) { //In lobby
			for(LGPlayer allP : LGPlayer.all())
				if(allP.getGame() != null) {
					this.hidePlayer(allP);
				} else {
					this.showPlayer(allP);
				}
			return;
		}
		if(getPlayer() == null) return;
		
		for(LGPlayer allP : LGPlayer.all())
			if(!getGame().getAlive().contains(allP))
				this.hidePlayer(allP);

		for (LGPlayer lgp : getGame().getAlive()) {
			if(lgp.isDead()) continue;
			
			if (lgp != this && lgp.getPlayer() != null)
				showPlayer(lgp);
			else {
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				team.setMode(Mode.TEAM_UPDATED);
				team.setName(lgp.getName());
				team.setPrefix(WrappedChatComponent.fromText(""));
				team.setPlayers(Arrays.asList(lgp.getName()));
				team.sendPacket(getPlayer());

				WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
				List<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
				info.setAction(PlayerInfoAction.ADD_PLAYER);
				infos.add(new PlayerInfoData(new WrappedGameProfile(getPlayer().getUniqueId(), getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getName())));
				info.setData(infos);
				info.sendPacket(getPlayer());
			}
		}
			
	}
	
	public void hideView() {
		if(getGame() == null) return;
		if(getPlayer() == null) return;
		getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
		getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 1, false, false));
		
		for(LGPlayer allP : LGPlayer.all())
			if(!getGame().getAlive().contains(allP))
				this.hidePlayer(allP);
		
		WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
		List<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
		info.setAction(PlayerInfoAction.ADD_PLAYER);
		for(LGPlayer lgp : getGame().getAlive())
			if(lgp != this && lgp.getPlayer() != null) {
				infos.add(new PlayerInfoData(new WrappedGameProfile(lgp.getPlayer().getUniqueId(), lgp.getName()), 0, lgp.isDead() ? NativeGameMode.SPECTATOR : NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(lgp.getName())));
				hidePlayer(lgp);
			}
		info.setData(infos);
		info.sendPacket(getPlayer());
	}
	
	public void updatePrefix() {
		if(getGame() == null) return;
		if(isDead()) return;
		if(getPlayer() == null) return;
				
		List<String> meList = Arrays.asList(getName());
		for(LGPlayer lgp : getGame().getInGame()) {
			WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
			ArrayList<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
			info.setAction(PlayerInfoAction.ADD_PLAYER);
			infos.add(new PlayerInfoData(new WrappedGameProfile(getPlayer().getUniqueId(), getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getName())));
			info.setData(infos);
			info.sendPacket(lgp.getPlayer());
			
			WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
			team.setMode(Mode.TEAM_UPDATED);
			team.setName(getName());
			team.setPrefix(WrappedChatComponent.fromText(""));
			team.setPlayers(meList);
			team.sendPacket(lgp.getPlayer());
		}
	}
	
	public void updateSkin() {
		if(getGame() == null) return;
		if(getPlayer() == null) return;
		
		for(LGPlayer lgp : getGame().getInGame()) {
			if(lgp == this) {
				WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
				List<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
				info.setAction(PlayerInfoAction.ADD_PLAYER);
				infos.add(new PlayerInfoData(new WrappedGameProfile(getPlayer().getUniqueId(), getName()), 0, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getName())));
				info.setData(infos);
				info.sendPacket(getPlayer());
			}else if(!isDead() && lgp.getPlayer() != null){
				lgp.hidePlayer(this);
				lgp.showPlayer(this);
			}
		}
	}
	
	public void updateOwnSkin() {
		if(getPlayer() == null) return;
		WrapperPlayServerPlayerInfo infos = new WrapperPlayServerPlayerInfo();
		infos.setAction(PlayerInfoAction.ADD_PLAYER);
		WrappedGameProfile gameProfile = new WrappedGameProfile(getPlayer().getUniqueId(), getPlayer().getName());
		infos.setData(Arrays.asList(new PlayerInfoData(gameProfile, 10, NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getPlayer().getName()))));
		infos.sendPacket(getPlayer());
		
		WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn();
		respawn.setDimension(0);
		respawn.setLevelType(WorldType.NORMAL);
		respawn.setGamemode(NativeGameMode.ADVENTURE);
		respawn.setHashedSeed(0L);
		respawn.sendPacket(getPlayer());
		getPlayer().teleport(getPlayer().getLocation());
			
		//TODO Speed after own skin ?
		/*float speed = getPlayer().getWalkSpeed();
		getPlayer().setWalkSpeed(0.2f);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				getPlayer().updateInventory();
				getPlayer().setWalkSpeed(speed);
			}
		}.runTaskLater(MainLg.getInstance(), 5);*/
	}
	
	public boolean canSelectDead;
	public LGPlayer getPlayerOnCursor(List<LGPlayer> list) {
		Location pointedLoc = getPlayer().getLocation();
		if(pointedLoc.getPitch() > 80 || pointedLoc.getPitch() < -60)
			if(blacklistedChoice.contains(this))
				return null;
			else
				return this;
		
		for(int i = 0; i < 50; i++) {//TODO Why 50 ?
			pointedLoc.add(pointedLoc.getDirection());
			for(LGPlayer targetedPlayer : list) {
				if(targetedPlayer != this && !blacklistedChoice.contains(targetedPlayer) && (!targetedPlayer.isDead() || canSelectDead) && 
					VariousUtils.distanceSquaredXZ(pointedLoc, targetedPlayer.getPlayer().getLocation()) < 0.35 && Math.abs(pointedLoc.getY()-targetedPlayer.getPlayer().getLocation().getY()) < 1.3) {
					return targetedPlayer;
				}
			}
		}
		return null;
	}
	
	public RoleType getRoleType() {
		return this.getCache().getBoolean(CacheType.INFECTED) ? RoleType.LOUP_GAROU : getRole().getType(this);
	}
	public RoleWinType getRoleWinType() {
		return this.getCache().getBoolean(CacheType.INFECTED) ? RoleWinType.LOUP_GAROU : getRole().getWinType(this);
	}
	public boolean isRoleActive() {
		return true;//TODO Old Vampire
	}
	
	public void joinChat(LGChat chat, @Nullable LGChatCallback callback, boolean muted) {
		if(this.chat != null && !muted)
			this.chat.leave(this);
		
		if(!muted)
			this.chat = chat;
		
		if(chat != null && getPlayer() != null)
			chat.join(this, callback == null ? chat.getDefaultCallback() : callback);
	}
	
	
	public void leaveAllChat() {
		joinChat(new LGChat(null, null) {
			public void sendMessage(LGPlayer sender, String message) {}
			public void join(LGPlayer player, LGChatCallback callback) {}
			public void leave(LGPlayer player) {}
		}, null, false);
	}
	
	public void onChat(String message) {
		if(this.chat == null) return;
		this.chat.sendMessage(this, message);
	}
	
	private long lastChooseClick;
	public void chooseAction() {
		long now = System.currentTimeMillis();
		if(lastChooseClick+200 < now) {
			if(chooseCallback != null)
				chooseCallback.callback(getPlayerOnCursor(getGame().getInGame()));
			lastChooseClick = now;
		}
	}
	
	public void reset() throws IllegalStateException {
		if(this.getPlayer() == null) throw new IllegalStateException("No player so nothing to reset...");
		for(LGSound sound : LGSound.values())
			this.stopAudio(sound);
		
		this.place = 0;
		this.dead = false;
		this.role = null;
		this.stopChoosing();
		this.cache.reset();
		this.game = null;
		this.latestObjective = null;
		this.lastChooseClick = 0L;	
		this.canSelectDead = false;
		this.setScoreboard(null);
		
		this.sendActionBarMessage("");
		this.sendTitle("", "", 0);
	}

}
