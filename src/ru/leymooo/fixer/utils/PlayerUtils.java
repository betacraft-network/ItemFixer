package ru.leymooo.fixer.utils;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class PlayerUtils {

    private static boolean isNewProtocolLib = false;

    static {
        try {
            Class.forName("com.comphenix.protocol.injector.server.TemporaryPlayer");
            isNewProtocolLib = true;
        } catch (ClassNotFoundException e) {
        }
    }

    public static Player getPlayerFromEvent(PacketEvent event) {
        Player eventPlayer = event.getPlayer();

        if (eventPlayer == null || !eventPlayer.isOnline()) return null;

        if (isNewProtocolLib && event.isPlayerTemporary()) return null;

        String playerName = eventPlayer.getName();

        if (playerName == null) return null;

        Player bukkitPlayer = Bukkit.getPlayerExact(playerName);

        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) return null;

        return bukkitPlayer;
    }
}
