package schauweg.unsuspiciousstew.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class MixinGui {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int toolHighlightTimer;

    @Shadow
    private ItemStack lastToolHighlight;

    @Shadow public abstract Font getFont();

    @Inject(at = @At("HEAD"), method = "renderSelectedItemName")
    public void onInjectTooltip(GuiGraphics graphics, CallbackInfo info) {
        if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
            MutableComponent mutableText = Component.empty().append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().color());
            if (this.lastToolHighlight.has(DataComponents.CUSTOM_NAME)) {
                mutableText.withStyle(ChatFormatting.ITALIC);
            }

            int mainItemNameWidth = this.getFont().width(mutableText);
            int textWidth = (graphics.guiWidth() - mainItemNameWidth) / 2;
            int hotbarOffset = graphics.guiHeight() - 59;
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                hotbarOffset += 14;
            }

            int opacity = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
            if (opacity > 255) {
                opacity = 255;
            }

            if (opacity > 0) {
                graphics.pose().pushMatrix();
                graphics.fill(textWidth - 2, hotbarOffset - 2, textWidth + mainItemNameWidth + 2, hotbarOffset + 9 + 2, this.minecraft.options.getBackgroundColor(0));
                if (lastToolHighlight.getItem() == Items.SUSPICIOUS_STEW) {
                    SuspiciousStewEffects stewEffects = lastToolHighlight.getOrDefault(
                        DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);
                        for (int i = 0; i < stewEffects.effects().size(); i++) {
                            SuspiciousStewEffects.Entry entry = stewEffects.effects().get(i);
                            MobEffectInstance effectInstance = entry.createEffectInstance();
                            Component time = MobEffectUtil.formatDuration(effectInstance, 1, 20);
                            Component completeText = Component.translatable(effectInstance.getDescriptionId()).append(" ").append(time);
                            textWidth = (graphics.guiWidth() - getFont().width(completeText)) / 2;
                            graphics.drawString(getFont(), completeText, textWidth, hotbarOffset - (i * 14) -14, 13421772 + (opacity << 24));
                        }
                }
                graphics.pose().popMatrix();
            }
        }
    }
}
