package net.pottercraft.ollivanders2.item;

public enum ItemEnchantmentType
{
    FLAGRANTE ("flagrante"),
    GEMINIO ("geminio"),
    ;

    String name;

    ItemEnchantmentType (String label)
    {
        name = label;
    }
}
