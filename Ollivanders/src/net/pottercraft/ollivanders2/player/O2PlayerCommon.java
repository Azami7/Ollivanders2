package net.pottercraft.ollivanders2.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Location;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

/**
 * Player common functions.
 */
public final class O2PlayerCommon
{
    /**
     * Reference to the plugin
     */
    final Ollivanders2 p;

    /**
     * Common functions
     */
    final Ollivanders2Common common;

    /**
     * Constructor
     *
     * @param plugin a reference to the MC plugin
     */
    public O2PlayerCommon(@NotNull Ollivanders2 plugin)
    {
        p = plugin;
        common = new Ollivanders2Common(plugin);
    }

    /**
     * Common (high probability) animagus shapes
     */
    private static final List<EntityType> commonAnimagusShapes = new ArrayList<>()
    {{
        add(EntityType.COW);
        add(EntityType.PIG);
        add(EntityType.HORSE);
        add(EntityType.SHEEP);
        add(EntityType.RABBIT);
        add(EntityType.MULE);
        add(EntityType.DONKEY);
        add(EntityType.CAT);
        add(EntityType.WOLF);
        add(EntityType.LLAMA);
        add(EntityType.FOX);
    }};

    /**
     * Rare (low probability) animagus hapes
     */
    private static final List<EntityType> rareAnimagusShapes = new ArrayList<>()
    {{
        add(EntityType.OCELOT);
        add(EntityType.POLAR_BEAR);
        add(EntityType.TRADER_LLAMA);
        add(EntityType.PANDA);
        add(EntityType.TURTLE);
        add(EntityType.IRON_GOLEM);
    }};

    /**
     * Animagus forms that can only be used when config is set.
     * <p>
     * Reference: https://github.com/Azami7/Ollivanders2/wiki/Configuration#hostile-mob-animagi
     */
    private static final List<EntityType> hostileAnimagusShapes = new ArrayList<>()
    {{
        add(EntityType.SPIDER);
        add(EntityType.SLIME);
        add(EntityType.CAVE_SPIDER);
        add(EntityType.CREEPER);
        add(EntityType.SILVERFISH);
        add(EntityType.SHULKER);
        add(EntityType.HOGLIN);
        add(EntityType.PIGLIN);
        add(EntityType.STRIDER);
    }};

    /**
     * Get the animagus form for this player
     *
     * @return the animagus form for this player - should always be the same since it is based on a hash of their PID
     */
    @NotNull
    public EntityType getAnimagusForm(@NotNull UUID pid)
    {
        // 1% chance to get a rare form
        int form = Math.abs(pid.hashCode() % 100);
        if (form < 1)
        {
            form = Math.abs(pid.hashCode() % rareAnimagusShapes.size());
            return rareAnimagusShapes.get(form);
        }

        // if using hostile mob animagi, 10% chance of getting a hostile mob form
        form = Math.abs(pid.hashCode() % 10);
        if (form < 1)
        {
            form = Math.abs(pid.hashCode() % hostileAnimagusShapes.size());
            return hostileAnimagusShapes.get(form);
        }

        // equal chance for common forms
        form = Math.abs(pid.hashCode() % commonAnimagusShapes.size());
        return commonAnimagusShapes.get(form);
    }

    /**
     * Determine if an EntityType is an allowed Animagus form.
     *
     * @param form the animagus form to check
     * @return true if this is an allowed form, false otherwise
     */
    public boolean isAllowedAnimagusForm(@NotNull EntityType form)
    {
        if (Ollivanders2.useHostileMobAnimagi)
        {
            if (hostileAnimagusShapes.contains(form))
                return true;
        }

        if (rareAnimagusShapes.contains(form))
            return true;

        return commonAnimagusShapes.contains(form);
    }

