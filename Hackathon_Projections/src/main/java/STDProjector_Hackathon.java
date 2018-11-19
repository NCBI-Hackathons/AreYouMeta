import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;

public class STDProjector_Hackathon {

	public ImagePlus run(String[] planelist, double[] sumarray, int width, int height) {
		System.out.println("list for std " + Arrays.toString(planelist));
		double[] finalvalue = new double[width * height];
		for(int i_plane =0; i_plane<planelist.length;i_plane++){
			
			ImagePlus tmpimg = IJ.openImage(planelist[i_plane]);
			
			//get pixel array in double format (data is in 8bit format and is saved in short for easier processing)
			byte[] currentpixelarray = (byte[]) tmpimg.getProcessor().getPixels();
	
			double[] doublecurrentpixelarray = new double[width * height];	
			for(int i_pixel=0;i_pixel<currentpixelarray.length;i_pixel++) {
				int value = ((int) currentpixelarray[i_pixel]) & 0xff;
				doublecurrentpixelarray[i_pixel]= (double) value;
			}
			
			//get sum of squared differences
			for(int i_pixel=0;i_pixel<currentpixelarray.length;i_pixel++) {
				finalvalue[i_pixel]= finalvalue[i_pixel]+ (sumarray[i_pixel]-doublecurrentpixelarray[i_pixel])*(sumarray[i_pixel]-doublecurrentpixelarray[i_pixel]);
			}
			
		}
		
		//divide by (N-1)
		for(int i_pixel=0;i_pixel<finalvalue.length;i_pixel++) {
			finalvalue[i_pixel]=finalvalue[i_pixel]/(double) (planelist.length-1);
		}
		
		//take square root to get standard deviation for every pixel
		for(int i_pixel=0;i_pixel<finalvalue.length;i_pixel++) {
			finalvalue[i_pixel]=Math.sqrt(finalvalue[i_pixel]);
		}
		
		FloatProcessor newimage = new FloatProcessor(width, height, finalvalue);
		ImagePlus float_image = new ImagePlus("float", newimage);	
		
		return float_image;
	}

	
	
}
