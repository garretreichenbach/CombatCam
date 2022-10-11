package thederpgamer.combatcam.manager;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.weapon.MissilePostAddEvent;
import api.mod.StarLoader;
import org.lwjgl.input.Keyboard;
import org.schema.game.common.controller.SegmentController;
import thederpgamer.combatcam.CombatCam;
import thederpgamer.combatcam.controller.MissileCamController;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class EventManager {

	public static void registerEvents(CombatCam combatCam) {
		StarLoader.registerListener(MissilePostAddEvent.class, new Listener<MissilePostAddEvent>() {
			@Override
			public void onEvent(MissilePostAddEvent event) {
				try {
					if(GameClient.getClientState() != null && event.getShooter() instanceof SegmentController && GameClient.getCurrentControl() instanceof SegmentController && (((SegmentController) event.getShooter()).railController.getRoot().equals(GameClient.getCurrentControl()) || event.getShooter().equals(GameClient.getCurrentControl()))) {
						MissileCamController.addCamera(event.getMissile());
					}
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}, combatCam);

		StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
			@Override
			public void onEvent(KeyPressEvent event) {
				if(event.getKey() == Keyboard.KEY_RCONTROL) CombatCam.camController.setActive(event.isKeyDown());
				else {
					if(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
						if(event.getKey() == Keyboard.KEY_RMETA && event.isKeyDown()) MissileCamController.next();
					} else CombatCam.camController.setActive(false);
				}
			}
		}, combatCam);
	}
}
