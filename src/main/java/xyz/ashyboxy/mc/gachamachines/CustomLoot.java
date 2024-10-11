package xyz.ashyboxy.mc.gachamachines;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomLoot {
    private List<Pair<ItemStack,Integer>> customConfig = new ArrayList<>();
    private Random random = new Random();

    public CustomLoot(List<Pair<ItemStack, Integer>> customConfig) {
        this.customConfig = customConfig;
    }

    public ItemStack getRandomItem() {
        int totalWeight = 0;
        for (Pair<ItemStack, Integer> pair : customConfig) {
            totalWeight += pair.getRight();
        }
        int randomValue = random.nextInt(totalWeight);
        for (Pair<ItemStack, Integer> pair : customConfig) {
            randomValue -= pair.getRight();
            if (randomValue < 0) {
                return pair.getLeft();
            }
        }
        return null;
    }

    public static CustomLoot fromNbt(NbtCompound nbtCompound) {
        var list = nbtCompound.getList("entries", NbtElement.COMPOUND_TYPE);
        List<Pair<ItemStack,Integer>> entries = new ArrayList<>();
        list.forEach(
            (t) -> {
                NbtCompound entry = (NbtCompound) t;
                entries.add(new Pair<>(ItemStack.fromNbt(entry.getCompound("stack")),entry.getInt("weight")));
            }
        );
        return new CustomLoot(entries);
    }

    public NbtCompound toNbt() {
        var nbt = new NbtCompound();
        var list = new NbtList();
        for (var e : customConfig) {
            var compound = new NbtCompound();
            var item = new NbtCompound();
            e.getLeft().writeNbt(item);
            compound.put("stack",item);
            compound.putInt("weight",e.getRight());
            list.add(compound);
        }
        nbt.put("entries",list);
        return nbt;
    }
}
