package picture;


public class Main {
	
    public static void main(String[] args) {
    	String command, outputlocation;
    	char flipdirection;
    	Picture inputpic;
    	Picture[] inputpics;
    	int rotation;
    	
    	command = args[0];
    	System.out.println("command: " + command);
    	if (command.equals("invert")) {
    		inputpic = Utils.loadImage(args[1]);
    		outputlocation = args[2];
    		Utils.savePicture(invert(inputpic), outputlocation);
    		System.out.println("The picture was inverted");
    		
    	} else if (command.equals("grayscale")) {
    		inputpic = Utils.loadImage(args[1]);
    		outputlocation = args[2];
    		Utils.savePicture(grayscale(inputpic), outputlocation);
    		System.out.println("The picture was converted to grayscale");
    		
    	} else if (command.equals("rotate")) {
    		rotation = Integer.parseInt(args[1]);
    		inputpic = Utils.loadImage(args[2]);
    		outputlocation = args[3];
    		Utils.savePicture(rotate(inputpic, rotation), outputlocation);
    		System.out.println("The picture was rotated by " + rotation + " degrees");
    		
    	} else if (command.equals("flip")) {
    		flipdirection = args[1].charAt(0);
    		inputpic = Utils.loadImage(args[2]);
    		outputlocation = args[3];
    		Utils.savePicture(flip(inputpic, flipdirection), outputlocation);
    		System.out.println("The picture was flipped");
    		
    	} else if (command.equals("blend")) {
    		inputpics = new Picture[args.length-2];
    		for (int i=1; i < args.length-1; i++) {
    			inputpics[i-1] = Utils.loadImage(args[i]);
    		}
    		outputlocation = args[args.length-1];
    		Utils.savePicture(blend(inputpics), outputlocation);
    		System.out.println("The pictures were blended");
    		
    	} else if (command.equals("blur")) {
    		inputpic = Utils.loadImage(args[1]);
    		outputlocation = args[2];
    		Utils.savePicture(blur(inputpic), outputlocation);
    		System.out.println("The picture was blurred");
    		
    	} else {
    		System.out.println("Unrecognised command");
    	}
    }
    
    public static Picture invert(Picture image) {
    	Color pixel;
    	Picture invertedpicture = image;
    	
    	for(int x=0; x < image.getWidth(); x++) {
    		for(int y=0; y < image.getHeight(); y++) {
    			pixel = image.getPixel(x, y);
    			pixel.setRed(255 - pixel.getRed());
    			pixel.setGreen(255 - pixel.getGreen()); 
    			pixel.setBlue(255 - pixel.getBlue());
    			invertedpicture.setPixel(x, y, pixel);
    		}
    	}
    	
    	return invertedpicture;
    }
    
    public static Picture grayscale(Picture image) {
    	Color pixel;
    	int average;
    	Picture grayimage = image;
    	
    	for(int x=0; x < image.getWidth(); x++) {
    		for(int y=0; y < image.getHeight(); y++) {
    			pixel = image.getPixel(x, y);
    			average = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
    			pixel.setRed(average);
    			pixel.setGreen(average); 
    			pixel.setBlue(average);
    			grayimage.setPixel(x, y, pixel);
    		}
    	}
    	
    	return grayimage;
    }
    
    public static Picture rotate(Picture image, int rotation){
    	Color pixel;
    	Picture rotatedimage;
    	int height, width, midheight, midwidth;
    	int xposition, yposition;
    	height = image.getHeight();
    	width = image.getWidth();
    	midheight = height / 2;
    	midwidth = width / 2;
    	
    	if (rotation == 90 || rotation == 270) {
    		rotatedimage = Utils.createPicture(height, width);
    	} else if (rotation == 180) {
    		rotatedimage = Utils.createPicture(width, height);
    	} else {
    		System.out.println("The rotation value passed is invalid");
    		return image;
    	}
    	

    	for(int x=0; x < width; x++) {
    		for(int y=0; y < height; y++) {
    			pixel = image.getPixel(x, y);
    			
    			if (rotation == 90) {
    				xposition = midwidth + (midheight - y) - 1;
    				yposition = midheight + (x - midwidth);
    			} else if (rotation == 180) {
    				xposition = width - x - 1;
    				yposition = height - y - 1;
    			} else {
    				xposition = midwidth - (midheight - y);
    				yposition = midheight - (x - midwidth) - 1;    				
    			}
    			rotatedimage.setPixel(xposition, yposition, pixel);
    		}
    	}
    	
    	return rotatedimage;    	
    }
    
