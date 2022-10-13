package pers.roinflam.carianstyle.enchantment.combatskill;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.roinflam.carianstyle.base.enchantment.rarity.UncommonBase;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.init.CarianStylePotion;
import pers.roinflam.carianstyle.utils.helper.task.SynchronizationTask;
import pers.roinflam.carianstyle.utils.util.EntityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantmentGravitas extends UncommonBase {
    public static final UUID ID = UUID.fromString("c853487c-4583-5e5d-dfd6-ecb56161b37b");
    public static final String NAME = "enchantment.gravitas";
    private static final HashMap<UUID, Integer> ACTIVE = new HashMap<>();

    public EnchantmentGravitas(EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(typeIn, slots, "gravitas");

        new SynchronizationTask(1, 1) {

            @Override
            public void run() {
                for (UUID uuid : new ArrayList<>(ACTIVE.keySet())) {
                    if (ACTIVE.get(uuid) > 1) {
                        ACTIVE.put(uuid, ACTIVE.get(uuid) - 1);
                    } else {
                        ACTIVE.remove(uuid);
                    }
                }
            }

        }.start();
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.GRAVITAS;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            if (evt.getSource().getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase hurter = evt.getEntityLiving();
                EntityLivingBase attacker = (EntityLivingBase) evt.getSource().getTrueSource();
                if (!attacker.getHeldItem(attacker.getActiveHand()).isEmpty()) {
                    int bonusLevel = EnchantmentHelper.getEnchantmentLevel(getEnchantment(), attacker.getHeldItem(attacker.getActiveHand()));
                    if (bonusLevel > 0) {
                        ACTIVE.put(attacker.getUniqueID(), bonusLevel * 4 * 20);
                        hurter.addPotionEffect(new PotionEffect(CarianStylePotion.GRAVITAS, bonusLevel * 2 * 20, 10 + bonusLevel * 4 - 1));
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            ACTIVE.remove(evt.getEntityLiving().getUniqueID());
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            EntityLivingBase entityLivingBase = evt.getEntityLiving();
            if (entityLivingBase.isEntityAlive()) {
                if (ACTIVE.containsKey(entityLivingBase.getUniqueID())) {
                    List<EntityLivingBase> entities = EntityUtil.getNearbyEntities(EntityLivingBase.class, entityLivingBase, 12, e -> !e.equals(entityLivingBase));
                    for (EntityLivingBase e : entities) {
                        e.addPotionEffect(new PotionEffect(CarianStylePotion.GRAVITAS, 2, 9));
                    }
                }
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 35;
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        return !CarianStyleEnchantments.COMBAT_SKILL.contains(ench) && !ench.equals(Enchantments.KNOCKBACK);
    }
}
