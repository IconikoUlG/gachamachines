package xyz.ashyboxy.mc.gachamachines;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GachaItems {
    public static final BlockItem GACHA_MACHINE = register("gacha_machine", new BlockItem(GachaMachines.GACHA_MACHINE, new Item.Settings()));

    public static final CapsuleItem RED_CAPSULE = register("red_capsule", new CapsuleItem(new Item.Settings()));
    public static final CapsuleItem GREEN_CAPSULE = register("green_capsule", new CapsuleItem(new Item.Settings()));
    public static final CapsuleItem YELLOW_CAPSULE = register("yellow_capsule", new CapsuleItem(new Item.Settings()));
    public static final CapsuleItem IRON_CAPSULE = register("iron_capsule", new CapsuleItem(new Item.Settings()));
    public static final CapsuleItem GOLD_CAPSULE = register("gold_capsule", new CapsuleItem(new Item.Settings()));
    public static final CapsuleItem DIAMOND_CAPSULE = register("diamond_capsule", new CapsuleItem(new Item.Settings()));
    public static final CapsuleItem EMERALD_CAPSULE = register("emerald_capsule", new CapsuleItem(new Item.Settings()));


    public static <T extends Item> T register(String id, T item) {
        return Registry.register(Registries.ITEM, GachaMachines.id(id), item);
    }

    public static void init() {}
}
