package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages all spells
 *
 * @author Azami7
 * @since 2.2.8
 */
public class O2Spells
{
    private final Ollivanders2Common common;

    final private Map<String, O2SpellType> O2SpellMap = new HashMap<>();

    public static final List<O2SpellType> wandlessSpells = new ArrayList<O2SpellType>()
    {{
        add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
    }};

    /**
     * Constructor
     *
     * @param plugin a callback to the MC plugin
     */
    public O2Spells(@NotNull Ollivanders2 plugin)
    {
        common = new Ollivanders2Common(plugin);

        for (O2SpellType spellType : O2SpellType.values())
        {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libsDisguisesSpells.contains(spellType))
                continue;

            O2SpellMap.put(spellType.getSpellName().toLowerCase(), spellType);
        }
    }

    /**
     * Get an O2Spell object from its type.
     *
     * @param spellType the type of spell to get
     * @return the O2Spell object, if it could be created, or null otherwise
     */
    @Nullable
    private O2Spell getSpellFromType(@NotNull O2SpellType spellType)
    {
        O2Spell spell;

        Class<?> spellClass = spellType.getClassName();

        try
        {
            spell = (O2Spell) spellClass.getConstructor().newInstance();
        }
        catch (Exception exception)
        {
            common.printDebugMessage("Exception trying to create new instance of " + spellType.toString(), exception, null, true);
            return null;
        }

        return spell;
    }

    /**
     * Get a spell type by name.
     *
     * @param name the name of the spell or potion
     * @return the type if found, null otherwise
     */
    @Nullable
    public O2SpellType getSpellTypeByName(@NotNull String name)
    {
        return O2SpellMap.get(name.toLowerCase());
    }

    /**
     * Verify this spell type is loaded. A spell may not be loaded if it depends on something such as LibsDisguises and that
     * dependency plugin does not exist.
     *
     * @param spellType the spell type to check
     * @return true if this spell type is loaded, false otherwise
     */
    public boolean isLoaded(@NotNull O2SpellType spellType)
    {
        return O2SpellMap.containsValue(spellType);
    }
}
