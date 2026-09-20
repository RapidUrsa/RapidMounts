package com.rapidursa.mounts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
final class ArtioAnchorOverlay extends Overlay
{
    private static final int LABEL_COUNT = 60;

    private final Client client;
    private final RapidUrsaMountsConfig config;
    private RapidUrsaMountsPlugin plugin;

    @Inject
    ArtioAnchorOverlay(Client client, RapidUrsaMountsConfig config)
    {
        this.client = client;
        this.config = config;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
    }

    void bind(RapidUrsaMountsPlugin plugin)
    {
        this.plugin = plugin;
    }

    void unbind()
    {
        plugin = null;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showArtioAnchorProbe())
        {
            return null;
        }

        RuneLiteObject artio = plugin == null ? null : plugin.getArtioObjectForProbe();
        Model model = artio == null ? null : artio.getModel();
        LocalPoint origin = artio == null ? null : artio.getLocation();
        if (model == null || origin == null)
        {
            return null;
        }

        float[] xs = model.getVerticesX();
        float[] ys = model.getVerticesY();
        float[] zs = model.getVerticesZ();
        int start = Math.min(config.artioAnchorProbeStart(), model.getVerticesCount());
        int spacing = Math.max(1, config.artioAnchorProbeSpacing());
        int orientation = artio.getOrientation();
        int plane = artio.getLevel();
        Font previousFont = graphics.getFont();
        graphics.setFont(previousFont.deriveFont(Font.BOLD, 11f));

        int shown = 0;
        for (int vertex = start; vertex < model.getVerticesCount() && shown < LABEL_COUNT; vertex += spacing)
        {
            LocalPoint point = RapidUrsaMountsPlugin.offsetFromPlayer(
                origin, orientation, -Math.round(zs[vertex]), Math.round(xs[vertex]));
            Point canvas = Perspective.localToCanvas(client, point, plane, -Math.round(ys[vertex]));
            if (canvas != null)
            {
                Color color = (shown & 1) == 0 ? Color.CYAN : Color.YELLOW;
                graphics.setColor(color);
                graphics.fillOval(canvas.getX() - 2, canvas.getY() - 2, 5, 5);
                OverlayUtil.renderTextLocation(graphics,
                    new Point(canvas.getX() + 4, canvas.getY() - 3),
                    Integer.toString(vertex), color);
            }
            shown++;
        }
        graphics.setFont(previousFont);
        return null;
    }
}
