package com.rocketmail.vaishnavanil.shadowmanager.traits;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.attributes.AttributeSet;
import com.herocraftonline.heroes.characters.Hero;
import com.rocketmail.vaishnavanil.shadowmanager.ShadowManager;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.event.NPCRemoveTraitEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class ShadowProtectTrait extends Trait implements Listener {
    Player owner;
    public ShadowProtectTrait(Player owner) {
        super("ShadowProtect");
        this.owner = owner;
        ShadowManager.getInstance().registerListener(this);

    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        if(npc.isSpawned()) {
            npc.getNavigator().setTarget(owner, false);
            npc.getNavigator().getDefaultParameters().attackDelayTicks(npc.getNavigator().getDefaultParameters().attackDelayTicks()/2);
            npc.getNavigator().getDefaultParameters().baseSpeed(npc.getNavigator().getDefaultParameters().baseSpeed()*3);
            Hero npcHero = Heroes.getInstance().getCharacterManager().getHero((Player) npc.getEntity());
            AttributeSet plr =  Heroes.getInstance().getCharacterManager().getHero(owner).getAllocatedAttributes();
            AttributeSet attr = npcHero.getAllocatedAttributes();
            attr.setCharismaValue(plr.getCharismaValue());
            attr.setConstitutionValue(plr.getConstitutionValue());
            attr.setDexterityValue(plr.getDexterityValue());
            attr.setEnduranceValue(plr.getEnduranceValue());
            attr.setIntellectValue(plr.getIntellectValue());
            attr.setStrengthValue(plr.getStrengthValue());
            attr.setWisdomValue(plr.getWisdomValue());
        }

    }

    @Override
    public void onAttach() {
        super.onAttach();
    }


    @Override
    public void onRemove() {
        super.onRemove();
        ShadowManager.getInstance().unregisterListener(this);
    }


    @EventHandler
    public void navComplete(NavigationCompleteEvent e){
        if(e.getNPC() != npc)return;

        npc.getNavigator().setTarget(owner,false);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            Player p = (Player) e.getDamager();
            if(p==owner){
                if(e.getEntity() == npc.getEntity()){
                    npc.getNavigator().cancelNavigation();
                    return;
                }
                npc.getNavigator().setTarget(e.getEntity(),true);
            }
        }else if(e.getEntity() instanceof  Player){
            Player p = (Player) e.getEntity();
            if(p==owner){
                npc.getNavigator().setTarget(e.getDamager(),true);
            }
        }
    }
    @EventHandler
    public void onRemoveTrait(NPCRemoveTraitEvent e){
        if(e.getTrait() == this){
            ShadowManager.getInstance().unregisterListener(this);
        }
    }
}
