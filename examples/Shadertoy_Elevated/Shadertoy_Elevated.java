/**
 * 
 * PixelFlow | Copyright (C]) 2017 Thomas Diewald - www.thomasdiewald.com
 * 
 * https://github.com/diwi/PixelFlow.git
 * 
 * A Processing/Java library for high performance GPU-Computing.
 * MIT License: https://opensource.org/licenses/MIT
 * 
 */


package Shadertoy_Elevated;



import java.nio.ByteBuffer;

import com.jogamp.opengl.GL2;
import com.thomasdiewald.pixelflow.java.DwPixelFlow;
import com.thomasdiewald.pixelflow.java.dwgl.DwGLTexture;
import com.thomasdiewald.pixelflow.java.imageprocessing.DwShadertoy;

import processing.core.PApplet;
import processing.opengl.PGraphics2D;


public class Shadertoy_Elevated extends PApplet {

  DwPixelFlow context;
  
  DwShadertoy toyA;
  DwShadertoy toy;
  PGraphics2D pg_canvas;
  
  DwGLTexture tex_noise = new DwGLTexture();
  
  public void settings() {
    size(1280, 720, P2D);
    smooth(0);
  }
  
  public void setup() {
    surface.setResizable(true);
    
    context = new DwPixelFlow(this);
    context.print();
    context.printGL();
    
    toyA = new DwShadertoy(context, "data/Elevated_BufA.frag");
    toy  = new DwShadertoy(context, "data/Elevated_Image.frag");
    
    // create noise texture
    int wh = 256;

    byte[] bdata = new byte[wh * wh];
    ByteBuffer bbuffer = ByteBuffer.wrap(bdata);
    for(int i = 0; i < bdata.length; i++){
      bdata[i] = (byte) random(0, 256);
    }
    tex_noise.resize(context, GL2.GL_R8, wh, wh, GL2.GL_RED, GL2.GL_UNSIGNED_BYTE, GL2.GL_LINEAR, GL2.GL_MIRRORED_REPEAT, 1, 1, bbuffer);
    frameRate(60);
  }

  public void resizeScene(){
    if(pg_canvas == null || width != pg_canvas.width || height != pg_canvas.height){
      pg_canvas = (PGraphics2D) createGraphics(width, height, P2D);
      toy.reset();
    }
    toyA.resize(width, height);
  }
  
  public void draw() {
    resizeScene();

    if(mousePressed){
      toyA.set_iMouse(mouseX, height-1-mouseY, mouseX, height-1-mouseY);
    }
    toyA.set_iChannel(0, tex_noise);
    toyA.apply();
    
    toy.set_iChannel(0, toyA);
    toy.apply(pg_canvas);
    
    blendMode(REPLACE);
    image(pg_canvas, 0, 0);
        
    String txt_fps = String.format(getClass().getSimpleName()+ "   [size %d/%d]   [frame %d]   [fps %6.2f]", width, height, frameCount, frameRate);
    surface.setTitle(txt_fps);
  }
  
  
  public static void main(String args[]) {
    PApplet.main(new String[] { Shadertoy_Elevated.class.getName() });
  }
}