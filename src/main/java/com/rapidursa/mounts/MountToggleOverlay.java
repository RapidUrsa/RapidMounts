package com.rapidursa.mounts;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import net.runelite.api.ItemID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@Singleton
final class MountToggleOverlay extends Overlay implements MouseListener
{
    private RapidUrsaMountsPlugin plugin;
    private final ClientThread clientThread;
    private final RapidUrsaMountsConfig config;
    private final BufferedImage unicornIcon;
    private final BufferedImage terrorbirdIcon;
    private final BufferedImage lavaDragonIcon;

    @Inject
    MountToggleOverlay(
        ClientThread clientThread,
        RapidUrsaMountsConfig config,
        ItemManager itemManager)
    {
        this.clientThread = clientThread;
        this.config = config;
        this.unicornIcon = itemManager.getImage(ItemID.BLACK_TOY_HORSEY);
        this.terrorbirdIcon = itemManager.getImage(ItemID.STRIPY_FEATHER);
        this.lavaDragonIcon = itemManager.getImage(ItemID.LAVA_DRAGON_BONES);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.TOP_LEFT);
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
        int size = Math.max(24, Math.min(96, config.buttonSize()));
        int padding = Math.max(2, size / 12);
        boolean mounted = plugin != null && plugin.isMounted();
        BufferedImage icon = unicornIcon;
        if (config.mountType() == MountType.TERRORBIRD)
        {
            icon = terrorbirdIcon;
        }
        else if (config.mountType() == MountType.LAVA_DRAGON)
        {
            icon = lavaDragonIcon;
        }

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(18, 20, 24, 210));
        graphics.fillRoundRect(0, 0, size, size, Math.max(7, size / 5), Math.max(7, size / 5));
        graphics.setColor(mounted ? new Color(231, 178, 83) : new Color(115, 120, 128));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 22f)));
        graphics.drawRoundRect(1, 1, size - 3, size - 3, Math.max(7, size / 5), Math.max(7, size / 5));

        if (icon != null)
        {
            Composite previous = graphics.getComposite();
            if (!mounted)
            {
                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.48f));
            }
            graphics.drawImage(icon, padding, padding, size - 2 * padding, size - 2 * padding, null);
            graphics.setComposite(previous);
        }
        return new Dimension(size, size);
    }

    @Override
    public MouseEvent mousePressed(MouseEvent event)
    {
        if (SwingUtilities.isLeftMouseButton(event) && getBounds().contains(event.getPoint()))
        {
            event.consume();
            RapidUrsaMountsPlugin current = plugin;
            if (current != null)
            {
                clientThread.invokeLater(current::toggleMounted);
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
