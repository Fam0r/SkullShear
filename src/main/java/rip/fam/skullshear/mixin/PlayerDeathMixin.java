package rip.fam.skullshear.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin {
    @Inject(method = "Lnet/minecraft/server/network/ServerPlayerEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("HEAD"))
    private void onDeath(final DamageSource source, CallbackInfo ci) {
        if (!(source instanceof EntityDamageSource)) return;
        Entity attacker = source.getAttacker();
        if (!(attacker instanceof PlayerEntity)) return;
        
        ServerPlayerEntity p = (ServerPlayerEntity)(Object)this;
        PlayerEntity killer = (PlayerEntity)attacker;
        Item killweapon = killer.getMainHandStack().getItem();
        
        if (!(killweapon instanceof ShearsItem)) return; // TODO: probably should be customizable/checked by id instead of class

        // Create Head
        // TODO: Too lazy to figure out how to create heads properly, will be a steve head until placed
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound tag = new NbtCompound();
        NbtCompound skullowner = new NbtCompound();
        skullowner.putUuid("Id", p.getUuid());
        skullowner.putString("Name", p.getEntityName());
        tag.put("SkullOwner", skullowner);
        head.setNbt(tag);

        p.dropStack(head);
    }
}