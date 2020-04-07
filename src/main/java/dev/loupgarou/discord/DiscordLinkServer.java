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

import javax.security.auth.login.LoginException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.Gson;

import dev.loupgarou.MainLg;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.utils.RandomString;
import dev.loupgarou.utils.CommonText.PrefixType;
import lombok.NonNull;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/*
 * TODO verify woring
 */
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

				System.out.println(readed);//TODO rm
				while(inputReader.hasNext()) {
					System.out.println(inputReader.next());//TODO rm
				}
				System.out.println("url:" + url);//TODO rm
				if(url.contains("#")) {//Contains wtf arguments
					String argsRaw = url.split("[#]")[1];
					url = url.split("[#]")[0];
					System.out.println(argsRaw);//TODO rm
					
					for(String arg : argsRaw.split("[&]")) {
						System.out.println(arg);//TODO rm
						if(arg.contains("="))
							arguments.put(arg.split("[=]")[0], arg.split("[=]")[1]);
					}
				}

				switch (url) { // Switch between different endpoints
				
				case "/discord":
					if(arguments.containsKey("access_token") && arguments.containsKey("state")) {
						link(arguments.get("access_token"), arguments.get("state"));
						sendText(out, "Fermez la page.");
					} else {
						throw new IllegalStateException(new Gson().toJson(arguments));
					}
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
		String hash = RandomString.toSHA1(lgp.getName());
		links.put(hash, lgp);
		String link = "https://discordapp.com/api/oauth2/authorize?response_type=token&state=" + hash + "&client_id=690997265384603830&redirect_uri=http%3A%2F%2Fwondalia.com%3A25564%2Fdiscord&scope=identify%20guilds";
		
		lgp.sendMessage(PrefixType.DISCORD + "§9§lLiaison Discord : ");
		lgp.sendMessage(PrefixType.DISCORD + "§9" + link);
	}

	private void link(@NonNull String access_token, @NonNull String state) {
		if(!links.containsKey(state)) throw new IllegalArgumentException("Mauvais state.");
		
		LGPlayer lgp = links.get(state);
		JDABuilder userJdaBuilder = new JDABuilder(AccountType.CLIENT);
		userJdaBuilder.setToken(access_token);
		
		try {
			JDA userJDA = userJdaBuilder.build();
			long userId = userJDA.getSelfUser().getIdLong();
			
			linkUserDiscord(lgp, userId);
			
			links.remove(state);
		} catch (LoginException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Login faux.");
		}
	}
	
	private void linkUserDiscord(@NonNull LGPlayer lgp, long userId) {
		if(lgp.getPlayer() == null) return;
		config.set(lgp.getPlayer().getUniqueId().toString(), userId);
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