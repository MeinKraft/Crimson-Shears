package crimsonfluff.crimsonshears;

import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CrimsonShears.MOD_ID)
public class CrimsonShears {
    public static final String MOD_ID = "crimsonshears";
    public static final Logger LOGGER = LogManager.getLogger("crimsonshears");
    public static final ConfigBuilder CONFIGURATION = new ConfigBuilder();

    private int dedupe=0;
    private final DamageSource dm = new DamageSource("shears");   // death.attack.shears

    public CrimsonShears() {
        dm.setScalesWithDifficulty();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIGURATION.COMMON);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!CrimsonShears.CONFIGURATION.ChickenFeatherDrop.get()) return;

        if (!event.getEntity().level.isClientSide()) {
            Entity living = event.getEntity();

            if (living instanceof ChickenEntity) {
                // Instead of a random number every tick, drop at same time as egg?
                if (living.level.random.nextInt(6000) == 0) living.spawnAtLocation(new ItemStack(Items.FEATHER));
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!CrimsonShears.CONFIGURATION.ShearGrass.get()) return;

        World world = event.getWorld();

        if (!world.isClientSide()) {
            PlayerEntity player = event.getPlayer();

            // TODO: Choose corresponding grass item and dirt types of biome?

            if (player.inventory.getSelected().getItem() instanceof ShearsItem) {
                BlockPos pos = event.getPos();

                if (world.getBlockState(pos).getBlock() instanceof GrassBlock) {
                    player.swing(player.getUsedItemHand(), true);
                    world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.BLOCKS, 1f, 1f);

                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GRASS)));

                    player.inventory.getSelected().hurtAndBreak(1, player, (playerIn) -> { playerIn.broadcastBreakEvent(EquipmentSlotType.MAINHAND); });

                    world.setBlock(pos, Blocks.DIRT.defaultBlockState(),1+2);
                    world.playSound(null, pos, SoundEvents.GRASS_BREAK, SoundCategory.BLOCKS, 1f, 1f);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        World world = event.getWorld();

        if (!world.isClientSide()) {
            PlayerEntity player = event.getPlayer();

            if (player.inventory.getSelected().getItem() instanceof ShearsItem) {
                dedupe++;
                if (dedupe == 2) {
                    dedupe = 0;
                    return;
                }

                Entity living = event.getTarget();
                if (((LivingEntity) living).isBaby())
                    if (!CrimsonShears.CONFIGURATION.ShearBabies.get()) return;

                int intDamageAmount = 0;
                Item item = null;

                if (CrimsonShears.CONFIGURATION.ShearSpider.get()) {
                    if (living instanceof SpiderEntity) {
                        item = Items.STRING;
                        intDamageAmount = 4;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearRabbit.get()) {
                    if (living instanceof RabbitEntity) {
                        item = Items.RABBIT_HIDE;
                        intDamageAmount = 2;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearChicken.get()) {
                    if (living instanceof ChickenEntity) {
                        item = Items.FEATHER;
                        intDamageAmount = 2;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearParrot.get()) {
                    if (living instanceof ParrotEntity) {
                        item = Items.FEATHER;
                        intDamageAmount = 2;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearGuardian.get()) {
                    if (living instanceof GuardianEntity) {
                        item = Items.PRISMARINE_SHARD;
                        intDamageAmount = 4;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearZombie.get()) {
                    if (living instanceof ZombieEntity) {
                        item = Items.ROTTEN_FLESH;
                        intDamageAmount = 4;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearHorse.get()) {
                    if (living instanceof HorseEntity || living instanceof DonkeyEntity || living instanceof MuleEntity) {
                        item = Items.LEATHER;
                        intDamageAmount = 6;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearPiglin.get()) {
                    if (living instanceof PiglinEntity || living instanceof PiglinBruteEntity) {
                        item = Items.LEATHER;
                        intDamageAmount = 6;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearPig.get()) {
                    if (living instanceof PigEntity) {
                        item = Items.LEATHER;
                        intDamageAmount = 4;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearHogZog.get()) {
                    if (living instanceof HoglinEntity) {
                        item = Items.LEATHER;
                        intDamageAmount = 6;
                    }
                    if (living instanceof ZoglinEntity) {
                        item = Items.ROTTEN_FLESH;
                        intDamageAmount = 6;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearSkeleton.get()) {
                    if (living instanceof SkeletonEntity) {
                        item = Items.BONE;
                        intDamageAmount = 4;

                    } else {
                        // Add 'NastyMobs' compatibility
                        if (living.getClass().getSimpleName().equals("NastySkeletonEntity")) {
                            item = Items.BONE;
                            intDamageAmount = 4;
                        }
//                        player.sendMessage(new StringTextComponent("ENTITY: " + living.getClass().getSimpleName()), Util.NIL_UUID);
//                        player.sendMessage(new StringTextComponent("ENTITY: " + living.getClass().getTypeName()), Util.NIL_UUID);
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearCreeper.get()) {
                    if (living instanceof CreeperEntity) {
                        item = Items.GUNPOWDER;
                        intDamageAmount = 4;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearZombieHorse.get()) {
                    if (living instanceof ZombieHorseEntity) {
                        item = Items.ROTTEN_FLESH;
                        intDamageAmount = 6;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearSkeletonHorse.get()) {
                    if (living instanceof SkeletonHorseEntity) {
                        item = Items.BONE;
                        intDamageAmount = 6;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearSquid.get()) {
                    if (living instanceof SquidEntity) {
                        item = Items.INK_SAC;
                        intDamageAmount = 2;
                    }
                }

                if (CrimsonShears.CONFIGURATION.ShearCow.get()) {
                    if (living instanceof CowEntity) {
                        item = Items.LEATHER;
                        intDamageAmount = 2;
                    }
                }

                if (intDamageAmount > 0) {
                    BlockPos pos = event.getPos();

                    // NOTE: If holding carrot in OFF_HAND and use shears, the carrots will animate arm swing -
                    // can this be stopped?
                    Hand handIn = (player.getItemInHand(Hand.MAIN_HAND) == player.inventory.getSelected()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
                    //player.setActiveHand(handIn);
                    player.swing(handIn, true);

                    world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.PLAYERS, 1f, 1f);
                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(item)));

                    player.inventory.getSelected().hurtAndBreak(1, player, (playerIn) -> { playerIn.broadcastBreakEvent(EquipmentSlotType.MAINHAND); });

                    living.hurt(dm, intDamageAmount);
                    ((ServerWorld) world).sendParticles(ParticleTypes.CRIT, event.getPos().getX(), event.getPos().getY() + living.getEyeHeight(), event.getPos().getZ(), 10, 0.5,0.5,0.5,0);


        // TODO: Stop this event firing Twice  (hence dedupe integer above !)

//                    event.setCancellationResult(ActionResultType.SUCCESS);
//                    //event.setResult(Event.Result.DENY);
//                    event.setCanceled(true);
                }
            }
        }
    }
}
