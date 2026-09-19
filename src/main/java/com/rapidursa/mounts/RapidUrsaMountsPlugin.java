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
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.HotkeyListener;

@PluginDescriptor(
    name = "Rapid Mounts",
    description = "Ride client-side cosmetic mounts",
    tags = {"mount", "unicorn", "terrorbird", "dragon", "gryphon", "cosmetic", "transmog"}
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
    private static final int FALLBACK_BODY_MODEL = 25754;
    private static final int FALLBACK_DETAILS_MODEL = 25756;
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
    private AppearanceComposer appearanceComposer;

    @Inject
    private ModelRepository modelRepository;

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
    private ClientToolbar clientToolbar;

    @Inject
    private MountStablePanel mountStablePanel;

    private NavigationButton stableNavigation;

    private RuneLiteObject unicorn;
    private RuneLiteObject saddle;
    private int[] lastGryphonReinAnchors;
    private int currentGryphonSaddleForward;
    private int currentGryphonSaddleSideways;
    private int currentGryphonSaddleHeight;
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
        hooks.registerRenderableDrawListener(drawListener);
        mounted = true;
        mountButton.bind(this);
        mountStablePanel.bind(this);
        stableNavigation = NavigationButton.builder()
            .tooltip("Rapid Mounts")
            .icon(mountStablePanel.getSidebarIcon())
            .priority(7)
            .panel(mountStablePanel)
            .build();
        clientToolbar.addNavigation(stableNavigation);
        overlayManager.add(mountButton);
        mouseManager.registerMouseListener(mountButton);
        keyManager.registerKeyListener(mountHotkey);
    }

    @Override
    protected void shutDown()
    {
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
            || "gryphonScale".equals(event.getKey()))
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
            || "gryphonRightReinHandSideways".equals(event.getKey()))
        {
            despawn();
        }
        else if ("animateUnicorn".equals(event.getKey()) && unicorn != null)
        {
            unicorn.setAnimationController(null);
            activeUnicornAnimation = -1;
        }
        else if (("useRidingPose".equals(event.getKey())
            || "ridingPose".equals(event.getKey())) && rider != null)
        {
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
            return;
        }

        Player player = client.getLocalPlayer();
        if (player == null || player.getLocalLocation() == null)
        {
            return;
        }

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
                if (moving)
                {
                    saddleForward += currentWalkForwardAdjustment() + currentSeatSway();
                    saddleSideways += currentLateralSway();
                    saddleHeight += currentWalkHeightAdjustment()
                        + currentStrideFollow() + currentSeatBounce();
                }
                else
                {
                    saddleHeight += currentIdleBounce();
                }
                currentGryphonSaddleForward = saddleForward;
                currentGryphonSaddleSideways = saddleSideways;
                currentGryphonSaddleHeight = saddleHeight;
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
        }

        if (config.animateUnicorn())
        {
            int wantedAnimation = currentWalkOrIdleAnimation(moving);
            if (wantedAnimation != activeUnicornAnimation)
            {
                unicorn.setAnimationController(loopingMountAnimation(wantedAnimation));
                for (RuneLiteObject part : mountParts)
                {
                    part.setAnimationController(loopingAnimation(wantedAnimation));
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

        if (config.mountType() == MountType.GRYPHON
            && config.showGryphonSaddle()
            && saddle != null)
        {
            updateGryphonReins();
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
                if (config.mountType() != MountType.BLACK_UNICORN && moving)
                {
                    riderForward += currentWalkForwardAdjustment();
                    riderForward += currentSeatSway();
                    riderSideways += currentLateralSway();
                    riderHeight += currentWalkHeightAdjustment();
                    riderHeight += currentStrideFollow();
                    riderHeight += currentSeatBounce();
                }
                else if (config.mountType() != MountType.BLACK_UNICORN && !moving)
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

                if (config.useRidingPose())
                {
                    int wantedRiderAnimation = config.ridingPose() == RidingPose.WIDE
                        ? WIDE_RIDER_ANIMATION_ID
                        : RIDER_ANIMATION_ID;
                    int wantedRiderFrame = config.ridingPose() == RidingPose.WIDE
                        ? WIDE_RIDER_FRAME
                        : -1;
                    if (wantedRiderAnimation != activeRiderAnimation
                        || wantedRiderFrame != activeRiderFrame)
                    {
                        AnimationController controller = config.ridingPose() == RidingPose.WIDE
                            ? frozenAnimation(wantedRiderAnimation, wantedRiderFrame)
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
        // Each fitted tack set belongs to its finished riding pose: the
        // unicorn uses Wide, while the gryphon uses Standard.
        if ((config.mountType() == MountType.BLACK_UNICORN
                && isWidePose()
                && config.showSaddleAndReins())
            || (config.mountType() == MountType.GRYPHON
                && !isWidePose()
                && config.showGryphonSaddle()))
        {
            activate(saddle);
        }
        else
        {
            deactivate(saddle);
        }
        if (riderReady)
        {
            activate(rider);
            mountedRenderReady = unicorn.isActive() && rider.isActive();
        }
        else
        {
            deactivate(rider);
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
        Model unicornModel = config.mountType() == MountType.GRYPHON
            ? buildUnscaledGryphonModel()
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

        if (config.mountType() == MountType.BLACK_UNICORN && config.showSaddlePrototype())
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
        else if (config.mountType() == MountType.GRYPHON && config.showGryphonSaddle())
        {
            Model saddleModel = buildGryphonSaddleModel(null);
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

    /**
     * Purpose-built low-poly interpretation of the Skybound concept: an ivory
     * seat, tall supported back, gold frame, layered wing guards, long side
     * cloths and metal stirrups. It deliberately uses broad OSRS-style facets
     * rather than trying to reproduce the concept art's fine surface detail.
     */
    private Model buildGryphonSaddleModel(int[] reinAnchors)
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

        int scale = Math.max(1, 128 * config.gryphonSaddleScale() / 100);
        data.scale(scale, scale, scale);
        return data.light(AMBIENT, CONTRAST, LIGHT_X, LIGHT_Y, LIGHT_Z);
    }

    private void updateGryphonReins()
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
        int[] anchors = new int[6];
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
            anchors[offset] = Math.round(
                (mountX[vertex] - currentGryphonSaddleSideways) * 128f / scale);
            anchors[offset + 1] = Math.round(
                (mountY[vertex] + currentGryphonSaddleHeight
                    + mouthLowerCorrection) * 128f / scale);
            // Both models use negative Z as forward. Keep the native sign and
            // compensate for the small origin mismatch between the animated
            // NPC model and our custom saddle model. Mirroring this axis sends
            // the complete bridle behind the rider instead of to the beak.
            anchors[offset + 2] = Math.round(
                (mountZ[vertex] + currentGryphonSaddleForward
                    - mouthForwardCorrection) * 128f / scale);
        }
        if (java.util.Arrays.equals(anchors, lastGryphonReinAnchors))
        {
            return;
        }
        Model model = buildGryphonSaddleModel(anchors);
        if (model != null)
        {
            saddle.setModel(model);
            lastGryphonReinAnchors = anchors;
        }
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
        if (config.mountType() == MountType.GRYPHON && config.showGryphonSaddle())
        {
            return config.gryphonSaddleScale();
        }
        if (config.mountType() != MountType.BLACK_UNICORN || !config.showSaddlePrototype())
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

    private Model buildUnscaledGryphonModel()
    {
        NPCComposition composition = client.getNpcDefinition(GRYPHON_NPC_ID);
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
        if (config.mountType() != MountType.GRYPHON)
        {
            return loopingAnimation(animationId);
        }

        NPCComposition composition = client.getNpcDefinition(GRYPHON_NPC_ID);
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

    private int currentStrideFollow()
    {
        if (config.mountType() == MountType.GRYPHON)
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
        if (config.mountType() == MountType.GRYPHON)
        {
            return 0;
        }
        int amount;
        if (isWidePose())
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
        return config.mountScale();
    }

    private int currentRiderHeight()
    {
        if (isWidePose())
        {
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
        return config.riderHeight();
    }

    private int currentRiderForward()
    {
        if (isWidePose())
        {
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
        return config.riderForward();
    }

    private int currentRiderSideways()
    {
        if (isWidePose())
        {
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
        return config.riderSideways();
    }

    private int currentWalkHeightAdjustment()
    {
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
        return config.useRidingPose() && config.ridingPose() == RidingPose.WIDE;
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
        deactivate(rider);
        deactivate(unicorn);
        deactivate(saddle);
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
        deactivate(rider);
        deactivate(unicorn);
        deactivate(saddle);
        for (RuneLiteObject part : mountParts)
        {
            deactivate(part);
        }
        mountParts.clear();
        rider = null;
        unicorn = null;
        saddle = null;
        lastGryphonReinAnchors = null;
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
