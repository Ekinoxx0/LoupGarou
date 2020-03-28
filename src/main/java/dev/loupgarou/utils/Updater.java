package dev.loupgarou.utils;

import java.io.IOException;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.loupgarou.MainLg;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Updater {
	
	private final String URL = "https://api.github.com/repos/Ekinoxx0/LoupGarou/releases/latest";
	private final JsonParser json = new JsonParser();
	private final int currentVersion;
	private final MainLg lg;
	
	public Updater(@Nonnull MainLg lg) {
		this.lg = lg;
		this.currentVersion = this.parseVersion(lg.getDescription().getVersion());
		Request request = new Request.Builder().url(URL).build();
		
		Response r = null;
		try {
			r = new OkHttpClient().newCall(request).execute();
			if(!r.isSuccessful()) throw new IOException("API returned code : " + r.code());
			
			this.parseResponse(r.body().string());
		} catch (Exception e) {
			lg.getLogger().log(Level.WARNING, "Unable to update plugin due to Exception : " + e.getMessage());
		} finally {
			if(r != null) r.close();
		}
	}
	
	private void parseResponse(@Nonnull String response) {
		JsonObject jsonRoot = json.parse(response).getAsJsonObject();
		
		String tag = jsonRoot.get("tag_name").getAsString();
		String name = jsonRoot.get("name").getAsString();
		boolean prerelease = jsonRoot.get("prerelease").getAsBoolean();
		boolean draft = jsonRoot.get("draft").getAsBoolean();
		String published_at = jsonRoot.get("published_at").getAsString();
		//String body = jsonRoot.get("body").getAsString();
		
		if(prerelease || draft)
			return;
		
		if(this.currentVersion < this.parseVersion(tag)) {
			this.lg.getLogger().warning("Nouvelle mise à jour disponible (" + tag + ") : " + name);
			this.lg.getLogger().warning("Publié le : " + published_at);
		}
	}
	
	/**
	 * v1.2.3 = 1002003
	 */
	private int parseVersion(@Nonnull String v) throws IllegalArgumentException {
		if(!v.startsWith("v"))
			throw new IllegalArgumentException("Version " + v + " doesn't validate format 'v0.0.0' : No v");
		
		v = v.substring(1);
		
		if(!v.contains("."))
			throw new IllegalArgumentException("Version " + v + " doesn't validate format 'v0.0.0' : No .");
		
		String[] nList = v.split("[.]");
		
		if(nList.length < 3)
			throw new IllegalArgumentException("Version " + v + " doesn't validate format 'v0.0.0' : n < 3 (" + nList.length + ")");
		
		int t = 0;
		for (int i = 0; i < nList.length; i++)
			t += Integer.parseInt(nList[i]) * Math.pow(100, ((nList.length-1) - i));
		
		return t;
	}
	
}
