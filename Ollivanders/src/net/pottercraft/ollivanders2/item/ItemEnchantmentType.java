package net.pottercraft.ollivanders2.item;

public enum ItemEnchantmentType
{
    FLAGRANTE ("flagrante"),
    GEMINIO ("geminio"),
    ;

    final String name;

    ItemEnchantmentType (String label)
    {
        name = label;
    }

    public String getName ()
    {
        return name;
    }
}
