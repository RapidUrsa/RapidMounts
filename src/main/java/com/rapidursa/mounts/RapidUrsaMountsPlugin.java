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
    private final List<RuneLiteObject> mountParts = new ArrayList<>();
    private RuneLiteObject rider;
    private int builtScale = -1;
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

        unicorn.setLocation(playerPoint, plane);
        unicorn.setZ(terrainZ);
        unicorn.setOrientation(orientation);
        for (RuneLiteObject part : mountParts)
        {
            part.setLocation(playerPoint, plane);
            part.setZ(terrainZ);
            part.setOrientation(orientation);
        }

        boolean moving = player.getPoseAnimation() != player.getIdlePoseAnimation();
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

        boolean riderReady = false;
        if (config.showRider())
        {
            Model playerModel = buildRiderModel(player);
            if (playerModel != null)
            {
                int riderForward = currentRiderForward();
                int riderHeight = currentRiderHeight();
                if (config.mountType() != MountType.BLACK_UNICORN && moving)
                {
                    riderForward += currentWalkForwardAdjustment();
                    riderForward += currentSeatSway();
                    riderHeight += currentWalkHeightAdjustment();
                    riderHeight += currentStrideFollow();
                    riderHeight += currentSeatBounce();
                }
                else if (!moving)
                {
                    riderHeight += currentIdleBounce();
                }
                LocalPoint riderPoint = offsetFromPlayer(
                    playerPoint,
                    orientation,
                    riderForward,
                    currentRiderSideways() + (moving ? currentLateralSway() : 0));
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
            && builtScale == currentMountScale())
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
        activeUnicornAnimation = -1;
        activeRiderAnimation = -1;
        return true;
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
            return 45;
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
            return config.mountType() == MountType.LAVA_DRAGON ? 6 : 0;
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

    private static LocalPoint offsetFromPlayer(
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
        for (RuneLiteObject part : mountParts)
        {
            deactivate(part);
        }
        mountParts.clear();
        rider = null;
        unicorn = null;
        builtRiderOutfit = null;
        builtRiderModel = null;
        builtScale = -1;
        builtMountType = null;
        activeUnicornAnimation = -1;
        activeRiderAnimation = -1;
        activeRiderFrame = -1;
        actionResumeTicks = 0;
        mountedRenderReady = false;
    }
}
