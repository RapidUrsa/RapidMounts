package com.rapidursa.mounts;

import net.runelite.api.Animation;
import net.runelite.api.AnimationController;
import net.runelite.api.Client;

final class FrozenAnimationController extends AnimationController
{
    FrozenAnimationController(Client client, Animation animation, int frame)
    {
        super(client, animation);
        setFrame(frame);
    }

    @Override
    public void tick(int ticks)
    {
        // The Wide riding pose deliberately holds one animation frame.
    }
}
