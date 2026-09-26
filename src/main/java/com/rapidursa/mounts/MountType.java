package com.rapidursa.mounts;

public enum MountType
{
    BLACK_UNICORN("Black unicorn"),
    TERRORBIRD("Terrorbird"),
    LAVA_DRAGON("Lava dragon"),
    GRYPHON("Gryphon"),
    BATTLE_TURTLE("Battle turtle"),
    ARTIO("Battle Bear"),
    ARAXXOR("Araxxor"),
    VORKATH("Vorkath"),
    BIG_WOLF("Big wolf"),
    CATABLEPON("Catablepon"),
    SHEEP("Sheep?");

    private final String displayName;

    MountType(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String toString()
    {
        return displayName;
    }
}
