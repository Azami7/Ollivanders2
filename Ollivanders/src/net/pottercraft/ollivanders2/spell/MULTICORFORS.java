package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.O2Color;
import org.bukkit.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Multicorfors changes the color of leather armor worn by a nearby entity.
 * <p>
 * Unlike block-targeting spells, Multicorfors scans for living entities along the projectile's
 * path each tick. The first non-caster entity with leather armor gets all its leather pieces
 * recolored to the same random dyeable color (via {@link O2Color#getRandomDyeableColor()}).
 * If the projectile reaches a non-caster entity that has no leather armor, or no equipment at
 * all, the spell is killed and a failure message is sent to the caster.
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Multicorfors_Spell">Multicorfors Spell</a>
 */
public final class MULTICORFORS extends O2Spell {
    /**
     * The color applied to the target's leather armor on the most recent successful cast.
     * Defaults to {@link Color#WHITE} before any armor is colored.
     */
    private Color color = Color.WHITE;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MULTICORFORS(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.TRANSFIGURATION;
        spellType = O2SpellType.MULTICORFORS;

        text = "Multicorfors will change the color of leather armor of the target.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MULTICORFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        // this is a transfiguration spell in HP but does not use the Transfiguration superclass.
        branch = O2MagicBranch.TRANSFIGURATION;
        spellType = O2SpellType.MULTICORFORS;

        initSpell();
    }

    /**
     * Scan for nearby living entities and recolor the first one's leather armor.
     * <p>
     * Each tick, the method scans within {@link #defaultRadius} of the projectile for living
     * entities. The caster is always skipped. When a non-caster entity is found, the spell is
     * killed and the entity's armor is inspected:
     * </p>
     * <ul>
     * <li>If any piece is leather armor, all leather pieces are recolored to the same random
     *     dyeable color, the modified armor array is written back via
     *     {@link org.bukkit.inventory.EntityEquipment#setArmorContents(ItemStack[])}, and the
     *     loop breaks (single-target).</li>
     * <li>If no leather armor is found on any non-caster entity in the scan, a failure message
     *     is sent to the caster.</li>
     * </ul>
     * <p>
     * If the projectile hits a block before finding an entity, the spell is killed and returns
     * silently (no failure message).
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        List<LivingEntity> nearbyLivingEntities = getNearbyLivingEntities(defaultRadius);

        if (nearbyLivingEntities.isEmpty())
            return;

        boolean colored = false;
        for (LivingEntity livingEntity : nearbyLivingEntities) {
            if (livingEntity.getUniqueId().equals(caster.getUniqueId()))
                continue;

            kill();

            EntityEquipment equipment = livingEntity.getEquipment();
            if (equipment == null)
                continue;

            color = O2Color.getRandomDyeableColor().getBukkitColor();

            // getArmorContents() returns a copy — modify in place then write back
            ItemStack[] armorContents = equipment.getArmorContents();

            for (ItemStack armor : armorContents) {
                if (armor != null && armor.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta) {
                    leatherArmorMeta.setColor(color);
                    armor.setItemMeta(leatherArmorMeta);

                    colored = true;
                }
            }

            if (colored) {
                equipment.setArmorContents(armorContents);
                break;
            }
        }

        if (!colored && isKilled())
            sendFailureMessage();
    }

    /**
     * Get the color applied to the target's leather armor on the most recent successful cast.
     * Returns {@link Color#WHITE} if the spell has not yet colored any armor.
     *
     * @return the color applied, or WHITE if no armor was colored
     */
    public Color getColor() {
       return color;
    }
}