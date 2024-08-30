package xyz.ashyboxy.mc.gachamachines;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public class GachaMachineScreenHandler extends ScreenHandler {
    public static final int ROLL_BUTTON = 0;

    @Nullable private GachaMachineBlockEntity blockEntity = null;
    private final Inventory inventory;
    private int currencyNeeded;

    public GachaMachineScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(5));
        currencyNeeded = buf.readInt();
    }

    public GachaMachineScreenHandler(int syncId, PlayerInventory playerInventory, GachaMachineBlockEntity blockEntity) {
        this(syncId, playerInventory, (Inventory) blockEntity);
        this.blockEntity = blockEntity;
        currencyNeeded = blockEntity.getCurrencyNeeded();
    }

    public GachaMachineScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(GachaMachines.GACHA_MACHINE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 5);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;

        // TODO: enforce stack validity in currency and capsule slots
        // currency slot
        addSlot(new Slot(inventory, 0, 23, 35));

        // capsule slots
        for (m = 0; m < 4; ++m) {
            addSlot(new Slot(inventory, m + 1, 78 + m * 21, 35));
        }


        // player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        // hotbar
        for (m = 0; m < 9; ++m) {
            addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    public int getCurrencyNeeded() {
        return currencyNeeded;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == ROLL_BUTTON) if (blockEntity != null) blockEntity.createOutputInSelf();
        return false;
    }

    // TODO: implement this
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
}
