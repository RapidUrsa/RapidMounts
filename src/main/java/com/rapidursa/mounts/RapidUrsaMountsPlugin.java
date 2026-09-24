package com.rapidursa.mounts;

import com.rapidursa.mounts.appearance.AppearanceComposer;
import com.rapidursa.mounts.appearance.ModelRepository;
import com.rapidursa.mounts.appearance.Outfit;
import com.rapidursa.mounts.appearance.SpotAnimRepository;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Animation;
import net.runelite.api.AnimationController;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.HotkeyListener;

@PluginDescriptor(
    name = "Rapid Mounts",
    description = "Ride client-side cosmetic mounts",
    tags = {"mount", "unicorn", "terrorbird", "dragon", "gryphon", "turtle", "artio", "bear", "araxxor", "cosmetic", "transmog"}
)
public class RapidUrsaMountsPlugin extends Plugin
{
    private static final int BLACK_UNICORN_NPC_ID = 2849;
    private static final int TERRORBIRD_NPC_ID = 2064;
    private static final int LAVA_DRAGON_NPC_ID = 6593;
    private static final int GRYPHON_NPC_ID = 14857;
    private static final int SADDLE_SOURCE_NPC_ID = 2067;
    private static final int BANK_BUFFALO_NPC_ID = 13128;
    private static final int CART_CAMEL_NPC_ID = 18960;
    private static final int RIDER_ANIMATION_ID = 4107;
    private static final int WIDE_RIDER_ANIMATION_ID = 7536;
    private static final int WIDE_RIDER_FRAME = 32;
    private static final int TERRORBIRD_IDLE_ANIMATION_ID = 6793;
    private static final int TERRORBIRD_WALK_ANIMATION_ID = 6796;
    private static final int LAVA_DRAGON_IDLE_ANIMATION_ID = 90;
    private static final int LAVA_DRAGON_WALK_ANIMATION_ID = 79;
    private static final int GRYPHON_IDLE_ANIMATION_ID = 12547;
    private static final int GRYPHON_WALK_ANIMATION_ID = 12549;
    private static final int ARAXXOR_NPC_ID = 13668;
    private static final int VS_SHIELD_ITEM_ID = 24266;
    private static final int GUTHANS_WARSPEAR_ITEM_ID = 4726;
    private static final int FALLBACK_BODY_MODEL = 25754;
    private static final int FALLBACK_DETAILS_MODEL = 25756;
    private static final int ARTIO_REIN_MOUTH_TARGET_FORWARD = 145;
    private static final int ARTIO_REIN_MOUTH_TARGET_HEIGHT = 5;
    private static final int ARTIO_REIN_MOUTH_TARGET_SPREAD = 18;
    private static final int REIN_LOOP_FRAMES = 32;
    private static final int REIN_REGULAR_LOOPS = 4;
    private static final int SADDLE_MOTION_FRAMES = REIN_LOOP_FRAMES * (REIN_REGULAR_LOOPS + 1);
    // Measured from one complete black-unicorn idle cycle. Keeping the two
    // axes as separate frame samples reproduces the muzzle's asymmetric dip,
    // brief lower pause and softer return instead of approximating a circle.
    private static final int[] UNICORN_REIN_FORWARD_PROFILE =
    {
        85, 100, 89, 57, 9, -36, -63, -81,
        -66, -58, -66, -65, -50, -30, 13, 31,
        13, -3, -11, 11, 34, 44, 28, 7,
        -7, -11, -15, -18, -21, -3, 25, 56
    };
    private static final int[] UNICORN_REIN_HEIGHT_PROFILE =
    {
        -8, -6, 0, -5, -13, -27, -43, -42,
        -34, -10, 29, 70, 93, 100, 73, 29,
        -13, -42, -60, -59, -44, -29, -15, -4,
        0, 8, 19, 21, 20, 8, -6, -11
    };

    private static final int AMBIENT = 64;
    private static final int CONTRAST = 850;
    private static final int LIGHT_X = -30;
    private static final int LIGHT_Y = -50;
    private static final int LIGHT_Z = -30;

    @Inject
    private Client client;

    @Inject
    private RapidUrsaMountsConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private AppearanceComposer appearanceComposer;

    @Inject
    private ModelRepository modelRepository;

    @Inject
    private ItemManager itemManager;

    private MountedHolsterSettings holsterSettings;

    private MountedHolsterRenderer mountedHolsterRenderer;
    private boolean holsterHandedOff;

    @Inject
    private SpotAnimRepository spotAnimRepository;

    @Inject
    private Hooks hooks;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private MountToggleOverlay mountButton;

    @Inject
    private ArtioAnchorOverlay artioAnchorOverlay;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private MountStablePanel mountStablePanel;

    private NavigationButton stableNavigation;

    private RuneLiteObject unicorn;
    private RuneLiteObject saddle;
    private RuneLiteObject artioShield;
    private RuneLiteObject artioWarspears;
    private int[] lastGryphonReinAnchors;
    private int gryphonSaddleAnchorVertex = -1;
    private int[] baseGryphonSaddleAnchor;
    private int currentGryphonSeatForward;
    private int currentGryphonSeatSideways;
    private int currentGryphonSeatHeight;
    private int[] baseArtioArmourAnchors;
    private int[] lastArtioArmourAnchors;
    private final int[] artioReinHeadVertices = {-1, -1};
    private int currentArtioSeatForward;
    private int currentArtioSeatSideways;
    private int currentArtioSeatHeight;
    private int currentArtioSaddleForward;
    private int currentArtioSaddleSideways;
    private int currentArtioSaddleHeight;
    private int currentGryphonSaddleForward;
    private int currentGryphonSaddleSideways;
    private int currentGryphonSaddleHeight;
    private int[] araxxorRiderAnchorVertices;
    private int[] baseAraxxorRiderAnchor;
    private int currentAraxxorSeatForward;
    private int currentAraxxorSeatSideways;
    private int currentAraxxorSeatHeight;
    private Model[] saddleMotionModels;
    private int activeSaddleMotionFrame = -1;
    private final List<RuneLiteObject> mountParts = new ArrayList<>();
    private RuneLiteObject rider;
    private int builtScale = -1;
    private int builtSaddleScale = -1;
    private MountType builtMountType;
    private int activeUnicornAnimation = -1;
    private int activeRiderAnimation = -1;
    private int activeRiderFrame = -1;
    private Outfit builtRiderOutfit;
    private Model builtRiderModel;
    private boolean mountedRenderReady;
    private boolean mounted = true;
    private int actionResumeTicks;
    private final List<RuneLiteObject> activeEffects = new ArrayList<>();

    private final HotkeyListener mountHotkey = new HotkeyListener(() -> config.mountHotkey())
    {
        @Override
        public void hotkeyPressed()
        {
            clientThread.invokeLater(RapidUrsaMountsPlugin.this::toggleMounted);
        }
    };

    private final Hooks.RenderableDrawListener drawListener = (renderable, drawingUI) ->
    {
        if (!drawingUI
            && config.hideOriginalPlayer()
            && mountedRenderReady
            && renderable == client.getLocalPlayer())
        {
            return false;
        }
        return true;
    };

