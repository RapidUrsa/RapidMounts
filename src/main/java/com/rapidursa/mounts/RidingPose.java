package com.rapidursa.mounts;

public enum RidingPose
{
    STANDARD("Standard"),
    WIDE("Wide");

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
}
