package com.rapidursa.mounts;

import net.runelite.api.Animation;
import net.runelite.api.AnimationController;
import net.runelite.api.Client;

/** Loops only the settled tail of a one-shot emote animation. */
final class TailLoopAnimationController extends AnimationController
{
    private final int startFrame;
    private final int endFrame;

    TailLoopAnimationController(Client client, Animation animation, int requestedStart, int requestedEnd)
    {
        super(client, animation);
        int frameCount = animation.isMayaAnim()
            ? animation.getDuration()
            : animation.getNumFrames();
        int lastFrame = Math.max(0, frameCount - 1);
        startFrame = Math.max(0, Math.min(requestedStart, lastFrame));
        endFrame = Math.max(startFrame + 1, Math.min(requestedEnd, lastFrame));
        setFrame(startFrame);
        // A one-shot sequence can finish inside super.tick before our frame
        // range check runs. Keep it active when that happens as well.
        setOnFinished(controller -> {
            controller.loop();
            controller.setFrame(startFrame);
        });
    }

    @Override
    public void tick(int ticks)
    {
        if (getFrame() < startFrame || getFrame() >= endFrame)
        {
            setFrame(startFrame);
        }
        super.tick(ticks);
        int frame = getFrame();
        if (frame < startFrame || frame >= endFrame)
        {
            setFrame(startFrame);
        }
    }
}
