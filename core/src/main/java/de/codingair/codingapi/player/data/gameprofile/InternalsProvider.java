package de.codingair.codingapi.player.data.gameprofile;

import de.codingair.codingapi.player.data.Skin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

abstract class InternalsProvider {

	abstract void updateGameProfile(Plugin plugin, Player p, Skin skin, String nickName);

	abstract void updateOtherGameProfile(Plugin plugin, Player p, Player other, Skin skin, String nickName);

}
