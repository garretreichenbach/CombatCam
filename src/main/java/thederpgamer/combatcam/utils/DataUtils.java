package thederpgamer.combatcam.utils;

import api.common.GameClient;
import api.common.GameCommon;
import thederpgamer.combatcam.CombatCam;

import java.util.logging.Level;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class DataUtils {

	public static String getResourcesPath() {
		return CombatCam.getInstance().getSkeleton().getResourcesFolder().getPath().replace('\\', '/');
	}

	public static String getWorldDataPath() {
		String universeName = GameCommon.getUniqueContextId();
		if(!universeName.contains(":")) return getResourcesPath() + "/data/" + universeName;
		else {
			try {
				CombatCam.log.log(Level.WARNING,"Client " + GameClient.getClientPlayerState().getName() + " attempted to illegally access server data.");
			} catch(Exception ignored) { }
			return null;
		}
	}
}
