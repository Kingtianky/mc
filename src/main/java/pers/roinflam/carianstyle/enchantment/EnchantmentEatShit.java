package pers.roinflam.carianstyle.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.roinflam.carianstyle.base.enchantment.rarity.UncommonBase;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.utils.helper.task.SynchronizationTask;
import pers.roinflam.carianstyle.utils.util.EnchantmentUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentEatShit extends UncommonBase {
    private final static Set<UUID> EAT_SHIT = new HashSet<>();

    public EnchantmentEatShit(EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(typeIn, slots, "eat_shit");
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.EAT_SHIT;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            if (evt.getSource().getImmediateSource() instanceof EntityLivingBase) {
                EntityLivingBase hurter = evt.getEntityLiving();
                EntityLivingBase attacker = (EntityLivingBase) evt.getSource().getImmediateSource();
                if (!attacker.getHeldItem(attacker.getActiveHand()).isEmpty()) {
                    int bonusLevel = EnchantmentHelper.getEnchantmentLevel(getEnchantment(), attacker.getHeldItem(attacker.getActiveHand()));
                    if (bonusLevel > 0) {
                        hurter.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, bonusLevel * 80));
                        attacker.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, bonusLevel * 30));
                        EAT_SHIT.add(hurter.getUniqueID());
                        new SynchronizationTask(bonusLevel * 80) {

                            @Override
                            public void run() {
                                EAT_SHIT.remove(hurter.getUniqueID());
                            }

                        }.start();
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHeal(LivingHealEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            if (EAT_SHIT.contains(evt.getEntity().getUniqueID())) {
                evt.setAmount(evt.getAmount() - evt.getAmount() * 0.75f);
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 35 + (enchantmentLevel - 1);
    }

}
