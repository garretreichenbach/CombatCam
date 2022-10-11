package thederpgamer.combatcam;

import api.listener.events.controller.ClientInitializeEvent;
import api.mod.StarMod;
import thederpgamer.combatcam.controller.MissileCamController;
import thederpgamer.combatcam.manager.ConfigManager;
import thederpgamer.combatcam.manager.EventManager;
import thederpgamer.combatcam.utils.DataUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Main class for EdenCore mod.
 *
 * @author TheDerpGamer
 * @version 1.0 - [06/27/2021]
 */
public class CombatCam extends StarMod {

	//Instance
	private static CombatCam instance;
	public CombatCam() {}
	public static CombatCam getInstance() {
		return instance;
	}
	public static void main(String[] args) {}

	//Data
	public static Logger log;
	public static MissileCamController camController;

	@Override
	public void onEnable() {
		instance = this;
		ConfigManager.initialize(this);
		initLogger();
		EventManager.registerEvents(this);
	}

	@Override
	public void onClientCreated(ClientInitializeEvent event) {
		super.onClientCreated(event);
		camController = new MissileCamController();
	}

	private void initLogger() {
		String logFolderPath = DataUtils.getWorldDataPath() + "/logs";
		File logsFolder = new File(logFolderPath);
		if(!logsFolder.exists()) logsFolder.mkdirs();
		else {
			if(logsFolder.listFiles() != null && logsFolder.listFiles().length > 0) {
				File[] logFiles = new File[logsFolder.listFiles().length];
				int j = logFiles.length - 1;
				for(int i = 0; i < logFiles.length && j >= 0; i++) {
					logFiles[i] = logsFolder.listFiles()[j];
					j--;
				}

				for(File logFile : logFiles) {
					if(!logFile.getName().endsWith(".txt")) continue;
					String fileName = logFile.getName().replace(".txt", "");
					int logNumber = Integer.parseInt(fileName.substring(fileName.indexOf("log") + 3)) + 1;
					String newName = logFolderPath + "/log" + logNumber + ".txt";
					if(logNumber < ConfigManager.getMainConfig().getInt("max-world-logs") - 1) logFile.renameTo(new File(newName));
					else logFile.delete();
				}
			}
		}
		try {
			File newLogFile = new File(logFolderPath + "/log0.txt");
			if(newLogFile.exists()) newLogFile.delete();
			newLogFile.createNewFile();
			log = Logger.getLogger(newLogFile.getPath());
			FileHandler handler = new FileHandler(newLogFile.getPath());
			log.addHandler(handler);
			SimpleFormatter formatter = new SimpleFormatter();
			handler.setFormatter(formatter);
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}
}
