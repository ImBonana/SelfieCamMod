package me.imbanana.selfiecam.gui;

import me.imbanana.selfiecam.ModShaders;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FilterSelectionWidget extends AbstractContainerWidget {
    private final List<FilterEntry> entries = new ArrayList<>();

    public FilterSelectionWidget(int i, int j, int k, int l) {
        super(i, j, k, l, Component.literal("Filter Selection"));

        this.addEntry(
                new FilterEntry(
                        Component.translatable("filter.selfiecam.normal"),
                        null
                )
        );

        for (Identifier shader : ModShaders.SHADERS) {
            this.addEntry(
                    new FilterEntry(
                            Component.translatable("filter.selfiecam." + shader.getPath()),
                            shader
                    )
            );
        }
    }

    public void show() {
        this.visible = true;
        this.active = true;
    }

    public void hide() {
        this.visible = false;
        this.active = false;
    }

    private void addEntry(FilterEntry filterEntry) {
        this.entries.add(filterEntry);
        updateEntriesPosition();
    }

    @Override
    protected int contentHeight() {
        return this.entries.stream().
                mapToInt(FilterEntry::getHeight)
                .sum();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    protected double scrollRate() {
        return 10.0;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderScrollbar(guiGraphics, i, j);

        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());

        entries.forEach(entry -> {
            if (entry.getY() + entry.getHeight() >= this.getY() && entry.getY() <= this.getBottom()) {
                entry.render(guiGraphics, i, j, f);
            }
        });

        guiGraphics.disableScissor();
    }

    private int getContentWidth() {
        return this.getWidth() - 6;
    }

    @Override
    public void setScrollAmount(double d) {
        super.setScrollAmount(d);
        this.updateEntriesPosition();
    }

    private void updateEntriesPosition() {
        int i = this.getY() - (int) this.scrollAmount();

        for (FilterEntry entry : this.entries) {
            entry.setY(i);
            i += entry.getHeight();
            entry.setX(this.getX());
            entry.setWidth(this.getContentWidth());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // IDK
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return this.entries;
    }

    class FilterEntry extends Button {
        private final Identifier shader;

        protected FilterEntry(Component message, Identifier shader) {
            super(0, 0, FilterSelectionWidget.this.getContentWidth(), 20, message, button -> {}, Supplier::get);

            this.shader = shader;
        }

        @Override
        protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
//                    SPRITES.get(this.active, this.isHoveredOrFocused()),
                    SelfiecamClient.idOf("widget/filter/" + (shader == null ? "normal" : shader.getPath())),
                    this.getX(),
                    this.getY(),
                    this.getWidth(),
                    this.getHeight(),
                    ARGB.white(this.alpha)
            );

            if (this.isHoveredOrFocused()) {
                guiGraphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        SelfiecamClient.idOf("widget/filter_button_highlight"),
                        this.getX(),
                        this.getY(),
                        this.getWidth(),
                        this.getHeight(),
                        ARGB.white(this.alpha)
                );
            }

            int k = ARGB.color(this.alpha, this.active ? -1 : -6250336);
            guiGraphics.drawCenteredString(minecraft.font, this.message, this.getX() + this.width / 2, this.getY() + this.height / 2 - minecraft.font.lineHeight / 2, k);
        }

        @Override
        public void onPress(InputWithModifiers inputWithModifiers) {
            if (shader == null) {
                Minecraft.getInstance().gameRenderer.clearPostEffect();
                return;
            }

            Minecraft.getInstance().gameRenderer.setPostEffect(shader);
        }
    }
}
