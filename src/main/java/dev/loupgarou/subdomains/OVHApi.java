package dev.loupgarou.subdomains;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import dev.loupgarou.discord.DiscordManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OVHApi {
	
	private static String appKey;
	private static String appSecret;
	private static String consumerKey;

	static {
		try {
			InputStream inputStream = DiscordManager.class.getResourceAsStream("/ovh_api");
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
			
			String[] lines = writer.toString().split("\n");
			for(String line : lines) {
				System.out.println("a:" + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static enum Method {
		GET,
		POST,
		PUT,
		DELETE;
	}
	
	private static final Gson GSON = new Gson();
	private static final String URL = "https://eu.api.ovh.com/1.0";
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final JsonParser JSON_PARSER = new JsonParser();
	private static final JsonElement EMPTY = JSON_PARSER.parse("{}");

	private final OkHttpClient client;

	public OVHApi() {
		this.client = new OkHttpClient.Builder().build();
	}
	
	public JsonElement domainZone() {
		return this.request(Method.GET, "/domain/zone", null);
	}
	
	public JsonElement domainZone(@NonNull String zoneName) {
		if(zoneName.contains("/")) throw new IllegalArgumentException();
		return this.request(Method.GET, "/domain/zone/" + zoneName, null);
	}
	
	public JsonElement domainZoneRefresh(@NonNull String zoneName) {
		if(zoneName.contains("/")) throw new IllegalArgumentException();
		return this.request(Method.GET, "/domain/zone/" + zoneName + "/refresh", null);
	}
	
	public JsonElement domainZoneRecord(@NonNull String zoneName) {
		if(zoneName.contains("/")) throw new IllegalArgumentException();
		return this.request(Method.GET, "/domain/zone/" + zoneName + "/record", null);
	}
	
	public JsonElement domainZoneRecord(@NonNull String zoneName, long id) {
		if(zoneName.contains("/")) throw new IllegalArgumentException();
		return this.request(Method.GET, "/domain/zone/" + zoneName + "/record/" + id, null);
	}
	
	public JsonElement domainZoneRecordDelete(@NonNull String zoneName, long id) {
		if(zoneName.contains("/")) throw new IllegalArgumentException();
		return this.request(Method.DELETE, "/domain/zone/" + zoneName + "/record/" + id, null);
	}
	
	@AllArgsConstructor
	public static class ZoneRecord {
		public String fieldType;
		public String subDomain;
		public String target;
	}
	
	public JsonElement domainZoneRecordPut(@NonNull String zoneName, @NonNull ZoneRecord record) {
		if(zoneName.contains("/")) throw new IllegalArgumentException();
		return this.request(Method.POST, "/domain/zone/" + zoneName + "/record", GSON.toJson(record));
	}
	
	private JsonElement request(@NonNull Method method, @NonNull String endpoint, @Nullable String body) {
		if (!endpoint.startsWith("/"))
			endpoint = "/" + endpoint;
		if (!endpoint.endsWith("/"))
			endpoint = endpoint + "/";

		URL url;
		try {
			url = new URL(URL + endpoint);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return EMPTY;
		}
		long time = System.currentTimeMillis() / 1000;

		String toSign = appSecret + "+" + consumerKey + "+" + method + "+" + url + "+" + (body == null ? "" : body) + "+" + time;
		String signature = "$1$" + hashSHA1(toSign);

		Request request = new Request.Builder().url(url.toString())
				.addHeader("X-Ovh-Application", appKey)
				.addHeader("X-Ovh-Consumer", consumerKey)
				.addHeader("X-Ovh-Signature", signature)
				.addHeader("X-Ovh-Timestamp", time + "")
				.method(method.toString(), body == null ? null : RequestBody.create(JSON, body))
				.build();

		Response r;
		try {
			r = client.newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
			return EMPTY;
		}
		
		try {
			return JSON_PARSER.parse(r.body().string());
		} catch(JsonParseException | IOException e) {
			e.printStackTrace();
			return EMPTY;
		}
	}

	private static String hashSHA1(String text) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

}
