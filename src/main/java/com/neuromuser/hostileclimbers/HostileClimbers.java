package com.neuromuser.hostileclimbers;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostileClimbers implements ModInitializer {
        public static final String MOD_ID = "hostileclimbers";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

        @Override
        public void onInitialize() {
                LOGGER.info("Hostile Climbers loaded.");
        }
}