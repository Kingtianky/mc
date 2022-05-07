package pers.roinflam.carianstyle.enchantment.recollect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.roinflam.carianstyle.enchantment.EnchantmentDarkMoon;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.utils.helper.task.SynchronizationTask;
import pers.roinflam.carianstyle.utils.util.EnchantmentUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentFullMoon extends Enchantment {
    private static final Set<UUID> FULL_MOON = new HashSet<>();
    private static final Set<UUID> FULL_MOON_COOLDING = new HashSet<>();

    public EnchantmentFullMoon(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
        EnchantmentUtil.registerEnchantment(this, "full_moon");
        CarianStyleEnchantments.ENCHANTMENTS.add(this);
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.FULL_MOON;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDeath(LivingDeathEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            if (!evt.getSource().canHarmInCreative()) {
                EntityLivingBase hurter = evt.getEntityLiving();
                int bonusLevel = 0;
                for (ItemStack itemStack : hurter.getArmorInventoryList()) {
                    if (itemStack != null) {
                        bonusLevel += EnchantmentHelper.getEnchantmentLevel(getEnchantment(), itemStack);
                    }
                }
                if (bonusLevel > 0) {
                    if (!FULL_MOON_COOLDING.contains(hurter.getUniqueID())) {
                        if (!hurter.isDead) {
                            evt.setCanceled(true);
                            hurter.setHealth((float) (hurter.getMaxHealth() * 0.0075));

                            FULL_MOON_COOLDING.add(hurter.getUniqueID());
                            FULL_MOON.add(hurter.getUniqueID());

                            if (hurter.getHeldItemMainhand() != null) {
                                if (EnchantmentHelper.getEnchantmentLevel(EnchantmentDarkMoon.getEnchantment(), hurter.getHeldItemMainhand()) > 0) {
                                    new SynchronizationTask(1, 1) {
                                        private int tick = 1;

                                        @Override
                                        public void run() {
                                            if (++tick > 400 || !hurter.isEntityAlive()) {
                                                this.cancel();
                                                FULL_MOON.remove(hurter.getUniqueID());
                                                return;
                                            }
                                            hurter.heal(hurter.getMaxHealth() / 200);
                                        }

                                    }.start();
                                    return;
                                }
                            }
                            new SynchronizationTask(1, 1) {
                                private int tick = 1;

                                @Override
                                public void run() {
                                    if (++tick > 200 || !hurter.isEntityAlive()) {
                                        this.cancel();
                                        FULL_MOON.remove(hurter.getUniqueID());
                                        return;
                                    }
                                    hurter.heal(hurter.getMaxHealth() / 200);
                                }

                            }.start();
                        }
                    }
                    new SynchronizationTask(3600 / (!hurter.world.isDaytime() ? 2 : 1)) {

                        @Override
                        public void run() {
                            FULL_MOON_COOLDING.remove(hurter.getUniqueID());
                        }

                    }.start();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            if (!evt.getSource().canHarmInCreative()) {
                EntityLivingBase hurter = evt.getEntityLiving();
                int bonusLevel = 0;
                for (ItemStack itemStack : hurter.getArmorInventoryList()) {
                    if (itemStack != null) {
                        bonusLevel += EnchantmentHelper.getEnchantmentLevel(getEnchantment(), itemStack);
                    }
                }
                if (bonusLevel > 0) {
                    if (FULL_MOON.contains(hurter.getUniqueID())) {
                        evt.setAmount((float) (evt.getAmount() * 0.5));
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHeal(LivingHealEvent evt) {
        if (!evt.getEntity().world.isRemote && !evt.getEntity().world.isDaytime()) {
            EntityLivingBase healer = evt.getEntityLiving();
            int bonusLevel = 0;
            for (ItemStack itemStack : healer.getArmorInventoryList()) {
                if (itemStack != null) {
                    bonusLevel += EnchantmentHelper.getEnchantmentLevel(getEnchantment(), itemStack);
                }
            }
            if (bonusLevel > 0) {
                evt.setAmount((float) (evt.getAmount() + evt.getAmount() * 0.25));
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
        return CarianStyleEnchantments.RECOLLECT_ENCHANTABILITY;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) * 2;
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        return !CarianStyleEnchantments.RECOLLECT.contains(ench) &&
                !ench.equals(CarianStyleEnchantments.HEALING_BY_FIRE) &&
                !ench.equals(CarianStyleEnchantments.SHELTER_OF_FIRE) &&
                !ench.equals(CarianStyleEnchantments.PRECISE_LIGHTNING) &&
                !ench.equals(CarianStyleEnchantments.ANCIENT_DRAGON_LIGHTNING);
    }
}