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
    // Keep the existing constant as an alias so saved saddle keys retain
    // their identity while the controls live inside Black unicorn settings.
    String SADDLE = UNICORN;

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
        keyName = "showSaddleAndReins",
        name = "Show saddle and reins",
        description = "Show the fitted saddle and animated reins when riding the Black unicorn in Wide pose",
        section = SADDLE
    )
    default boolean showSaddleAndReins()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showSaddlePrototype",
        name = "Show saddle prototype",
        description = "Show the experimental saddle part on the Black unicorn",
        section = SADDLE,
        hidden = true
    )
    default boolean showSaddlePrototype()
    {
        return true;
    }

    @ConfigItem(
        keyName = "useCustomSaddle",
        name = "Use custom saddle",
        description = "Use the purpose-built low-poly saddle instead of a donor NPC model",
        section = SADDLE,
        hidden = true
    )
    default boolean useCustomSaddle()
    {
        return true;
    }

    @ConfigItem(
        keyName = "saddleSource",
        name = "Saddle source",
        description = "Choose the mounted NPC whose model parts will be inspected",
        section = SADDLE,
        hidden = true
    )
    default SaddleSource saddleSource()
    {
        return SaddleSource.CART_CAMEL;
    }

    @Range(min = 0, max = 30000)
    @ConfigItem(
        keyName = "bankBuffaloNpcId",
        name = "Bank buffalo NPC ID",
        description = "Enter the Bank Buffalo ID shown by RuneLite Developer Tools",
        section = SADDLE,
        hidden = true
    )
    default int bankBuffaloNpcId()
    {
        return 13128;
    }

    @Range(min = 0, max = 30000)
    @ConfigItem(
        keyName = "cartCamelNpcId",
        name = "Cart camel NPC ID",
        description = "Enter the Cart camel ID shown by RuneLite Developer Tools",
        section = SADDLE,
        hidden = true
    )
    default int cartCamelNpcId()
    {
        return 18960;
    }

    @Range(min = -1, max = 20)
    @ConfigItem(
        keyName = "saddleSourcePart",
        name = "Source model part",
        description = "Use -1 for the whole source model, then cycle individual parts from 0 upward",
        section = SADDLE,
        hidden = true
    )
    default int saddleSourcePart()
    {
        return -1;
    }

    @Range(min = 20, max = 200)
    @ConfigItem(
        keyName = "saddleScale",
        name = "Saddle scale (%)",
        description = "Resize the saddle prototype",
        section = SADDLE,
        hidden = true
    )
    default int saddleScale()
    {
        return 64;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "saddleForward",
        name = "Saddle forward/back",
        description = "Move the saddle along the direction the unicorn faces",
        section = SADDLE,
        hidden = true
    )
    default int saddleForward()
    {
        return 7;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "saddleSideways",
        name = "Saddle sideways",
        description = "Move the saddle sideways across the unicorn",
        section = SADDLE,
        hidden = true
    )
    default int saddleSideways()
    {
        return 0;
    }

    @Range(min = -100, max = 200)
    @ConfigItem(
        keyName = "saddleHeight",
        name = "Saddle height",
        description = "Raise or lower the saddle prototype",
        section = SADDLE,
        hidden = true
    )
    default int saddleHeight()
    {
        return 132;
    }

    @Range(min = 60, max = 400)
    @ConfigItem(
        keyName = "reinLength",
        name = "Reins length",
        description = "Extend or shorten the reins towards the unicorn's head",
        section = SADDLE,
        hidden = true
    )
    default int reinLength()
    {
        return 205;
    }

    @Range(min = -60, max = 60)
    @ConfigItem(
        keyName = "reinEndHeight",
        name = "Reins end height",
        description = "Raise or lower the head end of the reins",
        section = SADDLE,
        hidden = true
    )
    default int reinEndHeight()
    {
        return 30;
    }

    @Range(min = -20, max = 40)
    @ConfigItem(
        keyName = "reinSpread",
        name = "Reins spread",
        description = "Move the pair of reins closer together or farther apart",
        section = SADDLE,
        hidden = true
    )
    default int reinSpread()
    {
        return 15;
    }

    @Range(min = 0, max = 20)
    @ConfigItem(
        keyName = "reinHeadBob",
        name = "Reins head bob",
        description = "How far the mouth end follows the unicorn's vertical head movement",
        section = SADDLE,
        hidden = true
    )
    default int reinHeadBob()
    {
        return 6;
    }

    @Range(min = -20, max = 20)
    @ConfigItem(
        keyName = "reinHeadSway",
        name = "Reins head forward/back",
        description = "How far the mouth end follows the unicorn's forward and backward head movement",
        section = SADDLE,
        hidden = true
    )
    default int reinHeadSway()
    {
        return 6;
    }

    @Range(min = 10, max = 100)
    @ConfigItem(
        keyName = "reinMotionSizePercent",
        name = "Reins motion size (%)",
        description = "Shrink or enlarge the oval followed by the mouth end of the reins",
        section = SADDLE,
        hidden = true
    )
    default int reinMotionSizePercent()
    {
        return 39;
    }

    @Range(min = 25, max = 400)
    @ConfigItem(
        keyName = "reinBobSpeedPercent",
        name = "Reins bob speed (%)",
        description = "Fine-tune the rein movement speed to match the unicorn's head bob",
        section = SADDLE,
        hidden = true
    )
    default int reinBobSpeedPercent()
    {
        return 200;
    }

    @Range(min = -31, max = 31)
    @ConfigItem(
        keyName = "reinBobTiming",
        name = "Reins bob timing",
        description = "Fine-tune the rein cycle so its high and low points match the head; negative values delay it",
        section = SADDLE,
        hidden = true
    )
    default int reinBobTiming()
    {
        return 18;
    }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "leftReinHandForward",
        name = "Left rein hand forward/back",
        description = "Move the rider end of the left rein forward or backward",
        section = SADDLE,
        hidden = true
    )
    default int leftReinHandForward()
    {
        return 41;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "leftReinHandHeight",
        name = "Left rein hand height",
        description = "Raise or lower the rider end of the left rein",
        section = SADDLE,
        hidden = true
    )
    default int leftReinHandHeight()
    {
        return 82;
    }

    @Range(min = -60, max = 60)
    @ConfigItem(
        keyName = "leftReinHandSideways",
        name = "Left rein hand sideways",
        description = "Move the rider end of the left rein sideways into the hand",
        section = SADDLE,
        hidden = true
    )
    default int leftReinHandSideways()
    {
        return 7;
    }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "rightReinHandForward",
        name = "Right rein hand forward/back",
        description = "Move the rider end of the right rein forward or backward",
        section = SADDLE,
        hidden = true
    )
    default int rightReinHandForward()
    {
        return 45;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "rightReinHandHeight",
        name = "Right rein hand height",
        description = "Raise or lower the rider end of the right rein",
        section = SADDLE,
        hidden = true
    )
    default int rightReinHandHeight()
    {
        return 44;
    }

    @Range(min = -60, max = 60)
    @ConfigItem(
        keyName = "rightReinHandSideways",
        name = "Right rein hand sideways",
        description = "Move the rider end of the right rein sideways into the hand",
        section = SADDLE,
        hidden = true
    )
    default int rightReinHandSideways()
    {
        return 0;
    }

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
        keyName = "hideCape",
        name = "Hide cape while mounted",
        description = "Remove the cape from the cosmetic rider when it clips badly; the real player's cape is unchanged",
        section = RIDER
    )
    default boolean hideCape()
    {
        return false;
    }

    @Range(min = -40, max = 40)
    @ConfigItem(
        keyName = "capeBackwardOffset",
        name = "Cape backward offset",
        description = "Move the mounted rider's cape backward or forward to reduce clipping",
        section = RIDER
    )
    default int capeBackwardOffset()
    {
        return 0;
    }

    @Range(min = -40, max = 40)
    @ConfigItem(
        keyName = "capeHeightOffset",
        name = "Cape height offset",
        description = "Raise or lower the mounted rider's cape",
        section = RIDER
    )
    default int capeHeightOffset()
    {
        return 0;
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
        return 98;
    }

    @ConfigItem(
        keyName = "showGryphonSaddle",
        name = "Show gryphon saddle",
        description = "Show the ivory-and-gold Skybound saddle on the gryphon",
        section = GRYPHON
    )
    default boolean showGryphonSaddle()
    {
        return true;
    }

    @Range(min = 40, max = 180)
    @ConfigItem(
        keyName = "gryphonSaddleScale",
        name = "Gryphon saddle scale (%)",
        description = "Resize the gryphon saddle while fitting it",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonSaddleScale()
    {
        return 64;
    }

    @Range(min = -120, max = 120)
    @ConfigItem(
        keyName = "gryphonSaddleForward",
        name = "Gryphon saddle forward/back",
        description = "Move the gryphon saddle along the direction the gryphon faces",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonSaddleForward()
    {
        return 22;
    }

    @Range(min = -120, max = 180)
    @ConfigItem(
        keyName = "gryphonSaddleHeight",
        name = "Gryphon saddle height",
        description = "Raise or lower the gryphon saddle",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonSaddleHeight()
    {
        return 123;
    }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "gryphonSaddleSideways",
        name = "Gryphon saddle sideways",
        description = "Move the gryphon saddle sideways",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonSaddleSideways()
    {
        return 0;
    }

    @Range(min = -100, max = 140)
    @ConfigItem(
        keyName = "gryphonLeftReinHandForward",
        name = "Left rein hand forward/back",
        description = "Move the rider end of the left gryphon rein forward or backward",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonLeftReinHandForward()
    {
        return 56;
    }

    @Range(min = -80, max = 160)
    @ConfigItem(
        keyName = "gryphonLeftReinHandHeight",
        name = "Left rein hand height",
        description = "Raise or lower the rider end of the left gryphon rein",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonLeftReinHandHeight()
    {
        return 42;
    }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "gryphonLeftReinHandSideways",
        name = "Left rein hand sideways",
        description = "Move the rider end of the left gryphon rein sideways into the hand",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonLeftReinHandSideways()
    {
        return -15;
    }

    @Range(min = -100, max = 140)
    @ConfigItem(
        keyName = "gryphonRightReinHandForward",
        name = "Right rein hand forward/back",
        description = "Move the rider end of the right gryphon rein forward or backward",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonRightReinHandForward()
    {
        return 50;
    }

    @Range(min = -80, max = 160)
    @ConfigItem(
        keyName = "gryphonRightReinHandHeight",
        name = "Right rein hand height",
        description = "Raise or lower the rider end of the right gryphon rein",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonRightReinHandHeight()
    {
        return 36;
    }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "gryphonRightReinHandSideways",
        name = "Right rein hand sideways",
        description = "Move the rider end of the right gryphon rein sideways into the hand",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonRightReinHandSideways()
    {
        return 24;
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
        return 44;
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
        return 34;
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
        return 15;
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
