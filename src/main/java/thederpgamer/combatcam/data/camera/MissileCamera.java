package thederpgamer.combatcam.data.camera;

import api.common.GameClient;
import org.lwjgl.input.Keyboard;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.missile.Missile;
import org.schema.schine.graphicsengine.camera.Camera;
import org.schema.schine.graphicsengine.camera.look.AxialCameraLook;
import org.schema.schine.graphicsengine.camera.viewer.AbstractViewer;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.combatcam.CombatCam;

import javax.vecmath.Vector3f;

import static thederpgamer.combatcam.controller.MissileCamController.currentCamera;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class MissileCamera extends Camera {

	private static final float DEFAULT_DISTANCE = 3.5f;
	private static final Vector3f backwards = new Vector3f();

	private Missile missile;

	public MissileCamera() {
		super(GameClient.getClientState(), new MissileCameraViewer());
	}

	@Override
	public void reset() {
		super.reset();
		if(checkValid() && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
			getWorldTransform().set(missile.getWorldTransform());
			setLookAlgorithm(new AxialCameraLook(this));
			backwards.set(GlUtil.getBackVector(backwards, getWorldTransform()));
			backwards.scale(15.0f);
			getWorldTransform().origin.add(backwards);
			getLookAlgorithm().force(getWorldTransform());
			Controller.setCamera(this);
		}
	}

	@Override
	public void update(Timer timer, boolean server) {
		if(!missile.isAlive()) {
			currentCamera = null;
			CombatCam.camController.setActive(false);
			return;
		}
		getWorldTransform().origin.set(missile.getWorldTransform().origin);
		backwards.set(GlUtil.getBackVector(backwards, getWorldTransform()));
		backwards.scale(5.0f);
		getWorldTransform().origin.add(backwards);
		getLookAlgorithm().force(getWorldTransform());
		updateViewer(timer);
	}

	public void setMissile(Missile missile) {
		this.missile = missile;
	}

	public Missile getMissile() {
		return missile;
	}

	public boolean checkValid() {
		return missile != null && missile.isAlive() && GameClient.getCurrentControl() instanceof SegmentController;
	}

	public static class MissileCameraViewer extends AbstractViewer {

		@Override
		public Vector3f getPos() {
			try {
				return currentCamera.getWorldTransform().origin;
			} catch(Exception e) {
				return new Vector3f();
			}
		}
	}
}
