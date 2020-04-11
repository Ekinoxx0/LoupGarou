package dev.loupgarou.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import dev.loupgarou.classes.LGChat;
import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.classes.LGVote;
import dev.loupgarou.classes.LGWinType;
import dev.loupgarou.events.daycycle.LGNightEndEvent;
import dev.loupgarou.events.game.LGGameEndEvent;
import dev.loupgarou.events.game.LGPlayerKilledEvent.Reason;
import dev.loupgarou.events.other.LGUpdatePrefixEvent;
import dev.loupgarou.events.roles.LGVampiredEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache.CacheType;
import lombok.Getter;

public class RVampire extends Role{

	public RVampire(LGGame game) {
		super(game);
	}
	
	@Override
	public int getMaxNb() {
		return Integer.MAX_VALUE;
	}

	@Override
	public String getColor() {
		return "§5";
	}

	@Override
	public String getName() {
		return getColor() + "§lVampire";
	}

	@Override
	public String getFriendlyName() {
		return "des §5§lVampires";
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes avec les §5§lVampires";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes avec les §5§lVampires§f. Chaque nuit, tu te réunis avec tes compères pour décider d'une victime à transformer en §5§lVampire§f... Lorsqu'une transformation a lieu, tous les §5§lVampires§f doivent se reposer la nuit suivante. Un joueur transformé perd tous les pouvoirs liés à son ancien rôle, et gagne avec les §5§lVampires§f.";
	}

	@Override
	public String getTask() {
		return "Votez pour une cible à mordre.";
	}

	@Override
	public String getBroadcastedTask() {
		return "Les §5§lVampires§9 choisissent leur cible.";
	}
	@Override
	public RoleType getType() {
		return RoleType.VAMPIRE;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.VAMPIRE;
	}

	@Override
	public int getTimeout() {
		return 30;
	}
	@Override
	public boolean hasPlayersLeft() {
		return nextCanInfect < getGame().getNight() && super.hasPlayersLeft();
	}

	@Getter private LGChat vampireChat = new LGChat((sender, message) -> {
		return "§5"+sender.getName()+" §6» §f"+message;
	}, "Vampire");
	
	int nextCanInfect = 0;
	LGVote vote;
	@Override
	public void join(LGPlayer player, boolean sendMessage) {
		super.join(player, sendMessage);
		for(LGPlayer p : getPlayers())
			p.updateTab();
	}

