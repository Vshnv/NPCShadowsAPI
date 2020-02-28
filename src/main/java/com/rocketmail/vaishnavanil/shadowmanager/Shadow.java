package com.rocketmail.vaishnavanil.shadowmanager;


import com.rocketmail.vaishnavanil.shadowmanager.profile.HeroProfileManager;
import com.rocketmail.vaishnavanil.shadowmanager.traits.ShadowImbueTrait;
import com.rocketmail.vaishnavanil.shadowmanager.traits.ShadowProtectTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class Shadow {
    /**
     * NPC instance
     */
    private NPC npc;

    /**
     * Player who spawned Shadow
     */
    private Player owner;

    /**
     * Displayname of Shadow
     */
    private String shadowName;

    /**
     * boolean that represents Sentry state of Shadow.
     * A sentry shadow has no Navigator
     */
    private boolean isSentry = false;
    /**
     * Instance that represents Imbued state of Shadow as well as the imbued Player.
     * The shadow hides within the Imbued Player
     */
    private Player imbued = null;

    /**
     * CREATE AN INSTANCE USING ~~  ShadowManager
     * @param owner   Player to spawn the shadow
     * @param ShadowName  Name of player to create a shadow of
     */
    protected Shadow(Player owner,String ShadowName){
        this.owner = owner;
        shadowName = ShadowName + ChatColor.WHITE+ "[S]";
        spawnAt(owner);
        HeroProfileManager.getInstance().getProfile(owner).registerShadow(this);
    }

    /**
     *
     * @param p Player to spawn shadow at
     */
    private void spawnAt(Player p){
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, shadowName);
        npc.spawn(p.getLocation());
        npc.addTrait(new ShadowProtectTrait(p));
    }


    /**
     * Set npc to sentry state
     */
    public void setSentry(){
        isSentry = true;
        npc.removeTrait(ShadowProtectTrait.class);
    }

    /**
     * Set npc to normal state/Non sentry state
     */
    public void setNotSentry(){
        if(isSentry) {
            isSentry = false;
            npc.addTrait(new ShadowProtectTrait(owner));
        }
    }

    /**
     * Allows player to swap location if he has a sentry
     * @return success = true || fail = false
     */
    public boolean sentryLocationSwap(){
        if(!isSentry)return false;
        Location mark = npc.getEntity().getLocation();
        npc.teleport(owner.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        owner.teleport(mark);
        return true;
    }

    /**
     *
     * @return true : Shadow is in Sentry state
     */
    public boolean isSentry(){
        return isSentry;
    }

    /**
     *
     * @return true: Shadow is imbued to a player
     */
    public boolean isImbued(){
        return (imbued !=null);
    }

    /**
     * Set Shadow to imbue state
     * @param imbue Player to imbue shadow to
     */
    public void setImbued(Player imbue){
        if(isImbued())return;
        imbued = imbue;
        npc.removeTrait(ShadowProtectTrait.class);
        npc.removeTrait(ShadowImbueTrait.class);

    }

    /**
     * Releases shadow from imbued player and add the Imbue trait
     * @return
     */
    public boolean triggerImbue(){
        if(!isImbued())return false;
        npc.spawn(imbued.getLocation());
        npc.addTrait(new ShadowImbueTrait(owner,imbued,this));
        return true;
    }


    /**
     * Destroys shadow and returns head to owner
     */
    public void returnToOwner(){
        String name = ChatColor.stripColor(shadowName).replace("[S]","");
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(name);
        meta.setDisplayName(name+"'s Head");
        skull.setItemMeta(meta);
        if(!owner.getInventory().addItem(skull).isEmpty()){
            owner.getLocation().getWorld().dropItem(owner.getLocation(),skull);
        }

        HeroProfileManager.getInstance().getProfile(owner).unregisterShadow(this);

        npc.destroy();
    }

    /**
     * Destroys shadow and drops shadow as head where Show last was.
     */
    public void dropShadowAsHead(){
        String name = ChatColor.stripColor(shadowName).replace("[S]","");
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(name);
        meta.setDisplayName(name+"'s Head");
        skull.setItemMeta(meta);
        npc.getEntity().getLocation().getWorld().dropItemNaturally(npc.getEntity().getLocation(),skull);

        HeroProfileManager.getInstance().getProfile(owner).unregisterShadow(this);
        npc.destroy();
    }

    /**
     * Destroys shadow . Shadow head is not retained
     */
    public void banishShadow(){
        HeroProfileManager.getInstance().getProfile(owner).unregisterShadow(this);
        npc.destroy();
    }

    /**
     * Banishes shadow and gives player a DarknessEssence
     */
    public void essensifyShadow(){
        owner.getLocation().getWorld().dropItemNaturally(owner.getLocation(),getEssenceOfDarknessItem(1));
        banishShadow();
    }

    /**
     *
     * @return Entity ID
     */
    public int getEntityID(){
        return npc.getEntity().getEntityId();
    }




    private static final String essenceDisplay = ChatColor.WHITE+""+ChatColor.BOLD + "" + ChatColor.UNDERLINE+"Essence Of Darkness";
    private static final List<String> essenceLore = Arrays.asList(ChatColor.GRAY+"Used to increase your capability to spawn Shadows!");
    public static ItemStack getEssenceOfDarknessItem(int Amount){
        ItemStack essence = new ItemStack(Material.GUNPOWDER);
        ItemMeta meta = essence.getItemMeta();
        meta.setDisplayName(essenceDisplay);
        meta.setLore(essenceLore);
        essence.setItemMeta(meta);
        essence.setAmount(Amount);
        return essence;
    }

    public static boolean isEssenceOfDarknessItem(ItemStack stack){
        if(!stack.hasItemMeta())return false;
        if(stack.getItemMeta().getDisplayName() == null)return false;
        return stack.getItemMeta().getDisplayName().equals(essenceDisplay);
    }
}
