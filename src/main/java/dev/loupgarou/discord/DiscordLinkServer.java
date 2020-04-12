package dev.loupgarou.discord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.utils.CommonText.PrefixType;
import dev.loupgarou.utils.RandomString;
import dev.loupgarou.utils.TComponent;
import dev.loupgarou.utils.TComponent.HEvent;
import lombok.NonNull;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscordLinkServer {

	private static final File file = new File(MainLg.getInstance().getDataFolder(), "discordlink.yml");
	public static int PORT = 25564;

	private final ServerSocket server;
	private boolean enabled;
	private FileConfiguration config;

	public DiscordLinkServer() throws IOException, BindException {
		this.enabled = true;
		if(!file.exists()) file.createNewFile();
		this.config = YamlConfiguration.loadConfiguration(file);
		this.server = new ServerSocket(PORT);
		PORT = this.server.getLocalPort();
		System.out.println("Listening on port " + server.getLocalPort());

		new Thread(() -> {
			while (DiscordLinkServer.this.enabled) {
				try {
					ConnectionThread thread = new ConnectionThread(server.accept());
					thread.start();
				} catch (SocketException ex) {
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			try {
				DiscordLinkServer.this.server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private class ConnectionThread extends Thread {
		private Socket client;

		public ConnectionThread(Socket client) {
			this.client = client;
		}

		public void run() {
			InputStream input = null;
			OutputStream output = null;
			Scanner inputReader = null;

			try {
				input = this.client.getInputStream();
				output = this.client.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			PrintStream out = new PrintStream(output);

			try {
				inputReader = new Scanner(input);
				inputReader.useDelimiter("\n");
				if (!inputReader.hasNext()) {
					return;
				}

				String readed = inputReader.next();
				String url = readed.split(" ")[1];
				HashMap<String, String> arguments = new HashMap<String, String>();

				
				if(url.contains("?")) {
					String argsRaw = url.split("[?]")[1];
					url = url.split("[?]")[0];
					
					for(String arg : argsRaw.split("[&]")) {
						if(arg.contains("="))
							arguments.put(arg.split("[=]")[0], arg.split("[=]")[1]);
					}
				}

				switch (url) {
				
				case "/discord":
					if(arguments.containsKey("code") && arguments.containsKey("state")) {
						link(arguments.get("code"), arguments.get("state"));
						sendText(out, "Fermez la page.");
					} else {
						throw new IllegalStateException("Doesn't contains code or state : " + new Gson().toJson(arguments));
					}
					break;
				case "/favicon.ico":
					out.println("HTTP/1.0 404 Not Found");
					out.println("");
					out.println("NOT FOUND");
					out.println("");
					out.flush();
					break;
					
				default:
					throw new IllegalAccessException("Unknown endpoint: " + url);
				
				}

				inputReader.close();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				out.println("HTTP/1.0 404 Not Found");
				out.println("");
				out.println("NOT FOUND : " + e.getMessage());
				out.println("");
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
				out.println("HTTP/1.0 500 Internal Server Error");
				out.println("");
				out.println("" + e.getMessage());
				out.println("");
				out.flush();
			} finally {
				if (inputReader != null)
					inputReader.close();

				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void sendText(PrintStream out, String text) {
			out.println("HTTP/1.0 200 OK");
			out.println("Content-Type:application/json");
			out.println("");
			out.println(text);
			out.println("");
		}

	}
	
	private HashMap<String, LGPlayer> links = new HashMap<String, LGPlayer>();
	
	public void generateLink(@NonNull LGPlayer lgp) {
		String hash = RandomString.toSHA1(RandomString.toSHA1(lgp.getName()));
		links.put(hash, lgp);
		String link = "https://discordapp.com/api/oauth2/authorize?response_type=code&state=" + hash + "&client_id=" + + DiscordManager.CLIENT_ID + "&redirect_uri=http%3A%2F%2Fwondalia.com%3A25564%2Fdiscord&scope=identify%20guilds";
		
		if(this.getLinked(lgp) > 0) {
			lgp.sendMessage(PrefixType.DISCORD + "§cVous êtes déjà lié ! Utilisez ce lien seulement si vous changez de compte.");
		}
		
		lgp.sendMessage(new TComponent(PrefixType.DISCORD + "§9"), 
				new TComponent("§aCliquez §lICI§a pour lier votre compte")
					.setHoverEvent(new HEvent(HoverEvent.Action.SHOW_TEXT, "§8En cliquant sur ce lien, \n§8vous serez redirigé vers discord où vous pourrez autoriser \n§8la permission de connaitre votre tag discord."))
					.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link))
					);
	}
	
	private static final JsonParser parser = new JsonParser();
	private void link(@NonNull String code, @NonNull String state) throws Exception {
		if(!links.containsKey(state)) throw new IllegalArgumentException("Mauvais state.");
		
		LGPlayer lgp = links.get(state);
		OkHttpClient client = new OkHttpClient();

	    RequestBody formBody = new FormBody.Builder()
	      .add("username", "test")
	      .add("client_id", DiscordManager.CLIENT_ID + "")
		  .add("client_secret", DiscordManager.SECRET)
		  .add("grant_type", "authorization_code")
		  .add("code", code)
		  .add("redirect_uri", "http://wondalia.com:25564/discord")
		  .add("scope", "identify guilds")
	      .build();
	 
	    Request request = new Request.Builder()
	      .url("https://discordapp.com/api/v6/oauth2/token")
	      .post(formBody)
	      .build();
	 
	    Response response = client.newCall(request).execute();
	    int codeResponse = response.code();
	    String body = response.body().string();
	    response.close();
	    if(!response.isSuccessful()) throw new Exception("Unable to retrieve token from code : " + codeResponse + " - " + body);
	    JsonObject responseJson = parser.parse(body).getAsJsonObject();
	    if(!responseJson.has("access_token")) throw new Exception("No access token in " + responseJson.toString());
	    
		String token = responseJson.get("access_token").getAsString();
		MainLg.debug("DISCORD TOKEN : " + lgp.getName() + ":" + token);

	    Request userIdResquest = new Request.Builder()
	      .url("https://discordapp.com/api/v6/users/@me")
	      .header("Authorization", responseJson.get("token_type").getAsString() + " " + token)
	      .build();
	 
	    Response userIdResponse = client.newCall(userIdResquest).execute();
	    int userIdCode = userIdResponse.code();
	    String userIdBody = userIdResponse.body().string();
	    userIdResponse.close();
	    if(!userIdResponse.isSuccessful()) throw new Exception("Unable to retrieve userId from token : " + userIdCode + " - " + userIdBody);
	    JsonObject responseUserIdJson = parser.parse(userIdBody).getAsJsonObject();
	    if(!responseUserIdJson.has("id")) throw new Exception("No id in " + responseUserIdJson.toString());
		
		linkUserDiscord(lgp, Long.parseLong(responseUserIdJson.get("id").getAsString()));
			
		links.remove(state);
	}
	
	private void linkUserDiscord(@NonNull LGPlayer lgp, long userId) {
		if(lgp.getPlayer() == null) return;
		lgp.sendMessage("§2Lié à Discord avec succès !");
		config.set(lgp.getPlayer().getUniqueId().toString(), userId);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public long getLinked(@NonNull LGPlayer lgp) {
		if(lgp.getPlayer() == null) return -1;
		if(!config.contains(lgp.getPlayer().getUniqueId().toString())) return -1;
		return config.getLong(lgp.getPlayer().getUniqueId().toString());
	}

	public void close() {
		try {
			this.server.close();
		} catch (IOException e) {}
		this.enabled = false;
	}
}