    /**
     * Checks what kind of wand a player holds in their primary hand. Returns a value based on the wand and its relation to the player.
     * <p>
     * Assumes:holdsWand has already been checked
     *
     * @param player player being checked. The player must be holding a wand.
     * @return 2 - the wand is not player's type AND/OR is not allied to player. <br>1 - the wand is player's type and is allied to player OR the wand is the elder wand and is not allied to player. <br>0.5 - the wand is the elder wand and it is allied to player.
     */
    public double wandCheck(@NotNull Player player)
    {
        return wandCheck(player, EquipmentSlot.HAND);
    }

    /**
     * Checks what kind of wand a player holds. Returns a value based on the wand and it's relation to the player.
     * <p>
     * Assumes: player is holding a wand in the equipment slot passed in.
     *
     * @param player player being checked. The player must be holding a wand.
     * @param hand   the hand that is holding the wand to check.
     * @return 2 - The wand is not player's type AND/OR is not allied to player <br>>1 - The wand is player's type and is allied to player OR the wand is the elder wand and is not allied to player <br>0.5 - The wand is the elder wand and it is allied to player.
     */
    public double wandCheck(@NotNull Player player, @NotNull EquipmentSlot hand)
    {
        ItemStack item;

        if (hand == EquipmentSlot.HAND)
            item = player.getInventory().getItemInMainHand();
        else
            item = player.getInventory().getItemInOffHand();

        return doWandCheck(player, item);
    }

    /**
     * Checks what kind of wand a player holds. Returns a value based on the wand and it's relation to the player.
     *
     * @param player    the player to check
     * @param itemStack the item to check
     * @return 2 - The wand is not player's type <br>1 - The wand is player's type <br>0.5 - The wand is the elder wand <br>-1 - The player is not holding a wand
     */
    private double doWandCheck(@NotNull Player player, @NotNull ItemStack itemStack)
    {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            // not an ollivanders item
            return -1;

        if (O2ItemType.ELDER_WAND.isItemThisType(itemStack))
            // elder wand
            return 0.5;

        if (!O2ItemType.WAND.isItemThisType(itemStack))
            // not a wand
            return -1;

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null)
        {
            // not a player
            common.printDebugMessage("O2Player is null", null, null, true);
            return -1;
        }

        if (!(Ollivanders2API.getItems().getWands().isDestinedWand(o2p, itemStack)))
        {
            // not the player's destined wand
            common.printDebugMessage("O2PlayerCommon.doWandCheck: player holds a wand which is not their destined wand", null, null, false);
            return 2;
        }

        // player's destined wand
        common.printDebugMessage("O2PlayerCommon.doWandCheck: player holds their destined wand", null, null, false);
        return 1;
    }

    /**
     * Restore a player to full health
     *
     * @param player the player to restore
     */
    static public void restoreFullHealth(@NotNull Player player)
    {
        // remove O2Effecs
        Ollivanders2API.getPlayers().playerEffects.onDeath(player.getUniqueId());

        // remove other potion effects
        Collection<PotionEffect> potions = player.getActivePotionEffects();
        for (PotionEffect potion : potions)
        {
            player.removePotionEffect(potion.getType());
        }

        // reset health to max
        AttributeInstance playerHealthMax = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
        if (playerHealthMax != null)
            player.setHealth(playerHealthMax.getBaseValue());
    }

    /**
     * Get a list of all the sorted online players
     *
     * @return the list of sorted online players
     */
    @NotNull
    public List<Player> getAllOnlineSortedPlayers()
    {
        ArrayList<Player> sortedPlayers = new ArrayList<>();

        for (Player player : p.getServer().getOnlinePlayers())
        {
            if (Ollivanders2API.getHouses().isSorted(player))
                sortedPlayers.add(player);
        }

        return sortedPlayers;
    }

    /**
     * Gives a player an item stack
     *
     * @param player the player to give the items to
     * @param kit    the items to give
     */
    static public void givePlayerKit(@NotNull Player player, @NotNull List<ItemStack> kit)
    {
        Location loc = player.getEyeLocation();
        ItemStack[] kitArray = kit.toArray(new ItemStack[kit.size()]);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(kitArray);
        for (ItemStack item : leftover.values())
        {
            player.getWorld().dropItem(loc, item);
        }
    }
}