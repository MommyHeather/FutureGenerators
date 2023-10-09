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

        public Server(ForgeConfigSpec.Builder builder) {

            builder.push("Turbine");

            turbineMaxSpeed = builder.comment("The max speed (mb/t consumption) the turbine can reach.").defineInRange("maxTurbineSpeed", 200, 1, Integer.MAX_VALUE);
            turbineFeRatio = builder.comment("How much FE the turbine makes for every mb of water it consumes.").defineInRange("turbineFERatio", 4, 1, Integer.MAX_VALUE);
            turbineSpinupTime = builder.comment("How long the turbine takes to spin up from still to max speed, in seconds.\nMinimum speed gain is 1 per tick / 20 per second!").defineInRange("turbineSpinupTime", 60, 0, Integer.MAX_VALUE);
            turbineWaterCapacity = builder.comment("How much water the turbine can hold.").defineInRange("turbineWaterCapacity", 10000, 1000, Integer.MAX_VALUE);
            turbinePowerCapacity = builder.comment("How much power the turbine can hold.").defineInRange("turbinePowerCapacity", 10000, 1000, Integer.MAX_VALUE);

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
