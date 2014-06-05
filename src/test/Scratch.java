package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Scratch {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file=new File(".");
		System.out.println("Absolute parent: "+file.getParent());
		System.out.println("Absolute getAbsolutePath: "+file.getAbsolutePath());
		System.out.println("Absolute getCanonicalPath: "+file.getCanonicalPath());
		System.out.println("Absolute getPath: "+file.getPath());
		System.out.println("Absolute File.pathSeparator: "+File.pathSeparator);
		System.out.println("Absolute file.toPath().toAbsolutePath().subpath(0, file.toPath().toAbsolutePath().getNameCount()-1): "+Paths.get(file.getCanonicalPath()).subpath(0, 2));
		
		
	}

}
