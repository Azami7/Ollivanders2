package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.ShieldSpell;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Shrinks a stationary shield spell's radius. Only the player who created the shield spell can change its size.
 * <p>
 * Restricting the effect to {@link ShieldSpell}s prevents it from changing other stationary spells such as the
 * floo network and vanishing cabinets.
 * </p>
 * <p>
 * This is also the base class for {@link CRESCERE_PROTEGAT}, which grows a shield's radius instead by setting
 * {@link #shrink} to false.
 * </p>
 *
 * @author Azami7
 * @see ShieldSpell
 */
public class HORREAT_PROTEGAT extends O2Spell {
    /**
     * Minimum radius change applied per cast, regardless of caster skill.
     */
    private static final int minChange = 1;

    /**
     * Maximum radius change applied per cast, regardless of caster skill.
     */
    private static final int maxChange = 10;

    /**
     * The radius change amount for this cast, set from caster skill in {@link #doInitSpell()}.
     */
    private int sizeChange;

    /**
     * When set to true spell radius decreases, when set to false spell radius increases
     */
    boolean shrink = true;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public HORREAT_PROTEGAT(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.HORREAT_PROTEGAT;
        branch = O2MagicBranch.CHARMS;

        text = "Horreat Protegat will shrink a stationary shield spell's radius. Only the creator of the stationary spell can affect it with this spell.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public HORREAT_PROTEGAT(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.HORREAT_PROTEGAT;
        branch = O2MagicBranch.CHARMS;

        // stationary spell projectiles can stop at water
        projectilePassThrough.remove(Material.WATER);

        initSpell();
    }

    /**
     * Set the radius change amount based on the caster's skill, clamped to [{@link #minChange}, {@link #maxChange}].
     */
    @Override
    void doInitSpell() {
        sizeChange = (int) usesModifier / 10;

        if (sizeChange < minChange)
            sizeChange = minChange;
        else if (sizeChange > maxChange)
            sizeChange = maxChange;
    }

    /**
     * On block impact, change the radius of every shield spell at the projectile's location that this caster created.
     * <p>
     * Each matching shield is shrunk or grown by {@link #sizeChange} depending on {@link #shrink}, then flaired.
     * Does nothing until the projectile has hit a block.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        for (O2StationarySpell spell : getShieldSpells()) {
            if (shrink)
               spell.decreaseRadius(sizeChange);
            else
                spell.increaseRadius(sizeChange);

            spell.flair(10);
        }

        kill();
    }

    /**
     * Get the radius change amount applied to each affected shield this cast.
     * <p>
     * Set by {@link #doInitSpell()} from the caster's skill, so the value is only meaningful after the
     * spell has been initialized for casting. The change is a decrease when {@link #shrink} is true and
     * an increase otherwise.
     * </p>
     *
     * @return the radius change amount for this cast
     */
    public int getSizeChange() {
        return sizeChange;
    }

    /**
     * Get all active shield spells at the projectile's location that were cast by this spell's caster.
     * <p>
     * Only {@link ShieldSpell} stationary spells are returned, so other stationary spells (floo network,
     * vanishing cabinets, etc.) are never affected.
     * </p>
     *
     * @return the caster's shield spells at the projectile's location; empty if there are none
     */
    public List<O2StationarySpell> getShieldSpells() {
        List<O2StationarySpell> shieldSpells = new ArrayList<>();
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
            if ((stationarySpell instanceof ShieldSpell)
                    && stationarySpell.isLocationInside(location)
                    && stationarySpell.getCasterID().equals(caster.getUniqueId())) {
                shieldSpells.add(stationarySpell);
            }
        }
        return shieldSpells;
    }
}