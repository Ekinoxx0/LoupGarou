package dev.loupgarou.loupgarou.classes.chat;

import java.util.HashMap;
import java.util.Map.Entry;

import dev.loupgarou.loupgarou.MainLg;
import dev.loupgarou.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LGChat {
	@Getter private final HashMap<LGPlayer, LGChatCallback> viewers = new HashMap<LGPlayer, LGChatCallback>();
	@Getter private final LGChatCallback defaultCallback;
	private final String chatName;
	
	public static interface LGChatCallback{
		public String receive(LGPlayer sender, String message);
		public default String send(LGPlayer sender, String message) {return null;};
	}
	

	public void sendMessage(LGPlayer sender, String message) {
		MainLg.debug("[" + this.chatName + "] " + sender.getName() + " : " + message);
		String sendMessage = getViewers().get(sender).send(sender, message);
		for(Entry<LGPlayer, LGChatCallback> entry : viewers.entrySet()) {
			entry.getKey().sendMessage(sendMessage != null ? sendMessage : entry.getValue().receive(sender, message));
		}
	}

	public void join(LGPlayer player, LGChatCallback callback) {
		MainLg.debug("[" + this.chatName + "] (JOIN) " + player.getName() + " ! ");
		if(getViewers().containsKey(player))
			getViewers().replace(player, callback);
		else
			getViewers().put(player, callback);
	}
	public void leave(LGPlayer player) {
		MainLg.debug("[" + this.chatName + "] (LEAVE) " + player.getName() + " ! ");
		getViewers().remove(player);
	}
}
