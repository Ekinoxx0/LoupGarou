package dev.loupgarou.classes.chat;

import dev.loupgarou.classes.LGPlayer;

public class LGNoChat extends LGChat{
	public LGNoChat() {
		super(null, "LGNoChat");
	}

	public void sendMessage(LGPlayer sender, String message) {}

	public void join(LGPlayer player, LGChatCallback callback) {
		
	}
	public void leave(LGPlayer player) {
		
	}
}