	public void onNightTurn(Runnable callback) {
		vote = new LGVote(getTimeout(), getTimeout()/3, getGame(), false, false, (player, secondsLeft)-> {
			return !getPlayers().contains(player) ? "§6C'est au tour "+getFriendlyName()+" §6(§e"+secondsLeft+" s§6)" : player.getCache().has(CacheType.VOTE) ? "§l§9Vous votez pour §c§l"+player.getCache().<LGPlayer>get(CacheType.VOTE).getName() : "§6Il vous reste §e"+secondsLeft+" seconde"+(secondsLeft > 1 ? "s" : "")+"§6 pour voter";
		});
		for(LGPlayer lgp : getGame().getAlive())
			if(lgp.getRoleType() == RoleType.VAMPIRE)
				lgp.showView();
		for(LGPlayer player : getPlayers()) {
			player.sendMessage("§6"+getTask());
		//	player.sendTitle("§6C'est à vous de jouer", "§a"+getTask(), 100);
			player.joinChat(vampireChat, null, false);
		}
		vote.start(getPlayers(), getPlayers(), ()->{
			onNightTurnEnd();
			callback.run();
		}, getPlayers());
	}
	private void onNightTurnEnd() {
		for(LGPlayer lgp : getGame().getAlive())
			if(lgp.getRoleType() == RoleType.VAMPIRE)
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
						equal = false;
						max = entry.getValue().size();
						choosen = entry.getKey();
					}else if(entry.getValue().size() == max)
						equal = true;
				if(equal) {
					choosen = null;
					ArrayList<LGPlayer> choosable = new ArrayList<LGPlayer>();
					for(Entry<LGPlayer, List<LGPlayer>> entry : vote.getVotes().entrySet())
						if(entry.getValue().size() == max && entry.getKey().getRoleType() != RoleType.VAMPIRE)
							choosable.add(entry.getKey());
					if(choosable.size() > 0)
						choosen = choosable.get(getGame().getRandom().nextInt(choosable.size()));
				}
			}
		}
		if(choosen != null) {
			if(choosen.getRoleType() == RoleType.LOUP_GAROU || choosen.getRoleType() == RoleType.VAMPIRE) {
				for(LGPlayer player : getPlayers())
					player.sendMessage("§cVotre cible est immunisée.");
				return;
			}else if(choosen.getRole() instanceof RChasseurDeVampire) {
				for(LGPlayer player : getPlayers())
					player.sendMessage("§cVotre cible est immunisée.");
				getGame().kill(getPlayers().get(getPlayers().size()-1), Reason.CHASSEUR_DE_VAMPIRE);
				return;
			}

			LGVampiredEvent event = new LGVampiredEvent(getGame(), choosen);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isImmuned()) {
				for(LGPlayer player : getPlayers())
					player.sendMessage("§cVotre cible est immunisée.");
				return;
			}else if(event.isProtect()) {
				for(LGPlayer player : getPlayers())
					player.sendMessage("§cVotre cible est protégée.");
				return;
			}
			for(LGPlayer player : getPlayers())
				player.sendMessage("§7§l"+choosen.getName()+" s'est transformé en §5§lVampire§6.");
			choosen.sendMessage("§6Tu as été infecté par les §5§lVampires §6pendant la nuit. Tu as perdu tes pouvoirs.");
			choosen.sendMessage("§6§oTu gagnes désormais avec les §5§l§oVampires§6§o.");
			choosen.getCache().set(CacheType.JUST_VAMPIRE, true);
			nextCanInfect = getGame().getNight()+1;
			join(choosen, false);
			LGCustomItems.updateItem(choosen);
		}else
			for(LGPlayer player : getPlayers())
				player.sendMessage("§6Personne n'a été infecté.");
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDayStart(LGNightEndEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer player : getGame().getAlive()) {
				if(player.getCache().getBoolean(CacheType.JUST_VAMPIRE)) {
					player.getCache().remove(CacheType.JUST_VAMPIRE);
					for(LGPlayer lgp : getGame().getInGame()) {
						if(lgp.getRoleType() == RoleType.VAMPIRE)
							lgp.sendMessage("§7§l"+player.getName()+"§6 s'est transformé en §5§lVampire§6...");
						else
							lgp.sendMessage("§6Quelqu'un s'est transformé en §5§lVampire§6...");
					}

					if(getGame().checkEndGame())
						e.setCancelled(true);
				}
			}
	}

/*	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSkinChange(LGSkinLoadEvent e) {
		if(e.getGame() == getGame())
			if(getPlayers().contains(e.getPlayer()) && getPlayers().contains(e.getTo()) && showSkins) {
				e.getProfile().getProperties().removeAll("textures");
				e.getProfile().getProperties().put("textures", LGCustomSkin.WEREWOLF.getProperty());
			}
	}*/
	@EventHandler
	public void onGameEnd(LGGameEndEvent e) {
		if(e.getGame() == getGame() && e.getWinType() == LGWinType.VAMPIRE)
			for(LGPlayer lgp : getGame().getInGame())
				if(lgp.getRoleWinType() == RoleWinType.VAMPIRE)//Changed to wintype
					e.getWinners().add(lgp);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onUpdatePrefix (LGUpdatePrefixEvent e) {
		if(e.getGame() == getGame())
			if(getPlayers().contains(e.getTo()) && getPlayers().contains(e.getPlayer()))
				e.setPrefix(e.getPrefix()+"§5");
	}

}