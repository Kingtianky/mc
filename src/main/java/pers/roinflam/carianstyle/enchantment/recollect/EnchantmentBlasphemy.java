package pers.roinflam.carianstyle.enchantment.recollect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.roinflam.carianstyle.base.enchantment.rarity.VeryRaryBase;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;

@Mod.EventBusSubscriber
public class EnchantmentBlasphemy extends VeryRaryBase {

    public EnchantmentBlasphemy(EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(typeIn, slots, "blasphemy");
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.BLASPHEMY;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            if (evt.getSource().getImmediateSource() instanceof EntityLivingBase) {
                EntityLivingBase killer = (EntityLivingBase) evt.getSource().getImmediateSource();
                EntityLivingBase deader = evt.getEntityLiving();
                if (killer.isEntityAlive() && !evt.getEntityLiving().equals(killer)) {
                    if (!killer.getHeldItem(killer.getActiveHand()).isEmpty()) {
                        int bonusLevel = EnchantmentHelper.getEnchantmentLevel(getEnchantment(), killer.getHeldItem(killer.getActiveHand()));
                        if (bonusLevel > 0) {
                            killer.heal((float) (deader.getMaxHealth() * 0.1));
                            if (killer instanceof EntityPlayer) {
                                EntityPlayer entityPlayer = (EntityPlayer) killer;
                                FoodStats foodStats = entityPlayer.getFoodStats();
                                foodStats.setFoodLevel(Math.min(foodStats.getFoodLevel() + 2, 20));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return CarianStyleEnchantments.RECOLLECT_ENCHANTABILITY;
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        return !CarianStyleEnchantments.RECOLLECT.contains(ench);
    }
}
