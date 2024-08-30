package xyz.ashyboxy.mc.gachamachines;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GachaMachineScreen extends HandledScreen<GachaMachineScreenHandler> {
    private static final Identifier TEXTURE = GachaMachines.id("textures/gui/gacha_machine.png");

    public GachaMachineScreen(GachaMachineScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private ButtonWidget rollButton;

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
//        int x = (width - backgroundWidth) / 2;
//        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        rollButton.active = getScreenHandler().getInventory().getStack(GachaMachineBlockEntity.CURRENCY_SLOT).getCount() >= getScreenHandler().getCurrencyNeeded();
        rollButton.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        rollButton = new ButtonWidget(x + 50, y + 34, 178, 0, 20, 18, GachaMachineScreenHandler.ROLL_BUTTON);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (rollButton.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    class ButtonWidget extends PressableWidget {
        private final int id;
        private final int texX;
        private final int texY;

        public ButtonWidget(int x, int y, int texX, int texY, int width, int height, int id) {
            super(x, y, width, height, Text.literal("Roll"));
            this.id = id;
            this.texX = texX;
            this.texY = texY;
        }

        @Override
        public void onPress() {
            GachaMachineScreen.this.client.interactionManager.clickButton(GachaMachineScreen.this.handler.syncId, this.id);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            appendDefaultNarrations(builder);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int eTexY = texY;
            if (this.hovered) eTexY = texY + (this.height + 4) * 2;
            if (!this.active) eTexY = texY + this.height + 4;
            context.drawTexture(GachaMachineScreen.TEXTURE, getX(), getY(), texX, eTexY, getWidth(), getHeight());
        }


    }
}
