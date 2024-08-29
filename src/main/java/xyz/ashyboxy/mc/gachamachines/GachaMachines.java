package xyz.ashyboxy.mc.gachamachines;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GachaMachines implements ModInitializer {
	public static final String MOD_ID = "gachamachines";
    public static final Logger LOGGER = LoggerFactory.getLogger("Gacha Machines");

	public static final Block GACHA_MACHINE = Registry.register(Registries.BLOCK, id("gacha_machine"),
			new GachaMachineBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).nonOpaque()));
	public static final BlockItem GACHA_MACHINE_ITEM = Registry.register(Registries.ITEM, id("gacha_machine"),new BlockItem(GACHA_MACHINE, new Item.Settings()));
	public static final BlockEntityType<GachaMachineBlockEntity> GACHA_MACHINE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("gacha_machine"), BlockEntityType.Builder.create(GachaMachineBlockEntity::new, GACHA_MACHINE).build(null));
	public static final ScreenHandlerType<GachaMachineScreenHandler> GACHA_MACHINE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, id("gacha_machine"), new ScreenHandlerType<>(GachaMachineScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

	@Override
	public void onInitialize() {
		LOGGER.info("It's about to be Genshin Impact over here");
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
