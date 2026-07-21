package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Disarms an entity of its held item, flinging the item away from the caster with force determined by the spell level.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Disarming_Charm">Harry Potter Wiki - Disarming Charm</a>
 */
public final class EXPELLIARMUS extends O2Spell {
    /**
     * Minimum launch speed of the expelled item, regardless of caster skill.
     */
    private final double minVelocity = 0.25;

    /**
     * Maximum launch speed of the expelled item, regardless of caster skill.
     */
    private final double maxVelocity = 3;

    /**
     * The speed at which the expelled item flies out of the target's hand, set from caster skill in
     * {@link #doInitSpell()} and limited to [{@link #minVelocity}, {@link #maxVelocity}].
     */
    private double velocity = 0.25;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EXPELLIARMUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.EXPELLIARMUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Disarming Charm");
            add("They made the most of the last few hours in which they were allowed to do magic before the holidays... and practised disarming each other by magic. Harry was getting very good at it.");
            add("A handy (even life-saving) spell for removing an object from an enemy’s grasp.");
            add("\"Expelliarmus is a useful spell, Harry, but the Death Eaters seem to think it is your signature move, and I urge you not to let it become so!” -Remus Lupin");
            add("The Disarming Charm lies at the heart of a good duelling technique. It allows the duelist to rebound an opponent's spell in the hope that the rebounded spell will strike the opponent and leave him or her vulnerable to further attack.");
        }};

        text = "Item held by an entity is flung a distance.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EXPELLIARMUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.EXPELLIARMUS;
        branch = O2MagicBranch.CHARMS;

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        initSpell();
    }

    /**
     * Set the item launch speed based on the caster's skill, limited to [{@link #minVelocity}, {@link #maxVelocity}].
     */
    @Override
    void doInitSpell() {
        velocity = usesModifier / 10;
        if (velocity < minVelocity)
            velocity = minVelocity;
        else if (velocity > maxVelocity)
            velocity = maxVelocity;
    }

    /**
     * Get the speed at which a successful cast flings the disarmed item.
     * <p>
     * Set by {@link #doInitSpell()} from the caster's skill, so the value is only meaningful after the
     * spell has been initialized for casting.
     * </p>
     *
     * @return the item launch speed for this cast
     */
    public double getLaunchVelocity() {
        return velocity;
    }

    /**
     * Get the minimum item launch speed, regardless of caster skill.
     *
     * @return the minimum launch speed
     */
    public double getMinVelocity() {
        return minVelocity;
    }

    /**
     * Get the maximum item launch speed, regardless of caster skill.
     *
     * @return the maximum launch speed
     */
    public double getMaxVelocity() {
        return maxVelocity;
    }

    /**
     * Disarm the first non-caster living entity at the projectile's location that is holding an item.
     * <p>
     * Checks the entity's main hand, falling back to the off-hand. The held item is removed from that hand
     * and dropped at the entity's eye location, then flung away from the caster at {@link #velocity}. The
     * spell is killed once an entity is disarmed, or once the projectile hits a block without finding one.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        List<LivingEntity> livingEntities = getNearbyLivingEntities(1.5);

        if (!livingEntities.isEmpty()) {
            for (LivingEntity entity : livingEntities) {
                if (entity.getUniqueId().equals(caster.getUniqueId()))
                    continue;

                // is entity holding something
                EntityEquipment entityEquipment = entity.getEquipment();
                if (entityEquipment == null) {
                    // they do not have any equipment
                    continue;
                }

                ItemStack held = entityEquipment.getItemInMainHand();
                boolean offHand = false;

                if (held.getType() == Material.AIR) {
                    // try the other hand
                    held = entityEquipment.getItemInOffHand();

                    // this entity is not holding something
                    if (held.getType() == Material.AIR)
                        continue;
                    else
                        offHand = true;
                }

                // clone the item so we do not change the actual object held by the player
                ItemStack itemInHand = held.clone();

                // remove the item in the player's hand
                if (offHand)
                    entityEquipment.setItemInOffHand(null);
                else
                    entityEquipment.setItemInMainHand(null);

                // drop the item the player held
                Item item = entity.getWorld().dropItem(entity.getEyeLocation(), itemInHand);

                // fling the item away from the target, out of the disarmed entity's hand; skip the launch
                // if the caster is exactly on the item, since normalizing a zero-length vector yields NaN
                Vector direction = item.getLocation().toVector().subtract(caster.getEyeLocation().toVector());
                if (direction.lengthSquared() > 0)
                    item.setVelocity(direction.normalize().multiply(velocity));

                kill();
                return;
            }
        }

        // projectile has stopped, kill the spell
        if (hasHitBlock())
            kill();
    }
}