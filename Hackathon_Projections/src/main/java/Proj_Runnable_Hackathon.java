  	 	

import java.io.File;

import ij.IJ;



public class Proj_Runnable_Hackathon implements Runnable {

	public String foldername_save;
	public String current_folder;
	public String experimentfolder;
	public int projectionovernbtimepoins;
	
	@Override
	public void run() {
		
			IJ.log("run" + experimentfolder);
			String pathsave= foldername_save +File.separator+ "projections" + File.separator + current_folder;
			IJ.log("save to " + pathsave);
			//make folders
			File dir = new File(pathsave); 
			dir.mkdirs();
			File timestepfolder = new File(dir, "timesteps");
			timestepfolder.mkdir();
			
			//generate a projection class object containing the paths for saving the different projections
			proj_stack_hackathon d = new proj_stack_hackathon();
			d.parentfolderstring = experimentfolder;
			d.stepsize = projectionovernbtimepoins;
			
			d.foldernamesave_min= pathsave + File.separator + "min_proj.tif";
			d.foldernamesave_max= pathsave + File.separator + "max_proj.tif ";
			d.foldernamesave_std= pathsave + File.separator + "std_proj.tif ";
			d.foldernamesave_avg= pathsave + File.separator + "avg_proj.tif ";
			
			d.foldernamesave_stdtimestep=pathsave + File.separator + "timesteps" + File.separator + "selected_std_proj_";
			d.foldernamesave_maxtimestep=pathsave + File.separator + "timesteps" + File.separator + "selected_max_proj_";
			d.foldernamesave_avgtimestep=pathsave + File.separator + "timesteps" + File.separator + "selected_avg_proj_";
			d.foldernamesave_mintimestep=pathsave + File.separator + "timesteps" + File.separator  + "selected_min_proj_";
				
			d.makeminproj();	
	}
	
}
