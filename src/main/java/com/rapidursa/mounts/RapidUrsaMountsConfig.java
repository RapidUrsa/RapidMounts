package com.rapidursa.mounts;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup(RapidUrsaMountsConfig.GROUP)
public interface RapidUrsaMountsConfig extends Config
{
    String GROUP = "rapidursamounts";
    String GENERAL = "general";
    String RIDER = "rider";
    String UNICORN = "unicorn";
    String TERRORBIRD = "terrorbird";
    String LAVA_DRAGON = "lavaDragon";
    String GRYPHON = "gryphon";

    @ConfigSection(name = "General", description = "Mount controls and behaviour", position = 0)
    String generalSection = GENERAL;

    @ConfigSection(name = "Rider", description = "Advanced rider appearance controls", position = 1, closedByDefault = true)
    String riderSection = RIDER;

    @ConfigSection(name = "Black unicorn", description = "Black unicorn appearance tuning", position = 2, closedByDefault = true)
    String unicornSection = UNICORN;

    @ConfigSection(name = "Terrorbird", description = "Terrorbird appearance and motion tuning", position = 3, closedByDefault = true)
    String terrorbirdSection = TERRORBIRD;

    @ConfigSection(name = "Lava dragon", description = "Lava dragon appearance and motion tuning", position = 4, closedByDefault = true)
    String lavaDragonSection = LAVA_DRAGON;

    @ConfigSection(name = "Gryphon", description = "Normal gryphon appearance and motion tuning", position = 5, closedByDefault = true)
    String gryphonSection = GRYPHON;

    @ConfigItem(
        keyName = "mountType",
        name = "Selected mount",
        description = "Choose which cosmetic mount to ride",
        section = GENERAL
    )
    default MountType mountType()
    {
        return MountType.BLACK_UNICORN;
    }

    @ConfigItem(
        keyName = "enabled",
        name = "Enable selected mount",
        description = "Show the selected cosmetic mount",
        section = GENERAL
    )
    default boolean enabled()
    {
        return true;
    }

    @Range(min = 60, max = 140)
    @ConfigItem(
        keyName = "mountScale",
        name = "Unicorn scale (%)",
        description = "Resize the black unicorn model",
        section = UNICORN
    )
    default int mountScale()
    {
        return 105;
    }

