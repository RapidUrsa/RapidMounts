package com.rapidursa.mounts;

import net.runelite.client.config.ConfigManager;

/** Reads Rapid Holster's saved positions without registering its settings as a Mounts config. */
final class MountedHolsterSettings
{
    private final ConfigManager config;

    MountedHolsterSettings(ConfigManager config)
    {
        this.config = config;
    }

    private int number(String key, int fallback)
    {
        String saved = config.getConfiguration("rapidholster", key);
        if (saved == null) return fallback;
        try { return Integer.parseInt(saved); }
        catch (NumberFormatException ignored) { return fallback; }
    }

    private boolean booleanValue(String key, boolean fallback)
    {
        String saved = config.getConfiguration("rapidholster", key);
        return saved == null ? fallback : Boolean.parseBoolean(saved);
    }

    private String text(String key, String fallback)
    {
        String saved = config.getConfiguration("rapidholster", key);
        return saved == null ? fallback : saved;
    }

    boolean hideCape() { return booleanValue("hideCape", false); }
    boolean holstered() { return booleanValue("holstered", true); }
    boolean showButton() { return booleanValue("showButton", true); }
    int buttonSize() { return number("buttonSize", 44); }
    int sideways() { return number("twoHandedSideways", -5); }
    int height() { return number("twoHandedHeight", 142); }
    int forward() { return number("twoHandedForward", 15); }
    int pitch() { return number("twoHandedPitch", -95); }
    int yaw() { return number("twoHandedYaw", 90); }
    int roll() { return number("twoHandedRoll", -41); }
    int scale() { return number("twoHandedScale", 100); }
    int shadowSideways() { return number("staffSideways", 2); }
    int shadowHeight() { return number("staffHeight", 148); }
    int shadowForward() { return number("staffForward", 15); }
    int shadowPitch() { return number("staffPitch", -77); }
    int shadowYaw() { return number("staffYaw", 90); }
    int shadowRoll() { return number("staffRoll", -41); }
    int shadowScale() { return number("staffScale", 100); }
    int twistedBowSideways() { return number("bowSideways", -1); }
    int twistedBowHeight() { return number("bowHeight", 149); }
    int twistedBowForward() { return number("bowForward", 24); }
    int twistedBowPitch() { return number("bowPitch", -98); }
    int twistedBowYaw() { return number("bowYaw", 90); }
    int twistedBowRoll() { return number("bowRoll", -41); }
    int twistedBowScale() { return number("bowScale", 100); }
    int oneHandSideways() { return number("oneHandLeftSideways", 24); }
    int oneHandHeight() { return number("oneHandHeight", 90); }
    int oneHandForward() { return number("oneHandForward", 12); }
    int oneHandPitch() { return number("oneHandPitch", 32); }
    int oneHandYaw() { return number("oneHandYaw", 180); }
    int oneHandRoll() { return number("oneHandRoll", 0); }
    int oneHandScale() { return number("oneHandScale", 100); }
    int wandSideways() { return number("wandLeftSideways", 24); }
    int wandHeight() { return number("wandHeight", 90); }
    int wandForward() { return number("wandForward", 12); }
    int wandPitch() { return number("wandPitch", 32); }
    int wandYaw() { return number("wandYaw", 180); }
    int wandRoll() { return number("wandRoll", 0); }
    int wandScale() { return number("wandScale", 100); }
    int shieldSideways() { return number("shieldSideways", 0); }
    int shieldHeight() { return number("shieldHeight", 158); }
    int shieldForward() { return number("shieldForward", 22); }
    int shieldPitch() { return number("shieldPitch", 0); }
    int shieldYaw() { return number("shieldYaw", -88); }
    int shieldRoll() { return number("shieldRoll", 0); }
    int shieldScale() { return number("shieldScale", 85); }
    int crossbowSideways() { return number("crossbowLeftSideways", -13); }
    int crossbowHeight() { return number("crossbowHeight", 166); }
    int crossbowForward() { return number("crossbowForward", 22); }
    int crossbowPitch() { return number("crossbowPitch", 2); }
    int crossbowYaw() { return number("crossbowYaw", 180); }
    int crossbowRoll() { return number("crossbowRoll", -33); }
    int crossbowScale() { return number("crossbowScale", 100); }
    int offHandHipSideways() { return number("offHandHipSideways", -26); }
    int offHandHipHeight() { return number("offHandHipHeight", 90); }
    int offHandHipForward() { return number("offHandHipForward", 10); }
    int offHandHipPitch() { return number("offHandHipPitch", 48); }
    int offHandHipYaw() { return number("offHandHipYaw", 174); }
    int offHandHipRoll() { return number("offHandHipRoll", 0); }
    int offHandHipScale() { return number("offHandHipScale", 100); }
    String itemPlacements() { return text("itemPlacements", ""); }
}
