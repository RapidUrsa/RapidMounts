package com.rapidursa.mounts;

public enum RidingPose
{
    STANDARD("Standard"),
    WIDE("Wide"),
    EXTRA_WIDE("Extra Wide"),
    CROSS_LEGGED("Cross-legged"),
    NO_SADDLE("No Saddle");

    private final String displayName;

    RidingPose(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String toString()
    {
        return displayName;
    }

    static RidingPose[] supportedFor(MountType mount)
    {
        switch (mount)
        {
            case BLACK_UNICORN:
                return new RidingPose[]{STANDARD, WIDE};
            case TERRORBIRD:
                return new RidingPose[]{STANDARD, WIDE};
            case LAVA_DRAGON:
                return new RidingPose[]{STANDARD, CROSS_LEGGED};
            case GRYPHON:
                return new RidingPose[]{STANDARD};
            case BATTLE_TURTLE:
                return new RidingPose[]{CROSS_LEGGED};
            case ARTIO:
                return new RidingPose[]{STANDARD, EXTRA_WIDE, NO_SADDLE};
            case ARAXXOR:
            case VORKATH:
            case BIG_WOLF:
            case CATABLEPON:
            case SHEEP:
                return new RidingPose[]{EXTRA_WIDE};
            default:
                return new RidingPose[]{STANDARD};
        }
    }

    static RidingPose defaultFor(MountType mount)
    {
        if (mount == MountType.BLACK_UNICORN)
        {
            return WIDE;
        }
        if (mount == MountType.ARTIO || mount == MountType.ARAXXOR || mount == MountType.VORKATH
            || mount == MountType.BIG_WOLF || mount == MountType.CATABLEPON
            || mount == MountType.SHEEP)
        {
            return EXTRA_WIDE;
        }
        if (mount == MountType.BATTLE_TURTLE)
        {
            return CROSS_LEGGED;
        }
        return STANDARD;
    }

    static boolean supports(MountType mount, RidingPose pose)
    {
        if (pose == null)
        {
            return false;
        }
        for (RidingPose supported : supportedFor(mount))
        {
            if (supported == pose)
            {
                return true;
            }
        }
        return false;
    }

    boolean usesExtraWideAnimation()
    {
        return this == EXTRA_WIDE || this == NO_SADDLE;
    }
}
