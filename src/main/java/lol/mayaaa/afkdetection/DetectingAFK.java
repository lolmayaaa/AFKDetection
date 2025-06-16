package lol.mayaaa.afkdetection;

import org.bukkit.entity.Player;
import org.bukkit.Sound;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;

public class DetectingAFK implements Runnable {

    private final AFKDetection plugin;
    private final HashMap<UUID, Integer> playertime = new HashMap<>();
    private final HashMap<UUID, org.bukkit.Location> lastloc = new HashMap<>();
    private final HashMap<UUID, Integer> lastwarning = new HashMap<>();

    public DetectingAFK(AFKDetection plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID playerid = player.getUniqueId();
            org.bukkit.Location currentloc = player.getLocation();

            if (lastloc.containsKey(playerid)) {
                if (lastloc.get(playerid).equals(currentloc)) {
                    int time = playertime.getOrDefault(playerid, 0) + 1;
                    playertime.put(playerid, time);

                    int remaining = plugin.getafktime() - time;
                    List<Integer> warnings = plugin.getwarnings();

                    if (warnings.contains(remaining)) {
                        int lastwarn = lastwarning.getOrDefault(playerid, -1);
                        if (lastwarn != remaining) {
                            player.sendMessage("§c§lWARNING §8▶ §fYou'll be §ckicked in " + remaining +
                                    (remaining == 1 ? " second" : " seconds") + " §fif you don't move!");
                            player.playSound(player.getLocation(), Sound.valueOf("NOTE_PLING"), 1.0f, 0.5f);
                            lastwarning.put(playerid, remaining);
                        }
                    }

                    if (time >= plugin.getafktime()) {
                        if (plugin.shouldannounce()) {
                            plugin.getServer().broadcastMessage("§c§lAFK DETECTION SYSTEM §8▶ §c"+ player.getName() + " §fhas been kicked for being AFK!");
                        }
                        player.kickPlayer(plugin.getkickreason());
                        playertime.remove(playerid);
                        lastloc.remove(playerid);
                        lastwarning.remove(playerid);
                    }
                } else {
                    playertime.put(playerid, 0);
                    lastwarning.remove(playerid);
                }
            }
            lastloc.put(playerid, currentloc);
        }
    }
}