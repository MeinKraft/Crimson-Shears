package crimsonfluff.crimsonshears;

import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CrimsonShears.MOD_ID)
public class CrimsonShears {
    public static final String MOD_ID = "crimsonshears";
    public static final Logger LOGGER = LogManager.getLogger("crimsonshears");
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();

    private int dedupe=0;
    private final DamageSource dm = new DamageSource("shears");   // death.attack.shears

    public CrimsonShears() {
        dm.setDifficultyScaled();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
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
                int intDamageAmount=0;
                Item item = null;

                if (living instanceof SpiderEntity) {
                    item=Items.STRING;
                    intDamageAmount=1;
                }

                if (living instanceof RabbitEntity) {
                    item=Items.RABBIT_HIDE;
                    intDamageAmount=1;
                }

                if (living instanceof ChickenEntity) {
                    item=Items.FEATHER;
                    intDamageAmount=1;
                }

                if (living instanceof ZombieEntity) {
                    item=Items.ROTTEN_FLESH;
                    intDamageAmount=4;
                }

                if (living instanceof HorseEntity) {
                    item=Items.LEATHER;
                    intDamageAmount=6;
                }

                if (living instanceof PigEntity) {
                    item=Items.LEATHER;
                    intDamageAmount=4;
                }

                if (living instanceof SkeletonEntity) {
                    item=Items.BONE;
                    intDamageAmount=4;
                }

                if (living instanceof CreeperEntity) {
                    item=Items.GUNPOWDER;
                    intDamageAmount=4;
                }

                if (living instanceof ZombieHorseEntity) {
                    item=Items.ROTTEN_FLESH;
                    intDamageAmount=6;
                }

                if (living instanceof SkeletonHorseEntity) {
                    item=Items.BONE;
                    intDamageAmount=6;
                }


                if (intDamageAmount>0) {
                    BlockPos pos = event.getPos();

                    player.swing(player.getActiveHand(), true);
                    world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1f, 1f);

                    // .damageItem checks for Creative mode !
                    player.inventory.getCurrentItem().damageItem(1, player, (playerIn) -> {
                        playerIn.sendBreakAnimation(player.inventory.player.getActiveHand()); });

                    living.attackEntityFrom(dm, intDamageAmount);
                    ((ServerWorld) world).spawnParticle(ParticleTypes.CRIT, living.getPosX(), living.getPosY()+living.getEyeHeight(), living.getPosZ(), 10, 0.5,0.5,0.5,0);

                    world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(item)));

        // TODO: Stop this event firing Twice  (hence dedupe integer above !)

//                    event.setCancellationResult(ActionResultType.SUCCESS);
//                    //event.setResult(Event.Result.DENY);
//                    event.setCanceled(true);
                }
            }
        }
    }
}