    public static Picture flip(Picture image, char direction) {
    	Color pixel;
    	Picture flippedimage;
    	int height, width;
    	int xposition, yposition;
    	
    	height = image.getHeight();
    	width = image.getWidth();
    	if (!(direction == 'H' || direction == 'h' || direction == 'V' || direction == 'v')) {
    		System.out.println("The direction you entered is invalid");
    		return image;
    	}
    	
    	flippedimage = Utils.createPicture(width, height);
    	
    	for(int x=0; x < width; x++) {
    		for(int y=0; y < height; y++) {
    			pixel = image.getPixel(x, y);
    			
    			if (direction == 'H' || direction == 'h') {
    				xposition = width - x - 1;
    				yposition = y;
    			} else {
    				xposition = x;
    				yposition = height - y - 1;
    			}
    			flippedimage.setPixel(xposition, yposition, pixel);
    			
    		}
    	}
    	
    	return flippedimage;
    }
    
    public static Picture blend(Picture[] imagelist) {
    	Color pixel;
    	int[] dimensions;
    	int redtotal, greentotal, bluetotal, avgred, avggreen, avgblue, imagelistlength;
    	Picture blendedimage;
    	
    	dimensions = getBestDimensions(imagelist);
    	blendedimage = Utils.createPicture(dimensions[0], dimensions[1]);
    	imagelistlength = imagelist.length;
    	
    	
    	for(int x=0; x < dimensions[0]; x++) {
    		for(int y=0; y < dimensions[1]; y++) {
    			redtotal = 0;
    			greentotal = 0;
    			bluetotal = 0;
    			for(Picture currentimage: imagelist) {
    				pixel = currentimage.getPixel(x, y);
    				redtotal += pixel.getRed();
    				greentotal += pixel.getGreen();
    				bluetotal += pixel.getBlue();
    			}
    			
    			avgred = redtotal / imagelistlength;
    			avggreen = greentotal / imagelistlength;
    			avgblue = bluetotal / imagelistlength;
    			
    			blendedimage.setPixel(x, y, new Color(avgred, avggreen, avgblue)); 			
    		}
    	}
    	
    	return blendedimage;
    	
    }
    
    public static Picture blur(Picture image) {
    	Color[] pixels;
    	Picture blurredimage;
    	int height, width;
    	
    	height = image.getHeight();
    	width = image.getWidth();
    	
    	blurredimage = Utils.createPicture(width, height);
    	for(int x=0; x < width; x++) {
    		for(int y=0; y < height; y++) {
    			
    			if (!(0 < x && x < (width - 1) && 0 < y && y < (height - 1))) {
    				blurredimage.setPixel(x, y, image.getPixel(x, y));
    			} else {
	    			pixels = new Color[]{
	    					image.getPixel(x-1, y-1),
	    					image.getPixel(x, y-1),
	    					image.getPixel(x+1, y-1),
	    					image.getPixel(x-1, y),
	    					image.getPixel(x, y),
	    					image.getPixel(x+1, y),
	    					image.getPixel(x-1, y+1),
	    					image.getPixel(x, y+1),
	    					image.getPixel(x+1, y+1)
	    					};
	    					
	    					blurredimage.setPixel(x, y, averagePixels(pixels));
	    			}
    			}
    		}
    	
    	return blurredimage;
    }
    
    public static Color averagePixels(Color[] pixels) {
    	int r, g, b;
    	r = 0;
    	g = 0;
    	b = 0;
    	Color averagepixel;
    	for (Color pixel : pixels) {
    		r += pixel.getRed();
    		g += pixel.getGreen();
    		b += pixel.getBlue();
    	}
    	
    	r /= pixels.length;
    	g /= pixels.length;
    	b /= pixels.length;
    	
    	averagepixel = new Color(r, g, b);
    	return averagepixel;
    }
    
    public static int[] getBestDimensions(Picture[] imagelist) {
    	int smallestwidth, smallestheight, currentwidth, currentheight;
    	int[] bestdimensions;
    	
    	smallestwidth = imagelist[0].getWidth();
    	smallestheight = imagelist[0].getHeight();
    	for (Picture currentimage: imagelist) {
    		currentwidth = currentimage.getWidth();
    		currentheight = currentimage.getHeight();
    		
    		if (currentwidth < smallestwidth) {
    			smallestwidth = currentwidth;
    		}
    		
    		if (currentheight < smallestheight) {
    			smallestheight = currentheight;
    		}
    	}
    	bestdimensions = new int[]{smallestwidth, smallestheight};
    	return bestdimensions;
    }
    	
}
