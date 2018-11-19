import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import ij.process.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class proj_stack_hackathon {
	

public String parentfolderstring;
public int stepsize;
public String foldernamesave_mintimestep;
public String foldernamesave_avgtimestep;
public String foldernamesave_maxtimestep;
public String foldernamesave_stdtimestep;
public String foldernamesave_max;
public String foldernamesave_std;
public String foldernamesave_avg;
public String foldernamesave_min;
public int height;
public int width;

	
	
	
	public static void main(String[] args2) {
		new ImageJ();
		
		String experimentfolder= "D:\\hackathon\\161225_m634_m634_m634_m634_series4";
		String pathsave = "D:\\hackathon\\simpledatasave";
		
		//make folders
		File dir = new File(pathsave); 
		dir.mkdirs();
		File timestepfolder = new File(dir, "timesteps");
		timestepfolder.mkdir();
		
		//run & set up code on test data
		proj_stack_hackathon test = new proj_stack_hackathon();

		test.stepsize =50;
		test.parentfolderstring = experimentfolder;
		test.foldernamesave_mintimestep="D:\\hackathon\\simpledatasave"+ File.separator +"timesteps" + File.separator  + "selected_min_proj_";
		test.foldernamesave_maxtimestep="D:\\hackathon\\simpledatasave"+ File.separator+ "timesteps" + File.separator  + "selected_max_proj_";
		test.foldernamesave_stdtimestep="D:\\hackathon\\simpledatasave"+ File.separator +"timesteps" + File.separator  + "selected_std_proj_";
		test.foldernamesave_avgtimestep="D:\\hackathon\\simpledatasave"+ File.separator +"timesteps" + File.separator  + "selected_avg_proj_";
		test.foldernamesave_max="D:\\hackathon\\simpledatasave"+ File.separator + "max_proj.tif ";
		test.foldernamesave_std="D:\\hackathon\\simpledatasave"+ File.separator + "std_proj.tif ";
		test.foldernamesave_min="D:\\hackathon\\simpledatasave" + File.separator + "min_proj.tif ";
		test.foldernamesave_avg="D:\\hackathon\\simpledatasave" + File.separator + "avg_proj.tif ";
		
		test.makeminproj();
		
	}
	
	public void makeminproj(){
	
		System.out.println("start processing of " + parentfolderstring);
		
		//initialize Projectors: min & max projectors
		MinProjector_Hackathon minprojector = new MinProjector_Hackathon(); //initialize projector	
		MinProjector_Hackathon minsmallwindowprojector = new MinProjector_Hackathon(); //initialize projector
		MaxProjector_Hackathon maxprojector = new MaxProjector_Hackathon(); //initialize projector	
		MaxProjector_Hackathon maxsmallwindowprojector = new MaxProjector_Hackathon(); //initialize projector
		
		//check directory for all .tif image files. Get a list of them out: planelist and the name of the condition 
		File parentfolder = new File(parentfolderstring);
		String[] planelist =  filter_end(parentfolder.list(), ".tif");Arrays.sort(planelist);
		String conditionname = planelist[0].substring(0,planelist[0].length()-5);	
				
		//get parameters for pixelarrays
		String path1 = parentfolderstring + File.separator + conditionname + "1" + ".tif";
		ImagePlus tmpimg_start = IJ.openImage(path1);
		int width_condition=tmpimg_start.getWidth();
		int height_condition = tmpimg_start.getHeight();
		
		//initialize arrays and filelists for average computation
		double[] sumarray = new double[width_condition * height_condition];
		double[] smallsumarray = new double[width_condition * height_condition];		
		ArrayList<String> smallwindow_planelist = new ArrayList<String>();
		ArrayList<String> all_planelist = new ArrayList<String>();

		///---do processing over movie
		for(int i_plane =0; i_plane<planelist.length;i_plane++){
			
			//open current image (timepoint)
			String path = parentfolderstring + File.separator + conditionname + i_plane + ".tif";
			//System.out.println("opening " + path);
			smallwindow_planelist.add(path);
			all_planelist.add(path);
			
			ImagePlus tmpimg = IJ.openImage(path);
					
			//get pixel array in short format (data is in 8bit format and is saved in short for easier processing)
			byte[] currentpixelarray = (byte[]) tmpimg.getProcessor().getPixels();
			short[] shortcurrentpixelarray = new short[width_condition * height_condition];
					
			for(int i_pixel=0;i_pixel<currentpixelarray.length;i_pixel++) {
				int value = ((int) currentpixelarray[i_pixel]) & 0xff;
				shortcurrentpixelarray[i_pixel]= (short) value;
			}
					
			//generate average
			for(int i_pixel=0;i_pixel<currentpixelarray.length;i_pixel++){
				sumarray[i_pixel] =  sumarray[i_pixel] +(double) shortcurrentpixelarray[i_pixel]/(double) planelist.length;
				smallsumarray[i_pixel] = smallsumarray[i_pixel] + (double) shortcurrentpixelarray[i_pixel]/ (double) stepsize;					
			}
					
			//as you want to also generate projections over a defined number of frames (nb = stepsize), do it here:
			if (i_plane % stepsize == 0 && i_plane>2) {
				System.out.println("do projection over small window " + i_plane);
				
				//do min-max-avg projection and save them to the folder
				ImageProcessor minintermediate = minsmallwindowprojector.getProjection();
				ImageProcessor maxintermediate = maxsmallwindowprojector.getProjection();
				ImagePlus min_projection_small = new ImagePlus("tmp", minintermediate);
				ImagePlus max_projection_small = new ImagePlus("tmp", maxintermediate);
				ImagePlus avg_projection_small = doublearray_to8bitimage(smallsumarray, width_condition, height_condition, "avg_timewindow");
				String timestepnamemin = foldernamesave_mintimestep + IJ.pad(i_plane, 4) + ".tif";
				String timestepnamemax = foldernamesave_maxtimestep + IJ.pad(i_plane, 4) + ".tif";
				String timestepnameavg = foldernamesave_avgtimestep + IJ.pad(i_plane, 4) + ".tif";
				IJ.save(min_projection_small, timestepnamemin);
				IJ.save(max_projection_small, timestepnamemax);
				IJ.save(avg_projection_small, timestepnameavg);
				
				//generate std projection
				String[] currentplanes = (String[]) smallwindow_planelist.toArray(new String[0]);
				STDProjector_Hackathon getstdprojection = new STDProjector_Hackathon();
				ImagePlus std_projection_small = getstdprojection.run(currentplanes, smallsumarray, width_condition, height_condition);
				
				String timestepnamestd = foldernamesave_stdtimestep + IJ.pad(i_plane, 4) + ".tif";
				IJ.save(std_projection_small, timestepnamestd);
				
				
				//release resources and reset projectors for small windows
				min_projection_small.flush();
				max_projection_small.flush();
				avg_projection_small.flush();
				minsmallwindowprojector.reset();
				maxsmallwindowprojector.reset();
				smallwindow_planelist.clear();
				
				for(int i_pixel = 0; i_pixel<smallsumarray.length;i_pixel++) {
					smallsumarray[i_pixel]=0;
				}		
			}
					
			//- do projections over the whole time-lapse
			
			//max-min projection
			maxsmallwindowprojector.add(tmpimg.getProcessor().duplicate());
			maxprojector.add(tmpimg.getProcessor().duplicate());
			minsmallwindowprojector.add(tmpimg.getProcessor().duplicate());
			minprojector.add(tmpimg.getProcessor().duplicate());
			
			//release resources
			tmpimg.flush();				
			}
			
		//---save projections over time-lapse and release resources
		ImagePlus min_final = new ImagePlus("min_projection", minprojector.getProjection());
		IJ.save(min_final, foldernamesave_min);
		min_final.flush();
		

		ImagePlus max_final = new ImagePlus("max_projection", maxprojector.getProjection());
		IJ.save(max_final, foldernamesave_max);
		max_final.flush();
		
		ImagePlus avg_final = doublearray_to8bitimage(sumarray, width_condition,  height_condition, "avg_final");
		IJ.save(avg_final, foldernamesave_avg);
		
		///------------now calculate STD given the above calculated average
		String[] allplanes = (String[]) all_planelist.toArray(new String[0]);
		STDProjector_Hackathon getstdprojection = new STDProjector_Hackathon();
		ImagePlus std_projection = getstdprojection.run(allplanes, sumarray, width_condition, height_condition);
		IJ.save(std_projection, foldernamesave_std);
	
		std_projection.flush();
		avg_final.flush();
	
		System.out.println("Processing done of " + parentfolderstring);
}
			


private static String[] filter_end(String[] in, String pattern) {
	ArrayList<String> all = new ArrayList<String>(in.length);
	for(String s : in)
		
		if(s.endsWith(pattern))
			all.add(s);
	String[] out = new String[all.size()];
	all.toArray(out);
	return out;
}


public ImagePlus doublearray_to8bitimage(double[] array, int width, int height, String imagestring) {
	
	FloatProcessor newimage = new FloatProcessor(width, height, array);
	ByteProcessor data8bit = newimage.convertToByteProcessor(false);
	ImagePlus show8bit = new ImagePlus("8bit", data8bit);
	//show8bit.show();
	return show8bit;
}


}


