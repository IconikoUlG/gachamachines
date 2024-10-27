package xyz.ashyboxy.mc.gachamachines;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ashyboxy.mc.gachamachines.mixin.LootContextTypesAccessor;

import java.util.function.Consumer;

public class GachaMachines implements ModInitializer {
	public static final String MOD_ID = "gachamachines";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Block GACHA_MACHINE = Registry.register(Registries.BLOCK, id("gacha_machine"),
			new GachaMachineBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER).mapColor(MapColor.DIAMOND_BLUE).strength(4f).sounds(BlockSoundGroup.METAL).nonOpaque()));
	public static final BlockEntityType<RealGachaMachineBlockEntity> GACHA_MACHINE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("gacha_machine"), BlockEntityType.Builder.create(RealGachaMachineBlockEntity::new, GACHA_MACHINE).build(null));
	public static final BlockEntityType<DummyGachaMachineBlockEntity> DUMMY_GACHA_MACHINE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("gacha_machine_dummy"), BlockEntityType.Builder.create(DummyGachaMachineBlockEntity::new, GACHA_MACHINE).build(null));
	public static final ScreenHandlerType<GachaMachineScreenHandler> GACHA_MACHINE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, id("gacha_machine"), new ExtendedScreenHandlerType<>(GachaMachineScreenHandler::new));

	public static final RegistryKey<ItemGroup> GACHA_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), id("item_group"));
	public static final ItemGroup GACHA_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, GACHA_ITEM_GROUP_KEY,
			FabricItemGroup.builder().icon(() -> new ItemStack(GachaItems.GREEN_CAPSULE)).displayName(Text.translatable("itemGroup.gacha_machines")).entries((c, e) -> {
				e.add(GachaItems.GACHA_MACHINE);
				e.add(GachaItems.RED_CAPSULE);
				e.add(GachaItems.GREEN_CAPSULE);
				e.add(GachaItems.YELLOW_CAPSULE);
				e.add(GachaItems.IRON_CAPSULE);
				e.add(GachaItems.GOLD_CAPSULE);
				e.add(GachaItems.DIAMOND_CAPSULE);
				e.add(GachaItems.EMERALD_CAPSULE);
			}).build());

	public static final LootContextType GACHA_MACHINE_LOOT_CONTEXT = registerLootContext(id("gacha_machine"), b -> b.require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_ENTITY));
	public static final LootContextType CAPSULE_LOOT_CONTEXT = registerLootContext(id("capsule"), b -> b.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.BLOCK_ENTITY));

	@Override
	public void onInitialize() {
		LOGGER.info("Pls AshyBoxy, don't leave things half-done. -Ultro");
		GachaItems.init();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	// this is basically a copy of vanilla code and should absolutely be in a library
	protected static LootContextType registerLootContext(Identifier id, Consumer<LootContextType.Builder> type) {
		LootContextType.Builder builder = new LootContextType.Builder();
		type.accept(builder);
		LootContextType lootContextType = builder.build();
		LootContextType check = LootContextTypesAccessor.getMAP().put(id, lootContextType);
		if (check != null) throw new IllegalStateException("Loot table parameter set " + id + " is already registered");
		return lootContextType;
	}
}
