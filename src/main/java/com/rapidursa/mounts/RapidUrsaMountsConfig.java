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
    String BATTLE_TURTLE = "battleTurtle";
    String ARTIO = "artio";
    String ARAXXOR = "araxxor";
    String VORKATH = "vorkath";
    String BIG_WOLF = "bigWolf";
    String CATABLEPON = "catablepon";
    String SHEEP = "sheep";
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

    @ConfigSection(name = "Battle turtle", description = "Battle turtle model, animation, and rider fitting", position = 6, closedByDefault = true)
    String battleTurtleSection = BATTLE_TURTLE;

    @ConfigSection(name = "Battle Bear", description = "Battle Bear model, animation, motion, and rider fitting", position = 7, closedByDefault = true)
    String artioSection = ARTIO;

    @ConfigSection(name = "Araxxor", description = "Initial Araxxor model, animation, and rider fitting", position = 8, closedByDefault = true)
    String araxxorSection = ARAXXOR;

    @ConfigSection(name = "Vorkath", description = "Vorkath model, animations, and rider fitting", position = 9, closedByDefault = true)
    String vorkathSection = VORKATH;

    @ConfigSection(name = "Big wolf", description = "Large wolf mount and rider fitting", position = 10, closedByDefault = true)
    String bigWolfSection = BIG_WOLF;

    @ConfigSection(name = "Catablepon", description = "Catablepon mount and rider fitting", position = 11, closedByDefault = true)
    String catableponSection = CATABLEPON;

    @Range(min = 30, max = 200)
    @ConfigItem(keyName = "bigWolfScale", name = "Big wolf scale (%)", description = "Size of the big wolf", section = BIG_WOLF)
    default int bigWolfScale() { return 120; }
    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "bigWolfIdleAnimation", name = "Idle animation ID", description = "-1 freezes the model until an animation is verified", section = BIG_WOLF)
    default int bigWolfIdleAnimation() { return 6580; }
    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "bigWolfWalkAnimation", name = "Walking animation ID", description = "-1 freezes the model until an animation is verified", section = BIG_WOLF)
    default int bigWolfWalkAnimation() { return 6556; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "bigWolfRiderForward", name = "Rider forward/back", description = "Position the rider along the wolf's back", section = BIG_WOLF)
    default int bigWolfRiderForward() { return -36; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "bigWolfRiderHeight", name = "Rider height", description = "Position the rider above the wolf", section = BIG_WOLF)
    default int bigWolfRiderHeight() { return 125; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "bigWolfRiderSideways", name = "Rider sideways", description = "Position the rider across the wolf's back", section = BIG_WOLF)
    default int bigWolfRiderSideways() { return 0; }

    @Range(min = 30, max = 200)
    @ConfigItem(keyName = "catableponScale", name = "Catablepon scale (%)", description = "Size of the catablepon", section = CATABLEPON)
    default int catableponScale() { return 120; }
    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "catableponIdleAnimation", name = "Idle animation ID", description = "-1 freezes the model until an animation is verified", section = CATABLEPON)
    default int catableponIdleAnimation() { return 4269; }
    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "catableponWalkAnimation", name = "Walking animation ID", description = "-1 freezes the model until an animation is verified", section = CATABLEPON)
    default int catableponWalkAnimation() { return 4268; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "catableponRiderForward", name = "Rider forward/back", description = "Position the rider along the catablepon's back", section = CATABLEPON)
    default int catableponRiderForward() { return -65; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "catableponRiderHeight", name = "Rider height", description = "Position the rider above the catablepon", section = CATABLEPON)
    default int catableponRiderHeight() { return 62; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "catableponRiderSideways", name = "Rider sideways", description = "Position the rider across the catablepon's back", section = CATABLEPON)
    default int catableponRiderSideways() { return 0; }

    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "bigWolfMountedHolsterSideways", name = "Mounted weapon sideways", description = "Adjust the weapon relative to the big wolf rider", section = BIG_WOLF)
    default int bigWolfMountedHolsterSideways() { return 0; }

    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "bigWolfMountedHolsterHeight", name = "Mounted weapon height", description = "Adjust the weapon relative to the big wolf rider", section = BIG_WOLF)
    default int bigWolfMountedHolsterHeight() { return -55; }

    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "bigWolfMountedHolsterForward", name = "Mounted weapon forward/back", description = "Adjust the weapon relative to the big wolf rider", section = BIG_WOLF)
    default int bigWolfMountedHolsterForward() { return 30; }

    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "catableponMountedHolsterSideways", name = "Mounted weapon sideways", description = "Adjust the weapon relative to the catablepon rider", section = CATABLEPON)
    default int catableponMountedHolsterSideways() { return 0; }

    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "catableponMountedHolsterHeight", name = "Mounted weapon height", description = "Adjust the weapon relative to the catablepon rider", section = CATABLEPON)
    default int catableponMountedHolsterHeight() { return -55; }

    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "catableponMountedHolsterForward", name = "Mounted weapon forward/back", description = "Adjust the weapon relative to the catablepon rider", section = CATABLEPON)
    default int catableponMountedHolsterForward() { return 38; }

    @ConfigSection(name = "Sheep?", description = "Sheep? model, animation and rider fitting", position = 12, closedByDefault = true)
    String sheepSection = SHEEP;

    @Range(min = 30, max = 200)
    @ConfigItem(keyName = "sheepScale", name = "Sheep? scale (%)", description = "Size of Sheep?", section = SHEEP)
    default int sheepScale() { return 100; }
    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "sheepIdleAnimation", name = "Idle animation ID", description = "-1 shows the base model until an animation is selected", section = SHEEP)
    default int sheepIdleAnimation() { return 3569; }
    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "sheepWalkAnimation", name = "Walking animation ID", description = "-1 shows the base model until an animation is selected", section = SHEEP)
    default int sheepWalkAnimation() { return 3568; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "sheepRiderForward", name = "Rider forward/back", description = "Position the rider along Sheep?'s back", section = SHEEP)
    default int sheepRiderForward() { return 0; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "sheepRiderHeight", name = "Rider height", description = "Position the rider above Sheep?", section = SHEEP)
    default int sheepRiderHeight() { return 90; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "sheepRiderSideways", name = "Rider sideways", description = "Position the rider across Sheep?'s back", section = SHEEP)
    default int sheepRiderSideways() { return 0; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "sheepMountedHolsterForward", name = "Mounted weapon forward/back", description = "Position the holstered weapon relative to Sheep?'s rider", section = SHEEP)
    default int sheepMountedHolsterForward() { return 0; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "sheepMountedHolsterHeight", name = "Mounted weapon height", description = "Raise or lower the mounted weapon", section = SHEEP)
    default int sheepMountedHolsterHeight() { return -55; }
    @Range(min = -250, max = 250)
    @ConfigItem(keyName = "sheepMountedHolsterSideways", name = "Mounted weapon sideways", description = "Move the mounted weapon across the rider", section = SHEEP)
    default int sheepMountedHolsterSideways() { return 0; }

    @Range(min = 1, max = 30000)
    @ConfigItem(keyName = "vorkathNpcId", name = "Vorkath NPC ID", description = "Model variant used for Vorkath", section = VORKATH)
    default int vorkathNpcId() { return 8061; }

    @Range(min = 30, max = 180)
    @ConfigItem(keyName = "vorkathScale", name = "Vorkath scale (%)", description = "Size of Vorkath", section = VORKATH)
    default int vorkathScale() { return 65; }

    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "vorkathIdleAnimation", name = "Vorkath idle animation", description = "Set after verifying the animation in game; -1 shows the base model", section = VORKATH)
    default int vorkathIdleAnimation() { return 7948; }

    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "vorkathWalkAnimation", name = "Vorkath moving animation", description = "Set after verifying the animation in game; -1 shows the base model", section = VORKATH)
    default int vorkathWalkAnimation() { return 7947; }

    @Range(min = -400, max = 400)
    @ConfigItem(keyName = "vorkathRiderForward", name = "Rider forward/back", description = "Position the rider along Vorkath's back", section = VORKATH)
    default int vorkathRiderForward() { return -59; }

    @Range(min = -400, max = 400)
    @ConfigItem(keyName = "vorkathRiderHeight", name = "Rider height", description = "Raise or lower the rider", section = VORKATH)
    default int vorkathRiderHeight() { return 98; }

    @Range(min = -200, max = 200)
    @ConfigItem(keyName = "vorkathRiderSideways", name = "Rider sideways", description = "Move the rider across Vorkath's back", section = VORKATH)
    default int vorkathRiderSideways() { return 0; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "vorkathWalkForward", name = "Walking rider forward", description = "Additional rider position while moving", section = VORKATH)
    default int vorkathWalkForward() { return 0; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "vorkathWalkHeight", name = "Walking rider height", description = "Additional rider height while moving", section = VORKATH)
    default int vorkathWalkHeight() { return 0; }

    @Range(min = -200, max = 200)
    @ConfigItem(keyName = "vorkathMountedHolsterSideways", name = "Mounted weapon sideways", description = "Move the holstered weapon across the Vorkath rider", section = VORKATH)
    default int vorkathMountedHolsterSideways() { return 0; }

    @Range(min = -200, max = 200)
    @ConfigItem(keyName = "vorkathMountedHolsterHeight", name = "Mounted weapon height", description = "Raise or lower the holstered weapon on the Vorkath rider", section = VORKATH)
    default int vorkathMountedHolsterHeight() { return -55; }

    @Range(min = -200, max = 200)
    @ConfigItem(keyName = "vorkathMountedHolsterForward", name = "Mounted weapon forward/back", description = "Move the holstered weapon along the Vorkath rider", section = VORKATH)
    default int vorkathMountedHolsterForward() { return 0; }

    @Range(min = 30, max = 180)
    @ConfigItem(keyName = "araxxorScale", name = "Araxxor scale (%)",
        description = "Size of the live Araxxor model", section = ARAXXOR)
    default int araxxorScale() { return 70; }

    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "araxxorIdleAnimation", name = "Araxxor idle animation",
        description = "Live Araxxor idle animation ID",
        section = ARAXXOR)
    default int araxxorIdleAnimation() { return 11473; }

    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "araxxorWalkAnimation", name = "Araxxor walk animation",
        description = "Live Araxxor walking animation ID",
        section = ARAXXOR)
    default int araxxorWalkAnimation() { return 11474; }

    @Range(min = -400, max = 400)
    @ConfigItem(keyName = "araxxorRiderForward", name = "Rider forward/back",
        description = "Move the seated rider toward the head or tail", section = ARAXXOR)
    default int araxxorRiderForward() { return -20; }

    @Range(min = -400, max = 400)
    @ConfigItem(keyName = "araxxorRiderHeight", name = "Rider height",
        description = "Raise or lower the seated rider", section = ARAXXOR)
    default int araxxorRiderHeight() { return 110; }

    @Range(min = -200, max = 200)
    @ConfigItem(keyName = "araxxorRiderSideways", name = "Rider sideways",
        description = "Move the seated rider across Araxxor's back", section = ARAXXOR)
    default int araxxorRiderSideways() { return 0; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "araxxorWalkForward", name = "Walking rider forward",
        description = "Additional rider forward/back offset while moving", section = ARAXXOR)
    default int araxxorWalkForward() { return 0; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "araxxorWalkHeight", name = "Walking rider height",
        description = "Additional rider height while moving", section = ARAXXOR)
    default int araxxorWalkHeight() { return 0; }

    @Range(min = 0, max = 40)
    @ConfigItem(keyName = "araxxorIdleBounce", name = "Idle rider bob",
        description = "Rider bounce while Araxxor is idle", section = ARAXXOR)
    default int araxxorIdleBounce() { return 0; }

    @ConfigItem(
        keyName = "showSaddleAndReins",
        name = "Show saddle and reins",
        description = "Show the fitted saddle and animated reins when riding the Black unicorn in Wide or Cross-legged pose",
        section = SADDLE,
        hidden = true
    )
    default boolean showSaddleAndReins()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showSaddlePrototype",
        name = "Show unicorn saddle",
        description = "Show the fitted saddle on the Black unicorn",
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
        description = "Resize the fitted unicorn saddle",
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
        description = "Raise or lower the fitted unicorn saddle",
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
        keyName = "mountedHolster",
        name = "Rapid Holster while mounted",
        description = "Show equipped gear on the seated rider when a compatible Rapid Holster crossover build is running",
        section = RIDER
    )
    default boolean mountedHolster()
    {
        return true;
    }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "unicornMountedHolsterSideways",
        name = "Wide holster sideways",
        description = "Move mounted gear left or right without changing on-foot placement",
        section = UNICORN)
    default int unicornMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "unicornMountedHolsterHeight",
        name = "Wide holster height",
        description = "Move mounted gear up or down; negative lowers without changing on-foot placement",
        section = UNICORN)
    default int unicornMountedHolsterHeight() { return -17; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "unicornMountedHolsterForward",
        name = "Wide holster forward",
        description = "Move mounted gear forward or back without changing on-foot placement",
        section = UNICORN)
    default int unicornMountedHolsterForward() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "terrorbirdMountedHolsterSideways",
        name = "Standard holster sideways",
        description = "Move mounted gear left or right without changing on-foot placement",
        section = TERRORBIRD)
    default int terrorbirdMountedHolsterSideways() { return -8; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "terrorbirdMountedHolsterHeight",
        name = "Standard holster height",
        description = "Move mounted gear up or down; negative lowers without changing on-foot placement",
        section = TERRORBIRD)
    default int terrorbirdMountedHolsterHeight() { return -36; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "terrorbirdMountedHolsterForward",
        name = "Standard holster forward",
        description = "Move mounted gear forward or back without changing on-foot placement",
        section = TERRORBIRD)
    default int terrorbirdMountedHolsterForward() { return 9; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "lavaDragonMountedHolsterSideways",
        name = "Standard holster sideways",
        description = "Move mounted gear left or right without changing on-foot placement",
        section = LAVA_DRAGON)
    default int lavaDragonMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "lavaDragonMountedHolsterHeight",
        name = "Standard holster height",
        description = "Move mounted gear up or down; negative lowers without changing on-foot placement",
        section = LAVA_DRAGON)
    default int lavaDragonMountedHolsterHeight() { return -40; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "lavaDragonMountedHolsterForward",
        name = "Standard holster forward",
        description = "Move mounted gear forward or back without changing on-foot placement",
        section = LAVA_DRAGON)
    default int lavaDragonMountedHolsterForward() { return 12; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "gryphonMountedHolsterSideways",
        name = "Standard holster sideways",
        description = "Move mounted gear left or right without changing on-foot placement",
        section = GRYPHON)
    default int gryphonMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "gryphonMountedHolsterHeight",
        name = "Standard holster height",
        description = "Move mounted gear up or down; negative lowers without changing on-foot placement",
        section = GRYPHON)
    default int gryphonMountedHolsterHeight() { return -34; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "gryphonMountedHolsterForward",
        name = "Standard holster forward",
        description = "Move mounted gear forward or back without changing on-foot placement",
        section = GRYPHON)
    default int gryphonMountedHolsterForward() { return 8; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "battleTurtleMountedHolsterSideways",
        name = "Cross-legged holster sideways",
        description = "Move mounted gear left or right without changing on-foot placement",
        section = BATTLE_TURTLE)
    default int battleTurtleMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "battleTurtleMountedHolsterHeight",
        name = "Cross-legged holster height",
        description = "Move mounted gear up or down; negative lowers without changing on-foot placement",
        section = BATTLE_TURTLE)
    default int battleTurtleMountedHolsterHeight() { return -89; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "battleTurtleMountedHolsterForward",
        name = "Cross-legged holster forward",
        description = "Move mounted gear forward or back without changing on-foot placement",
        section = BATTLE_TURTLE)
    default int battleTurtleMountedHolsterForward() { return 11; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioMountedHolsterSideways",
        name = "Extra Wide holster sideways",
        description = "Move mounted gear left or right without changing on-foot placement",
        section = ARTIO)
    default int artioMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioMountedHolsterHeight",
        name = "Extra Wide holster height",
        description = "Move mounted gear up or down; negative lowers without changing on-foot placement",
        section = ARTIO)
    default int artioMountedHolsterHeight() { return -55; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioMountedHolsterForward",
        name = "Extra Wide holster forward",
        description = "Move mounted gear forward or back without changing on-foot placement",
        section = ARTIO)
    default int artioMountedHolsterForward() { return 25; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "araxxorMountedHolsterSideways",
        name = "Extra Wide holster sideways",
        description = "Move mounted gear left or right without changing on-foot placement",
        section = ARAXXOR)
    default int araxxorMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "araxxorMountedHolsterHeight",
        name = "Extra Wide holster height",
        description = "Move mounted gear up or down; negative lowers without changing on-foot placement",
        section = ARAXXOR)
    default int araxxorMountedHolsterHeight() { return -55; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "araxxorMountedHolsterForward",
        name = "Extra Wide holster forward",
        description = "Move mounted gear forward or back without changing on-foot placement",
        section = ARAXXOR)
    default int araxxorMountedHolsterForward() { return 28; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "unicornStandardMountedHolsterSideways",
        name = "Standard holster sideways",
        description = "Move Unicorn's Standard mounted gear left or right; on-foot settings are unaffected",
        section = UNICORN)
    default int unicornStandardMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "unicornStandardMountedHolsterHeight",
        name = "Standard holster height",
        description = "Move Unicorn's Standard mounted gear up or down; negative lowers; on-foot settings are unaffected",
        section = UNICORN)
    default int unicornStandardMountedHolsterHeight() { return -29; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "unicornStandardMountedHolsterForward",
        name = "Standard holster forward",
        description = "Move Unicorn's Standard mounted gear forward or back; on-foot settings are unaffected",
        section = UNICORN)
    default int unicornStandardMountedHolsterForward() { return 12; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "terrorbirdWideMountedHolsterSideways",
        name = "Wide holster sideways",
        description = "Move Terrorbird's Wide mounted gear left or right; on-foot settings are unaffected",
        section = TERRORBIRD)
    default int terrorbirdWideMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "terrorbirdWideMountedHolsterHeight",
        name = "Wide holster height",
        description = "Move Terrorbird's Wide mounted gear up or down; negative lowers; on-foot settings are unaffected",
        section = TERRORBIRD)
    default int terrorbirdWideMountedHolsterHeight() { return -13; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "terrorbirdWideMountedHolsterForward",
        name = "Wide holster forward",
        description = "Move Terrorbird's Wide mounted gear forward or back; on-foot settings are unaffected",
        section = TERRORBIRD)
    default int terrorbirdWideMountedHolsterForward() { return -5; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "lavaDragonCrossleggedMountedHolsterSideways",
        name = "Cross-legged holster sideways",
        description = "Move Lava dragon's Cross-legged mounted gear left or right; on-foot settings are unaffected",
        section = LAVA_DRAGON)
    default int lavaDragonCrossleggedMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "lavaDragonCrossleggedMountedHolsterHeight",
        name = "Cross-legged holster height",
        description = "Move Lava dragon's Cross-legged mounted gear up or down; negative lowers; on-foot settings are unaffected",
        section = LAVA_DRAGON)
    default int lavaDragonCrossleggedMountedHolsterHeight() { return -93; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "lavaDragonCrossleggedMountedHolsterForward",
        name = "Cross-legged holster forward",
        description = "Move Lava dragon's Cross-legged mounted gear forward or back; on-foot settings are unaffected",
        section = LAVA_DRAGON)
    default int lavaDragonCrossleggedMountedHolsterForward() { return 5; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioStandardMountedHolsterSideways",
        name = "Standard holster sideways",
        description = "Move Battle Bear's Standard mounted gear left or right; on-foot settings are unaffected",
        section = ARTIO)
    default int artioStandardMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioStandardMountedHolsterHeight",
        name = "Standard holster height",
        description = "Move Battle Bear's Standard mounted gear up or down; negative lowers; on-foot settings are unaffected",
        section = ARTIO)
    default int artioStandardMountedHolsterHeight() { return -41; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioStandardMountedHolsterForward",
        name = "Standard holster forward",
        description = "Move Battle Bear's Standard mounted gear forward or back; on-foot settings are unaffected",
        section = ARTIO)
    default int artioStandardMountedHolsterForward() { return 10; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioNoSaddleMountedHolsterSideways",
        name = "No Saddle holster sideways",
        description = "Move Battle Bear's No Saddle mounted gear left or right; on-foot settings are unaffected",
        section = ARTIO)
    default int artioNoSaddleMountedHolsterSideways() { return 0; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioNoSaddleMountedHolsterHeight",
        name = "No Saddle holster height",
        description = "Move Battle Bear's No Saddle mounted gear up or down; negative lowers; on-foot settings are unaffected",
        section = ARTIO)
    default int artioNoSaddleMountedHolsterHeight() { return -55; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioNoSaddleMountedHolsterForward",
        name = "No Saddle holster forward",
        description = "Move Battle Bear's No Saddle mounted gear forward or back; on-foot settings are unaffected",
        section = ARTIO)
    default int artioNoSaddleMountedHolsterForward() { return 27; }

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
        section = RIDER,
        hidden = true
    )
    default boolean useRidingPose()
    {
        return true;
    }

    @ConfigItem(
        keyName = "ridingPose",
        name = "Riding pose",
        description = "Pose selected through the mount-aware Mount Stable selector",
        section = RIDER,
        hidden = true
    )
    default RidingPose ridingPose()
    {
        return RidingPose.STANDARD;
    }

    @Range(min = 0, max = 30000)
    @ConfigItem(
        keyName = "extraWideIdleAnimationId",
        name = "Extra Wide idle animation ID",
        description = "Player animation used by Extra Wide while stationary",
        section = RIDER,
        hidden = true
    )
    default int extraWideIdleAnimationId()
    {
        return 1461;
    }

    @Range(min = 0, max = 30000)
    @ConfigItem(
        keyName = "extraWideWalkAnimationId",
        name = "Extra Wide moving animation ID",
        description = "Player animation used by Extra Wide while moving",
        section = RIDER,
        hidden = true
    )
    default int extraWideWalkAnimationId()
    {
        return 1462;
    }

    @Range(min = 0, max = 20000)
    @ConfigItem(
        keyName = "crossLeggedAnimationId",
        name = "Cross-legged animation ID",
        description = "Advanced override for the player animation used by the Cross-legged pose",
        section = RIDER,
        hidden = true
    )
    default int crossLeggedAnimationId()
    {
        return 10061;
    }

    @Range(min = 0, max = 300)
    @ConfigItem(
        keyName = "crossLeggedLoopStartFrame",
        name = "Cross-legged loop start",
        description = "First settled Sit-emote frame used by the Cross-legged loop",
        section = RIDER,
        hidden = true
    )
    default int crossLeggedLoopStartFrame()
    {
        return 9;
    }

    @Range(min = 1, max = 300)
    @ConfigItem(
        keyName = "crossLeggedLoopEndFrame",
        name = "Cross-legged loop end",
        description = "Last settled Sit-emote frame before returning to the loop start",
        section = RIDER,
        hidden = true
    )
    default int crossLeggedLoopEndFrame()
    {
        return 32;
    }

    @Range(min = 0, max = 200)
    @ConfigItem(
        keyName = "crossLeggedRiderHeight",
        name = "Cross-legged rider height",
        description = "Raise or lower the rider only when using the Cross-legged pose",
        section = RIDER,
        hidden = true
    )
    default int crossLeggedRiderHeight()
    {
        return 80;
    }

    @Range(min = -150, max = 150)
    @ConfigItem(
        keyName = "crossLeggedRiderForward",
        name = "Cross-legged forward/back",
        description = "Move the Cross-legged rider forward or backward on the mount",
        section = RIDER,
        hidden = true
    )
    default int crossLeggedRiderForward()
    {
        return 10;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "crossLeggedRiderSideways",
        name = "Cross-legged sideways",
        description = "Move the Cross-legged rider left or right on the mount",
        section = RIDER,
        hidden = true
    )
    default int crossLeggedRiderSideways()
    {
        return 0;
    }

    @Range(min = 0, max = 200)
    @ConfigItem(
        keyName = "riderHeight",
        name = "Rider height",
        description = "Raise or lower the rider on the black unicorn",
        section = UNICORN,
        hidden = true
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
        section = UNICORN,
        hidden = true
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
        section = UNICORN,
        hidden = true
    )
    default int riderSideways()
    {
        return 0;
    }

    @Range(min = 0, max = 200)
    @ConfigItem(keyName = "unicornCrossLeggedRiderHeight", name = "Cross-legged rider height", description = "Raise or lower the Cross-legged rider on the black unicorn", section = UNICORN, hidden = true)
    default int unicornCrossLeggedRiderHeight() { return 132; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "unicornCrossLeggedRiderForward", name = "Cross-legged forward/back", description = "Move the Cross-legged rider forward or backward on the black unicorn", section = UNICORN, hidden = true)
    default int unicornCrossLeggedRiderForward() { return 9; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "unicornCrossLeggedRiderSideways", name = "Cross-legged sideways", description = "Move the Cross-legged rider sideways on the black unicorn", section = UNICORN, hidden = true)
    default int unicornCrossLeggedRiderSideways() { return 0; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "unicornExtraWideRiderHeight", name = "Extra Wide rider height", description = "Raise or lower the Extra Wide rider on the black unicorn", section = UNICORN, hidden = true)
    default int unicornExtraWideRiderHeight() { return 132; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "unicornExtraWideRiderForward", name = "Extra Wide forward/back", description = "Move the Extra Wide rider forward or backward on the black unicorn", section = UNICORN, hidden = true)
    default int unicornExtraWideRiderForward() { return 9; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "unicornExtraWideRiderSideways", name = "Extra Wide sideways", description = "Move the Extra Wide rider sideways on the black unicorn", section = UNICORN, hidden = true)
    default int unicornExtraWideRiderSideways() { return 0; }

    @Range(min = 0, max = 12)
    @ConfigItem(
        keyName = "unicornIdleBounce",
        name = "Unicorn idle bounce",
        description = "Subtle rider lift synchronized to the unicorn's idle animation",
        section = UNICORN,
        hidden = true
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

    @Range(min = -120, max = 120)
    @ConfigItem(keyName = "terrorbirdSeatRiderForward", name = "Standard rider forward/back", description = "Fine-tune the Standard pose Terrorbird rider independently of its saddle", section = TERRORBIRD)
    default int terrorbirdSeatRiderForward() { return 0; }

    @Range(min = -120, max = 120)
    @ConfigItem(keyName = "terrorbirdSeatRiderHeight", name = "Standard rider up/down", description = "Fine-tune the Standard pose Terrorbird rider height independently of its saddle", section = TERRORBIRD)
    default int terrorbirdSeatRiderHeight() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "terrorbirdSeatRiderSideways", name = "Standard rider left/right", description = "Fine-tune the Standard pose Terrorbird rider sideways independently of its saddle", section = TERRORBIRD)
    default int terrorbirdSeatRiderSideways() { return 0; }

    @Range(min = -120, max = 120)
    @ConfigItem(keyName = "terrorbirdSeatSaddleForward", name = "Saddle forward/back", description = "Fine-tune the Terrorbird saddle independently of its rider", section = TERRORBIRD)
    default int terrorbirdSeatSaddleForward() { return 0; }

    @Range(min = -120, max = 120)
    @ConfigItem(keyName = "terrorbirdSeatSaddleHeight", name = "Saddle up/down", description = "Fine-tune the Terrorbird saddle height independently of its rider", section = TERRORBIRD)
    default int terrorbirdSeatSaddleHeight() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "terrorbirdSeatSaddleSideways", name = "Saddle left/right", description = "Fine-tune the Terrorbird saddle sideways independently of its rider", section = TERRORBIRD)
    default int terrorbirdSeatSaddleSideways() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "terrorbirdLeftReinHandForward", name = "Left rein hand forward/back", description = "Move the left Terrorbird rein end forward or backward at the rider's hand", section = TERRORBIRD)
    default int terrorbirdLeftReinHandForward() { return 5; }

    @Range(min = -100, max = 120)
    @ConfigItem(keyName = "terrorbirdLeftReinHandHeight", name = "Left rein hand height", description = "Raise or lower the left Terrorbird rein end at the rider's hand", section = TERRORBIRD)
    default int terrorbirdLeftReinHandHeight() { return 48; }

    @Range(min = -80, max = 80)
    @ConfigItem(keyName = "terrorbirdLeftReinHandSideways", name = "Left rein hand sideways", description = "Move the left Terrorbird rein end sideways", section = TERRORBIRD)
    default int terrorbirdLeftReinHandSideways() { return -18; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "terrorbirdRightReinHandForward", name = "Right rein hand forward/back", description = "Move the right Terrorbird rein end forward or backward at the rider's hand", section = TERRORBIRD)
    default int terrorbirdRightReinHandForward() { return 5; }

    @Range(min = -100, max = 120)
    @ConfigItem(keyName = "terrorbirdRightReinHandHeight", name = "Right rein hand height", description = "Raise or lower the right Terrorbird rein end at the rider's hand", section = TERRORBIRD)
    default int terrorbirdRightReinHandHeight() { return 48; }

    @Range(min = -80, max = 80)
    @ConfigItem(keyName = "terrorbirdRightReinHandSideways", name = "Right rein hand sideways", description = "Move the right Terrorbird rein end sideways", section = TERRORBIRD)
    default int terrorbirdRightReinHandSideways() { return 18; }

    @Range(min = 40, max = 180)
    @ConfigItem(keyName = "terrorbirdReinHeadForward", name = "Reins head forward/back", description = "Fine-tune both animated rein ends at the Terrorbird's beak", section = TERRORBIRD)
    default int terrorbirdReinHeadForward() { return 90; }

    @Range(min = -60, max = 100)
    @ConfigItem(keyName = "terrorbirdReinHeadHeight", name = "Reins head height", description = "Raise or lower both animated rein ends at the Terrorbird's beak", section = TERRORBIRD)
    default int terrorbirdReinHeadHeight() { return 25; }

    @Range(min = 2, max = 50)
    @ConfigItem(keyName = "terrorbirdReinHeadSpread", name = "Reins head spread", description = "Move the animated rein ends across the Terrorbird's beak", section = TERRORBIRD)
    default int terrorbirdReinHeadSpread() { return 12; }

    @Range(min = 0, max = 200)
    @ConfigItem(
        keyName = "terrorbirdRiderHeight",
        name = "Terrorbird rider height",
        description = "Raise or lower the rider on the terrorbird",
        section = TERRORBIRD,
        hidden = true
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
        section = TERRORBIRD,
        hidden = true
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
        section = TERRORBIRD,
        hidden = true
    )
    default int terrorbirdRiderSideways()
    {
        return 0;
    }

    @Range(min = 0, max = 200)
    @ConfigItem(keyName = "terrorbirdCrossLeggedRiderHeight", name = "Cross-legged rider height", description = "Raise or lower the Cross-legged rider on the terrorbird", section = TERRORBIRD, hidden = true)
    default int terrorbirdCrossLeggedRiderHeight() { return 115; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "terrorbirdCrossLeggedRiderForward", name = "Cross-legged forward/back", description = "Move the Cross-legged rider forward or backward on the terrorbird", section = TERRORBIRD, hidden = true)
    default int terrorbirdCrossLeggedRiderForward() { return 13; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "terrorbirdCrossLeggedRiderSideways", name = "Cross-legged sideways", description = "Move the Cross-legged rider sideways on the terrorbird", section = TERRORBIRD, hidden = true)
    default int terrorbirdCrossLeggedRiderSideways() { return 0; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "terrorbirdExtraWideRiderHeight", name = "Extra Wide rider height", description = "Raise or lower the Extra Wide rider on the terrorbird", section = TERRORBIRD, hidden = true)
    default int terrorbirdExtraWideRiderHeight() { return 115; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "terrorbirdExtraWideRiderForward", name = "Extra Wide forward/back", description = "Move the Extra Wide rider forward or backward on the terrorbird", section = TERRORBIRD, hidden = true)
    default int terrorbirdExtraWideRiderForward() { return 13; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "terrorbirdExtraWideRiderSideways", name = "Extra Wide sideways", description = "Move the Extra Wide rider sideways on the terrorbird", section = TERRORBIRD, hidden = true)
    default int terrorbirdExtraWideRiderSideways() { return 0; }

    @Range(min = 0, max = 12)
    @ConfigItem(
        keyName = "terrorbirdIdleBounce",
        name = "Terrorbird idle bounce",
        description = "Subtle rider lift synchronized to the terrorbird's idle animation",
        section = TERRORBIRD,
        hidden = true
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
        section = TERRORBIRD,
        hidden = true
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
        section = TERRORBIRD,
        hidden = true
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
        section = TERRORBIRD,
        hidden = true
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
        section = TERRORBIRD,
        hidden = true
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
        section = TERRORBIRD,
        hidden = true
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
        section = LAVA_DRAGON,
        hidden = true
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
        section = LAVA_DRAGON,
        hidden = true
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
        section = LAVA_DRAGON,
        hidden = true
    )
    default int lavaDragonRiderSideways()
    {
        return 6;
    }

    @Range(min = 0, max = 200)
    @ConfigItem(keyName = "lavaDragonCrossLeggedRiderHeight", name = "Cross-legged rider height", description = "Raise or lower the Cross-legged rider on the lava dragon", section = LAVA_DRAGON, hidden = true)
    default int lavaDragonCrossLeggedRiderHeight() { return 139; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "lavaDragonCrossLeggedRiderForward", name = "Cross-legged forward/back", description = "Move the Cross-legged rider forward or backward on the lava dragon", section = LAVA_DRAGON, hidden = true)
    default int lavaDragonCrossLeggedRiderForward() { return 50; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "lavaDragonCrossLeggedRiderSideways", name = "Cross-legged sideways", description = "Move the Cross-legged rider sideways on the lava dragon", section = LAVA_DRAGON, hidden = true)
    default int lavaDragonCrossLeggedRiderSideways() { return 0; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "lavaDragonExtraWideRiderHeight", name = "Extra Wide rider height", description = "Raise or lower the Extra Wide rider on the lava dragon", section = LAVA_DRAGON, hidden = true)
    default int lavaDragonExtraWideRiderHeight() { return 139; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "lavaDragonExtraWideRiderForward", name = "Extra Wide forward/back", description = "Move the Extra Wide rider forward or backward on the lava dragon", section = LAVA_DRAGON, hidden = true)
    default int lavaDragonExtraWideRiderForward() { return 50; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "lavaDragonExtraWideRiderSideways", name = "Extra Wide sideways", description = "Move the Extra Wide rider sideways on the lava dragon", section = LAVA_DRAGON, hidden = true)
    default int lavaDragonExtraWideRiderSideways() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "lavaDragonWalkHeightAdjustment",
        name = "Lava dragon walk height adjust",
        description = "Change rider height only while the lava dragon is moving",
        section = LAVA_DRAGON,
        hidden = true
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
        section = LAVA_DRAGON,
        hidden = true
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
        section = LAVA_DRAGON,
        hidden = true
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
        section = LAVA_DRAGON,
        hidden = true
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
        section = LAVA_DRAGON,
        hidden = true
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
        section = LAVA_DRAGON,
        hidden = true
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

    @Range(min = 0, max = 200)
    @ConfigItem(keyName = "gryphonCrossLeggedRiderHeight", name = "Cross-legged rider height", description = "Raise or lower the Cross-legged rider on the gryphon", section = GRYPHON, hidden = true)
    default int gryphonCrossLeggedRiderHeight() { return 120; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "gryphonCrossLeggedRiderForward", name = "Cross-legged forward/back", description = "Move the Cross-legged rider forward or backward on the gryphon", section = GRYPHON, hidden = true)
    default int gryphonCrossLeggedRiderForward() { return 13; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "gryphonCrossLeggedRiderSideways", name = "Cross-legged sideways", description = "Move the Cross-legged rider sideways on the gryphon", section = GRYPHON, hidden = true)
    default int gryphonCrossLeggedRiderSideways() { return 0; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "gryphonExtraWideRiderHeight", name = "Extra Wide rider height", description = "Raise or lower the Extra Wide rider on the gryphon", section = GRYPHON, hidden = true)
    default int gryphonExtraWideRiderHeight() { return 93; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "gryphonExtraWideRiderForward", name = "Extra Wide forward/back", description = "Move the Extra Wide rider forward or backward on the gryphon", section = GRYPHON, hidden = true)
    default int gryphonExtraWideRiderForward() { return -22; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "gryphonExtraWideRiderSideways", name = "Extra Wide sideways", description = "Move the Extra Wide rider sideways on the gryphon", section = GRYPHON, hidden = true)
    default int gryphonExtraWideRiderSideways() { return 0; }

    @ConfigItem(
        keyName = "showGryphonSaddle",
        name = "Show gryphon saddle",
        description = "Show the ivory-and-gold Skybound saddle on the gryphon",
        section = GRYPHON,
        hidden = true
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
        name = "Saddle up/down",
        description = "Raise or lower the gryphon saddle",
        section = GRYPHON,
        hidden = true
    )
    default int gryphonSaddleHeight()
    {
        return 132;
    }

    @Range(min = -80, max = 80)
    @ConfigItem(
        keyName = "gryphonSaddleSideways",
        name = "Saddle left/right",
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
        section = GRYPHON,
        hidden = true
    )
    default int gryphonRiderHeight()
    {
        return 57;
    }

    @Range(min = 0, max = 300)
    @ConfigItem(
        keyName = "gryphonWideRiderHeight",
        name = "Wide pose rider height",
        description = "Raise or lower the rider when using the Wide riding pose",
        section = GRYPHON,
        hidden = true
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
        section = GRYPHON,
        hidden = true
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
        section = GRYPHON,
        hidden = true
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
        section = GRYPHON,
        hidden = true
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
        section = GRYPHON,
        hidden = true
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
        section = GRYPHON,
        hidden = true
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
        section = GRYPHON,
        hidden = true
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
        section = GRYPHON,
        hidden = true
    )
    default int gryphonLateralSway()
    {
        return 4;
    }

    @Range(min = 0, max = 30000)
    @ConfigItem(
        keyName = "battleTurtleNpcId",
        name = "Battle turtle NPC ID",
        description = "NPC definition used for the Battle turtle",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleNpcId()
    {
        return 6076;
    }

    @Range(min = 50, max = 180)
    @ConfigItem(
        keyName = "battleTurtleScale",
        name = "Battle turtle scale (%)",
        description = "Resize the Battle turtle model",
        section = BATTLE_TURTLE
    )
    default int battleTurtleScale()
    {
        return 100;
    }

    @Range(min = -1, max = 30000)
    @ConfigItem(
        keyName = "battleTurtleIdleAnimation",
        name = "Battle turtle idle animation",
        description = "Idle animation ID; use -1 while identifying the model",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleIdleAnimation()
    {
        return 3952;
    }

    @Range(min = -1, max = 30000)
    @ConfigItem(
        keyName = "battleTurtleWalkAnimation",
        name = "Battle turtle walk animation",
        description = "Walking animation ID; use -1 while identifying the model",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleWalkAnimation()
    {
        return 3953;
    }

    @Range(min = 0, max = 15)
    @ConfigItem(
        keyName = "battleTurtleIdleBounce",
        name = "Battle turtle idle bob",
        description = "Move the rider and saddle together with the turtle's idle animation",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleIdleBounce()
    {
        return 2;
    }

    @Range(min = -20, max = 20)
    @ConfigItem(
        keyName = "battleTurtleIdleBobTiming",
        name = "Idle bob timing",
        description = "Shift the idle bob along the animation; positive values move it earlier",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleIdleBobTiming()
    {
        return 4;
    }

    @Range(min = 0, max = 20)
    @ConfigItem(
        keyName = "battleTurtleWalkBounce",
        name = "Battle turtle walking bob",
        description = "Move the rider and saddle together with the turtle's walking animation",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleWalkBounce()
    {
        return 5;
    }

    @Range(min = -20, max = 20)
    @ConfigItem(
        keyName = "battleTurtleWalkBobTiming",
        name = "Walking bob timing",
        description = "Shift the walking bob along the animation; positive values move it earlier",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleWalkBobTiming()
    {
        return 0;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "battleTurtleWalkForwardAdjustment",
        name = "Walking rider forward/back",
        description = "Move only the rider forward or backward while the Battle turtle is walking",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleWalkForwardAdjustment()
    {
        return 0;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "battleTurtleWalkHeightAdjustment",
        name = "Walking rider height",
        description = "Raise or lower only the rider while the Battle turtle is walking",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleWalkHeightAdjustment()
    {
        return 0;
    }

    @Range(min = 0, max = 200)
    @ConfigItem(keyName = "battleTurtleCrossLeggedRiderHeight", name = "Cross-legged rider height", description = "Raise or lower the Cross-legged rider on the Battle turtle", section = BATTLE_TURTLE, hidden = true)
    default int battleTurtleCrossLeggedRiderHeight() { return 176; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "battleTurtleCrossLeggedRiderForward", name = "Cross-legged forward/back", description = "Move the Cross-legged rider forward or backward on the Battle turtle", section = BATTLE_TURTLE, hidden = true)
    default int battleTurtleCrossLeggedRiderForward() { return 18; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "battleTurtleCrossLeggedRiderSideways", name = "Cross-legged sideways", description = "Move the Cross-legged rider sideways on the Battle turtle", section = BATTLE_TURTLE, hidden = true)
    default int battleTurtleCrossLeggedRiderSideways() { return 0; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "battleTurtleExtraWideRiderHeight", name = "Extra Wide rider height", description = "Raise or lower the Extra Wide rider on the Battle turtle", section = BATTLE_TURTLE, hidden = true)
    default int battleTurtleExtraWideRiderHeight() { return 176; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "battleTurtleExtraWideRiderForward", name = "Extra Wide forward/back", description = "Move the Extra Wide rider forward or backward on the Battle turtle", section = BATTLE_TURTLE, hidden = true)
    default int battleTurtleExtraWideRiderForward() { return 18; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "battleTurtleExtraWideRiderSideways", name = "Extra Wide sideways", description = "Move the Extra Wide rider sideways on the Battle turtle", section = BATTLE_TURTLE, hidden = true)
    default int battleTurtleExtraWideRiderSideways() { return 0; }

    @ConfigItem(
        keyName = "showBattleTurtleSaddle",
        name = "Show battle saddle",
        description = "Show the armoured saddle and cannon on the Battle turtle",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default boolean showBattleTurtleSaddle()
    {
        return true;
    }

    @Range(min = 40, max = 180)
    @ConfigItem(
        keyName = "battleTurtleSaddleScale",
        name = "Battle saddle scale (%)",
        description = "Resize the Battle turtle saddle while fitting it",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleSaddleScale()
    {
        return 100;
    }

    @Range(min = -150, max = 150)
    @ConfigItem(
        keyName = "battleTurtleSaddleForward",
        name = "Battle saddle forward/back",
        description = "Move the battle saddle along the turtle's shell",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleSaddleForward()
    {
        return 25;
    }

    @Range(min = -150, max = 200)
    @ConfigItem(
        keyName = "battleTurtleSaddleHeight",
        name = "Battle saddle height",
        description = "Raise or lower the battle saddle",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleSaddleHeight()
    {
        return 150;
    }

    @Range(min = -100, max = 100)
    @ConfigItem(
        keyName = "battleTurtleSaddleSideways",
        name = "Battle saddle sideways",
        description = "Move the battle saddle sideways across the shell",
        section = BATTLE_TURTLE,
        hidden = true
    )
    default int battleTurtleSaddleSideways()
    {
        return 0;
    }

    @Range(min = 0, max = 30000)
    @ConfigItem(keyName = "artioNpcId", name = "Battle Bear NPC ID", description = "NPC definition used for the Battle Bear", section = ARTIO)
    default int artioNpcId() { return 11992; }

    @Range(min = 50, max = 180)
    @ConfigItem(keyName = "artioScale", name = "Battle Bear scale (%)", description = "Resize the Battle Bear model", section = ARTIO)
    default int artioScale() { return 104; }

    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "artioIdleAnimationV2", name = "Battle Bear idle animation", description = "Battle Bear idle animation ID", section = ARTIO)
    default int artioIdleAnimation() { return 10011; }

    @Range(min = -1, max = 30000)
    @ConfigItem(keyName = "artioWalkAnimationV2", name = "Battle Bear walk animation", description = "Battle Bear walking animation ID", section = ARTIO)
    default int artioWalkAnimation() { return 10009; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "artioRiderHeight", name = "Rider height", description = "Raise or lower the rider on the Battle Bear", section = ARTIO)
    default int artioRiderHeight() { return 120; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioRiderForward", name = "Rider forward/back", description = "Move the rider forward or backward on the Battle Bear", section = ARTIO)
    default int artioRiderForward() { return 3; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioRiderSideways", name = "Rider sideways", description = "Move the rider sideways on the Battle Bear", section = ARTIO)
    default int artioRiderSideways() { return -2; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "artioCrossLeggedRiderHeight", name = "Cross-legged rider height", description = "Raise or lower the Cross-legged rider on the Battle Bear", section = ARTIO)
    default int artioCrossLeggedRiderHeight() { return 120; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioCrossLeggedRiderForward", name = "Cross-legged forward/back", description = "Move the Cross-legged rider forward or backward on the Battle Bear", section = ARTIO)
    default int artioCrossLeggedRiderForward() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioCrossLeggedRiderSideways", name = "Cross-legged sideways", description = "Move the Cross-legged rider sideways on the Battle Bear", section = ARTIO)
    default int artioCrossLeggedRiderSideways() { return 0; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "artioExtraWideRiderHeight", name = "Extra Wide rider height", description = "Raise or lower the Extra Wide rider on the Battle Bear", section = ARTIO)
    default int artioExtraWideRiderHeight() { return 159; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioExtraWideRiderForward", name = "Extra Wide forward/back", description = "Move the Extra Wide rider forward or backward on the Battle Bear", section = ARTIO)
    default int artioExtraWideRiderForward() { return -14; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideRiderSideways", name = "Extra Wide sideways", description = "Move the Extra Wide rider sideways on the Battle Bear", section = ARTIO)
    default int artioExtraWideRiderSideways() { return -3; }

    @Range(min = -100, max = 250)
    @ConfigItem(keyName = "artioNoSaddleRiderHeight", name = "No Saddle rider height", description = "Raise or lower the bareback Extra Wide rider", section = ARTIO)
    default int artioNoSaddleRiderHeight() { return 99; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioNoSaddleRiderForward", name = "No Saddle forward/back", description = "Move the bareback Extra Wide rider forward or backward", section = ARTIO)
    default int artioNoSaddleRiderForward() { return -51; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioNoSaddleRiderSideways", name = "No Saddle sideways", description = "Move the bareback Extra Wide rider sideways", section = ARTIO)
    default int artioNoSaddleRiderSideways() { return -3; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioExtraWideSaddleWidth", name = "Extra Wide saddle width (%)", description = "Resize the ribbed Extra Wide saddle from side to side", section = ARTIO)
    default int artioExtraWideSaddleWidth() { return 71; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioExtraWideSaddleLength", name = "Extra Wide saddle length (%)", description = "Resize the ribbed Extra Wide saddle forward and backward", section = ARTIO)
    default int artioExtraWideSaddleLength() { return 101; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioExtraWideSaddleThickness", name = "Extra Wide saddle thickness (%)", description = "Resize the thickness of the ribbed Extra Wide saddle", section = ARTIO)
    default int artioExtraWideSaddleThickness() { return 68; }

    @Range(min = 4, max = 60)
    @ConfigItem(keyName = "artioExtraWideSaddleBodyHeight", name = "Extra Wide saddle body height", description = "Make the ribbed Extra Wide saddle body taller or shorter without moving it", section = ARTIO)
    default int artioExtraWideSaddleBodyHeight() { return 26; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideSaddleForward", name = "Extra Wide saddle forward/back", description = "Move the ribbed Extra Wide saddle along the Battle Bear's back", section = ARTIO)
    default int artioExtraWideSaddleForward() { return -5; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideSaddleHeight", name = "Extra Wide saddle height", description = "Raise or lower the ribbed Extra Wide saddle", section = ARTIO)
    default int artioExtraWideSaddleHeight() { return -1; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideSaddleSideways", name = "Extra Wide saddle sideways", description = "Move the ribbed Extra Wide saddle across the Battle Bear", section = ARTIO)
    default int artioExtraWideSaddleSideways() { return 0; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "artioExtraWideSpearBarForward", name = "Extra Wide spear bars forward/back", description = "Move the Battle Bear's spear bars forward or backward only when using the Extra Wide saddle", section = ARTIO)
    default int artioExtraWideSpearBarForward() { return 2; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideHandlebarForward", name = "Extra Wide handlebars forward/back", description = "Move the Extra Wide handlebars forward or backward", section = ARTIO)
    default int artioExtraWideHandlebarForward() { return 20; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideHandlebarHeight", name = "Extra Wide handlebars height", description = "Raise or lower the Extra Wide handlebars", section = ARTIO)
    default int artioExtraWideHandlebarHeight() { return -10; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideHandlebarSideways", name = "Extra Wide handlebars sideways", description = "Move both Extra Wide handlebars sideways", section = ARTIO)
    default int artioExtraWideHandlebarSideways() { return 0; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioExtraWideHandlebarSpread", name = "Extra Wide handlebar spread (%)", description = "Move the left and right grips closer together or farther apart", section = ARTIO)
    default int artioExtraWideHandlebarSpread() { return 94; }

    @Range(min = 1, max = 10)
    @ConfigItem(keyName = "artioExtraWideHandlebarThickness", name = "Extra Wide handlebar thickness", description = "Resize the thickness of both handlebars and grips", section = ARTIO)
    default int artioExtraWideHandlebarThickness() { return 2; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "artioExtraWideHandlebarAngle", name = "Extra Wide handlebar angle", description = "Angle the outer grips upward or downward", section = ARTIO)
    default int artioExtraWideHandlebarAngle() { return 11; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideReinHandForward", name = "Extra Wide reins hand forward/back", description = "Move both rein hand ends forward or backward from the grips", section = ARTIO)
    default int artioExtraWideReinHandForward() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideReinHandHeight", name = "Extra Wide reins hand height", description = "Raise or lower both rein hand ends", section = ARTIO)
    default int artioExtraWideReinHandHeight() { return -2; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "artioExtraWideReinHandSideways", name = "Extra Wide reins hand spread", description = "Move both rein hand ends inward or outward", section = ARTIO)
    default int artioExtraWideReinHandSideways() { return 0; }

    @Range(min = -100, max = 300)
    @ConfigItem(keyName = "artioExtraWideReinHeadForward", name = "Extra Wide reins head forward/back", description = "Move both rein head ends forward or backward", section = ARTIO)
    default int artioExtraWideReinHeadForward() { return 148; }

    @Range(min = -100, max = 160)
    @ConfigItem(keyName = "artioExtraWideReinHeadHeight", name = "Extra Wide reins head height", description = "Raise or lower both rein head ends", section = ARTIO)
    default int artioExtraWideReinHeadHeight() { return -19; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioExtraWideReinHeadSideways", name = "Extra Wide reins head sideways", description = "Move both rein ends and the mouth bar left or right", section = ARTIO)
    default int artioExtraWideReinHeadSideways() { return -3; }

    @Range(min = 2, max = 80)
    @ConfigItem(keyName = "artioExtraWideReinHeadSpread", name = "Extra Wide reins head spread", description = "Move the left and right rein ends across the Battle Bear's head", section = ARTIO)
    default int artioExtraWideReinHeadSpread() { return 18; }

    @Range(min = -40, max = 80)
    @ConfigItem(keyName = "artioExtraWideReinSag", name = "Extra Wide reins sag", description = "Increase or reduce the curve between the hands and head", section = ARTIO)
    default int artioExtraWideReinSag() { return 28; }

    @Range(min = 0, max = 100)
    @ConfigItem(keyName = "artioExtraWideReinNeckClearance", name = "Extra Wide reins neck clearance", description = "Curve the final section of each rein outward around the Battle Bear's neck", section = ARTIO)
    default int artioExtraWideReinNeckClearance() { return 22; }

    @Range(min = 1, max = 6)
    @ConfigItem(keyName = "artioExtraWideReinThickness", name = "Extra Wide reins thickness", description = "Resize both leather reins", section = ARTIO)
    default int artioExtraWideReinThickness() { return 1; }

    @Range(min = 1, max = 8)
    @ConfigItem(keyName = "artioExtraWideMouthBarThickness", name = "Extra Wide mouth bar thickness", description = "Resize the black mouth bar between the reins", section = ARTIO)
    default int artioExtraWideMouthBarThickness() { return 3; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "artioExtraWideMouthBarExtension", name = "Extra Wide mouth bar extension", description = "Extend the black mouth bar beyond each rein", section = ARTIO)
    default int artioExtraWideMouthBarExtension() { return 6; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "artioIdleBounce", name = "Battle Bear idle bob", description = "Move the rider with the Battle Bear's idle animation", section = ARTIO)
    default int artioIdleBounce() { return 2; }

    @Range(min = -20, max = 20)
    @ConfigItem(keyName = "artioIdleBobTiming", name = "Idle bob timing", description = "Shift the idle bob along the Battle Bear's animation", section = ARTIO)
    default int artioIdleBobTiming() { return 0; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "artioWalkBounce", name = "Battle Bear walking bob", description = "Move the rider with the Battle Bear's walking animation", section = ARTIO)
    default int artioWalkBounce() { return 4; }

    @Range(min = -20, max = 20)
    @ConfigItem(keyName = "artioWalkBobTiming", name = "Walking bob timing", description = "Shift the walking bob along the Battle Bear's animation", section = ARTIO)
    default int artioWalkBobTiming() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioWalkForwardAdjustment", name = "Walking rider forward/back", description = "Adjust the rider only while the Battle Bear is walking", section = ARTIO)
    default int artioWalkForwardAdjustment() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioWalkHeightAdjustment", name = "Walking rider height", description = "Raise or lower the rider only while the Battle Bear is walking", section = ARTIO)
    default int artioWalkHeightAdjustment() { return 0; }

    @ConfigItem(keyName = "showArtioArmour", name = "Show battle saddle", description = "Show the Battle Bear's curved hide saddle, V's Shield, and genuine Guthan's warspears", section = ARTIO)
    default boolean showArtioArmour() { return true; }

    @Range(min = 40, max = 180)
    @ConfigItem(keyName = "artioArmourScale", name = "Saddle scale (%)", description = "Resize the Battle Bear's Fremennik saddle", section = ARTIO)
    default int artioArmourScale() { return 128; }

    @Range(min = -150, max = 150)
    @ConfigItem(keyName = "artioArmourForward", name = "Saddle forward/back", description = "Move the saddle along the Battle Bear's back", section = ARTIO)
    default int artioArmourForward() { return 5; }

    @Range(min = -150, max = 250)
    @ConfigItem(keyName = "artioArmourHeight", name = "Saddle height", description = "Raise or lower the Battle Bear's saddle", section = ARTIO)
    default int artioArmourHeight() { return 154; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioArmourSideways", name = "Saddle sideways", description = "Move the saddle sideways across the Battle Bear", section = ARTIO)
    default int artioArmourSideways() { return -1; }

    @Range(min = 40, max = 150)
    @ConfigItem(keyName = "artioShieldScale", name = "V's Shield size (%)", description = "Resize the real V's Shield on the backrest", section = ARTIO)
    default int artioShieldScale() { return 82; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "artioShieldForward", name = "V's Shield forward/back", description = "Move V's Shield toward or away from the backrest", section = ARTIO)
    default int artioShieldForward() { return -7; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "artioShieldHeight", name = "V's Shield height", description = "Raise or lower V's Shield", section = ARTIO)
    default int artioShieldHeight() { return 1; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "artioShieldSideways", name = "V's Shield sideways", description = "Move V's Shield left or right across the backrest", section = ARTIO)
    default int artioShieldSideways() { return 0; }

    @Range(min = -90, max = 90)
    @ConfigItem(keyName = "artioShieldTilt", name = "V's Shield tilt", description = "Lean the top of V's Shield forward or backward", section = ARTIO)
    default int artioShieldTilt() { return 0; }

    @Range(min = -180, max = 180)
    @ConfigItem(keyName = "artioShieldTurn", name = "V's Shield turn", description = "Rotate V's Shield left or right around the backrest", section = ARTIO)
    default int artioShieldTurn() { return -90; }

    @Range(min = 40, max = 160)
    @ConfigItem(keyName = "artioWarspearScale", name = "Guthan's warspear size (%)", description = "Resize both genuine Guthan's warspears", section = ARTIO)
    default int artioWarspearScale() { return 134; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioWarspearForward", name = "Warspears forward/back", description = "Move both warspears along the saddle", section = ARTIO)
    default int artioWarspearForward() { return -14; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioWarspearHeight", name = "Warspears height", description = "Raise or lower both warspears", section = ARTIO)
    default int artioWarspearHeight() { return -21; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioLeftWarspearSideways", name = "Left warspear sideways", description = "Move the left warspear toward or away from the saddle", section = ARTIO)
    default int artioLeftWarspearSideways() { return -55; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioRightWarspearSideways", name = "Right warspear sideways", description = "Move the right warspear toward or away from the saddle", section = ARTIO)
    default int artioRightWarspearSideways() { return 54; }

    @Range(min = -90, max = 90)
    @ConfigItem(keyName = "artioWarspearTilt", name = "Warspears tilt", description = "Angle both warspears up or down", section = ARTIO)
    default int artioWarspearTilt() { return 25; }

    @Range(min = -180, max = 180)
    @ConfigItem(keyName = "artioWarspearTurn", name = "Warspears turn", description = "Rotate both warspears across the saddle", section = ARTIO)
    default int artioWarspearTurn() { return 0; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "artioSpearBarForward", name = "Spear bars forward/back", description = "Move both saddle-to-spear mounting bars forward or backward", section = ARTIO)
    default int artioSpearBarForward() { return 2; }

    @Range(min = -60, max = 60)
    @ConfigItem(keyName = "artioSpearBarHeight", name = "Spear bars outer height", description = "Raise or lower only the outer ends to change the mounting-bar angle", section = ARTIO)
    default int artioSpearBarHeight() { return -18; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioSpearBarVertical", name = "Spear holders up/down", description = "Move both complete spear holders up or down without changing their angle", section = ARTIO)
    default int artioSpearBarVertical() { return -8; }

    @Range(min = -20, max = 40)
    @ConfigItem(keyName = "artioSpearBarSideways", name = "Spear bars sideways", description = "Extend both mounting bars toward or away from the warspears", section = ARTIO)
    default int artioSpearBarSideways() { return 11; }

    @Range(min = 1, max = 8)
    @ConfigItem(keyName = "artioSpearBarThickness", name = "Spear bars thickness", description = "Adjust the thickness of both mounting bars", section = ARTIO)
    default int artioSpearBarThickness() { return 5; }

    @Range(min = 1, max = 30)
    @ConfigItem(keyName = "artioSpearBarWidth", name = "Spear bars width", description = "Widen both mounting bars across the saddle-to-spear gap", section = ARTIO)
    default int artioSpearBarWidth() { return 8; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioSeatFrameWidth", name = "Seat frame width (%)", description = "Resize the dark steel seat frame from side to side", section = ARTIO)
    default int artioSeatFrameWidth() { return 103; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioSeatFrameLength", name = "Seat frame length (%)", description = "Resize the dark steel seat frame forward and backward", section = ARTIO)
    default int artioSeatFrameLength() { return 105; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioSeatFrameThickness", name = "Seat frame thickness (%)", description = "Resize the vertical thickness of the dark steel seat frame", section = ARTIO)
    default int artioSeatFrameThickness() { return 116; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioSeatFrameForward", name = "Seat frame forward/back", description = "Move the dark steel seat frame along the saddle", section = ARTIO)
    default int artioSeatFrameForward() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioSeatFrameHeight", name = "Seat frame height", description = "Raise or lower the dark steel seat frame", section = ARTIO)
    default int artioSeatFrameHeight() { return -10; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioSeatFrameSideways", name = "Seat frame sideways", description = "Move the dark steel seat frame across the saddle", section = ARTIO)
    default int artioSeatFrameSideways() { return 0; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioSeatCushionWidth", name = "Orange cushion width (%)", description = "Resize the raised orange cushion from side to side", section = ARTIO)
    default int artioSeatCushionWidth() { return 146; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioSeatCushionLength", name = "Orange cushion length (%)", description = "Resize the raised orange cushion forward and backward", section = ARTIO)
    default int artioSeatCushionLength() { return 175; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioSeatCushionThickness", name = "Orange cushion thickness (%)", description = "Resize the vertical thickness of the raised orange cushion", section = ARTIO)
    default int artioSeatCushionThickness() { return 100; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioSeatCushionForward", name = "Orange cushion forward/back", description = "Move the raised orange cushion along the saddle", section = ARTIO)
    default int artioSeatCushionForward() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioSeatCushionHeight", name = "Orange cushion height", description = "Raise or lower the raised orange cushion", section = ARTIO)
    default int artioSeatCushionHeight() { return -14; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioSeatCushionSideways", name = "Orange cushion sideways", description = "Move the raised orange cushion across the saddle", section = ARTIO)
    default int artioSeatCushionSideways() { return 0; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioLowerSeatWidth", name = "Dark orange seat width (%)", description = "Resize the darker orange seat below the raised cushion from side to side", section = ARTIO)
    default int artioLowerSeatWidth() { return 90; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioLowerSeatLength", name = "Dark orange seat length (%)", description = "Resize the darker orange seat forward and backward", section = ARTIO)
    default int artioLowerSeatLength() { return 151; }

    @Range(min = 25, max = 250)
    @ConfigItem(keyName = "artioLowerSeatThickness", name = "Dark orange seat thickness (%)", description = "Resize the vertical thickness of the darker orange seat", section = ARTIO)
    default int artioLowerSeatThickness() { return 100; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioLowerSeatForward", name = "Dark orange seat forward/back", description = "Move the darker orange seat along the saddle", section = ARTIO)
    default int artioLowerSeatForward() { return 0; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioLowerSeatHeight", name = "Dark orange seat height", description = "Raise or lower the darker orange seat", section = ARTIO)
    default int artioLowerSeatHeight() { return 5; }

    @Range(min = -100, max = 100)
    @ConfigItem(keyName = "artioLowerSeatSideways", name = "Dark orange seat sideways", description = "Move the darker orange seat across the saddle", section = ARTIO)
    default int artioLowerSeatSideways() { return 0; }

    @ConfigItem(keyName = "showArtioAnchorProbeV2", name = "Show anchor probe", description = "Temporarily label animated Battle Bear vertices used to fit the armour", section = ARTIO)
    default boolean showArtioAnchorProbe() { return false; }

    @Range(min = 0, max = 3000)
    @ConfigItem(keyName = "artioAnchorProbeStart", name = "Anchor probe start", description = "First Battle Bear vertex index to label", section = ARTIO)
    default int artioAnchorProbeStart() { return 180; }

    @Range(min = 1, max = 25)
    @ConfigItem(keyName = "artioAnchorProbeSpacingV2", name = "Anchor probe spacing", description = "Label every Nth vertex; use 1 when narrowing down a body part", section = ARTIO)
    default int artioAnchorProbeSpacing() { return 1; }

    @ConfigItem(
        keyName = "showQuickMountButton",
        name = "Show quick mount button",
        description = "Show the movable mount and dismount button",
        section = GENERAL
    )
    default boolean showQuickMountButton()
    {
        return true;
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
