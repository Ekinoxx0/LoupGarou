package dev.loupgarou.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGChat;
import dev.loupgarou.classes.LGCustomSkin;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGVote;
import dev.loupgarou.classes.LGWinType;
import dev.loupgarou.events.daycycle.LGNightEndEvent;
import dev.loupgarou.events.daycycle.LGNightStartEvent;
import dev.loupgarou.events.game.LGGameEndEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import dev.loupgarou.events.other.LGSkinLoadEvent;
import dev.loupgarou.events.other.LGUpdatePrefixEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache.CacheType;
import lombok.Getter;

public class RLoupGarou extends Role{

	public RLoupGarou(LGGame game) {
		super(game);
	}
	
	@Override
	public int getMaxNb() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public String getColor() {
		return "§c";
	}

	@Override
	public String getName() {
		return getColor() + "§lLoup-Garou";
	}

	@Override
	public String getFriendlyName() {
		return "des §c§lLoups-Garous";
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes avec les §c§lLoups-Garous";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes avec les §c§lLoups-Garous§f. Chaque nuit, tu te réunis avec tes compères pour décider d'une victime à éliminer.";
	}

	@Override
	public String getTask() {
		return "Vote pour la cible à tuer.";
	}

	@Override
	public String getBroadcastedTask() {
		return "Les §c§lLoups-Garous§9 choisissent leur cible.";
	}
	@Override
	public RoleType getType() {
		return RoleType.LOUP_GAROU;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.LOUP_GAROU;
	}

	@Override
	public int getTimeout() {
		return 30;
	}
	
	@Getter private LGChat chat = new LGChat((sender, message) -> {
		return "§c"+sender.getName()+" §6» §f"+message;
	}, "LG");

	boolean showSkins = false;
	LGVote vote;
	@Override
	public void join(LGPlayer player, boolean sendMessage, boolean leavePrecedentRole) {
		super.join(player, sendMessage, leavePrecedentRole);
		//On peut créer des cheats grâce à ça (qui permettent de savoir qui est lg/inf)
		for(LGPlayer p : getPlayers())
			p.updateTab();
	}

	public void onNightTurn(Runnable callback) {
		vote = new LGVote(getTimeout(), getTimeout()/3, getGame(), false, false, (player, secondsLeft)-> {
			return !getPlayers().contains(player) ? (RLoupGarou.this.getGame().getConfig().isHideRole() ? "§6C'est au tour de quelqu'un..." : ("§6C'est au tour "+getFriendlyName()+" §6(§e"+secondsLeft+" s§6)")) : player.getCache().has(CacheType.VOTE) ? "§l§9Vous votez contre §c§l"+player.getCache().<LGPlayer>get(CacheType.VOTE).getName() : "§6Il vous reste §e"+secondsLeft+" seconde"+(secondsLeft > 1 ? "s" : "")+"§6 pour voter";
		}, this.getGame().getConfig().isHideVoteRole(), this.getGame().getConfig().isHideVoteRole());
		for(LGPlayer lgp : getGame().getAlive())
			if(lgp.getRoleType() == RoleType.LOUP_GAROU)
				lgp.showView();
		for(LGPlayer player : getPlayers()) {
			player.sendMessage("§6"+getTask());
		//	player.sendTitle("§6C'est à vous de jouer", "§a"+getTask(), 100);
			player.joinChat(chat, null, false);
		}
		vote.start(getPlayers(), getPlayers(), ()->{
			onNightTurnEnd();
			callback.run();
		}, Collections.emptyList());
	}
	private void onNightTurnEnd() {
		for(LGPlayer lgp : getGame().getAlive())
			if(lgp.getRoleType() == RoleType.LOUP_GAROU)
				lgp.hideView();
		for(LGPlayer player : getPlayers())
			player.leaveAllChat();

		LGPlayer choosen = vote.getChoosen();
		if(choosen == null) {
			if(vote.getVotes().size() > 0) {
				int max = 0;
				boolean equal = false;
				for(Entry<LGPlayer, List<LGPlayer>> entry : vote.getVotes().entrySet())
					if(entry.getValue().size() > max) {
						MainLg.debug(getGame().getKey(), entry.getKey()+" has "+entry.getValue().size()+" vote(s)");
						equal = false;
						max = entry.getValue().size();
						choosen = entry.getKey();
					}else if(entry.getValue().size() == max)
						equal = true;
				if(equal) {
					choosen = null;
					ArrayList<LGPlayer> choosable = new ArrayList<LGPlayer>();
					for(Entry<LGPlayer, List<LGPlayer>> entry : vote.getVotes().entrySet())
						if(entry.getValue().size() == max && entry.getKey().getRoleType() != RoleType.LOUP_GAROU)
							choosable.add(entry.getKey());
					MainLg.debug(getGame().getKey(), "Random choice for LG (removed lg from vote)");
					if(choosable.size() > 0)
						choosen = choosable.get(getGame().getRandom().nextInt(choosable.size()));
				}
			}
		}
		if(choosen != null) {
			getGame().kill(choosen, Reason.LOUP_GAROU);
			for(LGPlayer player : getPlayers())
				player.sendMessage("§6Les §c§lLoups§6 ont décidé de tuer §7§l"+choosen.getName()+"§6.");
		}else
			for(LGPlayer player : getPlayers())
				player.sendMessage("§6Personne n'a été désigné pour mourir.");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSkinChange(LGSkinLoadEvent e) {
		if(e.getGame() == getGame())
			if(getPlayers().contains(e.getPlayer()) && getPlayers().contains(e.getTo()) && showSkins) {
				e.getProfile().getProperties().removeAll("textures");
				e.getProfile().getProperties().put("textures", LGCustomSkin.WEREWOLF.getProperty());
			}
	}
	@EventHandler
	public void onGameEnd(LGGameEndEvent e) {
		if(e.getGame() == getGame() && e.getWinType() == LGWinType.LOUPGAROU)
			for(LGPlayer lgp : getGame().getInGame())
				if(lgp.getRoleWinType() == RoleWinType.LOUP_GAROU)//Changed to wintype
					e.getWinners().add(lgp);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onUpdatePrefix(LGUpdatePrefixEvent e) {
		if(e.getGame() == getGame())
			if(getPlayers().contains(e.getTo()) && getPlayers().contains(e.getPlayer()))
				e.setColor(ChatColor.RED);
	}
	
	@EventHandler
	public void onDay(LGNightEndEvent e) {
		if(e.getGame() == getGame()) {
			showSkins = false;
			for(LGPlayer player : getPlayers())
				player.updateOwnSkin();
		}
	}
	@EventHandler
	public void onNightStart(LGNightStartEvent e) {
		if(e.getGame() == getGame()) {
			showSkins = true;
			for(LGPlayer player : getPlayers())
				player.updateOwnSkin();
		}
	}
	
}
