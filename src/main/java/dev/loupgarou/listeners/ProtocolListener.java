package dev.loupgarou.listeners;

import java.util.ArrayList;

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
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.LGSkinLoadEvent;
import dev.loupgarou.events.LGUpdatePrefixEvent;
import dev.loupgarou.packetwrapper.WrapperPlayServerEntityEquipment;
import dev.loupgarou.packetwrapper.WrapperPlayServerNamedSoundEffect;
import dev.loupgarou.packetwrapper.WrapperPlayServerPlayerInfo;
import dev.loupgarou.packetwrapper.WrapperPlayServerScoreboardTeam;
import dev.loupgarou.packetwrapper.WrapperPlayServerUpdateHealth;
import dev.loupgarou.packetwrapper.WrapperPlayServerUpdateTime;

public class ProtocolListener {

	public ProtocolListener(MainLg mainLg) {
	    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_TIME) {
				@Override
				public void onPacketSending(PacketEvent event) {
					WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime(event.getPacket());
					LGPlayer lgp = LGPlayer.thePlayer(event.getPlayer());
					if(lgp.getGame() != null && lgp.getGame().getTime() != time.getTimeOfDay())
						event.setCancelled(true);
				}
			}
		);
		//Éviter que les gens s'entendent quand ils se sélectionnent et qu'ils sont trop proche
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
					WrapperPlayServerNamedSoundEffect sound = new WrapperPlayServerNamedSoundEffect(event.getPacket());
					if(sound.getSoundEffect() == Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)
						event.setCancelled(true);
			}
		}
		);
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event.getPacket());
				ArrayList<PlayerInfoData> datas = new ArrayList<PlayerInfoData>();
				for(PlayerInfoData data : info.getData()) {
					LGPlayer lgp = LGPlayer.thePlayer(Bukkit.getPlayer(data.getProfile().getUUID()));
					if(player.getGame() != null && player.getGame() == lgp.getGame()) {
						LGUpdatePrefixEvent evt2 = new LGUpdatePrefixEvent(player.getGame(), lgp, player, "");
						WrappedChatComponent displayName = data.getDisplayName();
						Bukkit.getPluginManager().callEvent(evt2);
						if(evt2.getPrefix().length() > 0) {
								try {
								if(displayName != null) {
									JSONObject obj = (JSONObject) new JSONParser().parse(displayName.getJson());
									displayName = WrappedChatComponent.fromText(evt2.getPrefix()+obj.get("text"));
								} else
									displayName = WrappedChatComponent.fromText(evt2.getPrefix()+data.getProfile().getName());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						LGSkinLoadEvent evt = new LGSkinLoadEvent(lgp.getGame(), lgp, player, data.getProfile());
						Bukkit.getPluginManager().callEvent(evt);
						datas.add(new PlayerInfoData(evt.getProfile(), data.getLatency(), data.getGameMode(), displayName));
					}else
						datas.add(data);
				}
				info.setData(datas);
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_HEALTH) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				if(player.getGame() != null && player.getGame().isStarted()) {
					WrapperPlayServerUpdateHealth health = new WrapperPlayServerUpdateHealth(event.getPacket());
					health.setFood(20);
				}
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.SCOREBOARD_TEAM) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam(event.getPacket());
				team.setColor(ChatColor.WHITE);
				Player other = Bukkit.getPlayer(team.getName());
				if(other == null)return;
				LGPlayer lgp = LGPlayer.thePlayer(other);
				if(player.getGame() != null && player.getGame() == lgp.getGame()) {
					LGUpdatePrefixEvent evt2 = new LGUpdatePrefixEvent(player.getGame(), lgp, player, "");
					Bukkit.getPluginManager().callEvent(evt2);
					if(evt2.getPrefix().length() > 0)
						team.setPrefix(WrappedChatComponent.fromText(evt2.getPrefix()));
					else
						team.setPrefix(WrappedChatComponent.fromText("§f"));
				}
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				if(player.getGame() != null) {
					WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment(event.getPacket());
					if(equip.getSlot() == ItemSlot.OFFHAND && equip.getEntityID() != player.getPlayer().getEntityId())
						equip.setItem(new ItemStack(Material.AIR));
				}
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(mainLg, ListenerPriority.NORMAL, PacketType.Play.Server.ANIMATION) {
			@Override
			public void onPacketSending(PacketEvent event) {
				LGPlayer player = LGPlayer.thePlayer(event.getPlayer());
				if(player.getGame() != null) {
					event.setCancelled(player.getGame().isHideVoteExtra() || player.getGame().isHideVote());
				}
			}
		});
	}

}