    @ConfigItem(
        keyName = "animateUnicorn",
        name = "Animate mount",
        description = "Use the selected mount's genuine idle and walking animations",
        section = GENERAL
    )
    default boolean animateUnicorn()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showRider",
        name = "Show rider",
        description = "Show the independently reconstructed mounted player",
        section = RIDER
    )
    default boolean showRider()
    {
        return true;
    }

    @ConfigItem(
        keyName = "hideHeldEquipment",
        name = "Hide held equipment",
        description = "Remove weapon and shield slots from the cosmetic rider while leaving real equipment unchanged",
        section = RIDER
    )
    default boolean hideHeldEquipment()
    {
        return true;
    }

    @ConfigItem(
        keyName = "hideOriginalPlayer",
        name = "Hide original player",
        description = "Hide the real player only while both the selected mount and stable rider are rendered",
        section = RIDER
    )
    default boolean hideOriginalPlayer()
    {
        return true;
    }

    @ConfigItem(
        keyName = "useRidingPose",
        name = "Use riding pose",
        description = "Apply the selected riding animation to the mounted player",
        section = RIDER
    )
    default boolean useRidingPose()
    {
        return true;
    }

    @ConfigItem(
        keyName = "ridingPose",
        name = "Riding pose",
        description = "Choose the standard saddle pose or the wider straddled riding pose",
        section = RIDER
    )
    default RidingPose ridingPose()
    {
        return RidingPose.STANDARD;
    }

    @Range(min = 0, max = 200)
    @ConfigItem(
        keyName = "riderHeight",
        name = "Rider height",
        description = "Raise or lower the rider on the black unicorn",
        section = UNICORN
    )
    default int riderHeight()
    {
        return 41;
    }

    @Range(min = -96, max = 96)
    @ConfigItem(
        keyName = "riderForward",
        name = "Rider forward/back",
        description = "Move the rider along the direction the unicorn faces",
        section = UNICORN
    )
    default int riderForward()
    {
        return -2;
    }

    @Range(min = -96, max = 96)
    @ConfigItem(
        keyName = "riderSideways",
        name = "Rider sideways",
        description = "Move the rider sideways relative to the unicorn",
        section = UNICORN
    )
    default int riderSideways()
    {
        return 0;
    }

    @Range(min = 0, max = 12)
    @ConfigItem(
        keyName = "unicornIdleBounce",
        name = "Unicorn idle bounce",
        description = "Subtle rider lift synchronized to the unicorn's idle animation",
        section = UNICORN
    )
    default int unicornIdleBounce()
    {
        return 2;
    }

    @Range(min = 60, max = 140)
    @ConfigItem(
        keyName = "terrorbirdScale",
        name = "Terrorbird scale (%)",
        description = "Resize the terrorbird model",
        section = TERRORBIRD
    )
    default int terrorbirdScale()
    {
        return 100;
    }

    @Range(min = 0, max = 200)
    @ConfigItem(
        keyName = "terrorbirdRiderHeight",
        name = "Terrorbird rider height",
        description = "Raise or lower the rider on the terrorbird",
        section = TERRORBIRD
    )
    default int terrorbirdRiderHeight()
    {
        return 43;
    }

    @Range(min = -96, max = 96)
    @ConfigItem(
        keyName = "terrorbirdRiderForward",
        name = "Terrorbird forward/back",
        description = "Move the rider along the direction the terrorbird faces",
        section = TERRORBIRD
    )
    default int terrorbirdRiderForward()
    {
        return -2;
    }

    @Range(min = -96, max = 96)
    @ConfigItem(
        keyName = "terrorbirdRiderSideways",
        name = "Terrorbird sideways",
        description = "Move the rider sideways relative to the terrorbird",
        section = TERRORBIRD
    )
    default int terrorbirdRiderSideways()
    {
        return 0;
    }

    @Range(min = 0, max = 12)
    @ConfigItem(
        keyName = "terrorbirdIdleBounce",
        name = "Terrorbird idle bounce",
        description = "Subtle rider lift synchronized to the terrorbird's idle animation",
        section = TERRORBIRD
    )
    default int terrorbirdIdleBounce()
    {
        return 2;
    }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "terrorbirdWalkHeightAdjustment",
        name = "Terrorbird walk height adjust",
        description = "Change rider height only while moving; negative values lower the rider",
        section = TERRORBIRD
    )
    default int terrorbirdWalkHeightAdjustment()
    {
        return 0;
    }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "terrorbirdWalkForwardAdjustment",
        name = "Terrorbird walk forward adjust",
        description = "Move the rider forward or backward only while the terrorbird is moving",
        section = TERRORBIRD
    )
    default int terrorbirdWalkForwardAdjustment()
    {
        return 25;
    }

    @Range(min = -30, max = 30)
    @ConfigItem(
        keyName = "terrorbirdStrideFollow",
        name = "Terrorbird stride follow",
        description = "Optional rider bob synchronized to the terrorbird stride; use a negative value to invert it",
        section = TERRORBIRD
    )
    default int terrorbirdStrideFollow()
    {
        return -6;
    }

    @Range(min = 0, max = 30)
    @ConfigItem(
        keyName = "terrorbirdSeatBounce",
        name = "Terrorbird seat bounce",
        description = "Move the complete rider up and down in time with the terrorbird stride",
        section = TERRORBIRD
    )
    default int terrorbirdSeatBounce()
    {
        return 0;
    }

    @Range(min = -30, max = 30)
    @ConfigItem(
        keyName = "terrorbirdSeatSway",
        name = "Terrorbird seat sway",
        description = "Move the complete rider forward and backward in time with the terrorbird stride",
        section = TERRORBIRD
    )
    default int terrorbirdSeatSway()
    {
        return 3;
    }

    @Range(min = 40, max = 140)
    @ConfigItem(
        keyName = "lavaDragonScale",
        name = "Lava dragon scale (%)",
        description = "Resize the lava dragon model",
        section = LAVA_DRAGON
    )
    default int lavaDragonScale()
    {
        return 100;
    }

    @Range(min = 0, max = 240)
    @ConfigItem(
        keyName = "lavaDragonRiderHeight",
        name = "Lava dragon rider height",
        description = "Raise or lower the rider on the lava dragon",
        section = LAVA_DRAGON
    )
    default int lavaDragonRiderHeight()
    {
        return 37;
    }

    @Range(min = -128, max = 128)
    @ConfigItem(
        keyName = "lavaDragonRiderForward",
        name = "Lava dragon forward/back",
        description = "Move the rider along the direction the lava dragon faces",
        section = LAVA_DRAGON
    )
    default int lavaDragonRiderForward()
    {
        return -95;
    }

    @Range(min = -128, max = 128)
    @ConfigItem(
        keyName = "lavaDragonRiderSideways",
        name = "Lava dragon sideways",
        description = "Move the rider sideways relative to the lava dragon",
        section = LAVA_DRAGON
    )
    default int lavaDragonRiderSideways()
    {
        return 6;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "lavaDragonWalkHeightAdjustment",
        name = "Lava dragon walk height adjust",
        description = "Change rider height only while the lava dragon is moving",
        section = LAVA_DRAGON
    )
    default int lavaDragonWalkHeightAdjustment()
    {
        return 5;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "lavaDragonWalkForwardAdjustment",
        name = "Lava dragon walk forward adjust",
        description = "Move the rider forward or backward only while the lava dragon is moving",
        section = LAVA_DRAGON
    )
    default int lavaDragonWalkForwardAdjustment()
    {
        return -12;
    }

    @Range(min = 0, max = 12)
    @ConfigItem(
        keyName = "lavaDragonIdleBounce",
        name = "Lava dragon idle bounce",
        description = "Subtle rider lift synchronized to the lava dragon's idle breathing",
        section = LAVA_DRAGON
    )
    default int lavaDragonIdleBounce()
    {
        return 2;
    }

    @Range(min = -30, max = 30)
    @ConfigItem(
        keyName = "lavaDragonStrideFollow",
        name = "Lava dragon stride follow",
        description = "Rider bob synchronized to the lava dragon stride; negative values invert it",
        section = LAVA_DRAGON
    )
    default int lavaDragonStrideFollow()
    {
        return -3;
    }

    @Range(min = 0, max = 30)
    @ConfigItem(
        keyName = "lavaDragonSeatBounce",
        name = "Lava dragon seat bounce",
        description = "Move the complete rider up and down in time with the lava dragon stride",
        section = LAVA_DRAGON
    )
    default int lavaDragonSeatBounce()
    {
        return 2;
    }

    @Range(min = -30, max = 30)
    @ConfigItem(
        keyName = "lavaDragonSeatSway",
        name = "Lava dragon seat sway",
        description = "Move the complete rider forward and backward in time with the lava dragon stride",
        section = LAVA_DRAGON
    )
    default int lavaDragonSeatSway()
    {
        return -2;
    }

    @Range(min = 60, max = 160)
    @ConfigItem(
        keyName = "gryphonScale",
        name = "Gryphon scale (%)",
        description = "Resize the normal gryphon model",
        section = GRYPHON
    )
    default int gryphonScale()
    {
        return 100;
    }

    @Range(min = 0, max = 300)
    @ConfigItem(
        keyName = "gryphonRiderHeight",
        name = "Standard pose rider height",
        description = "Raise or lower the rider when using the Standard riding pose",
        section = GRYPHON
    )
    default int gryphonRiderHeight()
    {
        return 45;
    }

    @Range(min = 0, max = 300)
    @ConfigItem(
        keyName = "gryphonWideRiderHeight",
        name = "Wide pose rider height",
        description = "Raise or lower the rider when using the Wide riding pose",
        section = GRYPHON
    )
    default int gryphonWideRiderHeight()
    {
        return 39;
    }

    @Range(min = -160, max = 160)
    @ConfigItem(
        keyName = "gryphonRiderForward",
        name = "Gryphon forward/back",
        description = "Move the rider along the direction the gryphon faces",
        section = GRYPHON
    )
    default int gryphonRiderForward()
    {
        return 5;
    }

    @Range(min = -128, max = 128)
    @ConfigItem(
        keyName = "gryphonRiderSideways",
        name = "Gryphon sideways",
        description = "Move the rider sideways relative to the gryphon",
        section = GRYPHON
    )
    default int gryphonRiderSideways()
    {
        return 0;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "gryphonWalkHeightAdjustment",
        name = "Gryphon walk height adjust",
        description = "Change rider height only while the gryphon is moving",
        section = GRYPHON
    )
    default int gryphonWalkHeightAdjustment()
    {
        return 0;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "gryphonWalkForwardAdjustment",
        name = "Gryphon walk forward adjust",
        description = "Move the rider forward or backward only while the gryphon is moving",
        section = GRYPHON
    )
    default int gryphonWalkForwardAdjustment()
    {
        return 0;
    }

    @Range(min = 0, max = 12)
    @ConfigItem(
        keyName = "gryphonIdleBounce",
        name = "Gryphon idle bounce",
        description = "Subtle rider lift synchronized to the gryphon's idle animation",
        section = GRYPHON
    )
    default int gryphonIdleBounce()
    {
        return 2;
    }

    @Range(min = -30, max = 30)
    @ConfigItem(
        keyName = "gryphonSeatSway",
        name = "Gryphon seat sway",
        description = "Move the rider forward and backward in time with the gryphon's walking stride",
        section = GRYPHON
    )
    default int gryphonSeatSway()
    {
        return 5;
    }

    @Range(min = -30, max = 30)
    @ConfigItem(
        keyName = "gryphonLateralSway",
        name = "Gryphon left/right sway",
        description = "Move the rider from side to side in time with the gryphon's walking stride",
        section = GRYPHON
    )
    default int gryphonLateralSway()
    {
        return 4;
    }

    @Range(min = 24, max = 96)
    @ConfigItem(
        keyName = "buttonSize",
        name = "Mount button size",
        description = "Size of the movable mount and dismount button in pixels",
        section = GENERAL
    )
    default int buttonSize()
    {
        return 44;
    }

    @ConfigItem(
        keyName = "pauseForActions",
        name = "Pause mount for actions",
        description = "Temporarily reveal the real player for combat, skilling, teleports, and other actions",
        section = GENERAL
    )
    default boolean pauseForActions()
    {
        return true;
    }

    @Range(min = 0, max = 10)
    @ConfigItem(
        keyName = "actionResumeDelay",
        name = "Remount delay (ticks)",
        description = "Wait this many game ticks after an action before automatically showing the mount again",
        section = GENERAL
    )
    default int actionResumeDelay()
    {
        return 2;
    }

    @ConfigItem(
        keyName = "mountEffect",
        name = "Mount smoke effect",
        description = "Play a dark magical smoke burst when manually mounting or dismounting",
        section = GENERAL
    )
    default boolean mountEffect()
    {
        return true;
    }

    @ConfigItem(
        keyName = "mountHotkey",
        name = "Mount/dismount hotkey",
        description = "Optional keyboard shortcut for mounting and dismounting",
        section = GENERAL
    )
    default Keybind mountHotkey()
    {
        return Keybind.NOT_SET;
    }

}
