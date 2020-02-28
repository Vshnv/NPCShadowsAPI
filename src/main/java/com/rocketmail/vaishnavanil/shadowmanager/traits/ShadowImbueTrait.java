package com.rocketmail.vaishnavanil.shadowmanager.traits;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.attributes.AttributeSet;
import com.herocraftonline.heroes.characters.Hero;
import com.rocketmail.vaishnavanil.shadowmanager.Shadow;
import com.rocketmail.vaishnavanil.shadowmanager.ShadowManager;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.event.NPCRemoveTraitEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ShadowImbueTrait extends Trait implements Listener {
    Player owner;
    Player imbued;
    Shadow shadow;
    public ShadowImbueTrait(Player owner, Player imbued, Shadow s) {
        super("ShadowImbue");
        this.owner = owner;
        this.imbued = imbued;
        this.shadow = s;
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
            if(p==imbued){
                if(isAlly(imbued)) {
                    if (e.getEntity() == npc.getEntity()) {
                        npc.getNavigator().cancelNavigation();
                        return;
                    }

                    npc.getNavigator().setTarget(e.getEntity(), true);
                }else{
                    npc.getNavigator().setTarget(imbued, true);
                }
            }
        }else if(e.getEntity() instanceof  Player){
            Player p = (Player) e.getEntity();
            if(p==imbued){
                if(isAlly(imbued)) {
                    if (e.getDamager() == npc.getEntity()) {
                        npc.getNavigator().cancelNavigation();
                        return;
                    }

                    npc.getNavigator().setTarget(e.getDamager(), true);
                }else{
                    npc.getNavigator().setTarget(imbued, true);
                }
            }
        }
    }
    @EventHandler
    public void onRemoveTrait(NPCRemoveTraitEvent e){
        if(e.getTrait() == this){
            ShadowManager.getInstance().unregisterListener(this);
        }
    }

    public boolean isAlly(Player p){
        return Heroes.getInstance().getCharacterManager().getHero(owner).getParty().isPartyMember(p);
    }



    @EventHandler
    public void onDeadImbueOrNPC(PlayerDeathEvent e){
        if(e.getEntity() == imbued){
            if(isAlly(e.getEntity())){
                shadow.dropShadowAsHead();
            }else{
                shadow.returnToOwner();
            }
        }else if(e.getEntity() == npc){
            if(isAlly(imbued)){
                shadow.banishShadow();
            }else{
                shadow.dropShadowAsHead();
            }
        }
    }
}