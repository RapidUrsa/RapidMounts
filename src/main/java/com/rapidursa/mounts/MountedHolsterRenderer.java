package com.rapidursa.mounts;

import com.rapidursa.mounts.appearance.ModelRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.ModelData;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;

/** Draws Rapid Holster's equipped item models at the same world transform as the mounted rider. */
final class MountedHolsterRenderer
{
    private final Client client;
    private final ModelRepository models;
    private final ItemManager items;
    private final MountedHolsterSettings placement;
    private RuneLiteObject weapon;
    private RuneLiteObject shield;
    private int weaponId = -1;
    private int shieldId = -1;
    private String placementSignature = "";

    MountedHolsterRenderer(Client client, ModelRepository models, ItemManager items,
        MountedHolsterSettings placement)
    {
        this.client = client;
        this.models = models;
        this.items = items;
        this.placement = placement;
    }

    void refresh(Player player, LocalPoint seat, int plane, int orientation, int seatZ,
        int mountedSideways, int mountedHeight, int mountedForward)
    {
        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
        if (equipment == null || player.getPlayerComposition() == null || !models.isLoaded())
        {
            clear();
            return;
        }
        Item[] worn = equipment.getItems();
        int wantedWeapon = worn.length > 3 && worn[3] != null ? worn[3].getId() : -1;
        int wantedShield = worn.length > 5 && worn[5] != null ? worn[5].getId() : -1;
        String overrides = placement.itemPlacements();
        if (overrides == null) overrides = "";
        // Placement changes take effect on the next frame; no client restart needed.
        String signature = mountedSideways + ":" + mountedHeight + ":" + mountedForward
            + ":" + overrides + ":" + placement.sideways() + ":" + placement.height()
            + ":" + placement.forward() + ":" + placement.pitch() + ":" + placement.yaw()
            + ":" + placement.roll() + ":" + placement.scale()
            + ":" + placement.shadowSideways() + ":" + placement.shadowHeight()
            + ":" + placement.shadowForward() + ":" + placement.shadowPitch()
            + ":" + placement.shadowYaw() + ":" + placement.shadowRoll()
            + ":" + placement.shadowScale() + ":" + placement.twistedBowSideways()
            + ":" + placement.twistedBowHeight() + ":" + placement.twistedBowForward()
            + ":" + placement.twistedBowPitch() + ":" + placement.twistedBowYaw()
            + ":" + placement.twistedBowRoll() + ":" + placement.twistedBowScale()
            + ":" + placement.oneHandSideways() + ":" + placement.oneHandHeight()
            + ":" + placement.oneHandForward() + ":" + placement.oneHandPitch()
            + ":" + placement.oneHandYaw() + ":" + placement.oneHandRoll()
            + ":" + placement.oneHandScale() + ":" + placement.wandSideways()
            + ":" + placement.wandHeight() + ":" + placement.wandForward()
            + ":" + placement.wandPitch() + ":" + placement.wandYaw()
            + ":" + placement.wandRoll() + ":" + placement.wandScale()
            + ":" + placement.shieldSideways() + ":" + placement.shieldHeight()
            + ":" + placement.shieldForward() + ":" + placement.shieldPitch()
            + ":" + placement.shieldYaw() + ":" + placement.shieldRoll()
            + ":" + placement.shieldScale()
            + ":" + placement.crossbowSideways() + ":" + placement.crossbowHeight()
            + ":" + placement.crossbowForward() + ":" + placement.crossbowPitch()
            + ":" + placement.crossbowYaw() + ":" + placement.crossbowRoll()
            + ":" + placement.crossbowScale()
            + ":" + placement.offHandHipSideways() + ":" + placement.offHandHipHeight()
            + ":" + placement.offHandHipForward() + ":" + placement.offHandHipPitch()
            + ":" + placement.offHandHipYaw() + ":" + placement.offHandHipRoll()
            + ":" + placement.offHandHipScale()
            + ":" + player.getPlayerComposition().getGender();
        if (wantedWeapon != weaponId || wantedShield != shieldId || !signature.equals(placementSignature))
        {
            clear();
            placementSignature = signature;
            weaponId = wantedWeapon;
            shieldId = wantedShield;
            if (group(wantedWeapon) != Group.NONE || override(wantedWeapon) != null)
            {
                weapon = build(wantedWeapon, false, player,
                    mountedSideways, mountedHeight, mountedForward);
            }
            if (wantedShield >= 0)
            {
                shield = build(wantedShield, true, player,
                    mountedSideways, mountedHeight, mountedForward);
            }
        }
        position(weapon, seat, plane, orientation, seatZ);
        position(shield, seat, plane, orientation, seatZ);
    }

