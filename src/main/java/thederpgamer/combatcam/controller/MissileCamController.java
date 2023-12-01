package thederpgamer.combatcam.controller;

import api.common.GameClient;
import api.utils.StarRunnable;
import org.schema.game.client.controller.manager.AbstractControlManager;
import org.schema.game.common.data.missile.Missile;
import org.schema.schine.graphicsengine.camera.Camera;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.combatcam.CombatCam;
import thederpgamer.combatcam.data.camera.MissileCamera;

import java.util.HashMap;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class MissileCamController extends AbstractControlManager {

	private static final HashMap<Missile, MissileCamera> cameras = new HashMap<>();
	public static MissileCamera currentCamera;

	private static int currentIndex;

	public MissileCamController() {
		super(GameClient.getClientState());
	}

	@Override
	public void setActive(boolean active) {
		if(!active) Controller.setCamera(getDefaultCamera());
		else next();
		super.setActive(active);
	}

	@Override
	public void update(Timer timer) {
		if(currentCamera != null && currentCamera.checkValid()) currentCamera.update(timer, false);
		else setActive(false);
	}

	@Override
	public void onSwitch(final boolean active) {
		if(cameras.isEmpty()) {
			getControlManagers().remove(this);
			try {
				GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getInShipControlManager().getShipControlManager().getShipExternalFlightController().suspend(false);
				GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getInShipControlManager().getShipControlManager().getSegmentBuildController().suspend(false);
			} catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return;
		}
		if(active) {
			setChanged();
			notifyObservers();
			next();
			getControlManagers().add(this);
		} else getControlManagers().remove(this);
		(new StarRunnable() {
			@Override
			public void run() {
				try {
					GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getInShipControlManager().getShipControlManager().getShipExternalFlightController().suspend(active);
					GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getInShipControlManager().getShipControlManager().getSegmentBuildController().suspend(active);
				} catch(NullPointerException exception) {
					exception.printStackTrace();
				}
			}
		}).runLater(CombatCam.getInstance(), 1);
	}

	public static void next() {
		if(currentCamera != null) {
			if(!cameras.isEmpty()) {
				MissileCamera nextCamera = null;
				for(MissileCamera camera : cameras.values()) {
					if(camera == currentCamera) {
						nextCamera = cameras.values().toArray(new MissileCamera[0])[(currentIndex + 1) % cameras.size()];
						currentIndex = (currentIndex + 1) % cameras.size();
						break;
					}
				}
				currentCamera = nextCamera;
				if(currentCamera != null) {
					Controller.setCamera(currentCamera);
					currentCamera.reset();
				} else Controller.setCamera(getDefaultCamera());
			}
		}
	}

	public static void addCamera(Missile missile) {
		if(!cameras.containsKey(missile) && missile.isAlive()) {
			MissileCamera camera = new MissileCamera();
			camera.setMissile(missile);
			cameras.put(missile, camera);
			currentCamera = camera;
			currentCamera.reset();
		}
	}

	public static void removeCamera(Missile missile) {
		if(cameras.containsKey(missile)) {
			cameras.remove(missile);
			next();
		}
	}

	public static Camera getDefaultCamera() {
		if(GameClient.getClientState().isInAnyStructureBuildMode()) return GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getInShipControlManager().getShipControlManager().getSegmentBuildController().getShipBuildCamera();
		else if(GameClient.getClientState().isInFlightMode()) return GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getInShipControlManager().getShipControlManager().getShipExternalFlightController().shipCamera;
		else return Controller.getCamera();
	}

	public static boolean hasCamera() {
		return !cameras.isEmpty();
	}
}
