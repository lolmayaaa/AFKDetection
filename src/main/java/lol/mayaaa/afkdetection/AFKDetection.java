package lol.mayaaa.afkdetection;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public final class AFKDetection extends JavaPlugin {

    private int afktime;
    private String kickreason;
    private boolean shouldannounce;
    private List<Integer> warnings;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File cfgfile = new File(getDataFolder(), "config.yml");
        if (!cfgfile.exists()) {
            createcfg(cfgfile);
        }

        reloadConfig();

        afktime = getConfig().getInt("afktimer", 300);
        kickreason = getConfig().getString("reason", "Stop being AFK!");
        shouldannounce = getConfig().getBoolean("announce", true);
        warnings = getConfig().getIntegerList("warnings");

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new DetectingAFK(this), 20L, 20L);
        getLogger().info("AFKDetection has been enabled!");
    }

    private void createcfg(File configFile) {
        try {
            String cfg =
                    "#########################################\n" +
                            "# AFK DETECTION by lolmayaaa\n" +
                            "# Test line 1\n" +
                            "# Version: 1.0 (Legacy)\n" +
                            "#########################################\n\n" +
                            "# How many seconds of not moving is going to alert the player? For example: 60\n" +
                            "afktimer: 60\n\n" +
                            "# What should be the reason for the kick?\n" +
                            "reason: 'Stop being AFK!'\n\n" +
                            "# Should it announce what player was kicked for being afk?\n" +
                            "announce: true\n\n" +
                            "# Warning intervals (in seconds)\n" +
                            "warnings: [30, 15, 5, 4, 3, 2, 1]";

            Files.write(configFile.toPath(), cfg.getBytes());
            getLogger().info("Created default config.yml with comments");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to create config file", e);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("AFKDetection has been disabled!");
    }

    public int getafktime() {
        return afktime;
    }

    public String getkickreason() {
        return kickreason;
    }

    public boolean shouldannounce() {
        return shouldannounce;
    }

    public List<Integer> getwarnings() {
        return warnings;
    }
}