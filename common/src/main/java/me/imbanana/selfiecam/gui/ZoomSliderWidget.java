package me.imbanana.selfiecam.gui;

import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;

// Thank you NeoForge :)
/**
 * Slider widget implementation which allows inputting values in a certain range with optional step size.
 *
 * @implNote Note that {@link ZoomSliderWidget#value} is the fractional progress of the slider from 0 to 1,
 *           whereas {@link ZoomSliderWidget#getValue()} is the actual value from {@code minValue} to {@code maxValue}.
 */
public class ZoomSliderWidget extends AbstractSliderButton {
    protected Component prefix;
    protected Component suffix;

    protected double minValue;
    protected double maxValue;

    /** Allows input of discontinuous values with a certain step */
    protected double stepSize;

    protected boolean drawString;

    private final DecimalFormat format;

    /**
     * @param x            x position of upper left corner
     * @param y            y position of upper left corner
     * @param width        Width of the widget
     * @param height       Height of the widget
     * @param prefix       {@link Component} displayed before the value string
     * @param suffix       {@link Component} displayed after the value string
     * @param minValue     Minimum (left) value of slider
     * @param maxValue     Maximum (right) value of slider
     * @param currentValue Starting value when widget is first displayed
     * @param stepSize     Size of step used. Precision will automatically be calculated based on this value if this value is not 0.
     * @param precision    Only used when {@code stepSize} is 0. Limited to a maximum of 4 (inclusive).
     * @param drawString   Should text be displayed on the widget
     */
    public ZoomSliderWidget(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, Component.empty(), 0D);
        this.prefix = prefix;
        this.suffix = suffix;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = Math.abs(stepSize);
        this.value = this.snapToNearest((currentValue - minValue) / (maxValue - minValue));
        this.drawString = drawString;

        if (stepSize == 0D) {
            precision = Math.min(precision, 4);

            StringBuilder builder = new StringBuilder("0");

            if (precision > 0)
                builder.append('.');

            while (precision-- > 0)
                builder.append('0');

            this.format = new DecimalFormat(builder.toString());
        } else if (Mth.equal(this.stepSize, Math.floor(this.stepSize))) {
            this.format = new DecimalFormat("0");
        } else {
            this.format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
        }

        this.updateMessage();
    }

    /**
     * Overload with {@code stepSize} set to 1, useful for sliders with whole number values.
     */
    public ZoomSliderWidget(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1D, 0, drawString);
    }

    /**
     * @return Current slider value as a double
     */
    public double getValue() {
        return this.value * (maxValue - minValue) + minValue;
    }

    /**
     * @return Current slider value as an long
     */
    public long getValueLong() {
        return Math.round(this.getValue());
    }

    /**
     * @return Current slider value as an int
     */
    public int getValueInt() {
        return (int) this.getValueLong();
    }

    /**
     * @param value The new slider value
     */
    public void setValue(double value) {
        setFractionalValue((value - this.minValue) / (this.maxValue - this.minValue));
    }

    public String getValueString() {
        return this.format.format(this.getValue());
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.setValueFromMouse(event.x());
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        super.onDrag(event, dragX, dragY);
        this.setValueFromMouse(event.x());
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        boolean flag = keyEvent.isLeft();
        if (flag || keyEvent.isRight()) {
            if (this.minValue > this.maxValue)
                flag = !flag;
            float f = flag ? -1F : 1F;
            if (stepSize <= 0D)
                this.setFractionalValue(this.value + (f / (this.width - 8)));
            else
                this.setValue(this.getValue() + f * this.stepSize);
        }

        return false;
    }

    private void setValueFromMouse(double mouseX) {
        this.setFractionalValue((mouseX - (this.getX() + 4)) / (this.width - 8));
    }

    /**
     * @param fractionalValue fractional progress between 0 and 1
     */
    private void setFractionalValue(double fractionalValue) {
        double oldValue = this.value;
        this.value = this.snapToNearest(fractionalValue);
        if (!Mth.equal(oldValue, this.value))
            this.applyValue();

        this.updateMessage();
    }

    /**
     * Snaps the value, so that the displayed value is the nearest multiple of {@code stepSize}.
     * If {@code stepSize} is 0, no snapping occurs.
     *
     * @param value fractional progress between 0 and 1
     * @return fractional progress between 0 and 1, snapped to the nearest allowed value
     */
    private double snapToNearest(double value) {
        if (stepSize <= 0D)
            return Mth.clamp(value, 0D, 1D);

        value = Mth.lerp(Mth.clamp(value, 0D, 1D), this.minValue, this.maxValue);

        value = (stepSize * Math.round(value / stepSize));

        if (this.minValue > this.maxValue) {
            value = Mth.clamp(value, this.maxValue, this.minValue);
        } else {
            value = Mth.clamp(value, this.minValue, this.maxValue);
        }

        return Mth.map(value, this.minValue, this.maxValue, 0D, 1D);
    }

    @Override
    protected void updateMessage() {
        if (this.drawString) {
            this.setMessage(Component.literal("").append(prefix).append(this.getValueString()).append(suffix));
        } else {
            this.setMessage(Component.empty());
        }
    }

    @Override
    protected void applyValue() {}

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        guiGraphics.drawCenteredString(minecraft.font, Component.translatable("gui.selfiecam.camera.zoom"), this.getX() + this.getWidth() / 2, this.getY() - minecraft.font.lineHeight, -1);
        guiGraphics.drawString(minecraft.font, "+", this.getX() - minecraft.font.width("+") - 5, (int) (this.getY() + minecraft.font.lineHeight / 1.5), -1);
        guiGraphics.drawString(minecraft.font, "-", this.getX() + this.getWidth() + 5, (int) (this.getY() + minecraft.font.lineHeight / 1.5), -1);

        ResourceLocation sprite = this.isActive() && this.isFocused() && !this.canChangeValue ? SelfiecamClient.idOf("widget/slider") : SelfiecamClient.idOf("widget/slider_highlight");
        ResourceLocation handleSprite = !this.isActive() || !this.isHovered && !this.canChangeValue ? SelfiecamClient.idOf("widget/slider_handle") : SelfiecamClient.idOf("widget/slider_handle_highlight");

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY() + this.getHeight() / 4, this.getWidth(), this.getHeight() / 2, ARGB.white(this.alpha));
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, handleSprite, this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 8, this.getHeight(), ARGB.white(this.alpha));

        int i = this.active ? 16777215 : 10526880;
        this.renderScrollingString(guiGraphics, minecraft.font, 2, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}