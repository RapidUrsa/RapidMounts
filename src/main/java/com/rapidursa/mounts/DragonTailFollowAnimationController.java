package com.rapidursa.mounts;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import net.runelite.api.AnimationController;
import net.runelite.api.Client;
import net.runelite.api.Model;

/**
 * Lifts the flying red dragon's animated tail into the line of the torso while
 * the player is moving. Animation 7870 bends the tail sharply downward even
 * though the rest of the dragon travels level. The correction is feathered
 * out from the tail root so the join stays smooth and the animation can still
 * flex the tail naturally.
 */
final class DragonTailFollowAnimationController extends AnimationController
{
    private static final float TAIL_ROOT_Z = 68f;
    private static final float FULL_LIFT_Z = 145f;
    private static final float MAX_TAIL_HALF_WIDTH = 58f;
    private static final float ROOT_BAND = 22f;

    private final BooleanSupplier moving;
    private final IntSupplier liftDegrees;

    DragonTailFollowAnimationController(Client client, int animationId,
        BooleanSupplier moving, IntSupplier liftDegrees)
    {
        super(client, animationId);
        this.moving = moving;
        this.liftDegrees = liftDegrees;
    }

    @Override
    public Model animate(Model model, AnimationController other)
    {
        Model animated = super.animate(model, other);
        int degrees = Math.max(0, Math.min(50, liftDegrees.getAsInt()));
        if (animated == null || !moving.getAsBoolean() || degrees == 0)
        {
            return animated;
        }

        int count = Math.min(model.getVerticesCount(), animated.getVerticesCount());
        if (count == 0)
        {
            return animated;
        }

        float[] baseX = model.getVerticesX();
        float[] baseY = model.getVerticesY();
        float[] baseZ = model.getVerticesZ();
        float[] y = animated.getVerticesY();
        float[] z = animated.getVerticesZ();

        float baseRootY = 0f;
        float animatedRootY = 0f;
        float animatedRootZ = 0f;
        int rootCount = 0;
        for (int vertex = 0; vertex < count; vertex++)
        {
            if (Math.abs(baseX[vertex]) <= 34f
                && Math.abs(baseZ[vertex] - TAIL_ROOT_Z) <= ROOT_BAND)
            {
                baseRootY += baseY[vertex];
                animatedRootY += y[vertex];
                animatedRootZ += z[vertex];
                rootCount++;
            }
        }
        if (rootCount == 0)
        {
            return animated;
        }

        baseRootY /= rootCount;
        animatedRootY /= rootCount;
        animatedRootZ /= rootCount;

        for (int vertex = 0; vertex < count; vertex++)
        {
            if (baseZ[vertex] <= TAIL_ROOT_Z
                || Math.abs(baseX[vertex]) > MAX_TAIL_HALF_WIDTH
                || Math.abs(baseY[vertex] - baseRootY) > 52f)
            {
                continue;
            }

            double weight = Math.min(1.0,
                (baseZ[vertex] - TAIL_ROOT_Z) / (FULL_LIFT_Z - TAIL_ROOT_Z));
            // Smoothstep keeps the torso-to-tail join free of a visible kink.
            weight = weight * weight * (3.0 - 2.0 * weight);
            double radians = Math.toRadians(degrees * weight);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            double dy = y[vertex] - animatedRootY;
            double dz = z[vertex] - animatedRootZ;
            y[vertex] = (float) (animatedRootY + dy * cos - dz * sin);
            z[vertex] = (float) (animatedRootZ + dy * sin + dz * cos);
        }
        return animated;
    }
}
