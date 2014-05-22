package TimeTracker;

import java.awt.AWTException;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;










import java.nio.file.Paths;

import javazoom.jlgui.basicplayer.BasicPlayerException;

import org.sikuli.basics.Settings;
import org.sikuli.script.App;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import common.Items;
import common.JWin;
import common.Logging;
import common.Sikuliz;

public class TimeTrackerTestCases extends common.Sikuliz {
	public static String timeTrackerOnImage=null;
	public static int totalOn=0;
	public static int totalOff=0;

	TimeTrackerTestCases() throws IOException, BasicPlayerException,AWTException {
		//timeTrackerOnImage=getFile(path+"TimeTrackerOn1.png");
		appTitle = "TimeTracker2";
		path = imageRoot + "TimeTracker/";
		
		exePath = "C:\\Program Files (x86)\\TimeTracker2\\TimeTracker2.exe";
		//setTrayIcon("", "Time Tracker Monitor");//TODO fix this
		
	}

	public boolean isLoggingTime() throws Exception{
		sPush();
		Settings.MinSimilarity=0.97;
		boolean result=false;
		//TODO if time tracker is paused
		//this needs precise detection, make a mechanism for pushing and popping settings
		//TODO how to specify a region in relative to another region
		Screen primaryScreen=new Screen(0);
		Screen secondaryScreen=new Screen(1);
		Region regionOnPrimaryScreen=Region.create(primaryScreen.w-300, primaryScreen.h-30, 300, 30); //rectangle containing tray icons
		Region regionOnSecondaryScreen=Region.create(secondaryScreen.w-300, secondaryScreen.h-30, 300, 30);
		//r.highlight(10);
		if (
				(anyExist(regionOnPrimaryScreen, Items.iGet("TimeTracker/TrayIcon/On").sGetAll()) != null)
				||
				(anyExist(regionOnSecondaryScreen, Items.iGet("TimeTracker/TrayIcon/On").sGetAll()) != null)
				||
				(anyExist(primaryScreen, Items.iGet("TimeTracker/TrayIcon/On").sGetAll()) != null)
				||
				(anyExist(secondaryScreen, Items.iGet("TimeTracker/TrayIcon/On").sGetAll()) != null)
				){//address the expected place to find try
			result=true;//TODO refine search region to last 30 pixels at the bottom 
			System.out.println("TimeTracker is Logging time :)");
		} else {
			System.out.println("TimeTracker is NOT Logging time :(");
		}
		
		sPop();
		return result;
	}
	
	public static String toHoursAndMinutes(int minutes){
		String result="";
		Integer hours;
		String hoursStr = Integer.toString(minutes / 60);
		if (hoursStr.length()<2){
			hoursStr="0"+hoursStr;
		}
		String minutesStr = Integer.toString(minutes % 60);
		if (minutesStr.length()<2){
			minutesStr="0"+minutesStr;
		}
		result=hoursStr+":"+minutesStr;
		
		return result;
		
	}
	
	public class TrackerAlert implements Runnable{
		
		

