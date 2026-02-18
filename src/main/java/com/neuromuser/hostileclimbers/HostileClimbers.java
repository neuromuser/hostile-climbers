package com.neuromuser.hostileclimbers;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostileClimbers implements ModInitializer {
        public static final String MOD_ID = "hostileclimbers";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        public static HostileClimbersConfig CONFIG;

        @Override
        public void onInitialize() {
                CONFIG = HostileClimbersConfig.load();
                LOGGER.info("Hostile Climbers loaded.");
        }
}