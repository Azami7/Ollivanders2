package net.pottercraft.ollivanders2.item.enchantment;

import org.jetbrains.annotations.NotNull;

public enum ItemEnchantmentType
{
    FLAGRANTE ("flagrante", FLAGRANTE.class),
    GEMINIO ("geminio", GEMINIO.class),
    ;

    final String name;
    final Class<?> className;

    ItemEnchantmentType (@NotNull String label, @NotNull Class<?> cName)
    {
        name = label;
        className = cName;
    }

    @NotNull
    public String getName ()
    {
        return name;
    }

    @NotNull
    public Class<?> getClassName()
    {
        return className;
    }
}
