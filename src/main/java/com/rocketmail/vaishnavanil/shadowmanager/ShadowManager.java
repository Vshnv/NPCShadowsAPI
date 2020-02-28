package com.rocketmail.vaishnavanil.shadowmanager;

import com.herocraftonline.heroes.Heroes;
import com.rocketmail.vaishnavanil.shadowmanager.profile.HeroProfile;
import com.rocketmail.vaishnavanil.shadowmanager.profile.HeroProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShadowManager extends JavaPlugin {
    Heroes hook;
    private static ShadowManager instance;

    /**
     *
     * @return Singleton instance of main class
     */
    public static ShadowManager getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        try {
             hook = Heroes.getInstance();

        }catch (Exception e){
            getServer().getLogger().info("Could not hook to Heroes! Are you sure you have it installed?");
            this.getPluginLoader().disablePlugin(this);
        }


        getServer().getPluginManager().registerEvents(HeroProfileManager.getInstance(),this);
        getConfig().addDefault("config.defaults.shadowCount",1);
        saveConfig();
    }

    /**
     * Register class to listen to Bukkit Events
     * @param lis
     */
    public void registerListener(Listener lis){
        this.getServer().getPluginManager().registerEvents(lis,this);
    }
    /**
     * Unregister class from listening to Bukkit Events
     * @param lis
     */
    public void unregisterListener(Listener lis){
        HandlerList.unregisterAll(lis);
    }

    /**
     *
     * @param owner  Owner who trigged shadow spawn
     * @param shadowName  name of Shadow player to spawn
     * @return  Shadow instance
     */
    public Shadow createShadow(Player owner,String shadowName){
        if(!HeroProfileManager.getInstance().getProfile(owner).hasReachedShadowLimit()){
            return new Shadow(owner,shadowName);
        }
        return null;
    }
}
