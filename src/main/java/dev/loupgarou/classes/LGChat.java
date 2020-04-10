package dev.loupgarou.classes;

import java.util.HashMap;
import java.util.Map.Entry;

import dev.loupgarou.MainLg;
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
		MainLg.debug(sender.getGame() != null ? sender.getGame().getKey() : "", "[" + this.chatName + "] " + sender.getName() + " : " + message);
		String sendMessage = getViewers().get(sender).send(sender, message);
		for(Entry<LGPlayer, LGChatCallback> entry : viewers.entrySet()) {
			entry.getKey().sendMessage(sendMessage != null ? sendMessage : entry.getValue().receive(sender, message));
		}
	}

	public void join(LGPlayer player, LGChatCallback callback) {
		MainLg.debug(player.getGame() != null ? player.getGame().getKey() : "", "[" + this.chatName + "] (JOIN) " + player.getName() + " ! ");
		getViewers().put(player, callback);
	}
	
	public void leave(LGPlayer player) {
		MainLg.debug(player.getGame() != null ? player.getGame().getKey() : "", "[" + this.chatName + "] (LEAVE) " + player.getName() + " ! ");
		getViewers().remove(player);
	}
	
	@Override
	public String toString() {
		return "LGChat(" + chatName + ")";
	}
}
