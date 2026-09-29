package com.rapidursa.mounts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.MouseListener;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@Singleton
final class DragonLandingOverlay extends Overlay implements MouseListener
{
    private final ClientThread clientThread;
    private final RapidUrsaMountsConfig config;
    private RapidUrsaMountsPlugin plugin;

    @Inject
    DragonLandingOverlay(ClientThread clientThread, RapidUrsaMountsConfig config)
    {
        this.clientThread = clientThread;
        this.config = config;
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.TOP_LEFT);
    }

    void bind(RapidUrsaMountsPlugin plugin) { this.plugin = plugin; }
    void unbind() { plugin = null; }

    private boolean isAvailable()
    {
        return plugin != null && plugin.isMounted() && config.enabled()
            && config.mountType() == MountType.FLYING_DRAGON;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!isAvailable())
        {
            return null;
        }
        int size = Math.max(24, Math.min(96, config.buttonSize()));
        int radius = Math.max(7, size / 5);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(18, 20, 24, 210));
        graphics.fillRoundRect(0, 0, size, size, radius, radius);
        graphics.setColor(plugin.isDragonLanded() ? new Color(231, 178, 83) : new Color(115, 120, 128));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 22f)));
        graphics.drawRoundRect(1, 1, size - 3, size - 3, radius, radius);
        Font previous = graphics.getFont();
        graphics.setFont(previous.deriveFont(Font.BOLD, Math.max(9f, size / 5f)));
        String label = plugin.isDragonLanded() ? "Fly" : "Land";
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.drawString(label, (size - metrics.stringWidth(label)) / 2,
            (size - metrics.getHeight()) / 2 + metrics.getAscent());
        graphics.setFont(previous);
        return new Dimension(size, size);
    }

    @Override
    public MouseEvent mousePressed(MouseEvent event)
    {
        if (isAvailable() && SwingUtilities.isLeftMouseButton(event)
            && getBounds().contains(event.getPoint()))
        {
            event.consume();
            if (!plugin.isDragonTransitioning())
            {
                clientThread.invokeLater(plugin::toggleDragonLanding);
            }
        }
        return event;
    }

    @Override public MouseEvent mouseClicked(MouseEvent event) { return event; }
    @Override public MouseEvent mouseReleased(MouseEvent event) { return event; }
    @Override public MouseEvent mouseEntered(MouseEvent event) { return event; }
    @Override public MouseEvent mouseExited(MouseEvent event) { return event; }
    @Override public MouseEvent mouseDragged(MouseEvent event) { return event; }
    @Override public MouseEvent mouseMoved(MouseEvent event) { return event; }
}
