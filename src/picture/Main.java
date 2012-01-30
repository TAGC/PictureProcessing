package picture;

import java.util.Arrays;


public class Main {
	
	private static String[] commandlist = {"map", "invert", "grayscale", "rotate", "flip", "blend",
	                                "blur", "mosaic"};
	private static String[] arg1commands = {"invert", "grayscale", "blur"};
	private static String[] arg2commands = {"rotate", "flip"};
	private static String[] argncommands = {"blend", "mosaic"};
	
    public static void main(String[] args) {
    	int instructioncount;
    	Picture finalpic;
    	String outputlocation;
    	String[][] instructions;
    	
    	instructioncount = 0;
    	for(String arg : args) {
    		if (contains(commandlist, arg) && !arg.equals("map")) {
    			instructioncount++;
    		}
    	}
    	
    	instructions = genInstructions(instructioncount, args);
    	outputlocation = args[args.length-1];  
    	finalpic = processInstructions(instructions);
    	Utils.savePicture(finalpic, outputlocation);
    	System.out.println("Processing complete");
    }
    
    public static boolean contains(String[] xs, String x) {
    	for(String word : xs) {
    		if (word.equals(x)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public static String[][] genInstructions(int instructioncount, String[] args) {
    	String[][] instructions = new String[instructioncount][];
    	String[] instruction;
    	int instructionlength, instructindex;
    	String command;
    	
    	instructindex = 0;
    	
    	for(int i=args.length-1; i >= 0; i--) {
    		
    		//deal with mapping instructions
    		if (i > 0 && args[i-1].equals("map")) {
    			command = args[i];
    			if (contains(arg1commands, command)) {
    				instructionlength = 2;
    			} else if (contains(arg2commands, command)) {
    				instructionlength = 3;
    			} else {
    				System.out.println("Error - map applied to unmappable command");
    				return null;
    			}
    			
    			while (!contains(commandlist, args[i-1+instructionlength]) && i-1+instructionlength < args.length-1) {
    				instructionlength++;
    			}
    			instruction = new String[instructionlength];
    			for(int j=0; j < instructionlength; j++) {
    				instruction[j] = args[i-1+j];
    			}
    			instructions[instructindex] = instruction;
    			instructindex++;
    		}
    		
    		//deal with 1-argument functions
    		else if (contains(arg1commands, args[i])) {
    			if (!contains(commandlist, args[i+1])) {
    				instruction = new String[]{args[i], args[i+1]};
    			} else {
    				instruction = new String[]{args[i]};
    			}
    			instructions[instructindex] = instruction;
    			instructindex++;
    		}
    		
    		//deal with 2-argument functions
    		else if (contains(arg2commands, args[i]))  {
    			if (!contains(commandlist, args[i+2])) {
    				instruction = new String[]{args[i], args[i+1], args[i+2]};
    			} else {
    				instruction = new String[]{args[i], args[i+1]};
    			}
    			instructions[instructindex] = instruction;
    			instructindex++;
    		}
    		
    		//deal with list-argument functions
    		else if (contains(argncommands, args[i])) {
    			command = args[i];
    			if (command.equals("blend")) {
    				instructionlength = 1;
    			} else {
    				instructionlength = 2;
    			}
    			
    			while (!contains(commandlist, args[i+instructionlength]) && i+instructionlength < args.length-1) {
    				instructionlength++;
    			}
    			
    			instruction = new String[instructionlength];
    			for(int j=0; j < instructionlength; j++) {
    				instruction[j] = args[i+j];
    			}
    			instructions[instructindex] = instruction;
    			instructindex++;
    		}
    	}
    	instructions = reverseArray(instructions);
    	return instructions;
    }
    
    public static String[][] reverseArray(String[][] xs) {
    	String[][] newarray;
    	newarray = new String[xs.length][];
    	for(int i=0; i < xs.length; i++) {
    		newarray[xs.length-i-1] = xs[i];
    	}
    	
    	return newarray;
    }
    
    public static Picture processInstructions(String[][] instructions) {
    	Picture finalpic;
    	finalpic = processHelper(0, instructions)[0];
    	return finalpic;
    }
    
    public static boolean determineIfBaseCase(String[] instruction) {
    	String suffix;
    	String[] picsuffixes;
    	
    	picsuffixes = new String[]{"jpg", "png"};
    	
    	for(int i=0; i < instruction.length; i++) {
    		if (instruction[i].length() >= 3) {
	    		suffix = instruction[i].substring(instruction[i].length()-3);
	    		if (contains(picsuffixes, suffix)) {
	    			return true;
	    		}
    		}
    	}
    	return false;
    }
    
    public static Picture[] processHelper(int instructionindex, String[][] instructions) {
    	boolean basecase;
    	String[] current_instruction;
    	String command, input;
    	Picture[] resultpics;
    	Picture currentpic;
    	
    	current_instruction = instructions[instructionindex];
    	basecase = determineIfBaseCase(current_instruction);
    	
    	//process map instructions
    	if (current_instruction[0].equals("map")) {
    		command = current_instruction[1];
    		if (command.equals("invert")) {
    			if (basecase) {
    				resultpics = new Picture[current_instruction.length-2];
	    			for (int i=2; i < current_instruction.length; i++) {
	    				input = current_instruction[i];
	    				currentpic = invert(Utils.loadImage(input));
	    				resultpics[i-2] = currentpic;
	    			}
    			} else {
    				resultpics = new Picture[instructions.length-instructionindex-1];
	    			for (int i=instructionindex+1; i < instructions.length; i++) {
		    			currentpic = invert(processHelper(i, instructions)[0]);
		    			resultpics[i-instructionindex-1] = currentpic;
		    		}
    			}
    			return resultpics;
    			
    		} else if (command.equals("grayscale")) {
    			if (basecase) {
    				resultpics = new Picture[current_instruction.length-2];
	    			for (int i=2; i < current_instruction.length; i++) {
	    				input = current_instruction[i];
	    				currentpic = grayscale(Utils.loadImage(input));
	    				resultpics[i-2] = currentpic;
	    			}
    			} else {
    				resultpics = new Picture[instructions.length-instructionindex-1];
	    			for (int i=instructionindex+1; i < instructions.length; i++) {
		    			currentpic = grayscale(processHelper(i, instructions)[0]);
		    			resultpics[i-instructionindex-1] = currentpic;
		    		}
    			}
    			return resultpics;
    			
    		} else if (command.equals("rotate")) {
    			int rotation;
    			rotation = Integer.parseInt(current_instruction[2]);
    			if (basecase) {
    				resultpics = new Picture[current_instruction.length-3];
	    			for (int i=3; i < current_instruction.length; i++) {
	    				input = current_instruction[i];
	    				currentpic = rotate(Utils.loadImage(input), rotation);
	    				resultpics[i-3] = currentpic;
	    			}
    			} else {
    				resultpics = new Picture[instructions.length-instructionindex-1];
	    			for (int i=instructionindex+1; i < instructions.length; i++) {
		    			currentpic = rotate(processHelper(i, instructions)[0], rotation);
		    			resultpics[i-instructionindex-1] = currentpic;
		    		}
    			}
    			return resultpics;
    			
    		} else if (command.equals("flip")) {
    			char flipdirection;
    			flipdirection = current_instruction[2].charAt(0);
    			if (basecase) {
    				resultpics = new Picture[current_instruction.length-3];
	    			for (int i=3; i < current_instruction.length; i++) {
	    				input = current_instruction[i];
	    				currentpic = flip(Utils.loadImage(input), flipdirection);
	    				resultpics[i-3] = currentpic;
	    			}
    			} else {
    				resultpics = new Picture[instructions.length-instructionindex-1];
	    			for (int i=instructionindex+1; i < instructions.length; i++) {
		    			currentpic = flip(processHelper(i, instructions)[0], flipdirection);
		    			resultpics[i-instructionindex-1] = currentpic;
		    		}
    			}
    			return resultpics;
    			
    		} else if (command.equals("blur")) {
    			if (basecase) {
    				resultpics = new Picture[current_instruction.length-2];
	    			for (int i=2; i < current_instruction.length; i++) {
	    				input = current_instruction[i];
	    				currentpic = blur(Utils.loadImage(input));
	    				resultpics[i-2] = currentpic;
	    			}
    			} else {
    				resultpics = new Picture[instructions.length-instructionindex-1];
	    			for (int i=instructionindex+1; i < instructions.length; i++) {
		    			currentpic = blur(processHelper(i, instructions)[0]);
		    			resultpics[i-instructionindex-1] = currentpic;
		    		}
    			}
    			return resultpics;
    			
    		} else {
    			return null;
    		}

    	}

    	//process invert instructions
    	else if (current_instruction[0].equals("invert")) {
    		if (basecase) {
    			currentpic = invert(Utils.loadImage(current_instruction[1]));
    		} else {
    			currentpic = invert(processHelper(instructionindex+1, instructions)[0]);
    		}
    		resultpics = new Picture[]{currentpic};
    		return resultpics;
    	}

    	//process grayscale instructions
    	else if (current_instruction[0].equals("grayscale")) {
    		if (basecase) {
    			currentpic = grayscale(Utils.loadImage(current_instruction[1]));
    		} else {
    			currentpic = grayscale(processHelper(instructionindex+1, instructions)[0]);
    		}
    		resultpics = new Picture[]{currentpic};
    		return resultpics;
    	}

    	//process rotate instructions
    	else if (current_instruction[0].equals("rotate")) {
    		int rotation;
    		rotation = Integer.parseInt(current_instruction[1]);
    		if (basecase) {
    			currentpic = rotate(Utils.loadImage(current_instruction[2]), rotation);
    		} else {
    			currentpic = rotate(processHelper(instructionindex+1, instructions)[0], rotation);
    		}
    		resultpics = new Picture[]{currentpic};
    		return resultpics;
    	}

    	//process flip instructions
    	else if (current_instruction[0].equals("flip")) {
    		char flipdirection;
    		flipdirection = current_instruction[1].charAt(0);
    		if (basecase) {
    			currentpic = flip(Utils.loadImage(current_instruction[2]), flipdirection);
    		} else {
    			currentpic = flip(processHelper(instructionindex+1, instructions)[0], flipdirection);
    		}
    		resultpics = new Picture[]{currentpic};
    		return resultpics;
    	}

    	//process blend instructions
    	else if (current_instruction[0].equals("blend")) {
    		Picture[] temppicarray;
    		if (basecase) {
    			temppicarray = new Picture[current_instruction.length-1];
    			for(int i=1; i < current_instruction.length; i++) {
    				temppicarray[i-1] = Utils.loadImage(current_instruction[i]);
    			}
    			currentpic = blend(temppicarray);
    		} else {
    			currentpic = blend(processHelper(instructionindex+1, instructions));
    		}
    		resultpics = new Picture[]{currentpic};
    		return resultpics;
    	}

    	//process blur instructions
    	else if (current_instruction[0].equals("blur")) {
    		if (basecase) {
    			currentpic = blur(Utils.loadImage(current_instruction[1]));
    		} else {
    			currentpic = blur(processHelper(instructionindex+1, instructions)[0]);
    		}
    		resultpics = new Picture[]{currentpic};
    		return resultpics;
    	}

    	//process mosaic instructions
    	else if (current_instruction[0].equals("mosaic")) {
    		int tilesize;
    		tilesize = Integer.parseInt(current_instruction[1]);
    		Picture[] temppicarray;
    		if (basecase) {
    			temppicarray = new Picture[current_instruction.length-2];
    			for(int i=2; i < current_instruction.length; i++) {
    				temppicarray[i-2] = Utils.loadImage(current_instruction[i]);
    			}
    			currentpic = mosaic(temppicarray, tilesize);
    		} else {
    			currentpic = mosaic(processHelper(instructionindex+1, instructions), tilesize);
    		}
    		resultpics = new Picture[]{currentpic};
    		return resultpics;
    	}
    	
    	else {
    		return null;
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
    	int redtotal, greentotal, bluetotal, avgred, avggreen, avgblue;
    	Picture blendedimage;
    	
    	dimensions = getBestDimensions(imagelist);
    	blendedimage = Utils.createPicture(dimensions[0], dimensions[1]);
    	
    	
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
    			
    			avgred = redtotal / imagelist.length;
    			avggreen = greentotal / imagelist.length;
    			avgblue = bluetotal / imagelist.length;
    			
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
    
    public static Picture mosaic(Picture[] imagelist, int tilesize) {
    	Color pixel;
    	int[] dimensions;
    	int  x_index, y_index;
    	Picture mosaicimage, currentimage;
    	dimensions = getBestDimensions(imagelist);
    	dimensions[0] -= dimensions[0] % tilesize;
    	dimensions[1] -= dimensions[1] % tilesize;
    	mosaicimage = Utils.createPicture(dimensions[0], dimensions[1]);
    	
    	for(int x=0; x < dimensions[0]; x++) {
    		for(int y=0; y < dimensions[1]; y++) {
    			x_index = (x / tilesize) % imagelist.length;
    			y_index = (y / tilesize) % imagelist.length;
    			currentimage = imagelist[(x_index + y_index) % imagelist.length];
    			pixel = currentimage.getPixel(x, y);
    			mosaicimage.setPixel(x, y, pixel);
    		}
    			
    	}
    	
    	return mosaicimage;
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
