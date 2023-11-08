package co.uk.mommyheather.futuregenerators.config;

import java.io.File;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public class FutureGeneratorsConfig {
    
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;


    public static class Server {
        //serverconfig

        public final ForgeConfigSpec.ConfigValue<Integer> turbineMaxSpeed;
        public final ForgeConfigSpec.ConfigValue<Integer> turbineFeRatio;
        public final ForgeConfigSpec.ConfigValue<Integer> turbineSpinupTime;
        public final ForgeConfigSpec.ConfigValue<Integer> turbineWaterCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> turbinePowerCapacity;

        public final ForgeConfigSpec.ConfigValue<Integer> lightningGeneratorConsumption;
        public final ForgeConfigSpec.ConfigValue<Integer> lightningGeneratorCapacity;

        public final ForgeConfigSpec.ConfigValue<Integer> lightningDynamoProduction;
        public final ForgeConfigSpec.ConfigValue<Integer> lightningDynamoCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> lightningDynamoMaxGenerators;
        
        public final ForgeConfigSpec.ConfigValue<Integer> washerPowerCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> washerPowerConsumption;
        public final ForgeConfigSpec.ConfigValue<Integer> washerWaterCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> washerWaterConsumption;
        public final ForgeConfigSpec.ConfigValue<Integer> washerProcessTime;

        public final ForgeConfigSpec.ConfigValue<Integer> boilerWaterCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> boilerHeatingRate;

        public Server(ForgeConfigSpec.Builder builder) {

            
            builder.push("Boiler");

            boilerWaterCapacity = builder.comment("The water capacity of the boiler. The hot water capacity is 2x this.").defineInRange("boilerWaterCapacity", 8000, Integer.MIN_VALUE, Integer.MAX_VALUE / 2);
            boilerHeatingRate = builder.comment("How much water the boiler converts into hot water each tick whilst running.").defineInRange("boilerHeatingRate", 100, 1, Integer.MAX_VALUE);
            
            builder.pop();


            builder.push("Turbine");

            turbineMaxSpeed = builder.comment("The max speed (mb/t consumption) the turbine can reach.").defineInRange("maxTurbineSpeed", 200, 1, Integer.MAX_VALUE);
            turbineFeRatio = builder.comment("How much FE the turbine makes for every mb of water it consumes.").defineInRange("turbineFERatio", 4, 1, Integer.MAX_VALUE);
            turbineSpinupTime = builder.comment("How long the turbine takes to spin up from still to max speed, in seconds.\nMinimum speed gain is 1 per tick / 20 per second!").defineInRange("turbineSpinupTime", 60, 0, Integer.MAX_VALUE);
            turbineWaterCapacity = builder.comment("How much water the turbine can hold.").defineInRange("turbineWaterCapacity", 10000, 1000, Integer.MAX_VALUE);
            turbinePowerCapacity = builder.comment("How much power the turbine can hold.").defineInRange("turbinePowerCapacity", 10000, 1000, Integer.MAX_VALUE);

            builder.pop();

            builder.push("LightningGenerator");

            lightningGeneratorConsumption = builder.comment("The power consumption of the lightning generator.").defineInRange("lightningGeneratorConsumption", 1000, 1, Integer.MAX_VALUE);
            lightningGeneratorCapacity = builder.comment("The lightning generator's power capacity.").defineInRange("lightningGeneratorCapacity", 100000, 1, Integer.MAX_VALUE);
            builder.pop();

            
            builder.push("LightningDynamo");

            lightningDynamoProduction = builder.comment("How much power each dynamo produces per connected lightning generator.").defineInRange("lightningDynamoProduction", 10000, 1, Integer.MAX_VALUE);
            lightningDynamoCapacity = builder.comment("How much power the lightning dynamo can store.").defineInRange("lightningDynamoCapacity", 10000000, 1, Integer.MAX_VALUE);
            lightningDynamoMaxGenerators = builder.comment("How many lightning generators a single dynamo can connect to.").defineInRange("lightningDynamoMaxGenerators", 4, 1, 4);

            builder.pop();

            
            builder.push("Washer");

            washerPowerCapacity = builder.comment("How much power the washer can hold.").defineInRange("washerPowerCapacity", 10000, 1000, Integer.MAX_VALUE);
            washerPowerConsumption = builder.comment("How much power is consumed per cobblestone 'washed'.").defineInRange("washerPowerConsumption", 1000, 1, Integer.MAX_VALUE);
            washerWaterCapacity = builder.comment("How much water the washer can hold.").defineInRange("washerWaterCapacity", 10000, 1000, Integer.MAX_VALUE);
            washerWaterConsumption = builder.comment("How much water is consumed per cobblestone 'washed'.").defineInRange("washerWaterConsumption", 250, 1, Integer.MAX_VALUE);
            washerProcessTime = builder.comment("How many ticks it takes to 'wash' a single block of cobblestone.").defineInRange("washerProcessTime", 5, 1, Integer.MAX_VALUE);

            builder.pop();


        }


    }

        
    static {
        Pair<Server, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = server.getLeft();
        SERVER_SPEC = server.getRight();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }

}
