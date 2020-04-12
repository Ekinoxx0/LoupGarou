package dev.loupgarou.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.other.LGSkinLoadEvent;
import dev.loupgarou.events.other.LGUpdatePrefixEvent;
import dev.loupgarou.packetwrapper.WrapperPlayServerEntityEquipment;
import dev.loupgarou.packetwrapper.WrapperPlayServerNamedSoundEffect;
import dev.loupgarou.packetwrapper.WrapperPlayServerPlayerInfo;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.packetwrapper.WrapperPlayServerUpdateTime;

public class ProtocolListener {

	public ProtocolListener(MainLg mainLg) {
	    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_TIME) {
				@Override
				public void onPacketSending(PacketEvent e) {
					WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime(e.getPacket());
					LGPlayer lgpTo = LGPlayer.thePlayer(e.getPlayer());
					if(lgpTo.getGame() != null && lgpTo.getGame().getTime() != time.getTimeOfDay())
						e.setCancelled(true);
				}
			}
		);
		
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override //Éviter que les gens s'entendent quand ils se sélectionnent et qu'ils sont trop proche
			public void onPacketSending(PacketEvent e) {
					WrapperPlayServerNamedSoundEffect sound = new WrapperPlayServerNamedSoundEffect(e.getPacket());
					if(sound.getSoundEffect() == Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)
						e.setCancelled(true);
			}
		});
		
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer lgpTo = LGPlayer.thePlayer(event.getPlayer());
				WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event.getPacket());
				List<PlayerInfoData> datas = new ArrayList<PlayerInfoData>();
				
				for(PlayerInfoData data : info.getData()) {
					Player target = Bukkit.getPlayer(data.getProfile().getUUID());
					if(target == null) continue;
					
					LGPlayer lgpInfo = LGPlayer.thePlayer(target);
					
					LGUpdatePrefixEvent prefixEvent = new LGUpdatePrefixEvent(lgpTo.getGame(), lgpInfo, lgpTo);
					Bukkit.getPluginManager().callEvent(prefixEvent);
					
					WrappedChatComponent displayName = WrappedChatComponent.fromText(prefixEvent.getPrefix() + "§" + prefixEvent.getColor().getChar() + lgpInfo.getName());
					
					LGSkinLoadEvent skinEvent = new LGSkinLoadEvent(lgpInfo.getGame(), lgpInfo, lgpTo, data.getProfile());
					Bukkit.getPluginManager().callEvent(skinEvent);
					datas.add(new PlayerInfoData(skinEvent.getProfile(), data.getLatency(), data.getGameMode(), displayName));
				}
				info.setData(datas);
			}
		});
		
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.SCOREBOARD_TEAM) {
			@Override
			public void onPacketSending(PacketEvent e) {
				LGPlayer lgpTo = LGPlayer.thePlayer(e.getPlayer());
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam(e.getPacket());
				Player target = Bukkit.getPlayer(team.getName());
				if(target == null) return;
				
				LGPlayer lgpTarget = LGPlayer.thePlayer(target);
				LGUpdatePrefixEvent prefixEvent = new LGUpdatePrefixEvent(lgpTo.getGame(), lgpTarget, lgpTo);
				Bukkit.getPluginManager().callEvent(prefixEvent);
				//MainLg.debug(team.getMode() + "|" + target.getName() + " have a team to display to " + lgpTo.getName() + " : " + prefixEvent.getPrefix().replace("§", "&"));
				team.setPrefix(WrappedChatComponent.fromText(prefixEvent.getPrefix()));
				team.setColor(prefixEvent.getColor());
			}
		});
		
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent e) {
				LGPlayer lgpTo = LGPlayer.thePlayer(e.getPlayer());
				if(lgpTo.getGame() == null) return;
				WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment(e.getPacket());
				if(equip.getSlot() == ItemSlot.OFFHAND && equip.getEntityID() != lgpTo.getPlayer().getEntityId())
					equip.setItem(new ItemStack(Material.AIR));
			}
		});
	}

}
