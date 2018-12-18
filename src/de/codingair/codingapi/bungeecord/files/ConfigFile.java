package de.codingair.codingapi.bungeecord.files;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

/**
 * Removing of this disclaimer is forbidden.
 *
 * @author codingair
 * @verions: 1.0.0
 **/

public class ConfigFile {
	private String name;
	private String path;
	private Plugin plugin;
	
	private Configuration config;
	
	public ConfigFile(String name, String path, Plugin plugin) {
		this.name = name;
		this.path = path;
		this.plugin = plugin;

		this.load();
		this.save();
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
	private File getDataFolder() {
		return plugin.getDataFolder();
	}
	
	public void load() {
		if(getDataFolder() == null || !getDataFolder().exists()) getDataFolder().mkdir();
		
		File file = new File(getDataFolder() + this.path, this.name + ".yml");
		
		if(!file.exists()) {
			try {
				file.createNewFile();
				try(InputStream in = plugin.getResourceAsStream(this.name + ".yml");
					OutputStream out = new FileOutputStream(file)) {
					copy(in, out);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private long copy(InputStream from, OutputStream to) throws IOException {
		if(from == null) return -1;
		if(to == null) throw new NullPointerException();

		byte[] buf = new byte[4096];
		long total = 0L;

		while(true) {
			int r = from.read(buf);
			if(r == -1) {
				return total;
			}

			to.write(buf, 0, r);
			total += (long) r;
		}
	}
	
	public void save() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), this.name + ".yml"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public Configuration getConfig() {
		if(config == null) this.load();
		return config;
	}
}
