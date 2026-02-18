package com.neuromuser.hostileclimbers.mixin;

import com.neuromuser.hostileclimbers.HostileClimbers;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @Inject(method = "createNavigation", at = @At("HEAD"), cancellable = true)
    private void hc$createNavigation(World world, CallbackInfoReturnable<EntityNavigation> cir) {
        MobEntity self = (MobEntity) (Object) this;
        if (!(self instanceof HostileEntity)) return;
        if (HostileClimbers.CONFIG == null || !HostileClimbers.CONFIG.isClimbingAllowed(self)) return;
        cir.setReturnValue(new SpiderNavigation(self, world));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void hc$tick(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;
        if (self.getWorld().isClient) return;
        if (!(self instanceof HostileEntity)) return;
        if (HostileClimbers.CONFIG == null || !HostileClimbers.CONFIG.isClimbingAllowed(self)) {
            hc$setClimbing(self, false);
            return;
        }
        hc$setClimbing(self, self.horizontalCollision);
    }

    @Unique
    private static void hc$setClimbing(MobEntity mob, boolean climbing) {
        try {
            var key = LivingEntityFlagsAccessor.getLivingFlags();
            byte flags = mob.getDataTracker().get(key);
            byte next = climbing ? (byte) (flags | 8) : (byte) (flags & ~8);
            if (next != flags) mob.getDataTracker().set(key, next);
        } catch (Exception ignored) {}
    }
}