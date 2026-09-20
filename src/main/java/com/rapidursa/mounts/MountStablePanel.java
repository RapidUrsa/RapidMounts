package com.rapidursa.mounts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

@Singleton
final class MountStablePanel extends PluginPanel
{
    private static final Color ACTIVE_BORDER = new Color(231, 178, 83);
    private static final Color INACTIVE_BORDER = new Color(70, 74, 80);

    private final RapidUrsaMountsConfig config;
    private final ConfigManager configManager;
    private final ClientThread clientThread;
    private final Map<MountType, JButton> mountButtons = new EnumMap<>(MountType.class);
    private final JComboBox<RidingPose> poseSelector = new JComboBox<>(RidingPose.values());
    private final JButton saddleButton = new JButton();
    private final JButton mountedButton = new JButton();
    private final BufferedImage sidebarIcon;
    private RapidUrsaMountsPlugin plugin;
    private boolean refreshing;

    @Inject
    MountStablePanel(
        RapidUrsaMountsConfig config,
        ConfigManager configManager,
        ClientThread clientThread)
    {
        super(false);
        this.config = config;
        this.configManager = configManager;
        this.clientThread = clientThread;

        BufferedImage unicorn = loadPreview("black-unicorn-preview.png");
        BufferedImage terrorbird = loadPreview("terrorbird-preview.png");
        BufferedImage lavaDragon = loadPreview("lava-dragon-preview.png");
        BufferedImage gryphon = loadPreview("gryphon-preview.png");
        BufferedImage battleTurtle = loadPreview("battle-turtle-preview.png");
        BufferedImage artio = loadPreview("artio-preview.png");
        sidebarIcon = createHorseshoeSidebarIcon();

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JLabel title = new JLabel("Mount Stable");
        title.setFont(FontManager.getRunescapeBoldFont().deriveFont(Font.PLAIN, 22f));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        content.add(title);

        JLabel subtitle = new JLabel("Choose your cosmetic mount");
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel cards = new JPanel(new GridLayout(0, 1, 0, 8));
        cards.setBackground(ColorScheme.DARK_GRAY_COLOR);
        cards.setAlignmentX(LEFT_ALIGNMENT);
        cards.add(createMountButton(MountType.TERRORBIRD, terrorbird));
        cards.add(createMountButton(MountType.BLACK_UNICORN, unicorn));
        cards.add(createMountButton(MountType.GRYPHON, gryphon));
        cards.add(createMountButton(MountType.ARTIO, artio));
        cards.add(createMountButton(MountType.LAVA_DRAGON, lavaDragon));
        cards.add(createMountButton(MountType.BATTLE_TURTLE, battleTurtle));
        content.add(cards);
        content.add(Box.createRigidArea(new Dimension(0, 14)));

        JLabel poseLabel = new JLabel("Riding pose");
        poseLabel.setForeground(Color.WHITE);
        poseLabel.setFont(FontManager.getRunescapeBoldFont());
        poseLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(poseLabel);
        content.add(Box.createRigidArea(new Dimension(0, 5)));

        poseSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        poseSelector.setAlignmentX(LEFT_ALIGNMENT);
        poseSelector.addActionListener(event ->
        {
            if (!refreshing)
            {
                RidingPose pose = (RidingPose) poseSelector.getSelectedItem();
                if (pose != null)
                {
                    clientThread.invokeLater(() -> configManager.setConfiguration(
                        RapidUrsaMountsConfig.GROUP, "ridingPose", pose));
                }
            }
        });
        content.add(poseSelector);
        content.add(Box.createRigidArea(new Dimension(0, 8)));

        saddleButton.setFont(FontManager.getRunescapeBoldFont());
        saddleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        saddleButton.setAlignmentX(LEFT_ALIGNMENT);
        saddleButton.setFocusPainted(false);
        saddleButton.addActionListener(event ->
        {
            MountType selected = config.mountType();
            if (selected == MountType.BLACK_UNICORN)
            {
                clientThread.invokeLater(() -> configManager.setConfiguration(
                    RapidUrsaMountsConfig.GROUP, "showSaddleAndReins",
                    !config.showSaddleAndReins()));
            }
            else if (selected == MountType.GRYPHON)
            {
                clientThread.invokeLater(() -> configManager.setConfiguration(
                    RapidUrsaMountsConfig.GROUP, "showGryphonSaddle",
                    !config.showGryphonSaddle()));
            }
            else if (selected == MountType.BATTLE_TURTLE)
            {
                clientThread.invokeLater(() -> configManager.setConfiguration(
                    RapidUrsaMountsConfig.GROUP, "showBattleTurtleSaddle",
                    !config.showBattleTurtleSaddle()));
            }
            else if (selected == MountType.ARTIO)
            {
                clientThread.invokeLater(() -> configManager.setConfiguration(
                    RapidUrsaMountsConfig.GROUP, "showArtioArmour",
                    !config.showArtioArmour()));
            }
        });
        content.add(saddleButton);
        content.add(Box.createRigidArea(new Dimension(0, 14)));

        mountedButton.setFont(FontManager.getRunescapeBoldFont());
        mountedButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        mountedButton.setAlignmentX(LEFT_ALIGNMENT);
        mountedButton.addActionListener(event ->
        {
            RapidUrsaMountsPlugin current = plugin;
            if (current != null)
            {
                clientThread.invokeLater(current::toggleMounted);
                SwingUtilities.invokeLater(this::refresh);
            }
        });
        content.add(mountedButton);
        content.add(Box.createVerticalGlue());
        add(content, BorderLayout.NORTH);
        refresh();
    }