    @Provides
    RapidUrsaMountsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(RapidUrsaMountsConfig.class);
    }

    @Override
    protected void startUp()
    {
        setHolsterHandoff(false);
        holsterSettings = new MountedHolsterSettings(configManager);
        mountedHolsterRenderer = new MountedHolsterRenderer(
            client, modelRepository, itemManager, holsterSettings);
        migrateAraxxorAnimationDefaults();
        migrateExtraWideAnimationDefaults();
        migrateV19FittedDefaults();
        hooks.registerRenderableDrawListener(drawListener);
        mounted = true;
        mountButton.bind(this);
        artioAnchorOverlay.bind(this);
        mountStablePanel.bind(this);
        stableNavigation = NavigationButton.builder()
            .tooltip("Rapid Mounts")
            .icon(mountStablePanel.getSidebarIcon())
            .priority(7)
            .panel(mountStablePanel)
            .build();
        clientToolbar.addNavigation(stableNavigation);
        overlayManager.add(mountButton);
        overlayManager.add(artioAnchorOverlay);
        mouseManager.registerMouseListener(mountButton);
        keyManager.registerKeyListener(mountHotkey);
    }

    /** Upgrade the first Araxxor preview's deliberately blank animation IDs. */
    private void migrateAraxxorAnimationDefaults()
    {
        final String migrationKey = "araxxorAnimationDefaultsApplied";
        if (configManager.getConfiguration(
            RapidUrsaMountsConfig.GROUP, migrationKey) != null)
        {
            return;
        }
        String idle = configManager.getConfiguration(
            RapidUrsaMountsConfig.GROUP, "araxxorIdleAnimation");
        String walking = configManager.getConfiguration(
            RapidUrsaMountsConfig.GROUP, "araxxorWalkAnimation");
        if ("-1".equals(idle))
        {
            configManager.setConfiguration(
                RapidUrsaMountsConfig.GROUP, "araxxorIdleAnimation", 11473);
        }
        if ("-1".equals(walking))
        {
            configManager.setConfiguration(
                RapidUrsaMountsConfig.GROUP, "araxxorWalkAnimation", 11474);
        }
        configManager.setConfiguration(
            RapidUrsaMountsConfig.GROUP, migrationKey, true);
    }

    /**
     * v1.8.0 accidentally shipped the two development animation IDs as the
     * Extra Wide defaults. Replace only those known-bad persisted values so
     * existing development installs are repaired without touching any other
     * custom override. Normal Plugin Hub installs have no stored value and
     * therefore pick up the corrected interface defaults automatically.
     */
    private void migrateExtraWideAnimationDefaults()
    {
        String idle = configManager.getConfiguration(
            RapidUrsaMountsConfig.GROUP, "extraWideIdleAnimationId");
        if ("146".equals(idle))
        {
            configManager.setConfiguration(
                RapidUrsaMountsConfig.GROUP, "extraWideIdleAnimationId", 1461);
        }

        String moving = configManager.getConfiguration(
            RapidUrsaMountsConfig.GROUP, "extraWideWalkAnimationId");
        if ("8653".equals(moving))
        {
            configManager.setConfiguration(
                RapidUrsaMountsConfig.GROUP, "extraWideWalkAnimationId", 1462);
        }
    }

    /**
     * v1.9 locks the approved rider and fitted-tack geometry into the plugin.
     * Clear development-era overrides once so upgraded installations receive
     * the same release fit as fresh installs, then leave user-facing mount
     * scale choices untouched.
     */
    private void migrateV19FittedDefaults()
    {
        final String migrationKey = "v19FittedDefaultsApplied";
        if (configManager.getConfiguration(
            RapidUrsaMountsConfig.GROUP, migrationKey) != null)
        {
            return;
        }

        String[] fittedKeys =
        {
            "terrorbirdRiderHeight", "terrorbirdRiderForward",
            "terrorbirdRiderSideways", "terrorbirdCrossLeggedRiderHeight",
            "terrorbirdCrossLeggedRiderForward", "terrorbirdCrossLeggedRiderSideways",
            "terrorbirdExtraWideRiderHeight", "terrorbirdExtraWideRiderForward",
            "terrorbirdExtraWideRiderSideways", "terrorbirdIdleBounce",
            "terrorbirdWalkHeightAdjustment", "terrorbirdWalkForwardAdjustment",
            "terrorbirdStrideFollow", "terrorbirdSeatBounce", "terrorbirdSeatSway",
            "lavaDragonRiderHeight", "lavaDragonRiderForward",
            "lavaDragonRiderSideways", "lavaDragonCrossLeggedRiderHeight",
            "lavaDragonCrossLeggedRiderForward", "lavaDragonCrossLeggedRiderSideways",
            "lavaDragonExtraWideRiderHeight", "lavaDragonExtraWideRiderForward",
            "lavaDragonExtraWideRiderSideways", "lavaDragonWalkHeightAdjustment",
            "lavaDragonWalkForwardAdjustment", "lavaDragonIdleBounce",
            "lavaDragonStrideFollow", "lavaDragonSeatBounce", "lavaDragonSeatSway",
            "gryphonCrossLeggedRiderHeight", "gryphonCrossLeggedRiderForward",
            "gryphonCrossLeggedRiderSideways", "gryphonExtraWideRiderHeight",
            "gryphonExtraWideRiderForward", "gryphonExtraWideRiderSideways",
            "gryphonSaddleScale", "gryphonSaddleForward", "gryphonSaddleHeight",
            "gryphonSaddleSideways", "gryphonLeftReinHandForward",
            "gryphonLeftReinHandHeight", "gryphonLeftReinHandSideways",
            "gryphonRightReinHandForward", "gryphonRightReinHandHeight",
            "gryphonRightReinHandSideways", "gryphonRiderHeight",
            "gryphonWideRiderHeight", "gryphonRiderForward", "gryphonRiderSideways",
            "gryphonWalkHeightAdjustment", "gryphonWalkForwardAdjustment",
            "gryphonIdleBounce", "gryphonSeatSway", "gryphonLateralSway",
            "battleTurtleIdleBounce", "battleTurtleIdleBobTiming",
            "battleTurtleWalkBounce", "battleTurtleWalkBobTiming",
            "battleTurtleWalkForwardAdjustment", "battleTurtleWalkHeightAdjustment",
            "battleTurtleCrossLeggedRiderHeight", "battleTurtleCrossLeggedRiderForward",
            "battleTurtleCrossLeggedRiderSideways", "battleTurtleExtraWideRiderHeight",
            "battleTurtleExtraWideRiderForward", "battleTurtleExtraWideRiderSideways",
            "battleTurtleSaddleScale", "battleTurtleSaddleForward",
            "battleTurtleSaddleHeight", "battleTurtleSaddleSideways",
            "artioRiderHeight", "artioRiderForward", "artioRiderSideways",
            "artioCrossLeggedRiderHeight", "artioCrossLeggedRiderForward",
            "artioCrossLeggedRiderSideways", "artioExtraWideRiderHeight",
            "artioExtraWideRiderForward", "artioExtraWideRiderSideways",
            "artioNoSaddleRiderHeight", "artioNoSaddleRiderForward",
            "artioNoSaddleRiderSideways", "artioExtraWideSaddleWidth",
            "artioExtraWideSaddleLength", "artioExtraWideSaddleThickness",
            "artioExtraWideSaddleBodyHeight", "artioExtraWideSaddleForward",
            "artioExtraWideSaddleHeight", "artioExtraWideSaddleSideways",
            "artioExtraWideSpearBarForward", "artioExtraWideHandlebarForward",
            "artioExtraWideHandlebarHeight", "artioExtraWideHandlebarSideways",
            "artioExtraWideHandlebarSpread", "artioExtraWideHandlebarThickness",
            "artioExtraWideHandlebarAngle", "artioExtraWideReinHandForward",
            "artioExtraWideReinHandHeight", "artioExtraWideReinHandSideways",
            "artioExtraWideReinHeadForward", "artioExtraWideReinHeadHeight",
            "artioExtraWideReinHeadSideways", "artioExtraWideReinHeadSpread",
            "artioExtraWideReinSag", "artioExtraWideReinNeckClearance",
            "artioExtraWideReinThickness", "artioExtraWideMouthBarThickness",
            "artioExtraWideMouthBarExtension", "artioIdleBounce",
            "artioIdleBobTiming", "artioWalkBounce", "artioWalkBobTiming",
            "artioWalkForwardAdjustment", "artioWalkHeightAdjustment",
            "artioArmourScale", "artioArmourForward", "artioArmourHeight",
            "artioArmourSideways", "artioShieldScale", "artioShieldForward",
            "artioShieldHeight", "artioShieldSideways", "artioShieldTilt",
            "artioShieldTurn", "artioWarspearScale", "artioWarspearForward",
            "artioWarspearHeight", "artioLeftWarspearSideways",
            "artioRightWarspearSideways", "artioWarspearTilt", "artioWarspearTurn",
            "artioSpearBarForward", "artioSpearBarHeight", "artioSpearBarVertical",
            "artioSpearBarSideways", "artioSpearBarThickness", "artioSpearBarWidth",
            "artioSeatFrameWidth", "artioSeatFrameLength", "artioSeatFrameThickness",
            "artioSeatFrameForward", "artioSeatFrameHeight", "artioSeatFrameSideways",
            "artioSeatCushionWidth", "artioSeatCushionLength",
            "artioSeatCushionThickness", "artioSeatCushionForward",
            "artioSeatCushionHeight", "artioSeatCushionSideways",
            "artioLowerSeatWidth", "artioLowerSeatLength", "artioLowerSeatThickness",
            "artioLowerSeatForward", "artioLowerSeatHeight", "artioLowerSeatSideways"
        };
        for (String key : fittedKeys)
        {
            configManager.unsetConfiguration(RapidUrsaMountsConfig.GROUP, key);
        }
        configManager.setConfiguration(
            RapidUrsaMountsConfig.GROUP, migrationKey, true);
    }

    @Override
    protected void shutDown()
    {
        setHolsterHandoff(false);
        mountedRenderReady = false;
        hooks.unregisterRenderableDrawListener(drawListener);
        if (stableNavigation != null)
        {
            clientToolbar.removeNavigation(stableNavigation);
            stableNavigation = null;
        }
        mountStablePanel.unbind();
        keyManager.unregisterKeyListener(mountHotkey);
        mouseManager.unregisterMouseListener(mountButton);
        overlayManager.remove(mountButton);
        overlayManager.remove(artioAnchorOverlay);
        artioAnchorOverlay.unbind();
        mountButton.unbind();
        despawn();
        clearEffects();
        mounted = false;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN)
        {
            despawn();
            clearEffects();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!RapidUrsaMountsConfig.GROUP.equals(event.getGroup()))
        {
            return;
        }

        if (!config.enabled())
        {
            despawn();
        }
        else if ("mountType".equals(event.getKey())
            || "mountScale".equals(event.getKey())
            || "terrorbirdScale".equals(event.getKey())
            || "lavaDragonScale".equals(event.getKey())
            || "gryphonScale".equals(event.getKey())
            || "artioNpcId".equals(event.getKey())
            || "artioScale".equals(event.getKey())
            || "araxxorScale".equals(event.getKey()))
        {
            despawn();
        }
        else if ("showSaddlePrototype".equals(event.getKey())
            || "useCustomSaddle".equals(event.getKey())
            || "saddleSource".equals(event.getKey())
            || "bankBuffaloNpcId".equals(event.getKey())
            || "cartCamelNpcId".equals(event.getKey())
            || "saddleSourcePart".equals(event.getKey())
            || "saddleScale".equals(event.getKey())
            || "reinLength".equals(event.getKey())
            || "reinEndHeight".equals(event.getKey())
            || "reinSpread".equals(event.getKey())
            || "reinHeadBob".equals(event.getKey())
            || "reinHeadSway".equals(event.getKey())
            || "reinMotionSizePercent".equals(event.getKey())
            || "leftReinHandForward".equals(event.getKey())
            || "leftReinHandHeight".equals(event.getKey())
            || "leftReinHandSideways".equals(event.getKey())
            || "rightReinHandForward".equals(event.getKey())
            || "rightReinHandHeight".equals(event.getKey())
            || "rightReinHandSideways".equals(event.getKey())
            || "showGryphonSaddle".equals(event.getKey())
            || "gryphonSaddleScale".equals(event.getKey())
            || "gryphonLeftReinHandForward".equals(event.getKey())
            || "gryphonLeftReinHandHeight".equals(event.getKey())
            || "gryphonLeftReinHandSideways".equals(event.getKey())
            || "gryphonRightReinHandForward".equals(event.getKey())
            || "gryphonRightReinHandHeight".equals(event.getKey())
            || "gryphonRightReinHandSideways".equals(event.getKey())
            || "showBattleTurtleSaddle".equals(event.getKey())
            || "battleTurtleSaddleScale".equals(event.getKey())
            || "showArtioArmour".equals(event.getKey())
            || "artioArmourScale".equals(event.getKey())
            || "artioShieldScale".equals(event.getKey())
            || "artioShieldForward".equals(event.getKey())
            || "artioShieldHeight".equals(event.getKey())
            || "artioShieldSideways".equals(event.getKey())
            || "artioShieldTilt".equals(event.getKey())
            || "artioShieldTurn".equals(event.getKey())
            || "artioWarspearScale".equals(event.getKey())
            || "artioWarspearForward".equals(event.getKey())
            || "artioWarspearHeight".equals(event.getKey())
            || "artioLeftWarspearSideways".equals(event.getKey())
            || "artioRightWarspearSideways".equals(event.getKey())
            || "artioWarspearTilt".equals(event.getKey())
            || "artioWarspearTurn".equals(event.getKey())
            || "artioSpearBarForward".equals(event.getKey())
            || "artioExtraWideSpearBarForward".equals(event.getKey())
            || "artioSpearBarHeight".equals(event.getKey())
            || "artioSpearBarVertical".equals(event.getKey())
            || "artioSpearBarSideways".equals(event.getKey())
            || "artioSpearBarThickness".equals(event.getKey())
            || "artioSpearBarWidth".equals(event.getKey())
            || "artioSeatFrameWidth".equals(event.getKey())
            || "artioSeatFrameLength".equals(event.getKey())
            || "artioSeatFrameThickness".equals(event.getKey())
            || "artioSeatFrameForward".equals(event.getKey())
            || "artioSeatFrameHeight".equals(event.getKey())
            || "artioSeatFrameSideways".equals(event.getKey())
            || "artioSeatCushionWidth".equals(event.getKey())
            || "artioSeatCushionLength".equals(event.getKey())
            || "artioSeatCushionThickness".equals(event.getKey())
            || "artioSeatCushionForward".equals(event.getKey())
            || "artioSeatCushionHeight".equals(event.getKey())
            || "artioSeatCushionSideways".equals(event.getKey())
            || "artioLowerSeatWidth".equals(event.getKey())
            || "artioLowerSeatLength".equals(event.getKey())
            || "artioLowerSeatThickness".equals(event.getKey())
            || "artioLowerSeatForward".equals(event.getKey())
            || "artioLowerSeatHeight".equals(event.getKey())
            || "artioLowerSeatSideways".equals(event.getKey())
            || "artioExtraWideSaddleWidth".equals(event.getKey())
            || "artioExtraWideSaddleLength".equals(event.getKey())
            || "artioExtraWideSaddleThickness".equals(event.getKey())
            || "artioExtraWideSaddleBodyHeight".equals(event.getKey())
            || "artioExtraWideSaddleForward".equals(event.getKey())
            || "artioExtraWideSaddleHeight".equals(event.getKey())
            || "artioExtraWideSaddleSideways".equals(event.getKey())
            || "artioExtraWideHandlebarForward".equals(event.getKey())
            || "artioExtraWideHandlebarHeight".equals(event.getKey())
            || "artioExtraWideHandlebarSideways".equals(event.getKey())
            || "artioExtraWideHandlebarSpread".equals(event.getKey())
            || "artioExtraWideHandlebarThickness".equals(event.getKey())
            || "artioExtraWideHandlebarAngle".equals(event.getKey())
            || "artioExtraWideReinHandForward".equals(event.getKey())
            || "artioExtraWideReinHandHeight".equals(event.getKey())
            || "artioExtraWideReinHandSideways".equals(event.getKey())
            || "artioExtraWideReinHeadForward".equals(event.getKey())
            || "artioExtraWideReinHeadHeight".equals(event.getKey())
            || "artioExtraWideReinHeadSideways".equals(event.getKey())
            || "artioExtraWideReinHeadSpread".equals(event.getKey())
            || "artioExtraWideReinSag".equals(event.getKey())
            || "artioExtraWideReinNeckClearance".equals(event.getKey())
            || "artioExtraWideReinThickness".equals(event.getKey())
            || "artioExtraWideMouthBarThickness".equals(event.getKey())
            || "artioExtraWideMouthBarExtension".equals(event.getKey()))
        {
            despawn();
        }
        else if ("animateUnicorn".equals(event.getKey()) && unicorn != null)
        {
            unicorn.setAnimationController(null);
            activeUnicornAnimation = -1;
        }
        else if (("battleTurtleNpcId".equals(event.getKey())
            || "battleTurtleScale".equals(event.getKey())))
        {
            despawn();
        }
        else if (("battleTurtleIdleAnimation".equals(event.getKey())
            || "battleTurtleWalkAnimation".equals(event.getKey())
            || "artioIdleAnimationV2".equals(event.getKey())
            || "artioWalkAnimationV2".equals(event.getKey())) && unicorn != null)
        {
            unicorn.setAnimationController(null);
            activeUnicornAnimation = -1;
        }
        else if (("useRidingPose".equals(event.getKey())
            || "ridingPose".equals(event.getKey())
            || "extraWideIdleAnimationId".equals(event.getKey())
            || "extraWideWalkAnimationId".equals(event.getKey())
            || "crossLeggedAnimationId".equals(event.getKey())
            || "crossLeggedLoopStartFrame".equals(event.getKey())
            || "crossLeggedLoopEndFrame".equals(event.getKey())) && rider != null)
        {
            // Battle Bear's Extra Wide pose has its own saddle silhouette, so
            // changing pose must rebuild the tack as well as the rider.
            if (("useRidingPose".equals(event.getKey())
                || "ridingPose".equals(event.getKey()))
                && config.mountType() == MountType.ARTIO)
            {
                despawn();
                mountStablePanel.refresh();
                return;
            }
            rider.setAnimationController(null);
            activeRiderAnimation = -1;
            activeRiderFrame = -1;
        }
        else if ("hideCape".equals(event.getKey())
            || "capeBackwardOffset".equals(event.getKey())
            || "capeHeightOffset".equals(event.getKey()))
        {
            builtRiderModel = null;
            builtRiderOutfit = null;
        }
        else if ("pauseForActions".equals(event.getKey()) && !config.pauseForActions())
        {
            actionResumeTicks = 0;
        }
        mountStablePanel.refresh();
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (actionResumeTicks > 0)
        {
            actionResumeTicks--;
        }
        if (holsterHandedOff)
        {
            // Expiring the handoff lets Holster recover if Mounts is stopped
            // unexpectedly before it can remove its transient flag.
            configManager.setConfiguration(RapidUrsaMountsConfig.GROUP,
                "mountedHolsterActive", System.currentTimeMillis());
        }
    }

    void toggleMounted()
    {
        playMountEffect();
        mounted = !mounted;
        if (!mounted)
        {
            despawn();
        }
        mountStablePanel.refresh();
    }

    boolean isMounted()
    {
        return mounted;
    }

    @Subscribe
    public void onBeforeRender(BeforeRender event)
    {
        mountedRenderReady = false;
        if (!mounted || !config.enabled() || client.getGameState() != GameState.LOGGED_IN)
        {
            setHolsterHandoff(false);
            if (mountedHolsterRenderer != null) mountedHolsterRenderer.clear();
            return;
        }

        Player player = client.getLocalPlayer();
        if (player == null || player.getLocalLocation() == null)
        {
            setHolsterHandoff(false);
            return;
        }

        // Claim the weapon before either plugin draws this frame. Holster's
        // on-foot object must not remain active while the mounted rider moves.
        boolean mountedHolsterActive = config.mountedHolster()
            && holsterSettings.holstered() && isMountedHolsterCompatible();
        setHolsterHandoff(mountedHolsterActive);

        if (config.pauseForActions())
        {
            if (player.getAnimation() != -1 || player.getInteracting() != null)
            {
                actionResumeTicks = config.actionResumeDelay();
                suspendCosmetics();
                return;
            }
            if (actionResumeTicks > 0)
            {
                suspendCosmetics();
                return;
            }
        }

        if (!ensureObjects())
        {
            setHolsterHandoff(false);
            return;
        }

        LocalPoint playerPoint = player.getLocalLocation();
        int plane = player.getWorldLocation().getPlane();
        int orientation = player.getCurrentOrientation();
        int terrainZ = Perspective.getTileHeight(client, playerPoint, plane);
        boolean moving = player.getPoseAnimation() != player.getIdlePoseAnimation();

        // The custom saddle and rider are separate RuneLite objects, so give them
        // one shared seat transform to keep them visually locked together.
        int linkedSeatForward = 0;
        int linkedSeatSideways = 0;
        int linkedSeatHeight = 0;
        if (config.mountType() == MountType.BLACK_UNICORN)
        {
            if (moving)
            {
                double phase = currentStridePhase();
                if (phase >= 0)
                {
                    linkedSeatForward = (int) Math.round(2.0 * Math.sin(phase));
                    linkedSeatSideways = (int) Math.round(Math.cos(phase));
                    linkedSeatHeight = (int) Math.round(3.0 * (0.5 - 0.5 * Math.cos(phase)));
                }
            }
            else
            {
                linkedSeatHeight = currentIdleBounce();
            }
        }

        unicorn.setLocation(playerPoint, plane);
        unicorn.setZ(terrainZ);
        unicorn.setOrientation(orientation);
        for (RuneLiteObject part : mountParts)
        {
            part.setLocation(playerPoint, plane);
            part.setZ(terrainZ);
            part.setOrientation(orientation);
        }
        if (saddle != null)
        {
            int saddleForward;
            int saddleSideways;
            int saddleHeight;
            if (config.mountType() == MountType.GRYPHON)
            {
                saddleForward = config.gryphonSaddleForward();
                saddleSideways = config.gryphonSaddleSideways();
                saddleHeight = config.gryphonSaddleHeight();
                currentGryphonSaddleForward = saddleForward;
                currentGryphonSaddleSideways = saddleSideways;
                currentGryphonSaddleHeight = saddleHeight;
            }
            else if (config.mountType() == MountType.BATTLE_TURTLE)
            {
                saddleForward = config.battleTurtleSaddleForward() + linkedSeatForward;
                saddleSideways = config.battleTurtleSaddleSideways() + linkedSeatSideways;
                saddleHeight = config.battleTurtleSaddleHeight() + linkedSeatHeight;
                saddleHeight += moving ? currentSeatBounce() : currentIdleBounce();
            }
            else if (config.mountType() == MountType.ARTIO)
            {
                saddleForward = config.artioArmourForward();
                saddleSideways = config.artioArmourSideways();
                saddleHeight = config.artioArmourHeight();
                if (moving)
                {
                    saddleForward += currentWalkForwardAdjustment();
                    saddleHeight += currentWalkHeightAdjustment() + currentSeatBounce();
                }
                else
                {
                    saddleHeight += currentIdleBounce();
                }
                currentArtioSaddleForward = saddleForward;
                currentArtioSaddleSideways = saddleSideways;
                currentArtioSaddleHeight = saddleHeight;
            }
            else
            {
                saddleForward = config.saddleForward() + linkedSeatForward;
                saddleSideways = config.saddleSideways() + linkedSeatSideways;
                saddleHeight = config.saddleHeight() + linkedSeatHeight;
            }
            LocalPoint saddlePoint = offsetFromPlayer(
                playerPoint,
                orientation,
                saddleForward,
                saddleSideways);
            saddle.setLocation(saddlePoint, plane);
            saddle.setZ(Perspective.getTileHeight(client, saddlePoint, plane)
                - saddleHeight);
            saddle.setOrientation(orientation);
            if (artioShield != null)
            {
                artioShield.setLocation(saddlePoint, plane);
                artioShield.setZ(Perspective.getTileHeight(client, saddlePoint, plane)
                    - saddleHeight);
                artioShield.setOrientation(orientation);
            }
            positionArtioAttachment(artioWarspears, saddlePoint, plane, saddleHeight, orientation);
        }

        if (config.animateUnicorn())
        {
            int wantedAnimation = currentWalkOrIdleAnimation(moving);
            if (wantedAnimation != activeUnicornAnimation)
            {
                unicorn.setAnimationController(wantedAnimation < 0
                    ? null : loopingMountAnimation(wantedAnimation));
                for (RuneLiteObject part : mountParts)
                {
                    part.setAnimationController(wantedAnimation < 0
                        ? null : loopingAnimation(wantedAnimation));
                }
                activeUnicornAnimation = wantedAnimation;
            }
        }
        else if (activeUnicornAnimation != -1)
        {
            unicorn.setAnimationController(null);
            for (RuneLiteObject part : mountParts)
            {
                part.setAnimationController(null);
            }
            activeUnicornAnimation = -1;
        }

        if (config.mountType() == MountType.GRYPHON && saddle != null)
        {
            updateGryphonSaddle();
        }
        else if (config.mountType() == MountType.ARTIO && saddle != null)
        {
            updateArtioArmour();
        }
        if (config.mountType() == MountType.ARAXXOR)
        {
            updateAraxxorRiderAnchor();
        }

        if (saddle != null && saddleMotionModels != null)
        {
            int motionFrame = 0;
            AnimationController controller = unicorn == null ? null : unicorn.getAnimationController();
            Animation animation = controller == null ? null : controller.getAnimation();
            if (!moving && animation != null && animation.getNumFrames() > 1)
            {
                // The complete ready animation contains four small muzzle
                // loops followed by a separate deep bow. Map the saddle to
                // that full source sequence so the bow cannot drift out of
                // sync with the unicorn.
                int sourceFrameCount = animation.getNumFrames();
                int sourceFrame = Math.floorMod(controller.getFrame(), sourceFrameCount);
                int unshiftedFrame = (int) Math.round(
                    sourceFrame * (saddleMotionModels.length - 1.0) / (sourceFrameCount - 1.0));
                // Shift the complete idle sequence, rather than wrapping the
                // timing adjustment inside the current 32-frame section. The
                // old section-local shift could change the bow's shape after
                // it began, but could never make the reins enter the bow early
                // enough to follow the unicorn's initial head dip.
                motionFrame = Math.floorMod(
                    unshiftedFrame + config.reinBobTiming(),
                    saddleMotionModels.length);
            }
            else
            {
                double phase = currentStridePhase();
                if (phase >= 0)
                {
                    double baseFrame = phase * REIN_LOOP_FRAMES / (2.0 * Math.PI);
                    motionFrame = Math.floorMod(
                        (int) Math.round(baseFrame * config.reinBobSpeedPercent() / 100.0)
                            + config.reinBobTiming(),
                        REIN_LOOP_FRAMES);
                }
            }
            if (motionFrame != activeSaddleMotionFrame)
            {
                saddle.setModel(saddleMotionModels[motionFrame]);
                activeSaddleMotionFrame = motionFrame;
            }
        }

        boolean riderReady = false;
        if (config.showRider())
        {
            Model playerModel = buildRiderModel(player);
            if (playerModel != null)
            {
                int riderForward = currentRiderForward() + linkedSeatForward;
                int riderSideways = currentRiderSideways() + linkedSeatSideways;
                int riderHeight = currentRiderHeight() + linkedSeatHeight;
                if (config.mountType() == MountType.ARTIO)
                {
                    // Use the exact same animated upper-torso vertex delta as
                    // Artio's saddle mesh. This keeps the rider physically locked
                    // to the seat throughout both idle and walking animations.
                    riderForward += currentArtioSeatForward;
                    riderSideways += currentArtioSeatSideways;
                    riderHeight += currentArtioSeatHeight;
                }
                if (config.mountType() == MountType.GRYPHON)
                {
                    // Share the Gryphon back vertex delta with the complete
                    // saddle so the rider cannot drift above or through it.
                    riderForward += currentGryphonSeatForward;
                    riderSideways += currentGryphonSeatSideways;
                    riderHeight += currentGryphonSeatHeight;
                }
                if (config.mountType() == MountType.ARAXXOR)
                {
                    // Follow a cluster of animated back vertices instead of
                    // approximating the spider's motion with a sine wave.
                    riderForward += currentAraxxorSeatForward;
                    riderSideways += currentAraxxorSeatSideways;
                    riderHeight += currentAraxxorSeatHeight;
                }
                if (config.mountType() != MountType.BLACK_UNICORN
                    && config.mountType() != MountType.GRYPHON && moving)
                {
                    riderForward += currentWalkForwardAdjustment();
                    riderForward += currentSeatSway();
                    riderSideways += currentLateralSway();
                    riderHeight += currentWalkHeightAdjustment();
                    riderHeight += currentStrideFollow();
                    riderHeight += currentSeatBounce();
                }
                else if (config.mountType() != MountType.BLACK_UNICORN
                    && config.mountType() != MountType.GRYPHON && !moving)
                {
                    riderHeight += currentIdleBounce();
                }
                LocalPoint riderPoint = offsetFromPlayer(
                    playerPoint,
                    orientation,
                    riderForward,
                    riderSideways);
                rider.setLocation(riderPoint, plane);
                rider.setZ(Perspective.getTileHeight(client, riderPoint, plane) - riderHeight);
                rider.setOrientation(orientation);

                if (mountedHolsterActive)
                {
                    mountedHolsterRenderer.refresh(player, riderPoint, plane,
                        orientation, Perspective.getTileHeight(client, riderPoint, plane) - riderHeight,
                        mountedHolsterSideways(), mountedHolsterHeight(), mountedHolsterForward());
                }
                else
                {
                    mountedHolsterRenderer.clear();
                }

                if (config.ridingPose() != null)
                {
                    boolean widePose = config.ridingPose() == RidingPose.WIDE;
                    boolean extraWidePose = config.ridingPose().usesExtraWideAnimation();
                    boolean crossLeggedPose = config.ridingPose() == RidingPose.CROSS_LEGGED;
                    int wantedRiderAnimation = crossLeggedPose
                        ? config.crossLeggedAnimationId()
                        : widePose
                            ? WIDE_RIDER_ANIMATION_ID
                            : extraWidePose
                                ? (moving
                                    ? config.extraWideWalkAnimationId()
                                    : config.extraWideIdleAnimationId())
                                : RIDER_ANIMATION_ID;
                    int wantedRiderFrame = widePose ? WIDE_RIDER_FRAME : -1;
                    if (wantedRiderAnimation != activeRiderAnimation
                        || wantedRiderFrame != activeRiderFrame)
                    {
                        AnimationController controller = widePose
                            ? frozenAnimation(wantedRiderAnimation, wantedRiderFrame)
                            : crossLeggedPose
                                ? tailLoopAnimation(
                                    wantedRiderAnimation,
                                    config.crossLeggedLoopStartFrame(),
                                    config.crossLeggedLoopEndFrame())
                                : loopingAnimation(wantedRiderAnimation);
                        rider.setAnimationController(controller);
                        activeRiderAnimation = wantedRiderAnimation;
                        activeRiderFrame = wantedRiderFrame;
                    }
                }
                else if (activeRiderAnimation != -1)
                {
                    rider.setAnimationController(null);
                    activeRiderAnimation = -1;
                    activeRiderFrame = -1;
                }
                riderReady = true;
            }
        }

        activate(unicorn);
        for (RuneLiteObject part : mountParts)
        {
            activate(part);
        }
        // Reserve the rider before optional Artio accessories are activated.
        // This keeps the rider visible when changing Artio's scale forces all
        // RuneLite objects to be rebuilt on the same tile.
        if (riderReady)
        {
            activate(rider);
            mountedRenderReady = unicorn.isActive() && rider.isActive();
        }
        else
        {
            deactivate(rider);
            mountedRenderReady = false;
        }
        if (!mountedRenderReady && mountedHolsterRenderer != null)
        {
            mountedHolsterRenderer.clear();
        }
        setHolsterHandoff(mountedHolsterActive);
        // Fitted tack is automatic for each approved mount/style combination.
        if ((config.mountType() == MountType.BLACK_UNICORN
                && isWidePose())
            || (config.mountType() == MountType.GRYPHON
                && config.ridingPose() == RidingPose.STANDARD)
            || (config.mountType() == MountType.BATTLE_TURTLE
                && isCrossLeggedPose())
            || (config.mountType() == MountType.ARTIO
                && !isNoSaddlePose()))
        {
            activate(saddle);
            if (config.mountType() == MountType.ARTIO)
            {
                if (!isExtraWidePose())
                {
                    activate(artioShield);
                }
                else
                {
                    deactivate(artioShield);
                }
                activate(artioWarspears);
            }
        }
        else
        {
            deactivate(saddle);
            deactivate(artioShield);
            deactivate(artioWarspears);
        }
    }

    private Model buildRiderModel(Player player)
    {
        if (player.getPlayerComposition() == null)
        {
            return null;
        }

        if (!modelRepository.isLoaded())
        {
            modelRepository.loadFromClient(client);
            if (!modelRepository.isLoaded())
            {
                return null;
            }
        }

        Outfit currentOutfit = Outfit.from(player.getPlayerComposition());
        if (config.hideHeldEquipment())
        {
            currentOutfit.clear(KitType.WEAPON);
            currentOutfit.clear(KitType.SHIELD);
        }
        if (config.hideCape())
        {
            currentOutfit.clear(KitType.CAPE);
        }
        appearanceComposer.setCapeAdjustment(
            config.capeHeightOffset(),
            config.capeBackwardOffset());
        if (builtRiderModel == null || !currentOutfit.equals(builtRiderOutfit))
        {
            builtRiderModel = appearanceComposer.compose(currentOutfit);
            builtRiderOutfit = builtRiderModel == null ? null : currentOutfit;
            if (builtRiderModel != null)
            {
                rider.setModel(builtRiderModel);
            }
            rider.setAnimationController(null);
            activeRiderAnimation = -1;
            activeRiderFrame = -1;
        }
        return builtRiderModel;
    }

    private boolean ensureObjects()
    {
        if (unicorn != null
            && rider != null
            && builtMountType == config.mountType()
            && builtScale == currentMountScale()
            && builtSaddleScale == currentSaddleBuildScale())
        {
            return true;
        }

        despawn();
        List<Model> separateModels = null;
        Model unicornModel = (config.mountType() == MountType.GRYPHON
            || config.mountType() == MountType.ARTIO)
            ? buildUnscaledPostScaleMountModel()
            : buildMountModel();
        if (unicornModel == null)
        {
            return false;
        }

        unicorn = client.createRuneLiteObject();
        rider = client.createRuneLiteObject();
        if (unicorn == null || rider == null)
        {
            despawn();
            return false;
        }

        unicorn.setModel(unicornModel);
        unicorn.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
        unicorn.setDrawFrontTilesFirst(true);

        if (config.mountType() == MountType.BLACK_UNICORN)
        {
            Model saddleModel;
            if (config.useCustomSaddle())
            {
                saddleMotionModels = new Model[SADDLE_MOTION_FRAMES];
                for (int i = 0; i < saddleMotionModels.length; i++)
                {
                    saddleMotionModels[i] = buildCustomSaddleModel(i);
                    if (saddleMotionModels[i] == null)
                    {
                        despawn();
                        return false;
                    }
                }
                saddleModel = saddleMotionModels[0];
                activeSaddleMotionFrame = 0;
            }
            else
            {
                saddleModel = buildSaddlePrototypeModel();
            }
            if (saddleModel != null)
            {
                saddle = client.createRuneLiteObject();
                if (saddle == null)
                {
                    despawn();
                    return false;
                }
                saddle.setModel(saddleModel);
                saddle.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
                saddle.setDrawFrontTilesFirst(true);
            }
        }
        else if (config.mountType() == MountType.GRYPHON)
        {
            Model saddleModel = buildGryphonSaddleModel(null, null);
            if (saddleModel != null)
            {
                saddle = client.createRuneLiteObject();
                if (saddle == null)
                {
                    despawn();
                    return false;
                }
                saddle.setModel(saddleModel);
                saddle.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
                saddle.setDrawFrontTilesFirst(true);
            }
        }
        else if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            Model saddleModel = buildBattleTurtleSaddleModel();
            if (saddleModel != null)
            {
                saddle = client.createRuneLiteObject();
                if (saddle == null)
                {
                    despawn();
                    return false;
                }
                saddle.setModel(saddleModel);
                saddle.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
                saddle.setDrawFrontTilesFirst(true);
            }
        }
        else if (config.mountType() == MountType.ARTIO)
        {
            Model saddleModel = buildArtioArmourModel(null, null);
            if (saddleModel != null)
            {
                saddle = client.createRuneLiteObject();
                if (saddle == null)
                {
                    despawn();
                    return false;
                }
                saddle.setModel(saddleModel);
                saddle.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
                saddle.setDrawFrontTilesFirst(true);

                // Cache-backed equipment is kept as a separate object. Some
                // cache models cannot be safely merged with the procedural
                // saddle and would otherwise make the entire saddle vanish.
                try
                {
                    // ensureObjects builds tack before buildRiderModel, which
                    // normally initialises this catalogue. Load it here first
                    // so the shield's worn model is available on initial spawn.
                    if (!modelRepository.isLoaded())
                    {
                        modelRepository.loadFromClient(client);
                    }
                    int scale = Math.max(1, 128 * config.artioArmourScale() / 100);
                    ModelData shieldData = isExtraWidePose()
                        ? null
                        : buildMountedVsShield(null, scale);
                    if (shieldData != null)
                    {
                        artioShield = client.createRuneLiteObject();
                        if (artioShield != null)
                        {
                            artioShield.setModel(shieldData.light(
                                AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z));
                            artioShield.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
                            artioShield.setDrawFrontTilesFirst(true);
                        }
                    }
                    artioWarspears = createArtioWarspears(null, scale);
                }
                catch (RuntimeException ignored)
                {
                    artioShield = null;
                }
            }
        }

        if (separateModels != null)
        {
            for (int i = 1; i < separateModels.size(); i++)
            {
                RuneLiteObject part = client.createRuneLiteObject();
                if (part == null)
                {
                    despawn();
                    return false;
                }
                part.setModel(separateModels.get(i));
                part.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
                part.setDrawFrontTilesFirst(true);
                mountParts.add(part);
            }
        }

        rider.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
        rider.setDrawFrontTilesFirst(true);

        builtMountType = config.mountType();
        builtScale = currentMountScale();
        builtSaddleScale = currentSaddleBuildScale();
        activeUnicornAnimation = -1;
        activeRiderAnimation = -1;
        return true;
    }

    private Model buildSaddlePrototypeModel()
    {
        if (config.useCustomSaddle())
        {
            return buildCustomSaddleModel(0);
        }

        int sourceNpcId;
        switch (config.saddleSource())
        {
            case BANK_BUFFALO:
                sourceNpcId = config.bankBuffaloNpcId() > 0
                    ? config.bankBuffaloNpcId()
                    : BANK_BUFFALO_NPC_ID;
                break;
            case CART_CAMEL:
                sourceNpcId = config.cartCamelNpcId() > 0
                    ? config.cartCamelNpcId()
                    : CART_CAMEL_NPC_ID;
                break;
            case MOUNTED_TERRORBIRD:
            default:
                sourceNpcId = SADDLE_SOURCE_NPC_ID;
                break;
        }
        if (sourceNpcId <= 0)
        {
            return null;
        }

        NPCComposition composition = client.getNpcDefinition(sourceNpcId);
        if (composition != null && composition.getConfigs() != null)
        {
            NPCComposition transformed = composition.transform();
            if (transformed != null)
            {
                composition = transformed;
            }
        }
        int[] ids = composition == null ? null : composition.getModels();
        int part = config.saddleSourcePart();
        if (ids == null || ids.length == 0 || part >= ids.length)
        {
            return null;
        }

        ModelData data;
        if (part == -1)
        {
            ModelData[] loaded = new ModelData[ids.length];
            int count = 0;
            for (int id : ids)
            {
                ModelData modelPart = client.loadModelData(id);
                if (modelPart != null)
                {
                    loaded[count++] = modelPart;
                }
            }
            if (count == 0)
            {
                return null;
            }
            if (count != loaded.length)
            {
                ModelData[] available = new ModelData[count];
                System.arraycopy(loaded, 0, available, 0, count);
                loaded = available;
            }
            data = count == 1 ? loaded[0] : client.mergeModels(loaded);
        }
        else
        {
            data = client.loadModelData(ids[part]);
        }
        if (data == null)
        {
            return null;
        }
        if (composition.getColorToReplace() != null
            && composition.getColorToReplaceWith() != null)
        {
            data = data.cloneColors();
            short[] from = composition.getColorToReplace();
            short[] to = composition.getColorToReplaceWith();
            for (int i = 0; i < Math.min(from.length, to.length); i++)
            {
                data.recolor(from[i], to[i]);
            }
        }
        data = data.cloneVertices();
        int scale = Math.max(1, 128 * config.saddleScale() / 100);
        data.scale(scale, scale, scale);
        return data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z);
    }

    private Model buildCustomSaddleModel(int motionFrame)
    {
        ModelData template = client.loadModelData(FALLBACK_BODY_MODEL);
        if (template == null)
        {
            return null;
        }

        ModelData data = client.mergeModels(new ModelData[]{
            template,
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy()
        });
        if (data == null || data.getVerticesCount() < 128 || data.getFaceCount() < 204)
        {
            return null;
        }

        data = data.cloneVertices().cloneColors();
        float[] x = data.getVerticesX();
        float[] y = data.getVerticesY();
        float[] z = data.getVerticesZ();
        int[] face1 = data.getFaceIndices1();
        int[] face2 = data.getFaceIndices2();
        int[] face3 = data.getFaceIndices3();
        short[] colors = data.getFaceColors();

        for (int i = 0; i < x.length; i++)
        {
            x[i] = 0;
            y[i] = 0;
            z[i] = 0;
        }
        for (int i = 0; i < face1.length; i++)
        {
            face1[i] = 0;
            face2[i] = 0;
            face3[i] = 0;
            colors[i] = 0;
        }

        final short clothRed = (short) 931;
        final short saddleBlack = (short) 13;
        final short blackHighlight = (short) 21;
        final short steelGrey = (short) 72;

        int vertex = 0;
        int face = 0;

        face = addSaddleArch(x, y, z, face1, face2, face3, colors,
            vertex, face,
            new int[]{-38, -30, 0, 30, 38},
            new int[]{17, 1, -10, 1, 17},
            new int[]{23, 7, -4, 7, 23},
            -41, 39, clothRed);
        vertex += 20;
        face = addSaddleArch(x, y, z, face1, face2, face3, colors,
            vertex, face,
            new int[]{-28, -22, 0, 22, 28},
            new int[]{4, -12, -22, -12, 4},
            new int[]{9, -6, -15, -6, 9},
            -32, 28, saddleBlack);
        vertex += 20;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -17, 17, -25, -15, 20, 29, blackHighlight);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -21, 21, -24, -15, -33, -25, blackHighlight);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -39, -34, 12, 31, -7, 8, steelGrey);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, 34, 39, 12, 31, -7, 8, steelGrey);
        vertex += 8;

        // Twin segmented reins. The three short sections on each side create a
        // deliberate low-poly curve from the pommel towards the unicorn's bit.
        int reinLength = config.reinLength();
        int reinEndHeight = config.reinEndHeight();
        int reinSpread = config.reinSpread();
        double reinMotionScale = config.reinMotionSizePercent() / 100.0;
        int reinHeightProfile;
        int reinForwardProfile;
        int regularFrames = REIN_LOOP_FRAMES * REIN_REGULAR_LOOPS;
        if (motionFrame < regularFrames)
        {
            int loopFrame = motionFrame % REIN_LOOP_FRAMES;
            reinHeightProfile = UNICORN_REIN_HEIGHT_PROFILE[loopFrame];
            reinForwardProfile = UNICORN_REIN_FORWARD_PROFILE[loopFrame];
        }
        else
        {
            // After four regular motions the source idle animation performs a
            // pronounced bow. Follow it as its own eased down-and-up section.
            double bowProgress = (motionFrame - regularFrames)
                / (double) (REIN_LOOP_FRAMES - 1);
            double bowAmount = Math.sin(Math.PI * bowProgress);
            // RuneLite model Y increases downwards here: follow the muzzle
            // down during the bow instead of lifting the bit through it.
            reinHeightProfile = (int) Math.round(350.0 * bowAmount);
            reinForwardProfile = (int) Math.round(85.0 + 150.0 * bowAmount);
        }
        int reinHeadHeightMotion = (int) Math.round(
            config.reinHeadBob() * reinMotionScale * reinHeightProfile / 100.0);
        int reinHeadForwardMotion = (int) Math.round(
            config.reinHeadSway() * reinMotionScale * reinForwardProfile / 100.0);
        if (motionFrame >= regularFrames)
        {
            // The final part of the unicorn idle is a full neck-driven bow,
            // not another small head bob. Do not attenuate this displacement
            // with the user's fine-motion percentage: doing so leaves the bit
            // almost stationary while the muzzle travels through it.
            double bowProgress = (motionFrame - regularFrames)
                / (double) (REIN_LOOP_FRAMES - 1);
            double bowAmount;
            final double bowPeak = 0.33;
            if (bowProgress <= bowPeak)
            {
                // Reach the confirmed mouth position slightly earlier so the
                // return begins with the unicorn's upward head movement.
                bowAmount = Math.sin(
                    bowProgress / bowPeak * Math.PI / 2.0);
            }
            else
            {
                // Complete the return another 20% faster than the previous
                // tuned return, then hold at rest. This lets the
                // bit follow the unicorn's quicker upward head movement.
                double returnProgress = (bowProgress - bowPeak)
                    / (0.32 / 1.2 / 1.2 / 1.2);
                bowAmount = returnProgress >= 1.0
                    ? 0.0
                    : Math.cos(returnProgress * Math.PI / 2.0);
            }
            reinHeadHeightMotion = (int) Math.round(27.2 * bowAmount);
            // Negative model Z extends the mouth end away from the rider.
            // Follow the bow forwards towards the dipping muzzle instead of
            // pulling the bit backwards into the unicorn's neck and body.
            reinHeadForwardMotion = (int) Math.round(-5.6 * bowAmount);
        }
        int[][][] reinPaths =
        {
            buildReinPath(-1, reinLength, reinEndHeight, reinSpread,
                reinHeadHeightMotion, reinHeadForwardMotion,
                config.leftReinHandForward(), config.leftReinHandHeight(),
                config.leftReinHandSideways()),
            buildReinPath(1, reinLength, reinEndHeight, reinSpread,
                reinHeadHeightMotion, reinHeadForwardMotion,
                config.rightReinHandForward(), config.rightReinHandHeight(),
                config.rightReinHandSideways())
        };
        for (int[][] reinPath : reinPaths)
        {
            for (int i = 0; i < reinPath.length - 1; i++)
            {
                int[] from = reinPath[i];
                int[] to = reinPath[i + 1];
                face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
                    vertex, face,
                    from[0], from[1], from[2],
                    to[0], to[1], to[2],
                    2, 1, clothRed);
                vertex += 8;
            }
        }

        // Steel-grey bit across the mouth, joining the two rein endpoints.
        int bitHalfWidth = Math.max(2, 11 + reinSpread) + 6;
        int bitY = -47 + reinEndHeight + reinHeadHeightMotion;
        int bitZ = -reinLength + reinHeadForwardMotion;
        addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face,
            -bitHalfWidth, bitHalfWidth,
            bitY - 2, bitY + 2,
            bitZ - 3, bitZ + 3,
            steelGrey);

        int scale = Math.max(1, 128 * config.saddleScale() / 100);
        data.scale(scale, scale, scale);
        return data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z);
    }

    /** Purpose-built low-poly siege saddle for the Battle turtle. */
    private Model buildBattleTurtleSaddleModel()
    {
        ModelData template = client.loadModelData(FALLBACK_BODY_MODEL);
        if (template == null)
        {
            return null;
        }

        ModelData data = client.mergeModels(new ModelData[]{
            template,
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy()
        });
        if (data == null || data.getVerticesCount() < 256 || data.getFaceCount() < 400)
        {
            return null;
        }

        data = data.cloneVertices().cloneColors();
        float[] x = data.getVerticesX();
        float[] y = data.getVerticesY();
        float[] z = data.getVerticesZ();
        int[] face1 = data.getFaceIndices1();
        int[] face2 = data.getFaceIndices2();
        int[] face3 = data.getFaceIndices3();
        short[] colors = data.getFaceColors();

        for (int i = 0; i < x.length; i++)
        {
            x[i] = 0;
            y[i] = 0;
            z[i] = 0;
        }
        for (int i = 0; i < face1.length; i++)
        {
            face1[i] = 0;
            face2[i] = 0;
            face3[i] = 0;
            colors[i] = 0;
        }

        // Earthy gnome-war-machine palette sampled to sit naturally against
        // the Battle turtle: shell olive, dark moss leather, walnut timber
        // and aged bronze rather than the earlier bright red/black scheme.
        final short darkWood = (short) 5057;
        final short woodEdge = (short) 5529;
        final short armourRed = (short) 9630;
        final short armourDark = (short) 4380;
        final short brass = (short) 7110;
        final short iron = (short) 72;

        int vertex = 0;
        int face = 0;

        // Broad arched foundation hugs the crown of the shell.
        face = addSaddleArch(x, y, z, face1, face2, face3, colors,
            vertex, face,
            new int[]{-68, -51, 0, 51, 68},
            new int[]{8, -3, -10, -3, 8},
            new int[]{19, 9, 2, 9, 19},
            -48, 43, darkWood);
        vertex += 20;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -61, 61, -14, -5, -44, 39, woodEdge);
        vertex += 8;

        // Low cross-legged command seat and a compact supported backrest.
        face = addSaddleArch(x, y, z, face1, face2, face3, colors,
            vertex, face,
            new int[]{-38, -28, 0, 28, 38},
            new int[]{-20, -28, -33, -28, -20},
            new int[]{-11, -18, -23, -18, -11},
            -26, 25, armourRed);
        vertex += 20;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -35, 35, -82, -24, 25, 37, armourDark);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -28, 28, -75, -29, 20, 27, armourRed);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -10, 10, -91, -81, 27, 35, brass);
        vertex += 8;

        // Layered flank armour makes the platform read as a battle saddle.
        int[][] armourPlates =
        {
            {-76, -60, -27, 15, -31, 31},
            {-82, -70, -18, 24, -20, 20},
            {-86, -76, -9, 31, -10, 10},
            {60, 76, -27, 15, -31, 31},
            {70, 82, -18, 24, -20, 20},
            {76, 86, -9, 31, -10, 10}
        };
        for (int i = 0; i < armourPlates.length; i++)
        {
            int[] plate = armourPlates[i];
            face = addSaddleBox(x, y, z, face1, face2, face3, colors,
                vertex, face,
                plate[0], plate[1], plate[2], plate[3], plate[4], plate[5],
                i % 3 == 1 ? armourDark : armourRed);
            vertex += 8;
        }

        // Gold shell straps visually clamp the whole rig to the turtle.
        int[][] straps =
        {
            {-66, -2, -42, -66, 27, 39},
            {-48, -3, -43, -48, 30, 40},
            {48, -3, -43, 48, 30, 40},
            {66, -2, -42, 66, 27, 39}
        };
        for (int[] strap : straps)
        {
            face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
                vertex, face,
                strap[0], strap[1], strap[2], strap[3], strap[4], strap[5],
                3, 2, brass);
            vertex += 8;
        }

        // Twin swivel cannons: iron carriages, stepped barrels, brass
        // reinforcement bands and dark recessed muzzles for a hollow bore.
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, 42, 79, -45, -26, -4, 26, armourDark);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, 51, 69, -50, -34, -70, 13, iron);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, 48, 72, -53, -31, -83, -68, brass);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, 45, 75, -56, -28, -96, -82, iron);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, 41, 79, -60, -24, -105, -94, brass);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, 48, 72, -53, -31, -109, -104, armourDark);
        vertex += 8;

        // Matching cannon on the opposite side of the command seat.
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -79, -42, -45, -26, -4, 26, armourDark);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -69, -51, -50, -34, -70, 13, iron);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -72, -48, -53, -31, -83, -68, brass);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -75, -45, -56, -28, -96, -82, iron);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -79, -41, -60, -24, -105, -94, brass);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -72, -48, -53, -31, -109, -104, armourDark);
        vertex += 8;

        // Pivot axles and rear aiming handles.
        face = addSaddleCylinderX(x, y, z, face1, face2, face3, colors,
            vertex, face, 35, 86, -34, 4, 6, brass);
        vertex += 16;
        face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
            vertex, face, 59, -37, 19, 59, -54, 42, 3, 2, darkWood);
        vertex += 8;
        face = addSaddleCylinderX(x, y, z, face1, face2, face3, colors,
            vertex, face, -86, -35, -34, 4, 6, brass);
        vertex += 16;
        addSaddleStrap(x, y, z, face1, face2, face3, colors,
            vertex, face, -59, -37, 19, -59, -54, 42, 3, 2, darkWood);

        int scale = Math.max(1, 128 * config.battleTurtleSaddleScale() / 100);
        data.scale(scale, scale, scale);
        return data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z);
    }

    /** Purpose-built low-poly Fremennik hunting saddle for Artio. */
    private Model buildArtioArmourModel(int[][] motion, int[] reinAnchors)
    {
        ModelData template = client.loadModelData(FALLBACK_BODY_MODEL);
        if (template == null)
        {
            return null;
        }

        ModelData data = client.mergeModels(new ModelData[]{
            template,
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy()
        });
        // Extra Wide with both five-section reins and its mouth bar uses 208
        // vertices and 316 faces. Retain the proven 248/396 backing capacity
        // used by the live saddle; requesting additional merged templates can
        // make RuneLite's procedural model merge fail and leave the complete
        // saddle absent.
        if (data == null || data.getVerticesCount() < 248 || data.getFaceCount() < 396)
        {
            return null;
        }

        data = data.cloneVertices().cloneColors();
        float[] x = data.getVerticesX();
        float[] y = data.getVerticesY();
        float[] z = data.getVerticesZ();
        int[] face1 = data.getFaceIndices1();
        int[] face2 = data.getFaceIndices2();
        int[] face3 = data.getFaceIndices3();
        short[] colors = data.getFaceColors();

        for (int i = 0; i < x.length; i++)
        {
            x[i] = 0;
            y[i] = 0;
            z[i] = 0;
        }
        for (int i = 0; i < face1.length; i++)
        {
            face1[i] = 0;
            face2[i] = 0;
            face3[i] = 0;
            colors[i] = 0;
        }

        final short blackSteel = (short) 13;
        final short darkSteel = (short) 21;
        final short gunmetal = (short) 72;
        final short edgeRed = (short) 931;
        final short deepRed = (short) 8120;
        // Muted, weathered hides. The previous brown rendered bright orange
        // in-game and made the saddle look like a temporary block prototype.
        final short leatherBrown = (short) 4653;
        final short leatherDark = (short) 4380;
        final short leatherLight = (short) 5931;

        int vertex = 0;
        int face = 0;

        int extraSeatHalfX = Math.max(4, 28 * config.artioExtraWideSaddleWidth() / 100);
        int extraSeatHalfY = Math.max(2,
            config.artioExtraWideSaddleBodyHeight()
                * config.artioExtraWideSaddleThickness() / 200);
        int extraSeatHalfZ = Math.max(8, 42 * config.artioExtraWideSaddleLength() / 100);
        int extraSeatX = config.artioExtraWideSaddleSideways();
        int extraSeatY = -24 - config.artioExtraWideSaddleHeight();
        int extraSeatZ = config.artioExtraWideSaddleForward();

        // Wide curved foundation follows the bear's shoulders and supports
        // the rider without becoming a square platform.
        face = addSaddleArch(x, y, z, face1, face2, face3, colors,
            vertex, face,
            new int[]{-43, -32, 0, 32, 43},
            new int[]{3, -3, -7, -3, 3},
            new int[]{13, 7, 3, 7, 13},
            -31, 30, leatherDark);
        vertex += 20;
        face = addSaddleArch(x, y, z, face1, face2, face3, colors,
            vertex, face,
            new int[]{-36, -27, 0, 27, 36},
            new int[]{0, -4, -9, -4, 0},
            new int[]{9, 4, 0, 4, 9},
            -26, 24, leatherBrown);
        vertex += 20;

        if (isExtraWidePose())
        {
            // A low V-shaped handlebar replaces the discarded front blocks.
            // Both grips are tied to the adjustable Extra Wide seat origin.
            int handleCenterX = extraSeatX + config.artioExtraWideHandlebarSideways();
            int handleReachX = Math.max(4,
                (extraSeatHalfX + 9) * config.artioExtraWideHandlebarSpread() / 100);
            int handleThickness = config.artioExtraWideHandlebarThickness();
            int handleStartY = extraSeatY - extraSeatHalfY - 7
                - config.artioExtraWideHandlebarHeight();
            int handleEndY = handleStartY - 10
                - config.artioExtraWideHandlebarAngle();
            int handleStartZ = extraSeatZ - extraSeatHalfZ + 7
                + config.artioExtraWideHandlebarForward();
            int handleEndZ = handleStartZ - 12;
            face = addSaddleMountBar(x, y, z, face1, face2, face3, colors,
                vertex, face,
                handleCenterX - 5, handleStartY, handleStartZ,
                handleCenterX - handleReachX, handleEndY, handleEndZ,
                handleThickness, handleThickness, blackSteel);
            vertex += 8;
            face = addSaddleMountBar(x, y, z, face1, face2, face3, colors,
                vertex, face,
                handleCenterX + 5, handleStartY, handleStartZ,
                handleCenterX + handleReachX, handleEndY, handleEndZ,
                handleThickness, handleThickness, blackSteel);
            vertex += 8;
        }
        else
        {
            // The regular saddle retains its narrow layered backrest.
            face = addSaddleBox(x, y, z, face1, face2, face3, colors,
                vertex, face, -25, 25, -57, -18, 22, 30, leatherDark);
            vertex += 8;
            face = addSaddleBox(x, y, z, face1, face2, face3, colors,
                vertex, face, -20, 20, -50, -20, 17, 25, leatherBrown);
            vertex += 8;
        }
        int lowerSeatHalfX = Math.max(1, 24 * config.artioLowerSeatWidth() / 100);
        int lowerSeatHalfY = Math.max(1, 5 * config.artioLowerSeatThickness() / 100);
        int lowerSeatHalfZ = Math.max(1, 12 * config.artioLowerSeatLength() / 100);
        int lowerSeatX = config.artioLowerSeatSideways();
        int lowerSeatY = -20 - config.artioLowerSeatHeight();
        int lowerSeatZ = 4 + config.artioLowerSeatForward();
        if (!isExtraWidePose())
        {
            face = addSaddleBox(x, y, z, face1, face2, face3, colors,
                vertex, face,
                lowerSeatX - lowerSeatHalfX, lowerSeatX + lowerSeatHalfX,
                lowerSeatY - lowerSeatHalfY, lowerSeatY + lowerSeatHalfY,
                lowerSeatZ - lowerSeatHalfZ, lowerSeatZ + lowerSeatHalfZ,
                leatherLight);
        }
        else
        {
            // A tapered four-section body gives the seat a raised rear, a
            // shallow rider pocket and a narrower nose instead of a wood-like
            // rectangular block. It deliberately spans this and the next
            // reserved eight-vertex slot.
            face = addExtraWideSaddleBody(x, y, z, face1, face2, face3, colors,
                vertex, face, extraSeatX, extraSeatY, extraSeatZ,
                extraSeatHalfX, extraSeatHalfY, extraSeatHalfZ,
                leatherDark, leatherDark);
        }
        vertex += 8;

        // Raised padded block closes the remaining visual gap between the
        // rider and the saddle. It overlaps the lower cushion so it reads as
        // one upholstered seat rather than another floating component.
        int cushionHalfX = Math.max(1, 21 * config.artioSeatCushionWidth() / 100);
        int cushionHalfY = Math.max(1, 6 * config.artioSeatCushionThickness() / 100);
        int cushionHalfZ = Math.max(1, 11 * config.artioSeatCushionLength() / 100);
        int cushionX = config.artioSeatCushionSideways();
        int cushionY = -29 - config.artioSeatCushionHeight();
        int cushionZ = 4 + config.artioSeatCushionForward();
        if (!isExtraWidePose())
        {
            face = addSaddleBox(x, y, z, face1, face2, face3, colors,
                vertex, face,
                cushionX - cushionHalfX, cushionX + cushionHalfX,
                cushionY - cushionHalfY, cushionY + cushionHalfY,
                cushionZ - cushionHalfZ, cushionZ + cushionHalfZ,
                leatherBrown);
        }
        else
        {
            // The shaped body above occupies this reserved slot too.
        }
        vertex += 8;

        // One continuous deck now joins both arches, the cushion and the
        // backrest. The old detached side rectangles and centre fittings have
        // deliberately been removed.
        int seatFrameHalfX = Math.max(1, 34 * config.artioSeatFrameWidth() / 100);
        int seatFrameHalfY = Math.max(1, 5 * config.artioSeatFrameThickness() / 100);
        int seatFrameHalfZ = Math.max(1, 30 * config.artioSeatFrameLength() / 100);
        int seatFrameX = config.artioSeatFrameSideways();
        int seatFrameY = -19 - config.artioSeatFrameHeight();
        int seatFrameZ = config.artioSeatFrameForward();
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face,
            seatFrameX - seatFrameHalfX, seatFrameX + seatFrameHalfX,
            seatFrameY - seatFrameHalfY, seatFrameY + seatFrameHalfY,
            seatFrameZ - seatFrameHalfZ, seatFrameZ + seatFrameHalfZ,
            blackSteel);
        vertex += 8;
        if (!isExtraWidePose())
        {
            face = addSaddleBox(x, y, z, face1, face2, face3, colors,
                vertex, face, -31, -21, -43, -14, 13, 29, leatherBrown);
        }
        else
        {
            face = addExtraWideSeatRib(x, y, z, face1, face2, face3, colors,
                vertex, face, extraSeatX, extraSeatY - extraSeatHalfY - 1,
                extraSeatZ, extraSeatHalfX, extraSeatHalfZ, 0, blackSteel);
            face = addExtraWideSeatRib(x, y, z, face1, face2, face3, colors,
                vertex + 4, face, extraSeatX, extraSeatY - extraSeatHalfY - 1,
                extraSeatZ, extraSeatHalfX, extraSeatHalfZ, 1, blackSteel);
        }
        vertex += 8;
        if (!isExtraWidePose())
        {
            face = addSaddleBox(x, y, z, face1, face2, face3, colors,
                vertex, face, 21, 31, -43, -14, 13, 29, leatherBrown);
        }
        else
        {
            face = addExtraWideSeatRib(x, y, z, face1, face2, face3, colors,
                vertex, face, extraSeatX, extraSeatY - extraSeatHalfY - 1,
                extraSeatZ, extraSeatHalfX, extraSeatHalfZ, 2, blackSteel);
            face = addExtraWideSeatRib(x, y, z, face1, face2, face3, colors,
                vertex + 4, face, extraSeatX, extraSeatY - extraSeatHalfY - 1,
                extraSeatZ, extraSeatHalfX, extraSeatHalfZ, 3, blackSteel);
        }
        vertex += 8;

        // Dark steel mounting bars run from the connected saddle deck out to
        // each genuine warspear. Their shared controls allow the outer ends
        // to be lined up precisely with the separately adjustable spears.
        int barOuterX = 46 + config.artioSpearBarSideways();
        int barVertical = config.artioSpearBarVertical();
        int barInnerY = -19 - barVertical;
        int barOuterY = -3 - config.artioSpearBarHeight() - barVertical;
        int barZ = -7 + (isExtraWidePose()
            ? config.artioExtraWideSpearBarForward()
            : config.artioSpearBarForward());
        int barThickness = config.artioSpearBarThickness();
        int barWidth = config.artioSpearBarWidth();
        face = addSaddleMountBar(x, y, z, face1, face2, face3, colors,
            vertex, face, -29, barInnerY, barZ, -barOuterX, barOuterY, barZ,
            barThickness, barWidth, blackSteel);
        vertex += 8;
        face = addSaddleMountBar(x, y, z, face1, face2, face3, colors,
            vertex, face, 29, barInnerY, barZ, barOuterX, barOuterY, barZ,
            barThickness, barWidth, blackSteel);
        vertex += 8;

        if (isExtraWidePose() && !isNoSaddlePose())
        {
            int handleCenterX = extraSeatX + config.artioExtraWideHandlebarSideways();
            int handleReachX = Math.max(4,
                (extraSeatHalfX + 9) * config.artioExtraWideHandlebarSpread() / 100);
            int handleStartY = extraSeatY - extraSeatHalfY - 7
                - config.artioExtraWideHandlebarHeight();
            int handleEndY = handleStartY - 10
                - config.artioExtraWideHandlebarAngle();
            int handleStartZ = extraSeatZ - extraSeatHalfZ + 7
                + config.artioExtraWideHandlebarForward();
            int handleEndZ = handleStartZ - 12;
            int headSpread = config.artioExtraWideReinHeadSpread();
            int headSideways = config.artioExtraWideReinHeadSideways();
            int[][] headEnds = reinAnchors != null && reinAnchors.length == 6
                ? new int[][]{
                    {reinAnchors[0], reinAnchors[1], reinAnchors[2]},
                    {reinAnchors[3], reinAnchors[4], reinAnchors[5]}}
                : new int[][]{
                    {headSideways - headSpread,
                        -config.artioExtraWideReinHeadHeight(),
                        -config.artioExtraWideReinHeadForward()},
                    {headSideways + headSpread,
                        -config.artioExtraWideReinHeadHeight(),
                        -config.artioExtraWideReinHeadForward()}};
            int mouthBarY = (headEnds[0][1] + headEnds[1][1]) / 2;
            int mouthBarZ = (headEnds[0][2] + headEnds[1][2]) / 2;
            for (int[] headEnd : headEnds)
            {
                headEnd[1] = mouthBarY;
                headEnd[2] = mouthBarZ;
            }
            int reinThickness = config.artioExtraWideReinThickness();
            int reinSag = config.artioExtraWideReinSag();
            int neckClearance = config.artioExtraWideReinNeckClearance();
            for (int side = 0; side < 2; side++)
            {
                int sign = side == 0 ? -1 : 1;
                int[] hand = {
                    handleCenterX + sign * (handleReachX
                        + config.artioExtraWideReinHandSideways()),
                    handleEndY - config.artioExtraWideReinHandHeight(),
                    handleEndZ - config.artioExtraWideReinHandForward()
                };
                int[] head = headEnds[side];
                int[] control1 = {
                    hand[0] + (head[0] - hand[0]) / 3
                        + sign * neckClearance / 3,
                    hand[1] + (head[1] - hand[1]) / 3 + reinSag,
                    hand[2] + (head[2] - hand[2]) / 3
                };
                int[] control2 = {
                    head[0] + (hand[0] - head[0]) / 3
                        + sign * neckClearance,
                    head[1] + (hand[1] - head[1]) / 3 + reinSag,
                    head[2] + (hand[2] - head[2]) / 3
                };
                int[][] path = buildCubicReinPath(hand, control1, control2, head, 6);
                for (int i = 0; i < path.length - 1; i++)
                {
                    face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
                        vertex, face,
                        path[i][0], path[i][1], path[i][2],
                        path[i + 1][0], path[i + 1][1], path[i + 1][2],
                        reinThickness, reinThickness, leatherDark);
                    vertex += 8;
                }
            }

            // Join the two animated mouth vertices with a level black bit.
            // Averaging Y/Z prevents unequal vertex heights from twisting the
            // bar diagonally through the bear's muzzle during animation.
            int mouthBarExtension = config.artioExtraWideMouthBarExtension();
            int mouthBarMinX = Math.min(headEnds[0][0], headEnds[1][0])
                - mouthBarExtension;
            int mouthBarMaxX = Math.max(headEnds[0][0], headEnds[1][0])
                + mouthBarExtension;
            face = addSaddleCylinderX(x, y, z, face1, face2, face3, colors,
                vertex, face,
                mouthBarMinX, mouthBarMaxX, mouthBarY, mouthBarZ,
                config.artioExtraWideMouthBarThickness(), blackSteel);
            vertex += 16;
        }
        if (motion != null)
        {
            // The complete saddle is one practical rigid assembly. Following
            // the stable upper-torso anchor keeps it attached without the
            // distortion caused by independently articulated armour plates.
            offsetVertexRange(x, y, z, 0, vertex, motion[0]);
        }

        int scale = Math.max(1, 128 * config.artioArmourScale() / 100);
        data.scale(scale, scale, scale);

        return data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z);
    }

    /** Loads the genuine equipped V's shield and fixes it to the backrest. */
    private ModelData buildMountedVsShield(int[][] motion, int saddleScale)
    {
        ModelRepository.Entry entry = modelRepository.item(VS_SHIELD_ITEM_ID);
        int[] modelIds = entry == null ? null : entry.models(0);
        if (modelIds == null || modelIds.length == 0)
        {
            return null;
        }

        List<ModelData> parts = new ArrayList<>();
        for (int modelId : modelIds)
        {
            if (modelId < 0)
            {
                continue;
            }
            ModelData part = client.loadModelData(modelId);
            if (part == null)
            {
                continue;
            }
            if (entry.cf != null && entry.cr != null)
            {
                part = part.cloneColors();
                for (int i = 0; i < Math.min(entry.cf.length, entry.cr.length); i++)
                {
                    part.recolor(entry.cf[i], entry.cr[i]);
                }
            }
            if (entry.tf != null && entry.tr != null)
            {
                part = part.cloneTextures();
                for (int i = 0; i < Math.min(entry.tf.length, entry.tr.length); i++)
                {
                    part.retexture(entry.tf[i], entry.tr[i]);
                }
            }
            parts.add(part);
        }
        if (parts.isEmpty())
        {
            return null;
        }

        ModelData shield = parts.size() == 1
            ? parts.get(0)
            : client.mergeModels(parts.toArray(new ModelData[0]));
        if (shield == null)
        {
            return null;
        }

        shield = shield.cloneVertices();
        float[] sx = shield.getVerticesX();
        float[] sy = shield.getVerticesY();
        float[] sz = shield.getVerticesZ();
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (int i = 0; i < shield.getVerticesCount(); i++)
        {
            minX = Math.min(minX, sx[i]);
            maxX = Math.max(maxX, sx[i]);
            minY = Math.min(minY, sy[i]);
            maxY = Math.max(maxY, sy[i]);
            minZ = Math.min(minZ, sz[i]);
            maxZ = Math.max(maxZ, sz[i]);
        }

        float centreX = (minX + maxX) * 0.5f;
        float centreY = (minY + maxY) * 0.5f;
        float centreZ = (minZ + maxZ) * 0.5f;
        float largestSpan = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
        float targetSize = 54f * config.artioShieldScale() / 100f;
        float fit = largestSpan <= 0f ? 1f : targetSize / largestSpan;
        double tilt = Math.toRadians(config.artioShieldTilt());
        double turn = Math.toRadians(config.artioShieldTurn());
        double sinTilt = Math.sin(tilt);
        double cosTilt = Math.cos(tilt);
        double sinTurn = Math.sin(turn);
        double cosTurn = Math.cos(turn);
        for (int i = 0; i < shield.getVerticesCount(); i++)
        {
            double localX = (sx[i] - centreX) * fit;
            double localY = (sy[i] - centreY) * fit;
            double localZ = (sz[i] - centreZ) * fit;

            double tiltedY = localY * cosTilt - localZ * sinTilt;
            double tiltedZ = localY * sinTilt + localZ * cosTilt;
            double turnedX = localX * cosTurn + tiltedZ * sinTurn;
            double turnedZ = -localX * sinTurn + tiltedZ * cosTurn;

            sx[i] = (float) turnedX + config.artioShieldSideways();
            sy[i] = (float) tiltedY - 38 - config.artioShieldHeight();
            sz[i] = (float) turnedZ + 39 + config.artioShieldForward();
        }
        if (motion != null)
        {
            offsetVertexRange(sx, sy, sz, 0, shield.getVerticesCount(), motion[0]);
        }
        shield.scale(saddleScale, saddleScale, saddleScale);
        return shield;
    }

    /** Builds one genuine Guthan's warspear as an independent saddle attachment. */
    private ModelData buildMountedGuthansWarspear(boolean left, int[][] motion, int saddleScale)
    {
        ModelRepository.Entry entry = modelRepository.item(GUTHANS_WARSPEAR_ITEM_ID);
        int[] modelIds = entry == null ? null : entry.models(0);
        if (modelIds == null || modelIds.length == 0)
        {
            return null;
        }
        List<ModelData> parts = new ArrayList<>();
        for (int modelId : modelIds)
        {
            if (modelId < 0) continue;
            ModelData part = client.loadModelData(modelId);
            if (part == null) continue;
            if (entry.cf != null && entry.cr != null)
            {
                part = part.cloneColors();
                for (int i = 0; i < Math.min(entry.cf.length, entry.cr.length); i++)
                    part.recolor(entry.cf[i], entry.cr[i]);
            }
            if (entry.tf != null && entry.tr != null)
            {
                part = part.cloneTextures();
                for (int i = 0; i < Math.min(entry.tf.length, entry.tr.length); i++)
                    part.retexture(entry.tf[i], entry.tr[i]);
            }
            parts.add(part);
        }
        if (parts.isEmpty()) return null;
        ModelData spear = parts.size() == 1 ? parts.get(0)
            : client.mergeModels(parts.toArray(new ModelData[0]));
        if (spear == null) return null;
        spear = spear.cloneVertices();
        float[] x = spear.getVerticesX(), y = spear.getVerticesY(), z = spear.getVerticesZ();
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (int i = 0; i < spear.getVerticesCount(); i++)
        {
            minX = Math.min(minX, x[i]); maxX = Math.max(maxX, x[i]);
            minY = Math.min(minY, y[i]); maxY = Math.max(maxY, y[i]);
            minZ = Math.min(minZ, z[i]); maxZ = Math.max(maxZ, z[i]);
        }
        float cx = (minX + maxX) * .5f, cy = (minY + maxY) * .5f, cz = (minZ + maxZ) * .5f;
        float spanX = maxX - minX, spanY = maxY - minY, spanZ = maxZ - minZ;
        float longest = Math.max(spanX, Math.max(spanY, spanZ));
        float fit = longest <= 0f ? 1f : (160f * config.artioWarspearScale() / 100f) / longest;
        double tilt = Math.toRadians(config.artioWarspearTilt());
        double turn = Math.toRadians(config.artioWarspearTurn());
        double sinTilt = Math.sin(tilt), cosTilt = Math.cos(tilt);
        double sinTurn = Math.sin(turn), cosTurn = Math.cos(turn);
        int sideways = left ? config.artioLeftWarspearSideways() : config.artioRightWarspearSideways();
        for (int i = 0; i < spear.getVerticesCount(); i++)
        {
            double rx, ry, rz;
            if (spanY >= spanX && spanY >= spanZ)
            {
                rx = x[i] - cx; ry = z[i] - cz; rz = y[i] - cy;
            }
            else if (spanX >= spanY && spanX >= spanZ)
            {
                rx = z[i] - cz; ry = y[i] - cy; rz = x[i] - cx;
            }
            else
            {
                rx = x[i] - cx; ry = y[i] - cy; rz = z[i] - cz;
            }
            rx *= fit; ry *= fit; rz *= fit;
            double tiltedY = ry * cosTilt - rz * sinTilt;
            double tiltedZ = ry * sinTilt + rz * cosTilt;
            double turnedX = rx * cosTurn + tiltedZ * sinTurn;
            double turnedZ = -rx * sinTurn + tiltedZ * cosTurn;
            x[i] = (float) turnedX + sideways;
            y[i] = (float) tiltedY - 3 - config.artioWarspearHeight();
            z[i] = (float) turnedZ - 7 + config.artioWarspearForward();
        }
        if (motion != null) offsetVertexRange(x, y, z, 0, spear.getVerticesCount(), motion[0]);
        spear.scale(saddleScale, saddleScale, saddleScale);
        return spear;
    }

    private RuneLiteObject createArtioWarspears(int[][] motion, int saddleScale)
    {
        try
        {
            ModelData left = buildMountedGuthansWarspear(true, motion, saddleScale);
            ModelData right = buildMountedGuthansWarspear(false, motion, saddleScale);
            ModelData data = left == null ? right : right == null ? left
                : client.mergeModels(new ModelData[]{left, right});
            RuneLiteObject object = data == null ? null : client.createRuneLiteObject();
            if (object != null)
            {
                object.setModel(data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z));
                object.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
                object.setDrawFrontTilesFirst(true);
            }
            return object;
        }
        catch (RuntimeException ignored)
        {
            return null;
        }
    }

    private void positionArtioAttachment(RuneLiteObject object, LocalPoint point,
        int plane, int saddleHeight, int orientation)
    {
        if (object != null)
        {
            object.setLocation(point, plane);
            object.setZ(Perspective.getTileHeight(client, point, plane) - saddleHeight);
            object.setOrientation(orientation);
        }
    }

    private void updateArtioArmour()
    {
        Model mountModel = unicorn == null ? null : unicorn.getModel();
        if (mountModel == null || mountModel.getVerticesCount() <= 199)
        {
            return;
        }

        float[] xs = mountModel.getVerticesX();
        float[] ys = mountModel.getVerticesY();
        float[] zs = mountModel.getVerticesZ();
        int[] vertices = {95, 155, 199, 159};
        int[] anchors = new int[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++)
        {
            int source = vertices[i];
            anchors[i * 3] = Math.round(xs[source]);
            anchors[i * 3 + 1] = Math.round(ys[source]);
            anchors[i * 3 + 2] = Math.round(zs[source]);
        }

        if (baseArtioArmourAnchors == null)
        {
            baseArtioArmourAnchors = anchors.clone();
        }

        // Anchor zero drives the complete rigid saddle assembly. Retain its
        // unscaled rendered-model delta for the independently rendered rider.
        // Model Y grows downward, whereas rider height grows upward.
        currentArtioSeatSideways = anchors[0] - baseArtioArmourAnchors[0];
        currentArtioSeatHeight = -(anchors[1] - baseArtioArmourAnchors[1]);
        currentArtioSeatForward = anchors[2] - baseArtioArmourAnchors[2];

        int scale = Math.max(1, 128 * config.artioArmourScale() / 100);
        int[][] motion = new int[vertices.length][3];
        for (int i = 0; i < vertices.length; i++)
        {
            int offset = i * 3;
            motion[i][0] = Math.round((anchors[offset] - baseArtioArmourAnchors[offset]) * 128f / scale);
            motion[i][1] = Math.round((anchors[offset + 1] - baseArtioArmourAnchors[offset + 1]) * 128f / scale);
            motion[i][2] = Math.round((anchors[offset + 2] - baseArtioArmourAnchors[offset + 2]) * 128f / scale);
        }

        int[] reinAnchors = buildArtioReinAnchors(mountModel, motion, scale);
        int[] combinedAnchors = new int[anchors.length
            + (reinAnchors == null ? 0 : reinAnchors.length)];
        System.arraycopy(anchors, 0, combinedAnchors, 0, anchors.length);
        if (reinAnchors != null)
        {
            System.arraycopy(reinAnchors, 0, combinedAnchors, anchors.length,
                reinAnchors.length);
        }
        if (java.util.Arrays.equals(combinedAnchors, lastArtioArmourAnchors))
        {
            return;
        }

        Model model = buildArtioArmourModel(motion, reinAnchors);
        if (model != null)
        {
            saddle.setModel(model);
            lastArtioArmourAnchors = combinedAnchors;
        }
        if (artioShield != null)
        {
            try
            {
                ModelData shieldData = buildMountedVsShield(motion, scale);
                if (shieldData != null)
                {
                    artioShield.setModel(shieldData.light(
                        AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z));
                }
            }
            catch (RuntimeException ignored)
            {
                deactivate(artioShield);
                artioShield = null;
            }
        }
        updateArtioWarspears(motion, scale);
    }

    private int[] buildArtioReinAnchors(Model mountModel, int[][] motion, int scale)
    {
        if (!isExtraWidePose() || isNoSaddlePose())
        {
            return null;
        }

        float[] mountX = mountModel.getVerticesX();
        float[] mountY = mountModel.getVerticesY();
        float[] mountZ = mountModel.getVerticesZ();
        int[] targetX = {
            -ARTIO_REIN_MOUTH_TARGET_SPREAD,
            ARTIO_REIN_MOUTH_TARGET_SPREAD
        };
        int targetY = -ARTIO_REIN_MOUTH_TARGET_HEIGHT;
        int targetZ = -ARTIO_REIN_MOUTH_TARGET_FORWARD;
        for (int side = 0; side < 2; side++)
        {
            if (artioReinHeadVertices[side] >= 0
                && artioReinHeadVertices[side] < mountModel.getVerticesCount())
            {
                continue;
            }
            float wantedX = currentArtioSaddleSideways
                + (targetX[side] + motion[0][0]) * scale / 128f;
            float wantedY = -currentArtioSaddleHeight
                + (targetY + motion[0][1]) * scale / 128f;
            float wantedZ = -currentArtioSaddleForward
                + (targetZ + motion[0][2]) * scale / 128f;
            double bestDistance = Double.MAX_VALUE;
            int bestVertex = -1;
            for (int vertex = 0; vertex < mountModel.getVerticesCount(); vertex++)
            {
                if (vertex == artioReinHeadVertices[1 - side])
                {
                    continue;
                }
                double dx = mountX[vertex] - wantedX;
                double dy = mountY[vertex] - wantedY;
                double dz = mountZ[vertex] - wantedZ;
                double distance = dx * dx + dy * dy + dz * dz;
                if (distance < bestDistance)
                {
                    bestDistance = distance;
                    bestVertex = vertex;
                }
            }
            artioReinHeadVertices[side] = bestVertex;
        }

        int[] anchors = new int[6];
        for (int side = 0; side < 2; side++)
        {
            int vertex = artioReinHeadVertices[side];
            if (vertex < 0)
            {
                return null;
            }
            int offset = side * 3;
            anchors[offset] = Math.round(
                (mountX[vertex] - currentArtioSaddleSideways) * 128f / scale)
                - motion[0][0];
            anchors[offset + 1] = Math.round(
                (mountY[vertex] + currentArtioSaddleHeight) * 128f / scale)
                - motion[0][1];
            anchors[offset + 2] = Math.round(
                (mountZ[vertex] + currentArtioSaddleForward) * 128f / scale)
                - motion[0][2];

            // Keep the chosen animated mouth vertex as the moving base, then
            // apply the visible tuning controls as real saddle-local offsets.
            // This avoids snapping back to the vertex whenever the model is
            // rebuilt and lets the complete mouth bar be positioned freely.
            int sideSign = side == 0 ? -1 : 1;
            anchors[offset] += config.artioExtraWideReinHeadSideways()
                + sideSign * (config.artioExtraWideReinHeadSpread()
                    - ARTIO_REIN_MOUTH_TARGET_SPREAD);
            anchors[offset + 1] -= config.artioExtraWideReinHeadHeight()
                - ARTIO_REIN_MOUTH_TARGET_HEIGHT;
            anchors[offset + 2] -= config.artioExtraWideReinHeadForward()
                - ARTIO_REIN_MOUTH_TARGET_FORWARD;
        }
        return anchors;
    }

    private void updateArtioWarspears(int[][] motion, int scale)
    {
        if (artioWarspears == null) return;
        try
        {
            ModelData left = buildMountedGuthansWarspear(true, motion, scale);
            ModelData right = buildMountedGuthansWarspear(false, motion, scale);
            ModelData data = left == null ? right : right == null ? left
                : client.mergeModels(new ModelData[]{left, right});
            if (data != null)
                artioWarspears.setModel(data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z));
        }
        catch (RuntimeException ignored)
        {
            deactivate(artioWarspears);
            artioWarspears = null;
        }
    }

    private static void offsetVertexRange(
        float[] x, float[] y, float[] z, int start, int end, int[] delta)
    {
        int safeEnd = Math.min(end, x.length);
        for (int i = Math.max(0, start); i < safeEnd; i++)
        {
            x[i] += delta[0];
            y[i] += delta[1];
            z[i] += delta[2];
        }
    }

    /**
     * Purpose-built low-poly interpretation of the Skybound concept: an ivory
     * seat, tall supported back, gold frame, layered wing guards, long side
     * cloths and metal stirrups. It deliberately uses broad OSRS-style facets
     * rather than trying to reproduce the concept art's fine surface detail.
     */
    private Model buildGryphonSaddleModel(int[] reinAnchors, int[] saddleMotion)
    {
        ModelData template = client.loadModelData(FALLBACK_BODY_MODEL);
        if (template == null)
        {
            return null;
        }

        ModelData data = client.mergeModels(new ModelData[]{
            template,
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy(),
            template.shallowCopy()
        });
        if (data == null || data.getVerticesCount() < 252 || data.getFaceCount() < 376)
        {
            return null;
        }

        data = data.cloneVertices().cloneColors();
        float[] x = data.getVerticesX();
        float[] y = data.getVerticesY();
        float[] z = data.getVerticesZ();
        int[] face1 = data.getFaceIndices1();
        int[] face2 = data.getFaceIndices2();
        int[] face3 = data.getFaceIndices3();
        short[] colors = data.getFaceColors();

        for (int i = 0; i < x.length; i++)
        {
            x[i] = 0;
            y[i] = 0;
            z[i] = 0;
        }
        for (int i = 0; i < face1.length; i++)
        {
            face1[i] = 0;
            face2[i] = 0;
            face3[i] = 0;
            colors[i] = 0;
        }

        final short ivory = (short) 127;
        final short gold = (short) 7110;
        final short goldShade = (short) 6041;
        final short armadylSteel = (short) 72;
        final short armadylBlue = (short) 43915;

        int vertex = 0;
        int face = 0;

        // Deep, curved padded seat with a slim gold rim.
        face = addSaddleArch(x, y, z, face1, face2, face3, colors,
            vertex, face,
            new int[]{-43, -32, 0, 32, 43},
            new int[]{10, -4, -13, -4, 10},
            new int[]{18, 5, -4, 5, 18},
            -42, 35, gold);
        vertex += 20;
        face = addSaddleArch(x, y, z, face1, face2, face3, colors,
            vertex, face,
            new int[]{-36, -27, 0, 27, 36},
            new int[]{7, -6, -16, -6, 7},
            new int[]{13, 1, -8, 1, 13},
            -37, 29, ivory);
        vertex += 20;

        // Extra-tall backrest: gold outer support, ivory cushion and crest.
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -34, 34, -112, -3, 27, 39, gold);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -27, 27, -101, -9, 22, 31, ivory);
        vertex += 8;
        face = addSaddleBox(x, y, z, face1, face2, face3, colors,
            vertex, face, -13, 13, -121, -108, 29, 37, goldShade);
        vertex += 8;

        // Armadyl kiteshield mounted on the rear face of the backrest. A steel
        // outer shield surrounds a recessed blue center, with a compact gold
        // wing-and-spear mark to keep the symbol readable at game scale.
        face = addSaddleShield(x, y, z, face1, face2, face3, colors,
            vertex, face, 25, -88, -27, 40, 45, armadylSteel);
        vertex += 10;
        face = addSaddleShield(x, y, z, face1, face2, face3, colors,
            vertex, face, 18, -81, -34, 45, 48, armadylBlue);
        vertex += 10;
        face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
            vertex, face, 0, -75, 49, 0, -39, 49, 2, 1, gold);
        vertex += 8;
        face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
            vertex, face, -1, -58, 49, -15, -70, 49, 2, 1, gold);
        vertex += 8;
        face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
            vertex, face, 1, -58, 49, 15, -70, 49, 2, 1, gold);
        vertex += 8;
        face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
            vertex, face, -1, -51, 49, -14, -56, 49, 2, 1, goldShade);
        vertex += 8;
        face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
            vertex, face, 1, -51, 49, 14, -56, 49, 2, 1, goldShade);
        vertex += 8;

        // Gold wing guards rise beside the seat. Staggered feathers keep the
        // silhouette readable at RuneLite scale.
        int[][] featherBoxes =
        {
            {-50, -39, -58, -5, 15, 34},
            {-57, -46, -43, 3, 2, 24},
            {-62, -51, -27, 12, -9, 13},
            {39, 50, -58, -5, 15, 34},
            {46, 57, -43, 3, 2, 24},
            {51, 62, -27, 12, -9, 13}
        };
        for (int i = 0; i < featherBoxes.length; i++)
        {
            int[] box = featherBoxes[i];
            face = addSaddleBox(x, y, z, face1, face2, face3, colors,
                vertex, face, box[0], box[1], box[2], box[3], box[4], box[5],
                i % 3 == 1 ? goldShade : gold);
            vertex += 8;
        }

        if (reinAnchors != null && reinAnchors.length == 6)
        {
            // The head end is supplied from animated gryphon vertices 520/561.
            // These coordinates are converted into saddle-local space before
            // this model is built, so the reins remain fixed to the beak on
            // every animation frame while their rider ends stay at the hands.
            int[][] mouthEnds =
            {
                {reinAnchors[0], reinAnchors[1], reinAnchors[2]},
                {reinAnchors[3], reinAnchors[4], reinAnchors[5]}
            };
            // The raw anchor vertices sit at the sides of the mouth. Extend
            // their connecting line to create exposed bit ends, and attach
            // the reins there rather than burying them at the raw vertices.
            int bitDx = mouthEnds[1][0] - mouthEnds[0][0];
            int bitDy = mouthEnds[1][1] - mouthEnds[0][1];
            int bitDz = mouthEnds[1][2] - mouthEnds[0][2];
            double bitLength = Math.max(1.0,
                Math.sqrt(bitDx * bitDx + bitDy * bitDy + bitDz * bitDz));
            int bitExtension = 10;
            int bitExtendX = (int) Math.round(bitDx * bitExtension / bitLength);
            int bitExtendY = (int) Math.round(bitDy * bitExtension / bitLength);
            int bitExtendZ = (int) Math.round(bitDz * bitExtension / bitLength);
            int[][] bitEnds =
            {
                {
                    mouthEnds[0][0] - bitExtendX,
                    mouthEnds[0][1] - bitExtendY,
                    mouthEnds[0][2] - bitExtendZ
                },
                {
                    mouthEnds[1][0] + bitExtendX,
                    mouthEnds[1][1] + bitExtendY,
                    mouthEnds[1][2] + bitExtendZ
                }
            };
            int[][] handEnds =
            {
                {
                    config.gryphonLeftReinHandSideways(),
                    -config.gryphonLeftReinHandHeight(),
                    -config.gryphonLeftReinHandForward()
                },
                {
                    config.gryphonRightReinHandSideways(),
                    -config.gryphonRightReinHandHeight(),
                    -config.gryphonRightReinHandForward()
                }
            };
            for (int side = 0; side < 2; side++)
            {
                int[] hand = handEnds[side];
                // The model's left/right vertex order is opposite the rider's
                // hand order, so cross the array lookup to keep each rein on
                // its own side instead of crossing over the gryphon's neck.
                int[] mouth = bitEnds[1 - side];
                // Sample one cubic curve instead of joining hand-placed
                // corners. The two controls keep the rein outside the neck
                // and below the bit, producing a smooth rise into the mouth.
                int outward = hand[0] < 0 ? -42 : 42;
                int[] control1 =
                {
                    hand[0] + (mouth[0] - hand[0]) / 3 + outward,
                    hand[1] + (mouth[1] - hand[1]) / 3 + 12,
                    hand[2] + (mouth[2] - hand[2]) / 3
                };
                int[] control2 =
                {
                    mouth[0] + (hand[0] - mouth[0]) / 4 + outward,
                    mouth[1] + 24,
                    mouth[2] + (hand[2] - mouth[2]) / 5
                };
                int[][] path = buildCubicReinPath(hand, control1, control2, mouth, 5);
                for (int i = 0; i < path.length - 1; i++)
                {
                    face = addSaddleStrap(x, y, z, face1, face2, face3, colors,
                        vertex, face,
                        path[i][0], path[i][1], path[i][2],
                        path[i + 1][0], path[i + 1][1], path[i + 1][2],
                        2, 1, goldShade);
                    vertex += 8;
                }
            }

            // Keep the visible mouth bar level across the beak. The two mouth
            // vertices are on opposite sides, but their animated Y/Z values
            // are not identical; extending their full 3-D vector turns the
            // bar backwards through the head. Average Y/Z and extend only on
            // the model's sideways X axis so the bit stays inside the mouth.
            int bitCenterY = (mouthEnds[0][1] + mouthEnds[1][1]) / 2;
            int bitCenterZ = (mouthEnds[0][2] + mouthEnds[1][2]) / 2;
            int bitMinX = Math.min(mouthEnds[0][0], mouthEnds[1][0]) - bitExtension;
            int bitMaxX = Math.max(mouthEnds[0][0], mouthEnds[1][0]) + bitExtension;
            face = addSaddleCylinderX(x, y, z, face1, face2, face3, colors,
                vertex, face,
                bitMinX, bitMaxX, bitCenterY, bitCenterZ, 4, gold);
            vertex += 16;
        }

        if (saddleMotion != null)
        {
            // Move the complete rigid saddle assembly with the animated back
            // vertex. Mouth anchors are converted back out of this motion so
            // the bit and reins still terminate on the animated beak.
            offsetVertexRange(x, y, z, 0, vertex, saddleMotion);
        }

        int scale = Math.max(1, 128 * config.gryphonSaddleScale() / 100);
        data.scale(scale, scale, scale);
        return data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z);
    }

    private void updateGryphonSaddle()
    {
        Model mountModel = unicorn == null ? null : unicorn.getModel();
        if (mountModel == null || mountModel.getVerticesCount() <= 561)
        {
            return;
        }
        float[] mountX = mountModel.getVerticesX();
        float[] mountY = mountModel.getVerticesY();
        float[] mountZ = mountModel.getVerticesZ();
        int scale = Math.max(1, 128 * config.gryphonSaddleScale() / 100);

        if (gryphonSaddleAnchorVertex < 0
            || gryphonSaddleAnchorVertex >= mountModel.getVerticesCount())
        {
            float wantedX = currentGryphonSaddleSideways;
            float wantedY = -currentGryphonSaddleHeight;
            float wantedZ = -currentGryphonSaddleForward;
            double bestDistance = Double.MAX_VALUE;
            for (int vertex = 0; vertex < mountModel.getVerticesCount(); vertex++)
            {
                double dx = mountX[vertex] - wantedX;
                double dy = mountY[vertex] - wantedY;
                double dz = mountZ[vertex] - wantedZ;
                double distance = dx * dx + dy * dy + dz * dz;
                if (distance < bestDistance)
                {
                    bestDistance = distance;
                    gryphonSaddleAnchorVertex = vertex;
                }
            }
        }
        if (gryphonSaddleAnchorVertex < 0)
        {
            return;
        }

        int[] saddleAnchor = {
            Math.round(mountX[gryphonSaddleAnchorVertex]),
            Math.round(mountY[gryphonSaddleAnchorVertex]),
            Math.round(mountZ[gryphonSaddleAnchorVertex])
        };
        if (baseGryphonSaddleAnchor == null)
        {
            baseGryphonSaddleAnchor = saddleAnchor.clone();
        }
        currentGryphonSeatSideways = saddleAnchor[0] - baseGryphonSaddleAnchor[0];
        currentGryphonSeatHeight = -(saddleAnchor[1] - baseGryphonSaddleAnchor[1]);
        currentGryphonSeatForward = saddleAnchor[2] - baseGryphonSaddleAnchor[2];
        int[] saddleMotion = {
            Math.round(currentGryphonSeatSideways * 128f / scale),
            Math.round(-currentGryphonSeatHeight * 128f / scale),
            Math.round(currentGryphonSeatForward * 128f / scale)
        };

        int[] reinAnchors = new int[6];
        int[] vertices = {520, 561};
        final int mouthLowerCorrection = 7;
        final int mouthForwardCorrection = 42;
        for (int side = 0; side < vertices.length; side++)
        {
            int vertex = vertices[side];
            int offset = side * 3;
            // Convert from the mount object's origin to the translated saddle
            // origin, then undo the saddle's model scale because the complete
            // saddle data is scaled once at the end of its builder.
            reinAnchors[offset] = Math.round(
                (mountX[vertex] - currentGryphonSaddleSideways) * 128f / scale)
                - saddleMotion[0];
            reinAnchors[offset + 1] = Math.round(
                (mountY[vertex] + currentGryphonSaddleHeight
                    + mouthLowerCorrection) * 128f / scale)
                - saddleMotion[1];
            // Both models use negative Z as forward. Keep the native sign and
            // compensate for the small origin mismatch between the animated
            // NPC model and our custom saddle model. Mirroring this axis sends
            // the complete bridle behind the rider instead of to the beak.
            reinAnchors[offset + 2] = Math.round(
                (mountZ[vertex] + currentGryphonSaddleForward
                    - mouthForwardCorrection) * 128f / scale)
                - saddleMotion[2];
        }
        int[] anchors = new int[9];
        System.arraycopy(saddleAnchor, 0, anchors, 0, 3);
        System.arraycopy(reinAnchors, 0, anchors, 3, 6);
        if (java.util.Arrays.equals(anchors, lastGryphonReinAnchors))
        {
            return;
        }
        Model model = buildGryphonSaddleModel(reinAnchors, saddleMotion);
        if (model != null)
        {
            saddle.setModel(model);
            lastGryphonReinAnchors = anchors;
        }
    }

    /**
     * RuneLite objects do not parent one object to another object's bones.
     * Track a small cluster of vertices nearest Araxxor's fitted seat and
     * apply their animated delta to the independently rendered rider instead.
     * Averaging several nearby vertices avoids the jitter a single triangle
     * vertex can introduce while retaining the real idle and walking motion.
     */
    private void updateAraxxorRiderAnchor()
    {
        Model mountModel = unicorn == null ? null : unicorn.getModel();
        if (mountModel == null || mountModel.getVerticesCount() == 0)
        {
            return;
        }

        int vertexCount = mountModel.getVerticesCount();
        if (araxxorRiderAnchorVertices == null
            || araxxorRiderAnchorVertices.length == 0
            || araxxorRiderAnchorVertices[araxxorRiderAnchorVertices.length - 1] >= vertexCount)
        {
            final int wantedCount = Math.min(12, vertexCount);
            int[] nearest = new int[wantedCount];
            double[] distances = new double[wantedCount];
            java.util.Arrays.fill(nearest, -1);
            java.util.Arrays.fill(distances, Double.MAX_VALUE);

            float[] x = mountModel.getVerticesX();
            float[] y = mountModel.getVerticesY();
            float[] z = mountModel.getVerticesZ();
            float wantedX = config.araxxorRiderSideways();
            float wantedY = -config.araxxorRiderHeight();
            float wantedZ = -config.araxxorRiderForward();
            for (int vertex = 0; vertex < vertexCount; vertex++)
            {
                double dx = x[vertex] - wantedX;
                double dy = y[vertex] - wantedY;
                double dz = z[vertex] - wantedZ;
                double distance = dx * dx + dy * dy + dz * dz;
                for (int slot = 0; slot < wantedCount; slot++)
                {
                    if (distance < distances[slot])
                    {
                        for (int shift = wantedCount - 1; shift > slot; shift--)
                        {
                            distances[shift] = distances[shift - 1];
                            nearest[shift] = nearest[shift - 1];
                        }
                        distances[slot] = distance;
                        nearest[slot] = vertex;
                        break;
                    }
                }
            }
            araxxorRiderAnchorVertices = nearest;
            baseAraxxorRiderAnchor = null;
        }

        float[] x = mountModel.getVerticesX();
        float[] y = mountModel.getVerticesY();
        float[] z = mountModel.getVerticesZ();
        double totalX = 0;
        double totalY = 0;
        double totalZ = 0;
        int count = 0;
        for (int vertex : araxxorRiderAnchorVertices)
        {
            if (vertex >= 0 && vertex < vertexCount)
            {
                totalX += x[vertex];
                totalY += y[vertex];
                totalZ += z[vertex];
                count++;
            }
        }
        if (count == 0)
        {
            return;
        }

        int[] anchor = {
            (int) Math.round(totalX / count),
            (int) Math.round(totalY / count),
            (int) Math.round(totalZ / count)
        };
        if (baseAraxxorRiderAnchor == null)
        {
            baseAraxxorRiderAnchor = anchor.clone();
        }
        currentAraxxorSeatSideways = anchor[0] - baseAraxxorRiderAnchor[0];
        currentAraxxorSeatHeight = -(anchor[1] - baseAraxxorRiderAnchor[1]);
        currentAraxxorSeatForward = anchor[2] - baseAraxxorRiderAnchor[2];
    }

    private static int addSaddleArch(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int[] across,
        int[] top,
        int[] bottom,
        int minZ,
        int maxZ,
        short color)
    {
        for (int i = 0; i < across.length; i++)
        {
            int offset = vertex + i * 4;
            x[offset] = across[i];
            y[offset] = top[i];
            z[offset] = minZ;
            x[offset + 1] = across[i];
            y[offset + 1] = top[i];
            z[offset + 1] = maxZ;
            x[offset + 2] = across[i];
            y[offset + 2] = bottom[i];
            z[offset + 2] = minZ;
            x[offset + 3] = across[i];
            y[offset + 3] = bottom[i];
            z[offset + 3] = maxZ;
        }

        for (int i = 0; i < across.length - 1; i++)
        {
            int a = vertex + i * 4;
            int b = a + 4;
            face = addSaddleTriangle(face1, face2, face3, colors, face, a, b, b + 1, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face, a, b + 1, a + 1, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face, a + 2, b + 3, b + 2, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face, a + 2, a + 3, b + 3, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face, a, a + 2, b + 2, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face, a, b + 2, b, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face, a + 1, b + 1, b + 3, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face, a + 1, b + 3, a + 3, color);
        }

        int left = vertex;
        int right = vertex + (across.length - 1) * 4;
        face = addSaddleTriangle(face1, face2, face3, colors, face, left, left + 1, left + 3, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face, left, left + 3, left + 2, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face, right, right + 3, right + 1, color);
        return addSaddleTriangle(face1, face2, face3, colors, face, right, right + 2, right + 3, color);
    }

    private static int addSaddleTriangle(
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int face,
        int a,
        int b,
        int c,
        short color)
    {
        face1[face] = a;
        face2[face] = b;
        face3[face] = c;
        colors[face] = color;
        return face + 1;
    }

    private int currentSaddleBuildScale()
    {
        if (config.mountType() == MountType.ARTIO)
        {
            return config.artioArmourScale();
        }
        if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            return config.battleTurtleSaddleScale();
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            return config.gryphonSaddleScale();
        }
        if (config.mountType() != MountType.BLACK_UNICORN)
        {
            return -1;
        }
        int signature = config.saddleScale();
        signature = 31 * signature + config.reinLength();
        signature = 31 * signature + config.reinEndHeight();
        signature = 31 * signature + config.reinSpread();
        signature = 31 * signature + config.reinHeadBob();
        signature = 31 * signature + config.reinHeadSway();
        signature = 31 * signature + config.reinMotionSizePercent();
        signature = 31 * signature + config.leftReinHandForward();
        signature = 31 * signature + config.leftReinHandHeight();
        signature = 31 * signature + config.leftReinHandSideways();
        signature = 31 * signature + config.rightReinHandForward();
        signature = 31 * signature + config.rightReinHandHeight();
        return 31 * signature + config.rightReinHandSideways();
    }

    private static int[][] buildReinPath(
        int side,
        int length,
        int endHeight,
        int spread,
        int headHeightMotion,
        int headForwardMotion,
        int handForward,
        int handHeight,
        int handSideways)
    {
        int startX = side * 16 + handSideways;
        int startY = -18 - handHeight;
        int startZ = -25 - handForward;
        int endX = side * Math.max(2, 11 + spread);
        int endY = -47 + endHeight + headHeightMotion;
        int endZ = -length + headForwardMotion;
        return new int[][]
        {
            {startX, startY, startZ},
            {startX + (endX - startX) / 3,
                startY + (endY - startY) / 3 + 7,
                startZ + (endZ - startZ) / 3},
            {startX + (endX - startX) * 2 / 3,
                startY + (endY - startY) * 2 / 3 + 5,
                startZ + (endZ - startZ) * 2 / 3},
            {endX, endY, endZ}
        };
    }

    private static int addSaddleBox(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int minX,
        int maxX,
        int minY,
        int maxY,
        int minZ,
        int maxZ,
        short color)
    {
        float[][] points =
        {
            {minX, minY, minZ}, {maxX, minY, minZ},
            {maxX, maxY, minZ}, {minX, maxY, minZ},
            {minX, minY, maxZ}, {maxX, minY, maxZ},
            {maxX, maxY, maxZ}, {minX, maxY, maxZ}
        };
        for (int i = 0; i < points.length; i++)
        {
            x[vertex + i] = points[i][0];
            y[vertex + i] = points[i][1];
            z[vertex + i] = points[i][2];
        }

        int[][] triangles =
        {
            {0, 2, 1}, {0, 3, 2}, {4, 5, 6}, {4, 6, 7},
            {0, 1, 5}, {0, 5, 4}, {3, 7, 6}, {3, 6, 2},
            {0, 4, 7}, {0, 7, 3}, {1, 2, 6}, {1, 6, 5}
        };
        for (int[] triangle : triangles)
        {
            face1[face] = vertex + triangle[0];
            face2[face] = vertex + triangle[1];
            face3[face] = vertex + triangle[2];
            colors[face] = color;
            face++;
        }
        return face;
    }

    /** Adds a rectangular saddle section whose top and bottom slope along Z. */
    private static int addSaddleSlope(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int minX,
        int maxX,
        int startTopY,
        int startBottomY,
        int endTopY,
        int endBottomY,
        int startZ,
        int endZ,
        short color)
    {
        float[][] points =
        {
            {minX, startTopY, startZ}, {maxX, startTopY, startZ},
            {maxX, startBottomY, startZ}, {minX, startBottomY, startZ},
            {minX, endTopY, endZ}, {maxX, endTopY, endZ},
            {maxX, endBottomY, endZ}, {minX, endBottomY, endZ}
        };
        for (int i = 0; i < points.length; i++)
        {
            x[vertex + i] = points[i][0];
            y[vertex + i] = points[i][1];
            z[vertex + i] = points[i][2];
        }

        int[][] triangles =
        {
            {0, 2, 1}, {0, 3, 2}, {4, 5, 6}, {4, 6, 7},
            {0, 1, 5}, {0, 5, 4}, {3, 7, 6}, {3, 6, 2},
            {0, 4, 7}, {0, 7, 3}, {1, 2, 6}, {1, 6, 5}
        };
        for (int[] triangle : triangles)
        {
            face1[face] = vertex + triangle[0];
            face2[face] = vertex + triangle[1];
            face3[face] = vertex + triangle[2];
            colors[face] = color;
            face++;
        }
        return face;
    }

    /** Adds a tapered, profiled four-section body for the Extra Wide saddle. */
    private static int addExtraWideSaddleBody(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int centerX,
        int centerY,
        int centerZ,
        int halfX,
        int halfY,
        int halfZ,
        short sideColor,
        short topColor)
    {
        int[] sectionZ = {-halfZ, -halfZ / 3, halfZ / 3, halfZ};
        int[] widthPercent = {60, 95, 100, 72};
        int[] topOffset = {4, 0, -2, -5};
        int[] bottomOffset = {1, 0, 0, -1};
        for (int section = 0; section < 4; section++)
        {
            int base = vertex + section * 4;
            int width = Math.max(2, halfX * widthPercent[section] / 100);
            int top = centerY - halfY + topOffset[section];
            int bottom = centerY + halfY + bottomOffset[section];
            int sectionPosition = centerZ + sectionZ[section];
            x[base] = centerX - width;
            y[base] = top;
            z[base] = sectionPosition;
            x[base + 1] = centerX + width;
            y[base + 1] = top;
            z[base + 1] = sectionPosition;
            x[base + 2] = centerX - width;
            y[base + 2] = bottom;
            z[base + 2] = sectionPosition;
            x[base + 3] = centerX + width;
            y[base + 3] = bottom;
            z[base + 3] = sectionPosition;
        }

        for (int section = 0; section < 3; section++)
        {
            int a = vertex + section * 4;
            int b = a + 4;
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                a, b + 1, b, topColor);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                a, a + 1, b + 1, topColor);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                a + 3, b + 3, b + 2, sideColor);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                a + 3, b + 2, a + 2, sideColor);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                a, b, b + 2, sideColor);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                a, b + 2, a + 2, sideColor);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                a + 1, a + 3, b + 3, sideColor);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                a + 1, b + 3, b + 1, sideColor);
        }
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex, vertex + 2, vertex + 1, sideColor);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 1, vertex + 2, vertex + 3, sideColor);
        int end = vertex + 12;
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            end, end + 1, end + 2, sideColor);
        return addSaddleTriangle(face1, face2, face3, colors, face,
            end + 1, end + 3, end + 2, sideColor);
    }

    /** Adds one contrasting transverse rib to the Extra Wide saddle top. */
    private static int addExtraWideSeatRib(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int centerX,
        int topY,
        int centerZ,
        int seatHalfX,
        int seatHalfZ,
        int ribIndex,
        short color)
    {
        int signedStep = ribIndex * 2 - 3;
        int ribZ = centerZ + signedStep * seatHalfZ / 5;
        int ribHalfZ = Math.max(1, seatHalfZ / 16);
        int ribHalfX = Math.max(2,
            seatHalfX * (92 - Math.abs(signedStep) * 6) / 100);
        float[][] points =
        {
            {centerX - ribHalfX, topY, ribZ - ribHalfZ},
            {centerX + ribHalfX, topY, ribZ - ribHalfZ},
            {centerX + ribHalfX, topY, ribZ + ribHalfZ},
            {centerX - ribHalfX, topY, ribZ + ribHalfZ}
        };
        for (int i = 0; i < points.length; i++)
        {
            x[vertex + i] = points[i][0];
            y[vertex + i] = points[i][1];
            z[vertex + i] = points[i][2];
        }
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex, vertex + 2, vertex + 1, color);
        return addSaddleTriangle(face1, face2, face3, colors, face,
            vertex, vertex + 3, vertex + 2, color);
    }

    private static int addSaddleShield(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int halfWidth,
        int topY,
        int pointY,
        int minZ,
        int maxZ,
        short color)
    {
        int[] px = {-halfWidth, halfWidth, halfWidth - 3, 0, -halfWidth + 3};
        int[] py = {topY, topY, topY + (pointY - topY) * 2 / 3,
            pointY, topY + (pointY - topY) * 2 / 3};
        for (int i = 0; i < 5; i++)
        {
            x[vertex + i] = px[i];
            y[vertex + i] = py[i];
            z[vertex + i] = minZ;
            x[vertex + 5 + i] = px[i];
            y[vertex + 5 + i] = py[i];
            z[vertex + 5 + i] = maxZ;
        }

        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex, vertex + 1, vertex + 3, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex, vertex + 3, vertex + 4, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 1, vertex + 2, vertex + 3, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 5, vertex + 8, vertex + 6, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 5, vertex + 9, vertex + 8, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 6, vertex + 8, vertex + 7, color);
        for (int i = 0; i < 5; i++)
        {
            int next = (i + 1) % 5;
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + i, vertex + next, vertex + 5 + next, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + i, vertex + 5 + next, vertex + 5 + i, color);
        }
        return face;
    }

    /** Adds a five-sided plate extruded across the model's left/right axis. */
    private static int addSideArmourPlate(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int minX,
        int maxX,
        int[] plateY,
        int[] plateZ,
        short color)
    {
        for (int i = 0; i < 5; i++)
        {
            x[vertex + i] = minX;
            y[vertex + i] = plateY[i];
            z[vertex + i] = plateZ[i];
            x[vertex + 5 + i] = maxX;
            y[vertex + 5 + i] = plateY[i];
            z[vertex + 5 + i] = plateZ[i];
        }

        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex, vertex + 3, vertex + 1, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 1, vertex + 3, vertex + 2, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex, vertex + 4, vertex + 3, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 5, vertex + 6, vertex + 8, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 6, vertex + 7, vertex + 8, color);
        face = addSaddleTriangle(face1, face2, face3, colors, face,
            vertex + 5, vertex + 8, vertex + 9, color);
        for (int i = 0; i < 5; i++)
        {
            int next = (i + 1) % 5;
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + i, vertex + next, vertex + 5 + next, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + i, vertex + 5 + next, vertex + 5 + i, color);
        }
        return face;
    }

    /** A true three-dimensional beam used for the Artio spear brackets. */
    private static int addSaddleMountBar(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int startX,
        int startY,
        int startZ,
        int endX,
        int endY,
        int endZ,
        int halfThickness,
        int halfWidth,
        short color)
    {
        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.max(1.0, Math.sqrt(dx * dx + dy * dy));
        float px = (float) (-dy / length * halfThickness);
        float py = (float) (dx / length * halfThickness);
        float[][] points =
        {
            {startX + px, startY + py, startZ - halfWidth},
            {startX - px, startY - py, startZ - halfWidth},
            {startX - px, startY - py, startZ + halfWidth},
            {startX + px, startY + py, startZ + halfWidth},
            {endX + px, endY + py, endZ - halfWidth},
            {endX - px, endY - py, endZ - halfWidth},
            {endX - px, endY - py, endZ + halfWidth},
            {endX + px, endY + py, endZ + halfWidth}
        };
        for (int i = 0; i < points.length; i++)
        {
            x[vertex + i] = points[i][0];
            y[vertex + i] = points[i][1];
            z[vertex + i] = points[i][2];
        }
        int[][] triangles =
        {
            // Outward-facing end caps and four closed sides. The old order
            // pointed the normals inward, which made the exterior disappear.
            {0, 1, 2}, {0, 2, 3}, {4, 6, 5}, {4, 7, 6},
            {0, 5, 1}, {0, 4, 5}, {3, 6, 7}, {3, 2, 6},
            {0, 7, 4}, {0, 3, 7}, {1, 6, 2}, {1, 5, 6}
        };
        for (int[] triangle : triangles)
        {
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + triangle[0], vertex + triangle[1], vertex + triangle[2], color);
        }
        return face;
    }

    private static int addSaddleStrap(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int startX,
        int startY,
        int startZ,
        int endX,
        int endY,
        int endZ,
        int halfWidth,
        int halfThickness,
        short color)
    {
        float[][] points =
        {
            {startX - halfWidth, startY - halfThickness, startZ},
            {startX + halfWidth, startY - halfThickness, startZ},
            {startX + halfWidth, startY + halfThickness, startZ},
            {startX - halfWidth, startY + halfThickness, startZ},
            {endX - halfWidth, endY - halfThickness, endZ},
            {endX + halfWidth, endY - halfThickness, endZ},
            {endX + halfWidth, endY + halfThickness, endZ},
            {endX - halfWidth, endY + halfThickness, endZ}
        };
        for (int i = 0; i < points.length; i++)
        {
            x[vertex + i] = points[i][0];
            y[vertex + i] = points[i][1];
            z[vertex + i] = points[i][2];
        }

        int[][] triangles =
        {
            {0, 2, 1}, {0, 3, 2}, {4, 5, 6}, {4, 6, 7},
            {0, 1, 5}, {0, 5, 4}, {3, 7, 6}, {3, 6, 2},
            {0, 4, 7}, {0, 7, 3}, {1, 2, 6}, {1, 6, 5}
        };
        for (int[] triangle : triangles)
        {
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + triangle[0], vertex + triangle[1], vertex + triangle[2], color);
        }
        return face;
    }

    private static int[][] buildCubicReinPath(
        int[] start,
        int[] control1,
        int[] control2,
        int[] end,
        int pointCount)
    {
        int[][] path = new int[pointCount][3];
        for (int point = 0; point < pointCount; point++)
        {
            double t = point / (double) (pointCount - 1);
            double oneMinusT = 1.0 - t;
            for (int axis = 0; axis < 3; axis++)
            {
                double value = oneMinusT * oneMinusT * oneMinusT * start[axis]
                    + 3.0 * oneMinusT * oneMinusT * t * control1[axis]
                    + 3.0 * oneMinusT * t * t * control2[axis]
                    + t * t * t * end[axis];
                path[point][axis] = (int) Math.round(value);
            }
        }
        return path;
    }

    /** Adds an eight-sided horizontal cylinder for a compact, rounded bit. */
    private static int addSaddleCylinderX(
        float[] x,
        float[] y,
        float[] z,
        int[] face1,
        int[] face2,
        int[] face3,
        short[] colors,
        int vertex,
        int face,
        int minX,
        int maxX,
        int centerY,
        int centerZ,
        int radius,
        short color)
    {
        final int sides = 8;
        for (int end = 0; end < 2; end++)
        {
            int endX = end == 0 ? minX : maxX;
            for (int i = 0; i < sides; i++)
            {
                double angle = 2.0 * Math.PI * i / sides;
                int index = vertex + end * sides + i;
                x[index] = endX;
                y[index] = centerY + Math.round((float) (radius * Math.cos(angle)));
                z[index] = centerZ + Math.round((float) (radius * Math.sin(angle)));
            }
        }

        for (int i = 0; i < sides; i++)
        {
            int next = (i + 1) % sides;
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + i, vertex + next, vertex + sides + next, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + i, vertex + sides + next, vertex + sides + i, color);
        }
        for (int i = 1; i < sides - 1; i++)
        {
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex, vertex + i + 1, vertex + i, color);
            face = addSaddleTriangle(face1, face2, face3, colors, face,
                vertex + sides, vertex + sides + i, vertex + sides + i + 1, color);
        }
        return face;
    }

    private Model buildUnscaledPostScaleMountModel()
    {
        int npcId = config.mountType() == MountType.ARTIO
            ? config.artioNpcId() : GRYPHON_NPC_ID;
        NPCComposition composition = client.getNpcDefinition(npcId);
        int[] ids = composition == null ? null : composition.getModels();
        if (ids == null || ids.length == 0)
        {
            return null;
        }

        ModelData[] parts = new ModelData[ids.length];
        int count = 0;
        for (int id : ids)
        {
            ModelData part = client.loadModelData(id);
            if (part != null)
            {
                parts[count++] = part;
            }
        }
        if (count == 0)
        {
            return null;
        }

        ModelData[] loaded = new ModelData[count];
        System.arraycopy(parts, 0, loaded, 0, count);
        ModelData merged = count == 1 ? loaded[0] : client.mergeModels(loaded);
        if (merged == null)
        {
            return null;
        }

        if (composition.getColorToReplace() != null
            && composition.getColorToReplaceWith() != null)
        {
            merged = merged.cloneColors();
            short[] from = composition.getColorToReplace();
            short[] to = composition.getColorToReplaceWith();
            for (int i = 0; i < Math.min(from.length, to.length); i++)
            {
                merged.recolor(from[i], to[i]);
            }
        }
        return merged.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z);
    }

    private List<Model> buildSeparateMountModels(int npcId)
    {
        NPCComposition composition = client.getNpcDefinition(npcId);
        int[] ids = composition == null ? null : composition.getModels();
        if (ids == null || ids.length == 0)
        {
            return null;
        }

        List<Model> models = new ArrayList<>();
        int widthBase = composition.getWidthScale();
        int heightBase = composition.getHeightScale();
        int mountScale = currentMountScale();
        int widthScale = Math.max(1, widthBase * mountScale / 100);
        int heightScale = Math.max(1, heightBase * mountScale / 100);

        for (int id : ids)
        {
            ModelData data = client.loadModelData(id);
            if (data == null)
            {
                continue;
            }
            if (composition.getColorToReplace() != null
                && composition.getColorToReplaceWith() != null)
            {
                data = data.cloneColors();
                short[] from = composition.getColorToReplace();
                short[] to = composition.getColorToReplaceWith();
                for (int i = 0; i < Math.min(from.length, to.length); i++)
                {
                    data.recolor(from[i], to[i]);
                }
            }
            data = data.cloneVertices();
            data.scale(widthScale, heightScale, widthScale);
            models.add(data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z));
        }
        return models.isEmpty() ? null : models;
    }

    private Model buildMountModel()
    {
        int npcId = BLACK_UNICORN_NPC_ID;
        if (config.mountType() == MountType.TERRORBIRD)
        {
            npcId = TERRORBIRD_NPC_ID;
        }
        else if (config.mountType() == MountType.LAVA_DRAGON)
        {
            npcId = LAVA_DRAGON_NPC_ID;
        }
        else if (config.mountType() == MountType.GRYPHON)
        {
            npcId = GRYPHON_NPC_ID;
        }
        else if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            npcId = config.battleTurtleNpcId();
        }
        else if (config.mountType() == MountType.ARTIO)
        {
            npcId = config.artioNpcId();
        }
        else if (config.mountType() == MountType.ARAXXOR)
        {
            npcId = ARAXXOR_NPC_ID;
        }
        NPCComposition composition = client.getNpcDefinition(npcId);
        int[] ids = null;
        if (composition != null)
        {
            ids = composition.getModels();
        }
        if ((ids == null || ids.length == 0)
            && config.mountType() == MountType.BLACK_UNICORN)
        {
            ids = new int[]{FALLBACK_BODY_MODEL, FALLBACK_DETAILS_MODEL};
        }
        if (ids == null || ids.length == 0)
        {
            return null;
        }

        ModelData[] parts = new ModelData[ids.length];
        int count = 0;
        for (int id : ids)
        {
            ModelData part = client.loadModelData(id);
            if (part != null)
            {
                parts[count++] = part;
            }
        }
        if (count == 0)
        {
            return null;
        }

        ModelData[] loaded = new ModelData[count];
        System.arraycopy(parts, 0, loaded, 0, count);
        ModelData merged = count == 1 ? loaded[0] : client.mergeModels(loaded);
        if (merged == null)
        {
            return null;
        }

        if (composition != null
            && composition.getColorToReplace() != null
            && composition.getColorToReplaceWith() != null)
        {
            short[] from = composition.getColorToReplace();
            short[] to = composition.getColorToReplaceWith();
            merged = merged.cloneColors();
            for (int i = 0; i < Math.min(from.length, to.length); i++)
            {
                merged.recolor(from[i], to[i]);
            }
        }

        merged = merged.cloneVertices();
        int widthBase = composition == null ? 128 : composition.getWidthScale();
        int heightBase = composition == null ? 128 : composition.getHeightScale();
        int mountScale = currentMountScale();
        int widthScale = Math.max(1, widthBase * mountScale / 100);
        int heightScale = Math.max(1, heightBase * mountScale / 100);
        merged.scale(widthScale, heightScale, widthScale);
        return merged.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z);
    }

    private int currentWalkOrIdleAnimation(boolean moving)
    {
        if (config.mountType() == MountType.TERRORBIRD)
        {
            return moving
                ? TERRORBIRD_WALK_ANIMATION_ID
                : TERRORBIRD_IDLE_ANIMATION_ID;
        }
        if (config.mountType() == MountType.LAVA_DRAGON)
        {
            return moving
                ? LAVA_DRAGON_WALK_ANIMATION_ID
                : LAVA_DRAGON_IDLE_ANIMATION_ID;
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            return moving
                ? GRYPHON_WALK_ANIMATION_ID
                : GRYPHON_IDLE_ANIMATION_ID;
        }
        if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            return moving
                ? config.battleTurtleWalkAnimation()
                : config.battleTurtleIdleAnimation();
        }
        if (config.mountType() == MountType.ARTIO)
        {
            return moving ? config.artioWalkAnimation() : config.artioIdleAnimation();
        }
        if (config.mountType() == MountType.ARAXXOR)
        {
            return moving ? config.araxxorWalkAnimation() : config.araxxorIdleAnimation();
        }
        return moving ? AnimationID.UNICORN_REWORK_WALK : AnimationID.UNICORN_REWORK_READY;
    }

    private AnimationController loopingAnimation(int animationId)
    {
        AnimationController controller = new AnimationController(client, animationId);
        controller.setOnFinished(AnimationController::reset);
        return controller;
    }

    private AnimationController loopingMountAnimation(int animationId)
    {
        if (config.mountType() != MountType.GRYPHON
            && config.mountType() != MountType.ARTIO)
        {
            return loopingAnimation(animationId);
        }

        int npcId = config.mountType() == MountType.ARTIO
            ? config.artioNpcId() : GRYPHON_NPC_ID;
        NPCComposition composition = client.getNpcDefinition(npcId);
        int widthBase = composition == null ? 128 : composition.getWidthScale();
        int heightBase = composition == null ? 128 : composition.getHeightScale();
        int mountScale = currentMountScale();
        int widthScale = Math.max(1, widthBase * mountScale / 100);
        int heightScale = Math.max(1, heightBase * mountScale / 100);
        AnimationController controller = new PostScaleAnimationController(
            client, animationId, widthScale, heightScale, widthScale);
        controller.setOnFinished(AnimationController::reset);
        return controller;
    }

    private AnimationController frozenAnimation(int animationId, int requestedFrame)
    {
        Animation animation = client.loadAnimation(animationId);
        if (animation == null)
        {
            return null;
        }

        int frameCount = animation.isMayaAnim()
            ? animation.getDuration()
            : animation.getNumFrames();
        if (frameCount <= 0)
        {
            return null;
        }

        int frame = Math.max(0, Math.min(requestedFrame, frameCount - 1));
        return new FrozenAnimationController(client, animation, frame);
    }

    private AnimationController tailLoopAnimation(int animationId, int startFrame, int endFrame)
    {
        Animation animation = client.loadAnimation(animationId);
        if (animation == null)
        {
            return null;
        }
        return new TailLoopAnimationController(client, animation, startFrame, endFrame);
    }

    private int currentStrideFollow()
    {
        if (config.mountType() == MountType.GRYPHON
            || config.mountType() == MountType.BATTLE_TURTLE
            || config.mountType() == MountType.ARTIO
            || config.mountType() == MountType.ARAXXOR)
        {
            return 0;
        }
        int amount;
        if (isWidePose())
        {
            amount = config.mountType() == MountType.TERRORBIRD ? -6 : -3;
        }
        else
        {
            amount = config.mountType() == MountType.TERRORBIRD
                ? config.terrorbirdStrideFollow()
                : config.lavaDragonStrideFollow();
        }
        AnimationController controller = unicorn == null ? null : unicorn.getAnimationController();
        Animation animation = controller == null ? null : controller.getAnimation();
        if (amount == 0 || animation == null || animation.getNumFrames() < 2)
        {
            return 0;
        }

        int frameCount = animation.getNumFrames();
        int frame = Math.floorMod(controller.getFrame(), frameCount);
        double phase = 4.0 * Math.PI * frame / frameCount;
        return (int) Math.round(amount * Math.sin(phase));
    }

    private int currentSeatBounce()
    {
        if (config.mountType() == MountType.GRYPHON
            || config.mountType() == MountType.ARAXXOR)
        {
            return 0;
        }
        int amount;
        if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            return currentTurtleBob(
                config.battleTurtleWalkBounce(),
                config.battleTurtleWalkBobTiming());
        }
        else if (config.mountType() == MountType.ARTIO)
        {
            return currentTurtleBob(config.artioWalkBounce(), config.artioWalkBobTiming());
        }
        else if (isWidePose())
        {
            amount = config.mountType() == MountType.TERRORBIRD ? 0 : 4;
        }
        else
        {
            amount = config.mountType() == MountType.TERRORBIRD
                ? config.terrorbirdSeatBounce()
                : config.lavaDragonSeatBounce();
        }
        double phase = currentStridePhase();
        return phase < 0 ? 0 : (int) Math.round(amount * (0.5 - 0.5 * Math.cos(phase)));
    }

    private int currentSeatSway()
    {
        if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            return 0;
        }
        if (config.mountType() == MountType.ARTIO)
        {
            return 0;
        }
        if (config.mountType() == MountType.ARAXXOR)
        {
            return 0;
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            int amount = config.gryphonSeatSway();
            double phase = currentStridePhase();
            return phase < 0 ? 0 : (int) Math.round(amount * Math.sin(phase));
        }
        int amount;
        if (isWidePose())
        {
            amount = config.mountType() == MountType.TERRORBIRD ? 3 : -4;
        }
        else
        {
            amount = config.mountType() == MountType.TERRORBIRD
                ? config.terrorbirdSeatSway()
                : config.lavaDragonSeatSway();
        }
        double phase = currentStridePhase();
        return phase < 0 ? 0 : (int) Math.round(amount * Math.sin(phase));
    }

    private int currentLateralSway()
    {
        if (config.mountType() != MountType.GRYPHON)
        {
            return 0;
        }
        double phase = currentStridePhase();
        return phase < 0
            ? 0
            : (int) Math.round(config.gryphonLateralSway() * Math.sin(phase));
    }

    private int currentIdleBounce()
    {
        int amount;
        if (config.mountType() == MountType.TERRORBIRD)
        {
            amount = config.terrorbirdIdleBounce();
        }
        else if (config.mountType() == MountType.LAVA_DRAGON)
        {
            amount = config.lavaDragonIdleBounce();
        }
        else if (config.mountType() == MountType.GRYPHON)
        {
            amount = config.gryphonIdleBounce();
        }
        else if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            return currentTurtleBob(
                config.battleTurtleIdleBounce(),
                config.battleTurtleIdleBobTiming());
        }
        else if (config.mountType() == MountType.ARTIO)
        {
            return currentTurtleBob(config.artioIdleBounce(), config.artioIdleBobTiming());
        }
        else if (config.mountType() == MountType.ARAXXOR)
        {
            return currentTurtleBob(config.araxxorIdleBounce(), 0);
        }
        else
        {
            amount = config.unicornIdleBounce();
        }
        double phase = currentStridePhase();
        return phase < 0 ? 0 : (int) Math.round(amount * (0.5 - 0.5 * Math.cos(phase)));
    }

    private double currentStridePhase()
    {
        AnimationController controller = unicorn == null ? null : unicorn.getAnimationController();
        Animation animation = controller == null ? null : controller.getAnimation();
        if (animation == null || animation.getNumFrames() < 2)
        {
            return -1;
        }

        int frameCount = animation.getNumFrames();
        int frame = Math.floorMod(controller.getFrame(), frameCount);
        return 4.0 * Math.PI * frame / frameCount;
    }

    private int currentTurtleBob(int amount, int timingOffset)
    {
        AnimationController controller = unicorn == null ? null : unicorn.getAnimationController();
        Animation animation = controller == null ? null : controller.getAnimation();
        if (amount == 0 || animation == null || animation.getNumFrames() < 2)
        {
            return 0;
        }

        int frameCount = animation.getNumFrames();
        int shiftedFrame = Math.floorMod(controller.getFrame() + timingOffset, frameCount);
        double phase = 4.0 * Math.PI * shiftedFrame / frameCount;
        return (int) Math.round(amount * (0.5 - 0.5 * Math.cos(phase)));
    }

    private int currentMountScale()
    {
        if (isWidePose())
        {
            if (config.mountType() == MountType.GRYPHON)
            {
                return config.gryphonScale();
            }
            return config.mountType() == MountType.BLACK_UNICORN ? 109 : 100;
        }
        if (config.mountType() == MountType.TERRORBIRD)
        {
            return config.terrorbirdScale();
        }
        if (config.mountType() == MountType.LAVA_DRAGON)
        {
            return config.lavaDragonScale();
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            return config.gryphonScale();
        }
        if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            return config.battleTurtleScale();
        }
        if (config.mountType() == MountType.ARTIO)
        {
            return config.artioScale();
        }
        if (config.mountType() == MountType.ARAXXOR)
        {
            return config.araxxorScale();
        }
        return config.mountScale();
    }

    private int currentRiderHeight()
    {
        if (isNoSaddlePose())
        {
            return config.artioNoSaddleRiderHeight();
        }
        if (isCrossLeggedPose())
        {
            return currentCrossLeggedRiderHeight();
        }
        if (isExtraWidePose())
        {
            return currentExtraWideRiderHeight();
        }
        if (isWidePose())
        {
            if (config.mountType() == MountType.ARTIO)
            {
                return config.artioRiderHeight();
            }
            if (config.mountType() == MountType.TERRORBIRD)
            {
                return 43;
            }
            if (config.mountType() == MountType.LAVA_DRAGON)
            {
                return 48;
            }
            if (config.mountType() == MountType.GRYPHON)
            {
                return config.gryphonWideRiderHeight();
            }
            return config.riderHeight();
        }
        if (config.mountType() == MountType.TERRORBIRD)
        {
            return config.terrorbirdRiderHeight();
        }
        if (config.mountType() == MountType.LAVA_DRAGON)
        {
            return config.lavaDragonRiderHeight();
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            return config.gryphonRiderHeight();
        }
        if (config.mountType() == MountType.ARTIO)
        {
            return config.artioRiderHeight();
        }
        return config.riderHeight();
    }

    private int currentRiderForward()
    {
        if (isNoSaddlePose())
        {
            return config.artioNoSaddleRiderForward();
        }
        if (isCrossLeggedPose())
        {
            return currentCrossLeggedRiderForward();
        }
        if (isExtraWidePose())
        {
            return currentExtraWideRiderForward();
        }
        if (isWidePose())
        {
            if (config.mountType() == MountType.ARTIO)
            {
                return config.artioRiderForward();
            }
            if (config.mountType() == MountType.TERRORBIRD)
            {
                return 11;
            }
            if (config.mountType() == MountType.LAVA_DRAGON)
            {
                return -81;
            }
            if (config.mountType() == MountType.GRYPHON)
            {
                return config.gryphonRiderForward();
            }
            return 10;
        }
        if (config.mountType() == MountType.TERRORBIRD)
        {
            return config.terrorbirdRiderForward();
        }
        if (config.mountType() == MountType.LAVA_DRAGON)
        {
            return config.lavaDragonRiderForward();
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            return config.gryphonRiderForward();
        }
        if (config.mountType() == MountType.ARTIO)
        {
            return config.artioRiderForward();
        }
        return config.riderForward();
    }

    private int currentRiderSideways()
    {
        if (isNoSaddlePose())
        {
            return config.artioNoSaddleRiderSideways();
        }
        if (isCrossLeggedPose())
        {
            return currentCrossLeggedRiderSideways();
        }
        if (isExtraWidePose())
        {
            return currentExtraWideRiderSideways();
        }
        if (isWidePose())
        {
            if (config.mountType() == MountType.ARTIO)
            {
                return config.artioRiderSideways();
            }
            if (config.mountType() == MountType.GRYPHON)
            {
                return config.gryphonRiderSideways();
            }
            if (config.mountType() == MountType.LAVA_DRAGON)
            {
                return config.lavaDragonRiderSideways();
            }
            if (config.mountType() == MountType.TERRORBIRD)
            {
                return config.terrorbirdRiderSideways();
            }
            return config.riderSideways();
        }
        if (config.mountType() == MountType.TERRORBIRD)
        {
            return config.terrorbirdRiderSideways();
        }
        if (config.mountType() == MountType.LAVA_DRAGON)
        {
            return config.lavaDragonRiderSideways();
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            return config.gryphonRiderSideways();
        }
        if (config.mountType() == MountType.ARTIO)
        {
            return config.artioRiderSideways();
        }
        return config.riderSideways();
    }

    private int currentCrossLeggedRiderHeight()
    {
        switch (config.mountType())
        {
            case TERRORBIRD: return config.terrorbirdCrossLeggedRiderHeight();
            case LAVA_DRAGON: return config.lavaDragonCrossLeggedRiderHeight();
            case GRYPHON: return config.gryphonCrossLeggedRiderHeight();
            case BATTLE_TURTLE: return config.battleTurtleCrossLeggedRiderHeight();
            case ARTIO: return config.artioCrossLeggedRiderHeight();
            case BLACK_UNICORN:
            default: return config.unicornCrossLeggedRiderHeight();
        }
    }

    private int currentCrossLeggedRiderForward()
    {
        switch (config.mountType())
        {
            case TERRORBIRD: return config.terrorbirdCrossLeggedRiderForward();
            case LAVA_DRAGON: return config.lavaDragonCrossLeggedRiderForward();
            case GRYPHON: return config.gryphonCrossLeggedRiderForward();
            case BATTLE_TURTLE: return config.battleTurtleCrossLeggedRiderForward();
            case ARTIO: return config.artioCrossLeggedRiderForward();
            case BLACK_UNICORN:
            default: return config.unicornCrossLeggedRiderForward();
        }
    }

    private int currentCrossLeggedRiderSideways()
    {
        switch (config.mountType())
        {
            case TERRORBIRD: return config.terrorbirdCrossLeggedRiderSideways();
            case LAVA_DRAGON: return config.lavaDragonCrossLeggedRiderSideways();
            case GRYPHON: return config.gryphonCrossLeggedRiderSideways();
            case BATTLE_TURTLE: return config.battleTurtleCrossLeggedRiderSideways();
            case ARTIO: return config.artioCrossLeggedRiderSideways();
            case BLACK_UNICORN:
            default: return config.unicornCrossLeggedRiderSideways();
        }
    }

    private int currentExtraWideRiderHeight()
    {
        if (config.mountType() == MountType.ARAXXOR)
        {
            return config.araxxorRiderHeight();
        }
        switch (config.mountType())
        {
            case TERRORBIRD: return config.terrorbirdExtraWideRiderHeight();
            case LAVA_DRAGON: return config.lavaDragonExtraWideRiderHeight();
            case GRYPHON: return config.gryphonExtraWideRiderHeight();
            case BATTLE_TURTLE: return config.battleTurtleExtraWideRiderHeight();
            case ARTIO: return config.artioExtraWideRiderHeight();
            case BLACK_UNICORN:
            default: return config.unicornExtraWideRiderHeight();
        }
    }

    private int currentExtraWideRiderForward()
    {
        if (config.mountType() == MountType.ARAXXOR)
        {
            return config.araxxorRiderForward();
        }
        switch (config.mountType())
        {
            case TERRORBIRD: return config.terrorbirdExtraWideRiderForward();
            case LAVA_DRAGON: return config.lavaDragonExtraWideRiderForward();
            case GRYPHON: return config.gryphonExtraWideRiderForward();
            case BATTLE_TURTLE: return config.battleTurtleExtraWideRiderForward();
            case ARTIO: return config.artioExtraWideRiderForward();
            case BLACK_UNICORN:
            default: return config.unicornExtraWideRiderForward();
        }
    }

    private int currentExtraWideRiderSideways()
    {
        if (config.mountType() == MountType.ARAXXOR)
        {
            return config.araxxorRiderSideways();
        }
        switch (config.mountType())
        {
            case TERRORBIRD: return config.terrorbirdExtraWideRiderSideways();
            case LAVA_DRAGON: return config.lavaDragonExtraWideRiderSideways();
            case GRYPHON: return config.gryphonExtraWideRiderSideways();
            case BATTLE_TURTLE: return config.battleTurtleExtraWideRiderSideways();
            case ARTIO: return config.artioExtraWideRiderSideways();
            case BLACK_UNICORN:
            default: return config.unicornExtraWideRiderSideways();
        }
    }

    private int currentWalkHeightAdjustment()
    {
        if (config.mountType() == MountType.ARAXXOR)
        {
            return config.araxxorWalkHeight();
        }
        if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            return config.battleTurtleWalkHeightAdjustment();
        }
        if (config.mountType() == MountType.ARTIO)
        {
            return config.artioWalkHeightAdjustment();
        }
        if (isWidePose())
        {
            if (config.mountType() == MountType.GRYPHON)
            {
                return config.gryphonWalkHeightAdjustment();
            }
            return config.mountType() == MountType.LAVA_DRAGON ? -10 : 0;
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            return config.gryphonWalkHeightAdjustment();
        }
        return config.mountType() == MountType.TERRORBIRD
            ? config.terrorbirdWalkHeightAdjustment()
            : config.lavaDragonWalkHeightAdjustment();
    }

    private int currentWalkForwardAdjustment()
    {
        if (config.mountType() == MountType.ARAXXOR)
        {
            return config.araxxorWalkForward();
        }
        if (config.mountType() == MountType.BATTLE_TURTLE)
        {
            return config.battleTurtleWalkForwardAdjustment();
        }
        if (config.mountType() == MountType.ARTIO)
        {
            return config.artioWalkForwardAdjustment();
        }
        if (isWidePose())
        {
            if (config.mountType() == MountType.GRYPHON)
            {
                return config.gryphonWalkForwardAdjustment();
            }
            return config.mountType() == MountType.TERRORBIRD ? 25 : -12;
        }
        if (config.mountType() == MountType.GRYPHON)
        {
            return config.gryphonWalkForwardAdjustment();
        }
        return config.mountType() == MountType.TERRORBIRD
            ? config.terrorbirdWalkForwardAdjustment()
            : config.lavaDragonWalkForwardAdjustment();
    }

    private boolean isWidePose()
    {
        return config.ridingPose() == RidingPose.WIDE;
    }

    private boolean isExtraWidePose()
    {
        return config.ridingPose().usesExtraWideAnimation();
    }

    private boolean isNoSaddlePose()
    {
        return config.mountType() == MountType.ARTIO
            && config.ridingPose() == RidingPose.NO_SADDLE;
    }

    private boolean isCrossLeggedPose()
    {
        return config.ridingPose() == RidingPose.CROSS_LEGGED;
    }

    private void playMountEffect()
    {
        if (!config.mountEffect() || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        Player player = client.getLocalPlayer();
        if (player == null || player.getLocalLocation() == null)
        {
            return;
        }

        if (!spotAnimRepository.isLoaded())
        {
            spotAnimRepository.loadFromClient(client);
        }
        SpotAnimRepository.Entry effect = spotAnimRepository.get(
            SpotanimID.VFX_MAHJARRAT_SMOKE_IMPACT_SMALL);
        if (effect == null)
        {
            return;
        }

        ModelData data = client.loadModelData(effect.modelId());
        Animation animation = client.loadAnimation(effect.animationId());
        if (data == null || animation == null)
        {
            return;
        }

        data = data.cloneColors();
        if (effect.cf != null && effect.cr != null)
        {
            for (int i = 0; i < Math.min(effect.cf.length, effect.cr.length); i++)
            {
                data.recolor(effect.cf[i], effect.cr[i]);
            }
        }
        if (effect.tf != null && effect.tr != null)
        {
            data = data.cloneTextures();
            for (int i = 0; i < Math.min(effect.tf.length, effect.tr.length); i++)
            {
                data.retexture(effect.tf[i], effect.tr[i]);
            }
        }

        int quarterTurns = Math.floorMod(effect.rotation(), 360) / 90;
        for (int i = 0; i < quarterTurns; i++)
        {
            data = data.cloneVertices().rotateY90Ccw();
        }
        if (effect.resizeX() != 128 || effect.resizeY() != 128)
        {
            data = data.cloneVertices().scale(
                effect.resizeX(), effect.resizeY(), effect.resizeX());
        }

        RuneLiteObject smoke = client.createRuneLiteObject();
        if (smoke == null)
        {
            return;
        }
        smoke.setModel(data.light(
            AMBIENT + effect.ambient(),
            CONTRAST + effect.contrast(),
            LIGHT_X,
            LIGHT_Y,
            LIGHT_Z));
        smoke.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
        smoke.setDrawFrontTilesFirst(true);

        LocalPoint point = player.getLocalLocation();
        int plane = player.getWorldLocation().getPlane();
        smoke.setLocation(point, plane);
        smoke.setZ(Perspective.getTileHeight(client, point, plane));
        smoke.setOrientation(player.getCurrentOrientation());

        AnimationController controller = new AnimationController(client, animation);
        controller.setOnFinished(finished ->
        {
            smoke.setActive(false);
            activeEffects.remove(smoke);
        });
        smoke.setAnimationController(controller);
        smoke.setActive(true);
        activeEffects.add(smoke);
    }

    static LocalPoint offsetFromPlayer(
        LocalPoint point,
        int orientation,
        int forward,
        int sideways)
    {
        double angle = orientation * Math.PI / 1024.0;
        int dx = (int) Math.round(forward * Math.sin(angle) + sideways * Math.cos(angle));
        int dy = (int) Math.round(forward * Math.cos(angle) - sideways * Math.sin(angle));
        return new LocalPoint(point.getX() + dx, point.getY() + dy, point.getWorldView());
    }

    RuneLiteObject getArtioObjectForProbe()
    {
        return mounted && config.mountType() == MountType.ARTIO ? unicorn : null;
    }

    private int mountedHolsterSideways()
    {
        if (config.mountType() == MountType.BLACK_UNICORN && config.ridingPose() == RidingPose.STANDARD)
            return config.unicornStandardMountedHolsterSideways();
        if (config.mountType() == MountType.TERRORBIRD && config.ridingPose() == RidingPose.WIDE)
            return config.terrorbirdWideMountedHolsterSideways();
        if (config.mountType() == MountType.LAVA_DRAGON && config.ridingPose() == RidingPose.CROSS_LEGGED)
            return config.lavaDragonCrossleggedMountedHolsterSideways();
        if (config.mountType() == MountType.ARTIO && config.ridingPose() == RidingPose.STANDARD)
            return config.artioStandardMountedHolsterSideways();
        if (config.mountType() == MountType.ARTIO && config.ridingPose() == RidingPose.NO_SADDLE)
            return config.artioNoSaddleMountedHolsterSideways();
        switch (config.mountType())
        {
            case BLACK_UNICORN: return config.unicornMountedHolsterSideways();
            case TERRORBIRD: return config.terrorbirdMountedHolsterSideways();
            case LAVA_DRAGON: return config.lavaDragonMountedHolsterSideways();
            case GRYPHON: return config.gryphonMountedHolsterSideways();
            case BATTLE_TURTLE: return config.battleTurtleMountedHolsterSideways();
            case ARTIO: return config.artioMountedHolsterSideways();
            case ARAXXOR: return config.araxxorMountedHolsterSideways();
            default: return 0;
        }
    }

    private int mountedHolsterHeight()
    {
        if (config.mountType() == MountType.BLACK_UNICORN && config.ridingPose() == RidingPose.STANDARD)
            return config.unicornStandardMountedHolsterHeight();
        if (config.mountType() == MountType.TERRORBIRD && config.ridingPose() == RidingPose.WIDE)
            return config.terrorbirdWideMountedHolsterHeight();
        if (config.mountType() == MountType.LAVA_DRAGON && config.ridingPose() == RidingPose.CROSS_LEGGED)
            return config.lavaDragonCrossleggedMountedHolsterHeight();
        if (config.mountType() == MountType.ARTIO && config.ridingPose() == RidingPose.STANDARD)
            return config.artioStandardMountedHolsterHeight();
        if (config.mountType() == MountType.ARTIO && config.ridingPose() == RidingPose.NO_SADDLE)
            return config.artioNoSaddleMountedHolsterHeight();
        switch (config.mountType())
        {
            case BLACK_UNICORN: return config.unicornMountedHolsterHeight();
            case TERRORBIRD: return config.terrorbirdMountedHolsterHeight();
            case LAVA_DRAGON: return config.lavaDragonMountedHolsterHeight();
            case GRYPHON: return config.gryphonMountedHolsterHeight();
            case BATTLE_TURTLE: return config.battleTurtleMountedHolsterHeight();
            case ARTIO: return config.artioMountedHolsterHeight();
            case ARAXXOR: return config.araxxorMountedHolsterHeight();
            default: return -55;
        }
    }

    private int mountedHolsterForward()
    {
        if (config.mountType() == MountType.BLACK_UNICORN && config.ridingPose() == RidingPose.STANDARD)
            return config.unicornStandardMountedHolsterForward();
        if (config.mountType() == MountType.TERRORBIRD && config.ridingPose() == RidingPose.WIDE)
            return config.terrorbirdWideMountedHolsterForward();
        if (config.mountType() == MountType.LAVA_DRAGON && config.ridingPose() == RidingPose.CROSS_LEGGED)
            return config.lavaDragonCrossleggedMountedHolsterForward();
        if (config.mountType() == MountType.ARTIO && config.ridingPose() == RidingPose.STANDARD)
            return config.artioStandardMountedHolsterForward();
        if (config.mountType() == MountType.ARTIO && config.ridingPose() == RidingPose.NO_SADDLE)
            return config.artioNoSaddleMountedHolsterForward();
        switch (config.mountType())
        {
            case BLACK_UNICORN: return config.unicornMountedHolsterForward();
            case TERRORBIRD: return config.terrorbirdMountedHolsterForward();
            case LAVA_DRAGON: return config.lavaDragonMountedHolsterForward();
            case GRYPHON: return config.gryphonMountedHolsterForward();
            case BATTLE_TURTLE: return config.battleTurtleMountedHolsterForward();
            case ARTIO: return config.artioMountedHolsterForward();
            case ARAXXOR: return config.araxxorMountedHolsterForward();
            default: return 0;
        }
    }

    /** Only the companion Holster preview advertises mounted handoff support. */
    private boolean isMountedHolsterCompatible()
    {
        String heartbeat = configManager.getConfiguration(
            "rapidholster", "mountedHandoffSupported");
        if (heartbeat == null) return false;
        try
        {
            long age = System.currentTimeMillis() - Long.parseLong(heartbeat);
            return age >= 0 && age < 2000;
        }
        catch (NumberFormatException ignored)
        {
            return false;
        }
    }

    /** Tiny config bridge: both plugins can run independently and share no Java classes. */
    private void setHolsterHandoff(boolean active)
    {
        if (holsterHandedOff == active && (active
            || configManager.getConfiguration(RapidUrsaMountsConfig.GROUP,
                "mountedHolsterActive") == null)) return;
        holsterHandedOff = active;
        if (active)
        {
            configManager.setConfiguration(RapidUrsaMountsConfig.GROUP,
                "mountedHolsterActive", System.currentTimeMillis());
        }
        else
        {
            configManager.unsetConfiguration(RapidUrsaMountsConfig.GROUP,
                "mountedHolsterActive");
        }
    }

    private static void activate(RuneLiteObject object)
    {
        if (object != null && !object.isActive())
        {
            object.setActive(true);
        }
    }

    private static void deactivate(RuneLiteObject object)
    {
        if (object != null && object.isActive())
        {
            object.setActive(false);
        }
    }

    private void suspendCosmetics()
    {
        mountedRenderReady = false;
        setHolsterHandoff(false);
        if (mountedHolsterRenderer != null) mountedHolsterRenderer.clear();
        deactivate(rider);
        deactivate(unicorn);
        deactivate(saddle);
        deactivate(artioShield);
        deactivate(artioWarspears);
        for (RuneLiteObject part : mountParts)
        {
            deactivate(part);
        }
    }

    private void clearEffects()
    {
        for (RuneLiteObject effect : new ArrayList<>(activeEffects))
        {
            deactivate(effect);
        }
        activeEffects.clear();
    }

    private void despawn()
    {
        mountedRenderReady = false;
        setHolsterHandoff(false);
        if (mountedHolsterRenderer != null) mountedHolsterRenderer.clear();
        deactivate(rider);
        deactivate(unicorn);
        deactivate(saddle);
        deactivate(artioShield);
        deactivate(artioWarspears);
        for (RuneLiteObject part : mountParts)
        {
            deactivate(part);
        }
        mountParts.clear();
        rider = null;
        unicorn = null;
        saddle = null;
        artioShield = null;
        artioWarspears = null;
        lastGryphonReinAnchors = null;
        gryphonSaddleAnchorVertex = -1;
        baseGryphonSaddleAnchor = null;
        currentGryphonSeatForward = 0;
        currentGryphonSeatSideways = 0;
        currentGryphonSeatHeight = 0;
        araxxorRiderAnchorVertices = null;
        baseAraxxorRiderAnchor = null;
        currentAraxxorSeatForward = 0;
        currentAraxxorSeatSideways = 0;
        currentAraxxorSeatHeight = 0;
        baseArtioArmourAnchors = null;
        lastArtioArmourAnchors = null;
        artioReinHeadVertices[0] = -1;
        artioReinHeadVertices[1] = -1;
        currentArtioSeatForward = 0;
        currentArtioSeatSideways = 0;
        currentArtioSeatHeight = 0;
        currentArtioSaddleForward = 0;
        currentArtioSaddleSideways = 0;
        currentArtioSaddleHeight = 0;
        saddleMotionModels = null;
        activeSaddleMotionFrame = -1;
        builtRiderOutfit = null;
        builtRiderModel = null;
        builtScale = -1;
        builtSaddleScale = -1;
        builtMountType = null;
        activeUnicornAnimation = -1;
        activeRiderAnimation = -1;
        activeRiderFrame = -1;
        actionResumeTicks = 0;
        mountedRenderReady = false;
    }
}
