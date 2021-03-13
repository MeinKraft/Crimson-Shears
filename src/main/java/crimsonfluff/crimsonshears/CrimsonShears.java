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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CrimsonShears.MOD_ID)
public class CrimsonShears {
    public static final String MOD_ID = "crimsonshears";
    public static final Logger LOGGER = LogManager.getLogger("crimsonshears");
    public static final ConfigBuilder CONFIGURATION = new ConfigBuilder();
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();

    private int dedupe=0;
    private final DamageSource dm = new DamageSource("shears");   // death.attack.shears

    public CrimsonShears() {
        dm.setDifficultyScaled();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIGURATION.COMMON);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!CrimsonShears.CONFIGURATION.ChickenFeatherDrop.get()) return;

        if (!event.getEntity().getEntityWorld().isRemote) {
            Entity living = event.getEntity();

            if (living instanceof ChickenEntity) {
                // Instead of a random number every tick, drop at same time as egg?
                if (living.world.rand.nextInt(6000) == 0) living.entityDropItem(new ItemStack(Items.FEATHER));
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!CrimsonShears.CONFIGURATION.ShearGrass.get()) return;

        World world = event.getWorld();

        if (!world.isRemote) {
            PlayerEntity player = event.getPlayer();

            // TODO: Choose corresponding grass item and dirt types of biome?

            if (player.inventory.getCurrentItem().getItem() instanceof ShearsItem) {
                BlockPos pos = event.getPos();

                if (world.getBlockState(pos).getBlock() instanceof GrassBlock) {
                    player.swing(player.getActiveHand(), true);
                    world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1f, 1f);

                    world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GRASS)));

                    // .damageItem checks for Creative mode !
                    player.inventory.getCurrentItem().damageItem(1, player, (playerIn) -> {
                        playerIn.sendBreakAnimation(player.inventory.player.getActiveHand()); });

                    world.setBlockState(pos, Blocks.DIRT.getDefaultState(),1+2);
                    world.playSound(null, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1f, 1f);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        World world = event.getWorld();

        if (!world.isRemote) {
            PlayerEntity player = event.getPlayer();

            if (player.inventory.getCurrentItem().getItem() instanceof ShearsItem) {
                dedupe++;
                if (dedupe == 2) {
                    dedupe = 0;
                    return;
                }

                Entity living = event.getTarget();
                if (((LivingEntity) living).isChild())
                    if (!CrimsonShears.CONFIGURATION.ShearBabies.get()) {
                        //CrimsonShears.LOGGER.info("NO BABIES !");
                        return;
                    }

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
//                        player.sendStatusMessage(new StringTextComponent("ENTITY: " + living.getClass().getSimpleName()), false);
//                        player.sendStatusMessage(new StringTextComponent("ENTITY: " + living.getClass().getTypeName()), false);
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
                    Hand handIn = (player.getHeldItem(Hand.MAIN_HAND) == player.inventory.getCurrentItem()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
                    //player.setActiveHand(handIn);
                    player.swing(handIn, true);

                    world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1f, 1f);

                    // .damageItem checks for Creative mode !
                    player.inventory.getCurrentItem().damageItem(1, player, (playerIn) -> {
                        playerIn.sendBreakAnimation(handIn); });

                    living.attackEntityFrom(dm, intDamageAmount);
                    ((ServerWorld) world).spawnParticle(ParticleTypes.CRIT, living.getPosX(), living.getPosY() + living.getEyeHeight(), living.getPosZ(), 10, 0.5,0.5,0.5,0);

                    living.entityDropItem(new ItemStack(item));

        // TODO: Stop this event firing Twice  (hence dedupe integer above !)

//                    event.setCancellationResult(ActionResultType.SUCCESS);
//                    //event.setResult(Event.Result.DENY);
//                    event.setCanceled(true);
                }
            }
        }
    }
}
