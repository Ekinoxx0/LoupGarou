package dev.loupgarou.utils;

import dev.loupgarou.utils.CommonText.PrefixType;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TComponent {

	private final TextComponent text;

	public TComponent(@NonNull PrefixType value) {
		this.text = new TextComponent(value.toString());
	}
	
	public TComponent(@NonNull String value) {
		this.text = new TextComponent(value);
	}
	
	public TComponent(@NonNull TComponent... values) {
		this.text = new TextComponent();
		for(TComponent t : values)
			this.text.addExtra(t.build());
	}

	public TComponent addExtra(BaseComponent component) {
		text.addExtra(component);
		return this;
	}

	public TComponent setClickEvent(ClickEvent click) {
		text.setClickEvent(click);
		return this;
	}

	public TComponent setHoverEvent(HEvent hover) {
		text.setHoverEvent(hover.build());
		return this;
	}

	public TComponent setHoverEvent(HoverEvent hover) {
		text.setHoverEvent(hover);
		return this;
	}
	
	public TextComponent build() {
		return text;
	}
	
	/////
	
	public static class HEvent {
		private HoverEvent he;
		public HEvent(HoverEvent.Action a, String t) {
			this.he = new HoverEvent(a, new BaseComponent[] {new TextComponent(t)});
		}
		public HoverEvent build() {return he;}
	}
}
