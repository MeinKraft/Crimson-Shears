package crimsonfluff.crimsonshears;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    public final ForgeConfigSpec COMMON;

    public ForgeConfigSpec.BooleanValue ShearGrass;
    public ForgeConfigSpec.BooleanValue ChickenFeatherDrop;
    public ForgeConfigSpec.BooleanValue ShearBabies;

    public ForgeConfigSpec.BooleanValue ShearSpider;
    public ForgeConfigSpec.BooleanValue ShearRabbit;
    public ForgeConfigSpec.BooleanValue ShearChicken;
    public ForgeConfigSpec.BooleanValue ShearParrot;
    public ForgeConfigSpec.BooleanValue ShearGuardian;
    public ForgeConfigSpec.BooleanValue ShearZombie;
    public ForgeConfigSpec.BooleanValue ShearHorse;
    public ForgeConfigSpec.BooleanValue ShearPig;
    public ForgeConfigSpec.BooleanValue ShearSkeleton;
    public ForgeConfigSpec.BooleanValue ShearSkeletonHorse;
    public ForgeConfigSpec.BooleanValue ShearCreeper;
    public ForgeConfigSpec.BooleanValue ShearZombieHorse;
    public ForgeConfigSpec.BooleanValue ShearSquid;
    public ForgeConfigSpec.BooleanValue ShearPiglin;
    public ForgeConfigSpec.BooleanValue ShearHogZog;
    public ForgeConfigSpec.BooleanValue ShearCow;

    public ConfigBuilder() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Shears Settings");


        ShearGrass = builder
                .comment("Should Grass Blocks be shearable ?  Default: true")
                .define("ShearGrass", true);

        ChickenFeatherDrop = builder
                .comment("Should Chickens randomly drop feathers ?  Default: false")
                .define("ChickenFeatherDrop", false);

        ShearBabies = builder
                .comment("Should babies (Chickens/Rabbits/Pigs) be shearable ?  Default: false")
                .define("ShearBabies", false);


        ShearSpider = builder
                .comment("Should Spiders be shearable ?  Default: true")
                .define("ShearSpider", true);

        ShearRabbit = builder
                .comment("Should Rabbits be shearable ?  Default: true")
                .define("ShearRabbit", true);

        ShearChicken = builder
                .comment("Should Chickens be shearable ?  Default: true")
                .define("ShearChicken", true);

        ShearParrot = builder
                .comment("Should Parrots be shearable ?  Default: true")
                .define("ShearParrot", true);

        ShearGuardian = builder
                .comment("Should Guardians be shearable ?  Default: true")
                .define("ShearGuardian", true);

        ShearZombie = builder
                .comment("Should Zombies/Husks be shearable ?  Default: true")
                .define("ShearZombie", true);

        ShearHorse = builder
                .comment("Should Horses be shearable ?  Default: true")
                .define("ShearHorse", true);

        ShearPig = builder
                .comment("Should Pigs be shearable ?  Default: true")
                .define("ShearPig", true);

        ShearSkeleton = builder
                .comment("Should Skeletons be shearable ?  Default: true")
                .define("ShearSkeleton", true);

        ShearSkeletonHorse = builder
                .comment("Should Skeleton Horses be shearable ?  Default: true")
                .define("ShearSkeletonHorse", true);

        ShearCreeper = builder
                .comment("Should Creepers be shearable ?  Default: true")
                .define("ShearCreeper", true);

        ShearZombieHorse = builder
                .comment("Should Zombie Horses be shearable ?  Default: true")
                .define("ShearZombieHorse", true);

        ShearSquid = builder
                .comment("Should Squid be shearable ?  Default: true")
                .define("ShearSquid", true);

        ShearPiglin = builder
                .comment("Should Piglins/Piglin Brutes be shearable ?  Default: true")
                .define("ShearPiglin", true);

        ShearHogZog = builder
                .comment("Should Hoglins/Zoglins be shearable ?  Default: true")
                .define("ShearHogZog", true);

        ShearCow = builder
                .comment("Should Cows be shearable ?  Default: true")
                .define("ShearCow", true);


        builder.pop();

        COMMON = builder.build();
    }
}
