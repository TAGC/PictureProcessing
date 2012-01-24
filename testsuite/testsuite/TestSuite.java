package testsuite;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

import picture.Utils;
import testsuite.internal.CatchExitRule;

import static testsuite.TestSuiteHelper.runMain;
import static junit.framework.Assert.*;

public class TestSuite {

    private static final int FIVE_SECONDS = 5000;

    @Rule
    public Timeout timeout = new Timeout(FIVE_SECONDS);

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public CatchExitRule disableSystemExit = new CatchExitRule();

    @Test
    public void invertBlack() throws IOException {
        assertEquals(runMain(tmpFolder, "invert", "images/black64x64.png"),
                Utils.loadImage("images/white64x64.png"));
    }

    @Test
    public void grayscaleBlack() throws IOException {
        assertEquals(runMain(tmpFolder, "grayscale", "images/black64x64.png"),
                Utils.loadImage("images/black64x64.png"));
    }

    @Test
    public void rotate90Green() throws IOException {
        assertEquals(runMain(tmpFolder, "rotate", "90",
                "images/green64x64doc.png"), Utils
                .loadImage("images/green64x64R90doc.png"));
    }
    
    @Test
    public void rotate180Blue() throws IOException {
    	assertEquals(runMain(tmpFolder, "rotate", "180",
        	"images/blueR9064x32doc.png"), Utils
        	.loadImage("images/blueR27064x32doc.png"));
    }

    @Test
    public void flipVGreen() throws IOException {
        assertEquals(
                runMain(tmpFolder, "flip", "V", "images/green64x64doc.png"),
                Utils.loadImage("images/green64x64FVdoc.png"));
    }
    
    @Test
    public void flipHBlue() throws IOException {
    	assertEquals(
                runMain(tmpFolder, "flip", "H", "images/blue64x32doc.png"),
                Utils.loadImage("images/blueFH64x32doc.png"));
    }

    @Test
    public void blurBWPatterns() throws IOException {
        assertEquals(runMain(tmpFolder, "blur", "images/bwpatterns64x64.png"),
                Utils.loadImage("images/bwpatternsblur64x64.png"));
    }
    
    @Test
    public void blurSunset() throws IOException {
    	assertEquals(runMain(tmpFolder, "blur", "images/sunset64x32.png"),
                Utils.loadImage("images/sunsetBlur64x32.png"));
    }

    @Test
    public void blendBWAndRainbow() throws IOException {
        assertEquals(runMain(tmpFolder, "blend", "images/bwpatterns64x64.png",
                "images/rainbow64x64doc.png"), Utils
                .loadImage("images/rainbowpatternsblend64x64.png"));
    }

}
