package pers.roinflam.carianstyle.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.RandomUtils;
import pers.roinflam.carianstyle.base.enchantment.rarity.RaryBase;
import pers.roinflam.carianstyle.init.CarianStyleEnchantments;
import pers.roinflam.carianstyle.utils.helper.task.SynchronizationTask;
import pers.roinflam.carianstyle.utils.util.EntityUtil;

import java.util.List;

@Mod.EventBusSubscriber
public class EnchantmentCallStar extends RaryBase {

    public EnchantmentCallStar(EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(typeIn, slots, "call_star");
    }

    public static Enchantment getEnchantment() {
        return CarianStyleEnchantments.CALL_STAR;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectileImpact_Arrow(ProjectileImpactEvent.Arrow evt) {
        if (!evt.getEntity().world.isRemote) {
            if (evt.getArrow().shootingEntity != null && evt.getRayTraceResult().entityHit == null) {
                EntityArrow entityArrow = evt.getArrow();
                EntityLivingBase attacker = (EntityLivingBase) evt.getArrow().shootingEntity;
                if (!attacker.getHeldItem(attacker.getActiveHand()).isEmpty()) {
                    int bonusLevel = EnchantmentHelper.getEnchantmentLevel(getEnchantment(), attacker.getHeldItem(attacker.getActiveHand()));
                    if (bonusLevel > 0) {
                        List<EntityLivingBase> entities = entityArrow.world.getEntitiesWithinAABB(
                                EntityLivingBase.class,
                                new AxisAlignedBB(entityArrow.getPosition()).expand(bonusLevel * 2, bonusLevel * 2, bonusLevel * 2),
                                entityLivingBase -> !entityLivingBase.equals(attacker)
                        );
                        for (EntityLivingBase entityLivingBase : entities) {
                            double x = entityLivingBase.posX - entityArrow.posX;
                            double z = entityLivingBase.posZ - entityArrow.posZ;
                            float stronge = (float) (bonusLevel * 0.35 * Math.max(Math.abs(x), Math.abs(z)) / 7);
                            entityLivingBase.knockBack(attacker, stronge, x, z);
                        }
                        new SynchronizationTask(20) {

                            @Override
                            public void run() {
                                List<EntityLivingBase> entities = entityArrow.world.getEntitiesWithinAABB(
                                        EntityLivingBase.class,
                                        new AxisAlignedBB(entityArrow.getPosition()).expand(bonusLevel, bonusLevel, bonusLevel),
                                        entityLivingBase -> !entityLivingBase.equals(attacker)
                                );
                                if (!entities.isEmpty()) {
                                    for (EntityLivingBase entityLivingBase : entities) {
                                        World world = entityLivingBase.world;
                                        world.addWeatherEffect(
                                                new EntityLightningBolt(
                                                        world,
                                                        entityLivingBase.posX,
                                                        entityLivingBase.posY,
                                                        entityLivingBase.posZ,
                                                        true
                                                )
                                        );
                                        int magnification = 1;
                                        if (entityLivingBase.world.isRaining()) {
                                            magnification *= 2;
                                        } else if (entityLivingBase.world.isThundering()) {
                                            magnification *= 4;
                                        }
                                        entityLivingBase.attackEntityFrom(DamageSource.LIGHTNING_BOLT, (float) (evt.getArrow().getDamage() * bonusLevel * 0.3 * magnification));
                                        if (entityLivingBase.onGround) {
                                            double x = RandomUtils.nextBoolean() ? entityArrow.posX - entityLivingBase.posX : entityLivingBase.posX - entityArrow.posX;
                                            double z = RandomUtils.nextBoolean() ? entityArrow.posZ - entityLivingBase.posZ : entityLivingBase.posZ - entityArrow.posZ;
                                            entityLivingBase.attackedAtYaw = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - (double) entityLivingBase.rotationYaw);
                                            entityLivingBase.knockBack(attacker, 0.2f, x, z);
                                        }
                                    }
                                } else {
                                    World world = entityArrow.world;
                                    world.addWeatherEffect(
                                            new EntityLightningBolt(
                                                    world,
                                                    entityArrow.posX,
                                                    entityArrow.posY,
                                                    entityArrow.posZ,
                                                    true
                                            )
                                    );
                                }
                            }

                        }.start();
                    }
                }
            }
        }
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 30 + (enchantmentLevel - 1) * 15;
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        return super.canApplyTogether(ench) && !ench.equals(CarianStyleEnchantments.LORETTA_BIG_BOW) && !ench.equals(CarianStyleEnchantments.LORETTA_TRICK);
    }
}