    private RuneLiteObject build(int itemId, boolean isShield, Player player,
        int mountedSideways, int mountedHeight, int mountedForward)
    {
        ModelRepository.Entry entry = models.item(itemId);
        if (entry == null) return null;
        int[] ids = entry.models(player.getPlayerComposition().getGender());
        if (ids == null) return null;
        List<ModelData> parts = new ArrayList<>();
        for (int id : ids)
        {
            if (id < 0) continue;
            ModelData part = client.loadModelData(id);
            if (part == null) continue;
            if (entry.cf != null && entry.cr != null)
            {
                part = part.cloneColors();
                for (int i = 0; i < Math.min(entry.cf.length, entry.cr.length); i++)
                    part.recolor(entry.cf[i], entry.cr[i]);
            }
            if (entry.tf != null && entry.tr != null)
            {
                part = part.cloneTextures();
                for (int i = 0; i < Math.min(entry.tf.length, entry.tr.length); i++)
                    part.retexture(entry.tf[i], entry.tr[i]);
            }
            parts.add(part);
        }
        if (parts.isEmpty()) return null;
        ModelData data = parts.size() == 1 ? parts.get(0)
            : client.mergeModels(parts.toArray(new ModelData[0]));
        if (data == null) return null;
        data = data.cloneVertices();
        int[] mountedPlacement = chosenPlacement(itemId, isShield).clone();
        mountedPlacement[0] += mountedSideways;
        mountedPlacement[1] += mountedHeight;
        mountedPlacement[2] += mountedForward;
        transform(data, mountedPlacement);
        RuneLiteObject object = client.createRuneLiteObject();
        object.setModel(data.light(64, 768, -50, -10, -50));
        object.setRenderMode(Renderable.RENDERMODE_SORTED_NO_DEPTH);
        object.setDrawFrontTilesFirst(true);
        return object;
    }

    private enum Group { NONE, STAFF, BOW, CROSSBOW, TWO_HANDED, ONE_HANDED, WAND }

    private Group group(int id)
    {
        if (id < 0) return Group.NONE;
        if (id == ItemID.TUMEKENS_SHADOW || id == ItemID.TUMEKENS_SHADOW_UNCHARGED)
            return Group.STAFF;
        if (id == ItemID.TWISTED_BOW) return Group.BOW;
        if (id == ItemID.SOULREAPER_AXE_28338) return Group.TWO_HANDED;
        String name = items.getItemComposition(id).getName().toLowerCase(Locale.ROOT);
        if (name.contains("crossbow")) return Group.CROSSBOW;
        if (name.contains("wand")) return Group.WAND;
        if (name.contains("staff") || name.contains("trident") || name.contains("sceptre")
            || name.contains("scepter") || name.contains("eye of ayak")) return Group.STAFF;
        if (name.contains("bow")) return Group.BOW;
        ItemStats stats = items.getItemStats(id);
        if (stats != null && stats.getEquipment() != null && stats.getEquipment().isTwoHanded())
            return Group.TWO_HANDED;
        if (stats != null && stats.getEquipment() != null && stats.getEquipment().getSlot() == 3)
            return Group.ONE_HANDED;
        return Group.NONE;
    }

