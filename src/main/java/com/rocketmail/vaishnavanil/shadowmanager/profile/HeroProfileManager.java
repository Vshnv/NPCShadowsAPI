package com.rocketmail.vaishnavanil.shadowmanager.profile;

import com.google.common.collect.ImmutableList;
import com.rocketmail.vaishnavanil.shadowmanager.Shadow;
import com.rocketmail.vaishnavanil.shadowmanager.ShadowManager;
import javafx.print.Collation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeroProfileManager implements Listener {
    private HeroProfileManager(){}
    static HeroProfileManager instance;

    /**
     *
     * @return Singleton instance of Manager
     */
    public static HeroProfileManager getInstance() {
        if(instance == null)instance = new HeroProfileManager();
        return instance;
    }

    /**
     * Loaded HeroProfiles
     */
    Map<UUID,HeroProfile> cache = new HashMap<>();


    /**
     *
     * @param p Player to get Profile of
     * @return HeroProfile of p
     */
    public HeroProfile getProfile(Player p) {
        if (!cache.containsKey(p.getUniqueId())) {
            FileConfiguration config = ShadowManager.getInstance().getConfig();
            int maxShadowCount = config.getInt("data."+p.getUniqueId().toString() + ".maxShadows",config.getInt("config.defaults.shadowCount",1));
            cache.put(p.getUniqueId(),new HeroProfile(maxShadowCount,p));
        }
        return cache.get(p.getUniqueId());
    }
    /**
     *
     * @param p Player to save profile of
     */
    public void saveProfile(Player p){
        if(!cache.containsKey(p.getUniqueId()))return;
        FileConfiguration config = ShadowManager.getInstance().getConfig();
        config.set("data."+p.getUniqueId().toString() + ".maxShadows",cache.get(p.getUniqueId()));
        ShadowManager.getInstance().saveConfig();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        saveProfile(e.getPlayer());
        ImmutableList<Shadow> shadows = cache.get(e.getPlayer().getUniqueId()).getShadowList();
        if(!shadows.isEmpty()) {
            for (Shadow s : shadows) {
                s.dropShadowAsHead();
            }
        }
        cache.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void disable(PluginDisableEvent e){
        ShadowManager.getInstance().getServer().getOnlinePlayers().forEach(p->{

            saveProfile(p);

        });
    }






}
