package com.rocketmail.vaishnavanil.shadowmanager.profile;

import com.google.common.collect.ImmutableList;
import com.rocketmail.vaishnavanil.shadowmanager.Shadow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HeroProfile {
    private int allowedShadowCount;
    private Player hero;
    private List<Shadow> activeShadows = new ArrayList<>();
    protected HeroProfile(int shadowCount,Player owner){
        allowedShadowCount = shadowCount;
        hero = owner;
    }

    public void registerShadow(Shadow shadow){
        activeShadows.add(shadow);
    }
    public void unregisterShadow(Shadow shadow){
        activeShadows.remove(shadow);
    }

    public boolean hasReachedShadowLimit(){
        return (activeShadows.size() >=allowedShadowCount);
    }

    public void setShadowLimit(int limit){
        allowedShadowCount = limit;
    }

    public int getShadowLimit(){
        return allowedShadowCount;
    }

    public ImmutableList<Shadow> getShadowList(){
        return ImmutableList.copyOf(activeShadows);
    }
    public Shadow selectShadow(LivingEntity ent){
        int id = ent.getEntityId();
        for (Shadow a : activeShadows) {
            if (a.getEntityID() == id) {
                continue;
            }

        }
        return null;
    }

}
