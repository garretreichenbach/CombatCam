package thederpgamer.combatcam.manager;

import api.mod.config.FileConfiguration;
import thederpgamer.combatcam.CombatCam;

public class ConfigManager {

	private static FileConfiguration mainConfig;
	private static final String[] defaultMainConfig = {
			"debug-mode: false",
			"max-world-logs: 5"
	};


	public static void initialize(CombatCam instance) {
		mainConfig = instance.getConfig("config");
		mainConfig.saveDefault(defaultMainConfig);
	}

	public static FileConfiguration getMainConfig() {
		return mainConfig;
	}
}