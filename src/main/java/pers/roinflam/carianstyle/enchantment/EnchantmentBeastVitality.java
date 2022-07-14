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
import pers.roinflam.carianstyle.base.enchantment.rarity.UncommonBase;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.utils.util.EnchantmentUtil;

@Mod.EventBusSubscriber
public class EnchantmentBeastVitality extends UncommonBase {

    public EnchantmentBeastVitality(EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(typeIn, slots, "beast_vitality");
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.BEAST_VITALITY;
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
                    evt.getPotionEffect().combine(new PotionEffect(potionEffect.getPotion(), (int) (potionEffect.getDuration() + potionEffect.getDuration() * bonusLevel * 0.3), potionEffect.getAmplifier()));
                }
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 5 + (enchantmentLevel - 1) * 25;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }
}
