package testCases;

import java.awt.AWTException;
import java.io.IOException;

import javazoom.jlgui.basicplayer.BasicPlayerException;

import org.sikuli.basics.Settings;
import org.sikuli.script.App;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Region;

import common.JWin;
import common.Logging;

public class testCase1 extends common.Sikuliz {

	public testCase1() throws BasicPlayerException, IOException, AWTException {
		super();
		appTitle = "TimeTracker2";
		path = imageRoot + "TimeTracker/";
		exePath = "C:\\Program Files (x86)\\TimeTracker2\\TimeTracker2.exe";
	}

	public void trainTimeTracker() throws InterruptedException, FindFailed,
			IOException {
	
		boolean online = false;
		Region r, r1;
		boolean result = false;
	
		if (!JWin.activate(appTitle)) {
			Logging.log("Launching: " + exePath);
			myApp = App.open(exePath);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = (myApp.window() != null);
			if (result ) {
				Logging.log("Launch success  ");
				System.out.println(appTitle+" is active.");
				maximize();
			} else {
				Logging.log("could not bring to front !");
				if (click(null, path + "TimeTrackerOff.png", 2)) {
					System.out.println("Alert: Time tracker is paused");
				} else if (click(null, path + "TimeTrackerOn.png", 2)) {
					System.out.println("Time tracker logging time");
				} else {
					System.out.println("Time tracker not loaded");
	
					if (click(null, path + "TimeTrackerIcon.png", 2)) {
	
						while ((r = s.exists(getFile(path + "loading.png"))) == null) {
							Thread.sleep(500);
						}
						while ((r = s.exists(getFile(path + "loading.png"))) != null) {
							Thread.sleep(500);
						}
	
					}
	
				}
			}
	
		}
	
		maximize();
	
		if (click(null, path + "workingOnline.png", 0)) {
			System.out.println("Working online");
			online = true;
		} else {
			s.waitVanish(getFile(path + "contactingServer.png"));
			System.out.println("Working offline");
		}
	
		click(null, path + "checkInOut.png");
	
		if (click(null, path + "reports.png")) {
	
			if (click(null, path + "httpError.png", 0)) { // handle error
															// connection with
															// server
				click(null, path + "ok.png");
	
			}
	
			if (click(null, path + "wait.png", 0)) {
				System.out.println("waiting for data fetch...");
				while (click(null, path + "wait.png", 0)) {
					Thread.sleep(100);
				}
				System.out.println("Done.");
			}
	
		}
	
		if (click(null, path + "offlineTime.png")) {
	
			Settings.MinSimilarity = 0.7;
	
			if ((r = s.exists(getFile(path + "logStartTime.png"))) != null) {
				r.highlight(1);
				if ((r1 = r.exists(getFile(path + "hh.png"))) != null) {
					r1.highlight(1);
					Thread.sleep(1000);
					r1 = r1.below(20);
					Thread.sleep(1000);
					r1.highlight(1);
					r1.click();
					r1.type("a", KeyModifier.CTRL);
					r1.type(Key.DELETE);
					r1.type("10");
				}
	
				if ((r1 = r.exists(getFile(path + "mm.png"))) != null) {
					r1.highlight(1);
					r1 = r1.below(20);
					
					r1.click();
					r1.type("a", KeyModifier.CTRL);
					r1.type(Key.DELETE);
					r1.type("00");
				} else {
					System.out.println("mm not found.");
				}
	
			}
	
			if ((r = s.exists(getFile(path + "logEndTime.png"))) != null) {
				r.highlight(1);
				if ((r1 = r.exists(getFile(path + "hh.png"))) != null) {
					r1.highlight(1);
					r1 = r1.below(20);
					r1.click();
	
					r1.type("a", KeyModifier.CTRL);
					r1.type(Key.DELETE);
					r1.type("10");
				}
	
				if ((r1 = r.exists(getFile(path + "mm.png"))) != null) {
					r1.highlight(1);
					r1 = r1.below(20);
					r1.click();
					r1.type("a", KeyModifier.CTRL);
					r1.type(Key.DELETE);
					r1.type("10");
				} else {
					System.out.println("mm not found.");
				}
	
			}
	
		}
	
		click(null, path + "checkboxUnchecked.png");
	
		click(null, path + "log.png", 0);
	
		click(null, path + "minimize.png", 1);
	
		System.out.println("Completed :)");
	
	}

}