    private JButton createMountButton(MountType type, BufferedImage image)
    {
        JButton button = new JButton(type.toString(), new ImageIcon(resize(image, 72, 43)));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(0, 64));
        button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(FontManager.getRunescapeBoldFont());
        button.addActionListener(event -> clientThread.invokeLater(() ->
            configManager.setConfiguration(RapidUrsaMountsConfig.GROUP, "mountType", type)));
        mountButtons.put(type, button);
        return button;
    }

    void bind(RapidUrsaMountsPlugin plugin)
    {
        this.plugin = plugin;
        refresh();
    }

    void unbind()
    {
        plugin = null;
    }

    BufferedImage getSidebarIcon()
    {
        return sidebarIcon;
    }

    void refresh()
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(this::refresh);
            return;
        }

        refreshing = true;
        MountType selected = config.mountType();
        for (Map.Entry<MountType, JButton> entry : mountButtons.entrySet())
        {
            boolean active = entry.getKey() == selected;
            entry.getValue().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(active ? ACTIVE_BORDER : INACTIVE_BORDER, active ? 2 : 1),
                BorderFactory.createEmptyBorder(active ? 5 : 6, 8, active ? 5 : 6, 8)));
        }
        poseSelector.setSelectedItem(config.ridingPose());
        boolean supportsSaddle = selected == MountType.BLACK_UNICORN
            || selected == MountType.GRYPHON
            || selected == MountType.BATTLE_TURTLE
            || selected == MountType.ARTIO;
        boolean saddleEnabled = selected == MountType.BLACK_UNICORN
            ? config.showSaddleAndReins()
            : selected == MountType.GRYPHON
                ? config.showGryphonSaddle()
                : selected == MountType.BATTLE_TURTLE
                    ? config.showBattleTurtleSaddle()
                    : selected == MountType.ARTIO
                        && config.showArtioArmour();
        saddleButton.setEnabled(supportsSaddle);
        String saddleLabel = selected == MountType.BATTLE_TURTLE
            ? "Battle saddle & cannon"
            : selected == MountType.ARTIO
                ? "Fremennik saddle & weapons"
                : "Saddle & reins";
        saddleButton.setText(supportsSaddle
            ? saddleLabel + ": " + (saddleEnabled ? "On" : "Off")
            : "Saddle & reins: Unavailable");
        saddleButton.setBackground(saddleEnabled
            ? new Color(104, 73, 42)
            : ColorScheme.DARKER_GRAY_COLOR);
        saddleButton.setForeground(supportsSaddle ? Color.WHITE : Color.GRAY);
        boolean mounted = plugin != null && plugin.isMounted();
        mountedButton.setText(mounted ? "Dismount" : "Mount");
        mountedButton.setBackground(mounted ? new Color(104, 73, 42) : new Color(48, 93, 58));
        refreshing = false;
        revalidate();
        repaint();
    }

    private static BufferedImage resize(BufferedImage source, int width, int height)
    {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        if (source != null)
        {
            java.awt.Graphics2D graphics = result.createGraphics();
            graphics.setRenderingHint(
                java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(source.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
            graphics.dispose();
        }
        return result;
    }

    private static BufferedImage loadPreview(String fileName)
    {
        try (java.io.InputStream input = MountStablePanel.class.getResourceAsStream(fileName))
        {
            if (input != null)
            {
                return javax.imageio.ImageIO.read(input);
            }
        }
        catch (java.io.IOException ignored)
        {
            // Fall through to an empty image so the panel remains usable.
        }
        return new BufferedImage(72, 43, BufferedImage.TYPE_INT_ARGB);
    }

    private static BufferedImage createHorseshoeSidebarIcon()
    {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(
            java.awt.RenderingHints.KEY_ANTIALIASING,
            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        java.awt.geom.Path2D.Double horseshoe = new java.awt.geom.Path2D.Double();
        horseshoe.moveTo(3.5, 13.0);
        horseshoe.lineTo(3.5, 7.5);
        horseshoe.curveTo(3.5, 1.0, 12.5, 1.0, 12.5, 7.5);
        horseshoe.lineTo(12.5, 13.0);

        graphics.setStroke(new java.awt.BasicStroke(
            5.0f,
            java.awt.BasicStroke.CAP_ROUND,
            java.awt.BasicStroke.JOIN_ROUND));
        graphics.setColor(new Color(82, 94, 105));
        graphics.draw(horseshoe);

        graphics.setStroke(new java.awt.BasicStroke(
            3.0f,
            java.awt.BasicStroke.CAP_ROUND,
            java.awt.BasicStroke.JOIN_ROUND));
        graphics.setColor(new Color(184, 195, 204));
        graphics.draw(horseshoe);
        graphics.dispose();
        return image;
    }

}
