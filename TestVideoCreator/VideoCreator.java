import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class VideoCreator {

    
	public static void main(String[] args) throws IOException {
		String output_dir = args[0];
		String fps  = args[1];
		String resolution  = args[2];
		String title  = args[3];
		String duration = args[4];
		int width = 480;
		int height = 320;

		if(resolution.contentEquals("1080p")) {
			width = 1920;
			height = 1080;
		}
		else if(resolution.contentEquals("720p")) {
			width = 1280;
			height = 720;
		}
		else if(resolution.contentEquals("480p")) {
			width = 640;
			height = 480;
		}
		else if(resolution.contentEquals("360p")) {
			width = 640;
			height = 360;
		}
		else if(resolution.contentEquals("240p")) {
			width = 426;
			height = 240;
		}

		int iFps = Integer.parseInt(fps);
		int count = Integer.parseInt(duration) * iFps;
		File outDir = getOutpuDir(output_dir);

		Font font = new Font("Arial", Font.PLAIN, 48);
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		
		Image logo = ImageIO.read(new File("logo2.png"));
		logo = logo.getScaledInstance(width, height,Image.SCALE_DEFAULT);
		
		System.out.println("Writing frames...");
		for(int i=0; i<count;i++) {			
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, width, height);
			
			g2d.drawImage(logo, 0, 0, null);			

			g2d.setColor(new Color(0f, 0f, 0f, .8f));
			g2d.drawString(title, 0, 1*fm.getHeight());
			g2d.drawString("frame: "+i+"/"+count, 0, 2*fm.getHeight());
			int time = i*1000/iFps;
			g2d.drawString("time (ms):"+time, 0, 3*fm.getHeight());
			ImageIO.write(img, "png", new File(outDir, i+".png"));

			printProcess(i+1, count);
		}
		g2d.dispose();
		System.out.println("Writing frames...[done]");
	}

	private static void printProcess(int index, int total) {
		int pointNo = 10;
		String process = "[";
		int done = index*pointNo/total;
		int remaining = pointNo - done;
		for(int i=0;i<done;i++) {process+="X";};
		for(int i=0;i<remaining;i++) {process+=".";};
		process += "]"+"  ("+index+"/"+total+")";
		System.out.print("\r"+process);
	}

	private static File getOutpuDir(String outDir) {
		File f = new File(outDir+"/images");
		if(!f.exists()) {
			f.mkdirs();
		}
		return f;
	}
}
