package pers.roinflam.carianstyle.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.roinflam.carianstyle.base.enchantment.rarity.VeryRaryBase;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.utils.util.EnchantmentUtil;

@Mod.EventBusSubscriber
public class EnchantmentBeastRobust extends VeryRaryBase {

    public EnchantmentBeastRobust(EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(typeIn, slots, "beast_robust");
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.BEAST_ROBUST;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPotionAdded(PotionEvent.PotionAddedEvent evt) {
        if (!evt.getEntity().world.isRemote) {
            EntityLivingBase entityLivingBase = evt.getEntityLiving();
            int bonusLevel = 0;
            for (ItemStack itemStack : entityLivingBase.getArmorInventoryList()) {
                if (!itemStack.isEmpty()) {
                    bonusLevel += EnchantmentHelper.getEnchantmentLevel(getEnchantment(), itemStack);
                }
            }
            if (bonusLevel > 0) {
                PotionEffect potionEffect = evt.getPotionEffect();
                Potion potion = potionEffect.getPotion();
                if (!potion.isInstant() && potionEffect.getPotion().shouldRender(potionEffect)) {
                    evt.getPotionEffect().combine(new PotionEffect(potionEffect.getPotion(), (int) (potionEffect.getDuration() * 0.4), potionEffect.getAmplifier() * 2 + 1));
                }
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 35;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }
}
