package timeTrackerMonitor;

import java.awt.AWTException;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;










import java.nio.file.Paths;

import javazoom.jlgui.basicplayer.BasicPlayerException;

import org.sikuli.basics.Settings;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import common.Items;
import common.Logging;
import common.Sikuliz;

/**
 * 
 * @author Amr Lotfy
 *
 */
public class TimeTrackerMonitor extends common.Sikuliz implements Runnable{
	public static int totalOn=0;
	public static int totalOff=0;
	Region lastFound=null;
	TimeTrackerMonitor() throws IOException, BasicPlayerException,AWTException {
		super();
	}

	public boolean isLoggingTime() throws Exception{
		sPush();
		Settings.MinSimilarity=0.97;
		boolean result=false;
		setTrayIcon(Items.sGet("monitor/TrayIcon/progress"), "in progress ...");
		int numOfScreens=Screen.getNumberScreens();
		Screen[] screens=new  Screen[numOfScreens];
		Region[] regions=new Region[numOfScreens];
		if (lastFound!=null){
			lastFound=anyExist(lastFound, Items.sGetAll("TimeTracker/TrayIcon/On"));
			result=(lastFound!=null);
		}
		if (!result){
			for(int i=0;i<numOfScreens;i++){
				screens[i]=new Screen(i);
				regions[i]=Region.create(screens[i].w*2/3,screens[i].h-30,screens[i].w,30);
				if ((lastFound=anyExist(regions[i], Items.sGetAll("TimeTracker/TrayIcon/On"))) != null){
					result=true; 
					break;
				}
			}
			if (!result){
				for(int i=0;i<numOfScreens;i++){
					screens[i]=new Screen(i);
					if ((lastFound=anyExist(screens[i], Items.sGetAll("TimeTracker/TrayIcon/On"))) != null){
						result=true; 
						break;
					}
				}
			}
		}
		if (result){//address the expected place to find try
			Logging.log("TimeTracker is Logging time :)");
			setTrayIcon(Items.sGet("monitor/TrayIcon/on"), "On");
		} else {
			Logging.log("TimeTracker is NOT Logging time :(");
			setTrayIcon(Items.sGet("monitor/TrayIcon/Off"), "Off");
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

	@Override
	public void run() {
		ExtractionDir="c:\\TimetrackerMonitor";
		//(new File(ExtractionDir+"\\Log")).mkdir();
		Logging.setLogFile(ExtractionDir+"\\Log\\"+Logging.getDateStamp()+".txt");
		try {
			Sikuliz.getCFGsettings(ExtractionDir+"\\config.txt");
			Sikuliz.getCFG();
		} catch (IOException e4) {
			e4.printStackTrace();
		}
		Items items=null;
		try {
			items=new Items(ExtractionDir+"\\resources");
			failSoundPlayer.open(new File(Items.sGet("Sounds/Error")));
			passSoundPlayer.open(new File(Items.sGet("Sounds/Beeb")));
		} catch (IOException | BasicPlayerException e3) {
			e3.printStackTrace();
		}
		int minutesOff=0;
		int minutesOn=0;
		try {
			setTrayIcon(Items.sGet("Monitor/TrayIcon/Progress"), "");
			Sikuliz.notify("Time Tracker Monitor started", "Enjoy !");
		} catch (IOException | AWTException | InterruptedException e1) {
			e1.printStackTrace();
		}
		boolean praying=false;
		boolean prayNotified=false;
		while(true){
			try {
				items=new Items(ExtractionDir+"\\resources");
				if(!isLoggingTime()){
					if (prayNotified || praying){
						praying=true;
						prayNotified=false;
						//Logging.log("Tracker is NOT counting time.");
						setTrayIcon(Items.sGet("Monitor/TrayIcon/Off"),"Monitored OFF time: "+toHoursAndMinutes(minutesOff)+ 
								", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+toHoursAndMinutes(totalOff));
						for (int i=0; i<3;i++){
							Sikuliz.notify("Monitored OFF time: "+toHoursAndMinutes(minutesOff)+ 
									", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+
									toHoursAndMinutes(totalOff),"Tracker is NOT counting time");
							failSound();
							Thread.sleep(1000);
						}
					} else {
						Logging.log("minutesOff="+minutesOff+
								",getIntegerCFGValue('Long-off')="+getIntegerCFGValue("Long-off"));
						if (minutesOff>=getIntegerCFGValue("Long-off")){
							failSoundPlayer.open(new File(Items.sGet("Sounds/police")));
							Logging.log("Entered a long off state");
						} else{

							failSoundPlayer.open(new File(Items.sGet("Sounds/Error")));
							Logging.log("Entered a short off state");
						}
						setTrayIcon(Items.sGet("Monitor/TrayIcon/Off"),
								"Monitored continous OFF time: "+toHoursAndMinutes(minutesOff)+
								", Total Off in this session: "+toHoursAndMinutes(totalOff));
						for (int i=0; i<3;i++){
							Sikuliz.notify("Monitored OFF time: "+toHoursAndMinutes(minutesOff)+ 
									", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+
									toHoursAndMinutes(totalOff),"Tracker is NOT counting time :(");
							failSound();
							Thread.sleep(1000);
						}
					}
					minutesOn=0;
					if (items!=null) {
						failSound();	
					}
					Logging.log("Monitored OFF time: "+toHoursAndMinutes(minutesOff++) +
							", Total On: "+toHoursAndMinutes(totalOn)+
							", Total Off: "+toHoursAndMinutes(totalOff++) );
				}else {
					//Logging.log("Tracker is counting time.");
					if (praying){
						praying=false;
					}
					//use sound 
					// use tray notifications
					minutesOff=0;
					setTrayIcon(Items.sGet("Monitor/TrayIcon/On"),
							"Monitored ON time: "+toHoursAndMinutes(minutesOn)+ 
							", Total On: "+toHoursAndMinutes(totalOn)+", Total Off: "+
									toHoursAndMinutes(totalOff));
					passSound();
					Logging.log("Monitored ON  time: "+toHoursAndMinutes(minutesOn++) + ", Total On: "+toHoursAndMinutes(totalOn++) +
							", Total Off: "+toHoursAndMinutes(totalOff));
					if ((minutesOn % getIntegerCFGValue("prayer_interval"))==0){
						Logging.log("GOTO Pray :)");
						for (int i=0; i<3;i++){
							Sikuliz.notify("GOTO Pray :)",getIntegerCFGValue("prayer_interval")+" minutes of work completed, go pray and thank Allah :)");
							passSound();
							playMp3(Items.sGet("Sounds/doorbell-1"));
							Thread.sleep(2000);
						}
						prayNotified=true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(60000);//one minute sleep
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) throws IOException, InterruptedException, BasicPlayerException, AWTException {
		Thread thread= new Thread(new TimeTrackerMonitor(),"Time Tracker Monitor");
		thread.start();
		while(thread.isAlive()){
			Thread.sleep(1000*60*60*24);
		}
		//thread.join(); //this is very bad cpu consuming way to wait a thread, results is a very bad thread performance
	}
}
