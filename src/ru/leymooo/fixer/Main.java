package ru.leymooo.fixer;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.catcoder.updatechecker.PluginUpdater;
import me.catcoder.updatechecker.UpdaterException;
import me.catcoder.updatechecker.UpdaterResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    private boolean useArtMap;
    private ItemChecker checker;
    private ProtocolManager manager;
    public String version;
    private final PluginUpdater updater = new PluginUpdater(this, "Dimatert9", "ItemFixer");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        checkNewConfig();
        PluginManager pmanager = Bukkit.getPluginManager();
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        useArtMap = initArtMapApi();
        checker = new ItemChecker(this);
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new NBTListener(this, version));
        pmanager.registerEvents(new NBTBukkitListener(this), this);
        pmanager.registerEvents(new TextureFix(version, this), this);
        if (getConfig().getBoolean("check-update")) checkUpdate();
        Bukkit.getConsoleSender().sendMessage("§b[ItemFixer] §aenabled");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        manager.removePacketListeners(this);
        NBTListener.cancel.invalidateAll();
        NBTListener.cancel = null;
        checker = null;
        manager = null;
    }

    public boolean checkItem(ItemStack stack, Player p) {
        return checker.isHackedItem(stack, p);
    }

    private void checkNewConfig() {
        if (!getConfig().isSet("ignored-tags")) {
            File config = new File(getDataFolder(), "config.yml");
            config.delete();
            saveDefaultConfig();
        }
        if (getConfig().isSet("max-pps")) {
            getConfig().set("max-pps", null);
            getConfig().set("max-pps-kick-msg", null);
            saveConfig();
        }
    }

    public boolean isUnsupportedVersion() {
        return version.startsWith("v1_11_R") || version.startsWith("v1_12_R") || version.startsWith("v1_13_R");
    }

    private boolean initArtMapApi() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ArtMap");
        return plugin != null && plugin.isEnabled();
    }

    private void checkUpdate() {
        new Thread(() -> {
            try {
                UpdaterResult result = updater.checkUpdates();
                if (result.hasUpdates()) {
                    Bukkit.getConsoleSender().sendMessage("§b[ItemFixer] §cНовое обновление найдено! | The new version found!");
                } else {
                    Bukkit.getConsoleSender().sendMessage("§b[ItemFixer] §aОбновлений не найдено. | No updates found.");
                }
            } catch (UpdaterException e) {
                e.print();
            }
        }).start();
    }
}
