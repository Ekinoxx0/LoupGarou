package dev.loupgarou.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
					LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
					if(lgp.getGame() != null && lgp.getGame().getTime() != time.getTimeOfDay())
						e.setCancelled(true);
				}
			}
		);
		//Éviter que les gens s'entendent quand ils se sélectionnent et qu'ils sont trop proche
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent e) {
					WrapperPlayServerNamedSoundEffect sound = new WrapperPlayServerNamedSoundEffect(e.getPacket());
					if(sound.getSoundEffect() == Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)
						e.setCancelled(true);
			}
		}
		);
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
			@Override
			public void onPacketSending(PacketEvent e) {
				LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
				WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(e.getPacket());
				List<PlayerInfoData> datas = new ArrayList<PlayerInfoData>();
				
				for(PlayerInfoData data : info.getData()) {
					Player target = Bukkit.getPlayer(data.getProfile().getUUID());
					if(target == null) continue;
					
					LGPlayer lgpT = LGPlayer.thePlayer(target);
					if(lgp.getGame() != null && lgp.getGame() == lgpT.getGame()) {
						LGUpdatePrefixEvent prefixEvent = new LGUpdatePrefixEvent(lgp.getGame(), lgpT, lgp, "");
						WrappedChatComponent displayName = data.getDisplayName();
						Bukkit.getPluginManager().callEvent(prefixEvent);
						if(prefixEvent.getPrefix().length() > 0) {
								try {
								if(displayName != null) {
									JSONObject obj = (JSONObject) new JSONParser().parse(displayName.getJson());
									displayName = WrappedChatComponent.fromText(prefixEvent.getPrefix()+obj.get("text"));
								} else
									displayName = WrappedChatComponent.fromText(prefixEvent.getPrefix()+target.getDisplayName());
							} catch (ParseException pe) {
								pe.printStackTrace();
							}
						}
						LGSkinLoadEvent skinLoadEvent = new LGSkinLoadEvent(lgpT.getGame(), lgpT, lgp, data.getProfile());
						Bukkit.getPluginManager().callEvent(skinLoadEvent);
						datas.add(new PlayerInfoData(skinLoadEvent.getProfile(), data.getLatency(), data.getGameMode(), displayName));
					}else
						datas.add(data);
				}
				info.setData(datas);
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.SCOREBOARD_TEAM) {
			@Override
			public void onPacketSending(PacketEvent e) {
				LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam(e.getPacket());
				team.setColor(ChatColor.GRAY);
				Player target = Bukkit.getPlayer(team.getName());
				if(target == null) return;
				LGPlayer lgpTarget = LGPlayer.thePlayer(target);
				if(lgp.getGame() != null && lgp.getGame() == lgpTarget.getGame()) {
					LGUpdatePrefixEvent prefixEvent = new LGUpdatePrefixEvent(lgp.getGame(), lgpTarget, lgp, "");
					Bukkit.getPluginManager().callEvent(prefixEvent);
					if(prefixEvent.getPrefix().length() > 0)
						team.setPrefix(WrappedChatComponent.fromText(prefixEvent.getPrefix()));
					else
						team.setPrefix(WrappedChatComponent.fromText("§f"));
				}
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent e) {
				LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
				if(lgp.getGame() == null) return;
				WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment(e.getPacket());
				if(equip.getSlot() == ItemSlot.OFFHAND && equip.getEntityID() != lgp.getPlayer().getEntityId())
					equip.setItem(new ItemStack(Material.AIR));
			}
		});
	}

}
