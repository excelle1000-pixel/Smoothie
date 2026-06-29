// Directory
package ww.smoothiemod.client.mixins;

// Imports

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3d;
import ww.smoothiemod.client.Smoothie;

// Spongie
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InterpolationHandler.class)
public abstract class PositionInterpolatorMixin {

    @Shadow @Final
    private Entity entity;

    @Shadow
    public abstract void setLerpDuration(int lerpDuration);

    @Unique private long smoothlerp$lastUpdateNanos = 0L;
    @Unique private double smoothlerp$avgIntervalMs = 100.0;
    @Unique private boolean smoothlerp$hasSample = false;

    @Inject(method = "interpolateTo", at = @At("HEAD"))
    private void smoothlerp$onRefresh(Vector3d position, float yaw, float pitch /*, CallbackInfo ci */) {
        if (!Smoothie.ENABLED) return;
        if (!(this.entity instanceof Player)) return;

        long now = System.nanoTime();
        if (smoothlerp$lastUpdateNanos != 0L) {
            double intervalMs = (now - smoothlerp$lastUpdateNanos) / 1_000_000.0;
            if (intervalMs > 0.0 && intervalMs < 1000.0) {
                if (!smoothlerp$hasSample) {
                    smoothlerp$avgIntervalMs = intervalMs;
                    smoothlerp$hasSample = true;
                } else {
                    smoothlerp$avgIntervalMs =
                            smoothlerp$avgIntervalMs * 0.80 + intervalMs * 0.20;
                }
            }
        }
        smoothlerp$lastUpdateNanos = now;

        double ticks = smoothlerp$avgIntervalMs / 50.0;
        int target = (int) Math.ceil(ticks) + Smoothie.JITTER_BUFFER_TICKS;
        int steps = Math.clamp(
                Math.max(target, InterpolationHandler.DEFAULT_INTERPOLATION_STEPS),
                1,
                Smoothie.MAX_STEPS
        );
        setLerpDuration(steps);
    }
}
