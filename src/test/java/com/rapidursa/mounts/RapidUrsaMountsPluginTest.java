package com.rapidursa.mounts;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RapidUrsaMountsPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(RapidUrsaMountsPlugin.class);
        RuneLite.main(args);
    }
}