    private int[] chosenPlacement(int id, boolean isShield)
    {
        int[] custom = override(id);
        if (custom != null) return custom;
        if (isShield)
        {
            String name = items.getItemComposition(id).getName().toLowerCase(Locale.ROOT);
            if (name.contains("shield") || name.contains("ward")
                || name.contains("buckler") || name.contains("toktz-ket-xil"))
                return new int[]{placement.shieldSideways(), placement.shieldHeight(),
                    placement.shieldForward(), placement.shieldPitch(), placement.shieldYaw(),
                    placement.shieldRoll(), placement.shieldScale()};
            return new int[]{placement.offHandHipSideways(), placement.offHandHipHeight(),
                placement.offHandHipForward(), placement.offHandHipPitch(),
                placement.offHandHipYaw(), placement.offHandHipRoll(),
                placement.offHandHipScale()};
        }
        Group type = group(id);
        if (type == Group.CROSSBOW) return new int[]{placement.crossbowSideways(),
            placement.crossbowHeight(), placement.crossbowForward(),
            placement.crossbowPitch(), placement.crossbowYaw(),
            placement.crossbowRoll(), placement.crossbowScale()};
        if (type == Group.ONE_HANDED) return new int[]{placement.oneHandSideways(),
            placement.oneHandHeight(), placement.oneHandForward(), placement.oneHandPitch(),
            placement.oneHandYaw(), placement.oneHandRoll(), placement.oneHandScale()};
        if (type == Group.WAND) return new int[]{placement.wandSideways(), placement.wandHeight(),
            placement.wandForward(), placement.wandPitch(), placement.wandYaw(),
            placement.wandRoll(), placement.wandScale()};
        if (type == Group.STAFF) return new int[]{placement.shadowSideways(),
            placement.shadowHeight(), placement.shadowForward(), placement.shadowPitch(),
            placement.shadowYaw(), placement.shadowRoll(), placement.shadowScale()};
        if (type == Group.BOW) return new int[]{placement.twistedBowSideways(),
            placement.twistedBowHeight(), placement.twistedBowForward(), placement.twistedBowPitch(),
            placement.twistedBowYaw(), placement.twistedBowRoll(), placement.twistedBowScale()};
        return new int[]{placement.sideways(), placement.height(), placement.forward(),
            placement.pitch(), placement.yaw(), placement.roll(), placement.scale()};
    }

    private int[] override(int id)
    {
        if (id < 0 || placement.itemPlacements() == null) return null;
        for (String entry : placement.itemPlacements().split(";"))
        {
            String[] parts = entry.trim().split(":");
            if (parts.length != 2) continue;
            try
            {
                if (Integer.parseInt(parts[0].trim()) != id) continue;
                String[] values = parts[1].split(",");
                if (values.length != 7) continue;
                int[] result = new int[7];
                for (int i = 0; i < 7; i++) result[i] = Integer.parseInt(values[i].trim());
                boolean valid = result[6] >= 25 && result[6] <= 200;
                for (int i = 0; i < 6; i++)
                    if (result[i] < (i < 3 ? -200 : -180)
                        || result[i] > (i < 3 ? 200 : 180)) valid = false;
                if (valid) return result;
            }
            catch (NumberFormatException ignored) { /* Fall back to category settings. */ }
        }
        return null;
    }

    private static void transform(ModelData data, int[] placement)
    {
        float[] x = data.getVerticesX(), y = data.getVerticesY(), z = data.getVerticesZ();
        int count = data.getVerticesCount();
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (int i = 0; i < count; i++)
        {
            minX = Math.min(minX, x[i]); maxX = Math.max(maxX, x[i]);
            minY = Math.min(minY, y[i]); maxY = Math.max(maxY, y[i]);
            minZ = Math.min(minZ, z[i]); maxZ = Math.max(maxZ, z[i]);
        }
        float cx = (minX + maxX) * .5f, cy = (minY + maxY) * .5f;
        float cz = (minZ + maxZ) * .5f;
        double pitch = Math.toRadians(placement[3]), yaw = Math.toRadians(placement[4]);
        double roll = Math.toRadians(placement[5]);
        double cp = Math.cos(pitch), sp = Math.sin(pitch);
        double cyaw = Math.cos(yaw), syaw = Math.sin(yaw);
        double cr = Math.cos(roll), sr = Math.sin(roll), size = placement[6] / 100.0;
        for (int i = 0; i < count; i++)
        {
            double vx = (x[i] - cx) * size, vy = (y[i] - cy) * size;
            double vz = (z[i] - cz) * size;
            double py = vy * cp - vz * sp, pz = vy * sp + vz * cp;
            double yx = vx * cyaw + pz * syaw, yz = -vx * syaw + pz * cyaw;
            x[i] = (float) (yx * cr - py * sr) + placement[0];
            y[i] = (float) (yx * sr + py * cr) - placement[1];
            z[i] = (float) yz + placement[2];
        }
    }

    private static void position(RuneLiteObject object, LocalPoint seat, int plane,
        int orientation, int seatZ)
    {
        if (object == null) return;
        object.setLocation(seat, plane);
        object.setZ(seatZ);
        object.setOrientation(orientation);
        if (!object.isActive()) object.setActive(true);
    }

    void clear()
    {
        if (weapon != null) weapon.setActive(false);
        if (shield != null) shield.setActive(false);
        weapon = null;
        shield = null;
        weaponId = -1;
        shieldId = -1;
    }
}
