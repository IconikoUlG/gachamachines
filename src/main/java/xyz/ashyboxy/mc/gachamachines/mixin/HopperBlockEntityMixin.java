package xyz.ashyboxy.mc.gachamachines.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
    @ModifyExpressionValue(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;" + "Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;canMergeItems(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean gachamachines$canMergeItemsMaxCount(boolean original,
                                                               @Local(argsOnly = true, ordinal = 1) Inventory inventory,
                                                               @Local(ordinal = 1) ItemStack targetStack) {
        if (targetStack.getCount() >= inventory.getMaxCountPerStack()) return false;
        return original;
    }

    @ModifyExpressionValue(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;" + "Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private static int gachamachines$decrementAmountMaxCount(int original,
                                                             @Local(argsOnly = true, ordinal = 1) Inventory inventory,
                                                             @Local(argsOnly = true) ItemStack hopperStack, @Local(ordinal = 1) ItemStack targetStack) {
        return Math.min(inventory.getMaxCountPerStack() - targetStack.getCount(), original);
    }
}
