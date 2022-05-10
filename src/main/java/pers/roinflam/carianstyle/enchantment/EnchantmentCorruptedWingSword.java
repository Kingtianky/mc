package pers.roinflam.carianstyle.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.utils.helper.task.SynchronizationTask;
import pers.roinflam.carianstyle.utils.util.EnchantmentUtil;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentCorruptedWingSword extends Enchantment {
    private static final HashMap<UUID, Integer> COMMB = new HashMap<>();

    public EnchantmentCorruptedWingSword(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
        EnchantmentUtil.registerEnchantment(this, "corrupted_wing_sword");
        CarianStyleEnchantments.ENCHANTMENTS.add(this);
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.CORRUPTED_WING_SWORD;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            if (evt.getSource().getImmediateSource() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) evt.getSource().getImmediateSource();
                if (attacker.getHeldItemMainhand() != null) {
                    int bonusLevel = EnchantmentHelper.getEnchantmentLevel(getEnchantment(), attacker.getHeldItemMainhand());
                    if (bonusLevel > 0) {
                        if (COMMB.getOrDefault(attacker.getUniqueID(), 0) < 20) {
                            COMMB.put(attacker.getUniqueID(), COMMB.getOrDefault(attacker.getUniqueID(), 0) + 1);
                            new SynchronizationTask(300) {

                                @Override
                                public void run() {
                                    if (COMMB.get(attacker.getUniqueID()) > 1) {
                                        COMMB.put(attacker.getUniqueID(), COMMB.get(attacker.getUniqueID()) - 1);
                                    } else {
                                        COMMB.remove(attacker.getUniqueID());
                                    }
                                }

                            }.start();
                            evt.setAmount((float) (evt.getAmount() + evt.getAmount() * (COMMB.get(attacker.getUniqueID()) / 4) * 0.03 * bonusLevel));
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
        return 3;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 5;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) * 2;
    }

}
