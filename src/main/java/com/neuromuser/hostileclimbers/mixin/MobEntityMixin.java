package com.neuromuser.hostileclimbers.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Unique
    private int hc_ceilingCooldown = 0;
    @Unique
    private int hc_explodingCooldown = 0;

    @Inject(method = "createNavigation", at = @At("HEAD"))
    private void hc$useSpiderNavigation(World world, CallbackInfoReturnable<EntityNavigation> cir) {
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void hc$tick(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;
        if (self.getWorld().isClient) return;
        if (!(self instanceof HostileEntity)) return;
        if (self instanceof CreeperEntity creeper) {
            if (creeper.getFuseSpeed() > 0) {
                hc_explodingCooldown = 40;
            }
            if (hc_explodingCooldown > 0) {
                hc_explodingCooldown--;
                setClimbFlag(self, false);
                return;
            }
        }

        if (hc_ceilingCooldown > 0) hc_ceilingCooldown--;

        World world = self.getWorld();
        BlockPos pos = self.getBlockPos();

        boolean ceilingBlocked = world.getBlockState(pos.up(2)).isSolid();

        if (ceilingBlocked) {
            hc_ceilingCooldown = 60;
        }

        boolean shouldClimb = self.horizontalCollision && !ceilingBlocked && hc_ceilingCooldown <= 0;
        setClimbFlag(self, shouldClimb);

        if (self.age % 10 != 0) return;
        if (!self.isOnGround() || self.horizontalCollision) return;
        if (hc_ceilingCooldown > 0) return;

        LivingEntity target = self.getTarget();
        if (target == null) return;
        if (target.getY() - self.getY() < 2.0) return;
        if (!self.getNavigation().isIdle()) return;

        double dx = target.getX() - self.getX();
        double dz = target.getZ() - self.getZ();
        net.minecraft.util.math.Direction bestDir = null;
        double bestDot = Double.NEGATIVE_INFINITY;
        for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
            double dot = dx * dir.getOffsetX() + dz * dir.getOffsetZ();
            if (dot > bestDot) { bestDot = dot; bestDir = dir; }
        }
        if (bestDir == null) return;

        for (int i = 1; i <= 12; i++) {
            BlockPos check = pos.offset(bestDir, i);
            if (world.getBlockState(check).isSolid() || world.getBlockState(check.up()).isSolid()) {
                BlockPos standPos = check.offset(bestDir.getOpposite());
                if (world.getBlockState(standPos.down()).isSolid()
                        && world.getBlockState(standPos).isAir()
                        && world.getBlockState(standPos.up()).isAir()) {
                    self.getNavigation().startMovingTo(
                            standPos.getX() + 0.5, standPos.getY(), standPos.getZ() + 0.5, 1.0);
                }
                break;
            }
        }
    }

    @Unique
    private static void setClimbFlag(MobEntity mob, boolean climbing) {
        try {
            var flagsData = LivingEntityFlagsAccessor.getLivingFlags();
            byte flags = mob.getDataTracker().get(flagsData);
            byte next = climbing ? (byte)(flags | 8) : (byte)(flags & ~8);
            if (next != flags) mob.getDataTracker().set(flagsData, next);
        } catch (Exception ignored) {}
    }
}