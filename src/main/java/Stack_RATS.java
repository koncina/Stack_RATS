import ij.*;
import ij.gui.*; 
import ij.process.*;
import ij.plugin.*;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.RankFilters;

/**
	* Stack_RATS (Stack Robust Automatic Threshold Selection) ImageJ plugin.
	* This plugin modifies the RATS plugin proposed by Ben Tupper allowing
	* it to easily handle a stack of images.
	* Please refer to the original RATS plugin for detailed informations.
	*
	* Plugin Author: Eric Koncina (mail@koncina.eu)
**/

public class Stack_RATS implements PlugIn {
	public void run(String arg) {
		int noise = 25;
		int lambda = 3;
		int leaf = 0;
		ImagePlus imp = IJ.getImage();
		if (imp.getHeight() < imp.getWidth()) leaf = (int) Math.round(imp.getHeight() / 5.0);
		else leaf = (int) Math.round(imp.getWidth() / 5.0);
		GenericDialog gd = new GenericDialog("Options");
		gd.addNumericField("Noise threshold: ", noise, 0);
		gd.addNumericField("Lambda factor: ", lambda, 0);
		gd.addNumericField("Minimum leaf size: ", leaf, 0);
		gd.showDialog();
		if (gd.wasCanceled()){
			IJ.log("Canceled");
			return;
			}
		noise = (int)gd.getNextNumber();
		lambda = (int)gd.getNextNumber();
		leaf = (int)gd.getNextNumber();
		IJ.log("Starting to apply RATS with noise=" + noise + ", lambda=" + lambda + ", leaf=" + leaf);
		Apply_RATS(imp, noise, lambda, leaf);
	}
	
	void Apply_RATS(ImagePlus imp, int noise, int lambda, int leaf) {
		long startTime = System.currentTimeMillis();
		IJ.showStatus("Applying RATS...");
		ImageStack istack = imp.getStack();
		ImageProcessor ip;
		ImageStack ostack = new ImageStack(imp.getWidth(), imp.getHeight()); 
		RankFilters rf = new RankFilters();
		RATS_For_Plugin rats = new RATS_For_Plugin();
		for (int i = 1; i < imp.getNFrames() + 1; i++) {
			for (int j = 1; j < imp.getNSlices() + 1; j++) {
				int index = imp.getStackIndex(imp.getC(), j, i);
				ip = istack.getProcessor(index).duplicate();
				ostack.addSlice(istack.getSliceLabel(index), rats.exec(ip, noise, lambda, leaf));
			}
		}
		ImagePlus imp2 = new ImagePlus("", ostack);
		imp2.setDimensions(1, imp.getNSlices(), imp.getNFrames());
		imp2.setOpenAsHyperStack(true);
		imp2.setTitle(imp.getTitle() + "_RATS");
		imp2.show();
		IJ.showStatus("");
		long elapsed = System.currentTimeMillis() - startTime;
		IJ.log("  Finished the task in " + elapsed + " ms");
	}
}