package com.rapidursa.mounts;

import net.runelite.api.Animation;
import net.runelite.api.AnimationController;
import net.runelite.api.Client;

/** Traverse every idle frame in both directions without restarting the sequence. */
final class FullIdlePingPongController extends AnimationController
{
    private final Animation animation;
    private final int lastFrame;
    private int direction = 1;
    private int elapsedTicks;

    FullIdlePingPongController(Client client, Animation animation)
    {
        super(client, animation);
        this.animation = animation;
        lastFrame = Math.max(0, (animation.isMayaAnim()
            ? animation.getDuration() : animation.getNumFrames()) - 1);
        setFrame(0);
    }

    @Override
    public void tick(int ticks)
    {
        elapsedTicks += Math.max(0, ticks);
        int[] lengths = animation.isMayaAnim() ? null : animation.getFrameLengths();
        int frame = getFrame();
        int duration = lengths == null || frame >= lengths.length
            ? 1 : Math.max(1, lengths[frame]);
        while (elapsedTicks >= duration && lastFrame > 0)
        {
            elapsedTicks -= duration;
            if (frame >= lastFrame)
            {
                direction = -1;
            }
            else if (frame <= 0)
            {
                direction = 1;
            }
            frame += direction;
            setFrame(frame);
            duration = lengths == null || frame >= lengths.length
                ? 1 : Math.max(1, lengths[frame]);
        }
    }
}
