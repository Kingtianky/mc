package pers.roinflam.carianstyle.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.utils.java.random.RandomUtil;
import pers.roinflam.carianstyle.utils.util.EnchantmentUtil;
import pers.roinflam.carianstyle.utils.util.EntityUtil;

import java.util.List;

@Mod.EventBusSubscriber
public class EnchantmentCalamity extends Enchantment {

    public EnchantmentCalamity(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
        EnchantmentUtil.registerEnchantment(this, "calamity");
        CarianStyleEnchantments.ENCHANTMENTS.add(this);
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.CALAMITY;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            EntityLivingBase hurter = evt.getEntityLiving();
            int bonusLevel = 0;
            for (ItemStack itemStack : hurter.getArmorInventoryList()) {
                if (itemStack != null) {
                    bonusLevel += EnchantmentHelper.getEnchantmentLevel(getEnchantment(), itemStack);
                }
            }
            if (bonusLevel > 0) {
                evt.setAmount(evt.getAmount() * 2);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent evt) {
        if (!evt.player.world.isRemote) {
            if (evt.phase.equals(TickEvent.Phase.END)) {
                if (RandomUtil.percentageChance(2)) {
                    EntityPlayer entityPlayer = evt.player;
                    if (entityPlayer.isEntityAlive()) {
                        int bonusLevel = 0;
                        if (entityPlayer.getHeldItemMainhand() != null) {
                            bonusLevel += EnchantmentHelper.getEnchantmentLevel(getEnchantment(), entityPlayer.getHeldItemMainhand());
                        }
                        for (ItemStack itemStack : entityPlayer.getArmorInventoryList()) {
                            if (itemStack != null) {
                                bonusLevel += EnchantmentHelper.getEnchantmentLevel(getEnchantment(), itemStack);
                            }
                        }
                        if (bonusLevel > 0) {
                            List<Entity> entities = EntityUtil.getNearbyEntities(
                                    entityPlayer,
                                    32,
                                    32,
                                    entity -> entity instanceof EntityMob
                            );
                            for (Entity entity : entities) {
                                EntityMob entityMob = (EntityMob) entity;
                                EntityLivingBase attackTarget = entityMob.getAttackTarget();
                                if (attackTarget == null || !entityMob.getAttackTarget().isEntityAlive()) {
                                    if (RandomUtil.percentageChance(25)) {
                                        entityMob.setAttackTarget(entityPlayer);
                                    }
                                } else {
                                    if (!attackTarget.equals(entityPlayer)) {
                                        if (RandomUtil.percentageChance(50)) {
                                            entityMob.setAttackTarget(entityPlayer);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 35;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) * 2;
    }

    @Override
    public boolean isCurse() {
        return true;
    }
}