		@Override
		public void run() {
			ExtractionDir="c:\\TimetrackerMonitor";
			//(new File(ExtractionDir+"\\Log")).mkdir();
			Logging.setLogFile(ExtractionDir+"\\Log\\"+Logging.getDateStamp()+".txt");
			try {
				Sikuliz.getCFGsettings(ExtractionDir+"\\config.txt");
				Sikuliz.getCFG();
			} catch (IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			Items items=null;
			
			try {
				 items=new Items(ExtractionDir+"\\resources");
				 failSoundPlayer.open(new File(items.iGet("Sounds").sGet("Error")));
				 passSoundPlayer.open(new File(items.iGet("Sounds").sGet("Beeb")));
				 
			} catch (IOException | BasicPlayerException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			int minutesOff=0;
			int minutesOn=0;
			try {
				setTrayIcon(Items.iGet("Monitor/TrayIcon/Progress").sGet(), "");
				Sikuliz.notify("Time Tracker Monitor started", "Enjoy !");
				
			} catch (IOException | AWTException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			boolean praying=false;
			boolean prayNotified=false;
			// TODO Auto-generated method stub
			while(true){
				try {
					if(!isLoggingTime()){
						
						if (prayNotified || praying){
							praying=true;
							prayNotified=false;
							Logging.log("Tracker is NOT counting time.");
							setTrayIcon(Items.iGet("Monitor/TrayIcon/Off").sGet(),"Monitored OFF time: "+toHoursAndMinutes(minutesOff)+ ", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+toHoursAndMinutes(totalOff));
							for (int i=0; i<3;i++){
								Sikuliz.notify("Monitored OFF time: "+toHoursAndMinutes(minutesOff)+ ", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+toHoursAndMinutes(totalOff),"Tracker is NOT counting time");
								failSound();
								Thread.sleep(1000);
							}
						} else {
							if (minutesOff>=getIntegerCFGValue("Long-off")){
								failSoundPlayer.open(new File(items.iGet("Sounds").sGet("police")));
							} else{
								failSoundPlayer.open(new File(items.iGet("Sounds").sGet("Error")));
							}
							setTrayIcon(Items.iGet("Monitor/TrayIcon/Off").sGet(),"Monitored continous OFF time: "+toHoursAndMinutes(minutesOff)+", Total Off in this session: "+toHoursAndMinutes(totalOff));
							for (int i=0; i<3;i++){
								Sikuliz.notify("Monitored OFF time: "+toHoursAndMinutes(minutesOff)+ ", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+toHoursAndMinutes(totalOff),"Tracker is NOT counting time :(");
								failSound();
								Thread.sleep(1000);
							}
						}
						minutesOn=0;
						
						if (items!=null) {
							failSound();	
						}
						
						
						Logging.log("monitored continous OFF time: "+toHoursAndMinutes(minutesOff++) + ", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+toHoursAndMinutes(totalOff++) );
						
					}else {
						//Logging.log("Tracker is counting time.");
						if (praying){
							praying=false;
						}
						//use sound 
						// use tray notifications
						minutesOff=0;
						setTrayIcon(Items.iGet("Monitor/TrayIcon/On").sGet(),"Monitored ON time: "+toHoursAndMinutes(minutesOn)+ ", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+toHoursAndMinutes(totalOff));
						passSound();
						Logging.log("Monitored continous ON  time: "+toHoursAndMinutes(minutesOn++) + ", Total On: "+toHoursAndMinutes(totalOn++) +", Total Off: "+toHoursAndMinutes(totalOff));
						if ((minutesOn % getIntegerCFGValue("prayer_interval"))==0){
							Logging.log("GOTO Pray :)");
							for (int i=0; i<3;i++){
								Sikuliz.notify("GOTO Pray :)",getIntegerCFGValue("prayer_interval")+" minutes of work completed, go pray and thank Allah :)");
								passSound();
								playMp3(Items.iGet("Sounds").sGet("doorbell-1"));
								Thread.sleep(2000);
							}
							prayNotified=true;

						}
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			try {
				Thread.sleep(60000);//one minute sleep
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}

	}
	
			
	Thread t = new Thread(new TrackerAlert());
		
	
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
				// TODO Auto-generated catch block
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

	public static void main(String[] args) throws IOException, InterruptedException, BasicPlayerException, AWTException {
		
//		tt.t.start();
//		while(tt.t.isAlive()){
//			Thread.sleep(100000000);
//		}
//		//tt.isLoggingTime();
//		if (true){//for testing
//			return;
//		}
//		;
		
TimeTrackerTestCases tt = new TimeTrackerTestCases();

		tt.t.start();
		while(tt.t.isAlive()){
			Thread.sleep(43200000);//sleep 12 hours :) 1000*60*60*12
		}
		
	}
}
