package timeTrackerMonitor;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.sikuli.script.Key;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import com.sun.jna.platform.KeyboardUtils;

import javazoom.jlgui.basicplayer.BasicPlayerException;
import common.*;

public class AutoLock extends Sikuliz implements Runnable{
	Items items=new Items("c:\\autolock\\resources");

	public AutoLock() throws BasicPlayerException, IOException, AWTException {
		super();
		ExtractionDir="c:\\AutoLock";
		Logging.setLogFile("c:\\autolock\\log\\suspect.log");
		failSoundPlayer.open(new File(Items.sGet("Sounds/police")));
		//Logging.saveToFile=false;
		CFG_PATH="c:\\autolock\\locker.cfg";
		setSim(0.97);
	}

	public static void lockScreen() throws Exception{
		Screen screen=new Screen(0);
			
			failSoundPlayer.play();
			if (isWindows()){
				Logging.log("Attempting to lock screen ... ");
				Runtime.getRuntime().exec("rundll32 user32.dll,LockWorkStation");
			} else if (isUnix()){
				Logging.log("Not implemented yet for unix based OS.");
				
			} else if(isMac()){
				Logging.log("Not implemented yet for Mac OS.");
			} else {
				screen.keyDown(Key.WIN);
				screen.keyUp(Key.WIN);
				Thread.sleep(10);
				screen.keyDown(Key.TAB);
				screen.keyUp(Key.TAB);
				Thread.sleep(10);
				screen.keyDown(Key.UP);
				screen.keyUp(Key.UP);
				Thread.sleep(10);
				screen.keyDown(Key.RIGHT);
				screen.keyUp(Key.RIGHT);
				Thread.sleep(10);
				screen.type("o");
			}
			
			
	}

	public static void main(String[] args) throws Exception{
		AutoLock al=new AutoLock();
		al.run();
	}
	//
	@Override
	public void run() {
		try {
			items=new Items("c:\\autolock\\resources");
			setTrayIcon(Items.sGet("monitor/trayicon/on"), "autoLock activated");
			playMp3(Items.sGet("sounds/police"));
			while (true){
				items=new Items("c:\\autolock\\resources");
				setTrayIcon(Items.sGet("monitor/trayicon/searching"), "autoLock activated");
				if (FindThread.anyExist(Items.sGetAll("monitor/suspect"))!=null){
					setTrayIcon(Items.sGet("monitor/trayicon/suspectFound"), "a suspeted attept was catched :D :D :D");
					//Logging.saveToFile=true;
					Logging.log("a suspeted attept was catched :D :D :D");
					//Logging.saveToFile=false;
					try {
						lockScreen();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					setTrayIcon(Items.sGet("monitor/trayicon/on"), "autoLock activated");
				}
				Thread.sleep(getIntegerCFGValue("check_interval"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BasicPlayerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
