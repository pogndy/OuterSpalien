

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class SpriteAnimationExample {

	public static void main(String[] args) {
		
		myGraphicsLabJFrame f = new myGraphicsLabJFrame();
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setSize(myGraphicsLabJFrame.WIDTH,myGraphicsLabJFrame.HEIGHT);
		f.setVisible(true);
		f.setup();
		f.draw();
	}
}
class Sprite
{
	public BufferedImage spriteSheet;
	public BufferedImage currentFrame;
	public int animationNumber = 9;
	public int frameNumber = 0;
	public int tileSize = 64;
	public static final int FRAME_NUMBER = 8;
	
	public Sprite(String fileName)
	{
		this(fileName,64);
	}
	public Sprite(String fileName, int tileSize)
	{	
		this.tileSize = tileSize;
		try {
			spriteSheet = ImageIO.read(new File(fileName+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		//init currentFrame
		currentFrame = spriteSheet.getSubimage(0,0, tileSize, tileSize);
	}
	public void Update()
	{
		frameNumber = (frameNumber + 1) % FRAME_NUMBER;
		currentFrame = spriteSheet.getSubimage(frameNumber*tileSize, animationNumber*tileSize, tileSize, tileSize);
	}	
}
class Character implements KeyListener
{
	public int X,Y;
	public Sprite sprite;
	//hard coded
	public Sprite sprite2;
	private boolean UP, DOWN, LEFT, RIGHT;
	public static final int MOVE_SPEED= 8;
	public Character(int x, int y, String fileName, String fileName2)
	{
		X=x;
		Y=y;
		sprite = new Sprite(fileName);
		if (fileName2 != null)
			sprite2 = new Sprite(fileName2);
	}
	//shows 1 frame
	long lastUpdate = 0;
	public void Act()
	{
		boolean update=false;
		if (UP)
		{
			Y-=MOVE_SPEED;
			sprite.animationNumber=8;
			if (sprite2 != null)
				sprite2.animationNumber = 8;
			update=true;
		}
		if (DOWN)
		{
			Y+=MOVE_SPEED;
			sprite.animationNumber=10;
			if (sprite2 != null)
				sprite2.animationNumber = 10;
			update=true;
		}
		if (RIGHT)
		{
			X+=MOVE_SPEED;
			sprite.animationNumber=11;
			if (sprite2 != null)
				sprite2.animationNumber = 11;
			update=true;
		}
		if (LEFT)
		{
			X-=MOVE_SPEED;
			sprite.animationNumber=9;
			if (sprite2 != null)
				sprite2.animationNumber = 9;
			update=true;			
		}
		
		if (System.currentTimeMillis() - lastUpdate > 100)
		{
			lastUpdate = System.currentTimeMillis();
		
		
			if (update)
			{
				sprite.Update();
				if (sprite2 != null)
					sprite2.Update();
			}
			else //makes them go to the standing position if you stop moving
			{
				sprite.frameNumber=-1;
				sprite.Update();
				
				if (sprite2 != null)
				{
					sprite2.frameNumber=-1;
					sprite2.Update();
				}
			}
		}
	}
	public void draw(Graphics g)
	{
		g.drawImage(sprite.currentFrame,X-sprite.tileSize/2,Y-sprite.tileSize/2,null);
		if (sprite2 != null)
			g.drawImage(sprite2.currentFrame,X-sprite.tileSize/2,Y-sprite.tileSize/2,null);
	}
	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
    	{
    		UP=true;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_DOWN)
    	{
    		DOWN=true;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_RIGHT)
    	{
    		RIGHT=true;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_LEFT)
    	{
    		LEFT=true;
    	}
	}
	public void keyReleased(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
    	{
    		UP=false;
     	}
    	if (e.getKeyCode() == KeyEvent.VK_DOWN)
    	{
    		DOWN=false;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_RIGHT)
    	{
    		RIGHT=false;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_LEFT)
    	{
    		LEFT=false;
    	}
	}
	public void keyTyped(KeyEvent arg0) {}
}
class myGraphicsLabJFrame extends JFrame
{
	public static final int WIDTH = 1000, HEIGHT=700;
	//buffer for drawing off screen
	private Image raster;
	private Graphics rasterGraphics;
		
	int testing=0;
	public void setup()
	{
		raster = this.createImage(WIDTH, HEIGHT);
		rasterGraphics = raster.getGraphics();				
	}
	public void draw()
	{		
		Character c = new Character(100,100,"classSprite","classSpriteItem");
		addKeyListener(c);
		while (true)
		{
			rasterGraphics.fillRect(0, 0, WIDTH,HEIGHT);
						
			//hard code the Character to Act and Draw
			c.Act();
			c.draw(rasterGraphics);
						
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//NOTE: *4 was so that the animation is larger and we can see it better
			//the WIDTH and HEIGHT part is for scalling the image
			this.getGraphics().drawImage(raster,0,0,WIDTH*4,HEIGHT*4,null);
		}		
	}
}

