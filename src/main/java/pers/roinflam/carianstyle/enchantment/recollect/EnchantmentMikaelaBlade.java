package pers.roinflam.carianstyle.enchantment.recollect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.roinflam.carianstyle.base.enchantment.rarity.VeryRaryBase;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.utils.helper.task.SynchronizationTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentMikaelaBlade extends VeryRaryBase {
    private static final HashMap<UUID, Integer> COMMB = new HashMap<>();

    public EnchantmentMikaelaBlade(EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(typeIn, slots, "mikaela_blade");

        new SynchronizationTask(40, 40) {

            @Override
            public void run() {
                for (UUID uuid : new ArrayList<>(COMMB.keySet())) {
                    if (COMMB.get(uuid) > 1) {
                        COMMB.put(uuid, COMMB.get(uuid) - 1);
                    } else {
                        COMMB.remove(uuid);
                    }
                }
            }

        }.start();
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.MIKAELA_BLADE;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            if (evt.getSource().getImmediateSource() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) evt.getSource().getImmediateSource();
                if (!attacker.getHeldItem(attacker.getActiveHand()).isEmpty()) {
                    int bonusLevel = EnchantmentHelper.getEnchantmentLevel(getEnchantment(), attacker.getHeldItem(attacker.getActiveHand()));
                    if (bonusLevel > 0) {
                        evt.setAmount((float) (evt.getAmount() * 0.4 + evt.getAmount() * COMMB.getOrDefault(attacker.getUniqueID(), 0) * 0.2));
                        COMMB.put(attacker.getUniqueID(), COMMB.getOrDefault(attacker.getUniqueID(), 0) + 1);
                    }
                }
            }
            EntityLivingBase hurter = evt.getEntityLiving();
            evt.setAmount((float) (evt.getAmount() + evt.getAmount() * COMMB.getOrDefault(hurter.getUniqueID(), 0) * 0.1));
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
