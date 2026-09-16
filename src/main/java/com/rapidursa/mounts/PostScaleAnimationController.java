package com.rapidursa.mounts;

import net.runelite.api.AnimationController;
import net.runelite.api.Client;
import net.runelite.api.Model;

/**
 * Applies a skeletal animation before NPC-definition scaling. Newer Maya-style
 * animations may contain absolute bone translations and deform if their base
 * vertices are scaled first.
 */
final class PostScaleAnimationController extends AnimationController
{
    private final int scaleX;
    private final int scaleY;
    private final int scaleZ;

    PostScaleAnimationController(Client client, int animationId, int scaleX, int scaleY, int scaleZ)
    {
        super(client, animationId);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    @Override
    public Model animate(Model model, AnimationController other)
    {
        Model animated = super.animate(model, other);
        return animated == null ? null : animated.scale(scaleX, scaleY, scaleZ);
    }
}
