# Stack Robust Automatic Threshold

## Introduction

The Robust Automatic Threshold (RATS) plugin, based on the algorithm of [Wilkinson et al.][1], has been developed for the thresholding of images with variable background (see: [2], [3]). Unfortunately the original plugin can only handle single images and not a stack of images.    
The here proposed plugin slightly enhances the original one to circumvent this restriction.

[1]: http://www.sciencedirect.com/science/article/pii/S1077316998904786
[2]: http://fiji.sc/RATS:_Robust_Automatic_Threshold_Selection
[3]: http://rsb.info.nih.gov/ij/plugins/rats/

The `RATS_For_Plugin.java` file is a copy of the original `RATS_.java` file containing an additional public function which returns the imageprocessor:

``` java
public ImageProcessor exec(ImageProcessor ip, int sigma, int lambda, int leaf) {
  // 0 - Check validity of parameters
  if (null == ip) return null;
  this.ip = ip;
  this.dim[0] = ip.getWidth();
  this.dim[1] = ip.getHeight();
  this.sigma = sigma;
  this.lambda = lambda;
  this.minSzPx[0] = leaf;
  this.minSzPx[1] = minSzPx[0];
  this.qtTop = new RATSQuadtree(this.dim, this.minSzPx);
  this.qtBot = new RATSQuadtree(this.dim, this.minSzPx);
  fillArrays();
  gradientMHFW();
  this.topIp = new FloatProcessor(top);
  qtTop.fillWithSums(topIp);
  this.botIp = new FloatProcessor(bot);
  qtBot.fillWithSums(botIp);
  if (bVerbose) tock("  FillWithSums:");
  float[][] thresh = rats();
  thresh = resize(thresh);
  this.threshIp = new FloatProcessor(thresh);
  return threshIp.convertToByte(true);
}
```
