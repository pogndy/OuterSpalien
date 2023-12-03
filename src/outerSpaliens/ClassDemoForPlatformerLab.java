package outerSpaliens;
 

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.JPanel;



public class ClassDemoForPlatformerLab extends JFrame
{
	public final static String PATH = ".\\src\\outerSpaliens\\";
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new myPanel());
		frame.setSize(800,800);
		frame.setVisible(true);
		ClassDemoForPlatformerLab f = new ClassDemoForPlatformerLab();
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setSize(800,800);		
		f.setVisible(true);	
		f.setup();
		f.draw();
	}
	
	//buffer for drawing off screen
	private Image raster;
	//graphics for the buffer
	private Graphics rasterGraphics;
	//this is the current x and y of the ball
	
	private Image background;
	
	public static int CameraX=0, CameraY = 0;
	
	ArrayList<Block> blocks = new ArrayList<Block>();
	ArrayList<Block> killblocks = new ArrayList<Block>();

	
	GameState state = GameState.RUNNING;
	
	boolean CLICK = false;
	
	public void setup()
	{
		//setup buffered graphics
		raster = this.createImage(800, 800);
		//raster = new BufferedImage(BufferedImage.TYPE_4BYTE_ABGR,500,500);
		rasterGraphics = raster.getGraphics();
		
		//Just handle pause menu key listener
		addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_P)
				{
					if (state == GameState.RUNNING)
						state = GameState.PAUSED;
					else
						state = GameState.RUNNING;
				}
			}
			public void keyTyped(KeyEvent e) {}			
		});
		

		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				CLICK = true;
			}
			public void mouseReleased(MouseEvent e) 
			{
				CLICK = false;
			}
		});
		
	}	
	public void readLevel()
	{
		try {			
			Scanner file = new Scanner(new File(PATH+"SimplePlatformerExampleLevel"));
			while (file.hasNextLine())
			{
				String line = file.nextLine();
				if (line.startsWith("Block"))
				{
					String tokens[] = line.split(",");
					blocks.add(new Block(Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2]),
							Integer.parseInt(tokens[3]),
							Integer.parseInt(tokens[4]),
							new Color(Integer.parseInt(tokens[5]),
									Integer.parseInt(tokens[6]),
									Integer.parseInt(tokens[7]))
							));
				}
				else if (line.startsWith("KillBlock"))
				{
					String tokens[] = line.split(",");
					killblocks.add(new Block(Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2]),
							Integer.parseInt(tokens[3]),
							Integer.parseInt(tokens[4]),
							new Color(Integer.parseInt(tokens[5]),
									Integer.parseInt(tokens[6]),
									Integer.parseInt(tokens[7]))
							));
				}
			}
		
			
		} catch (FileNotFoundException e) {}
	}
	
	enum GameState
	{
		RUNNING,
		PAUSED,
	}
	
	/**
	 * This is the workhorse of the program. This is where the main loop for graphics is done
	 */
	public void draw()
	{
		//create and add the balls to an array to use later
		PlatPlayer p = new PlatPlayer(100,100,Color.WHITE, this);
		this.addKeyListener(p);
		long deltaTime = 0;		
		
		readLevel();
		while(true)
		{
			//get the start time of the loop to use later
				long time = System.currentTimeMillis();
			
			if (state == GameState.RUNNING)
			{	
			//draw and move the background and balls
				DrawBackground(rasterGraphics);
				
			//level
				for (Block b : blocks)
				{
					//System.out.println(b.C);
					b.draw(rasterGraphics);
				}
				for (Block kb : killblocks)
				{
					//System.out.println(kb.getLocation());
					kb.draw(rasterGraphics);
					kb.Move();
				}

				
				
			//player and other actors(enemies)
				p.Move();
				p.DrawBall(rasterGraphics);
				for (Block b : blocks)
					p.checkCollision(b);
				for (Block kb : killblocks)
				{
					p.checkkillCollision(kb);
				}
				
			//update carmera
//				int current = (int)p.Location.getX();
//				if (current - CameraX > getWidth() * 4 / 5)
//					CameraX = current - (getWidth() * 4 / 5); 
//				else if (current - CameraX < getWidth() * 1 / 5)
//					CameraX = current - (getWidth() * 1 / 5);
				CameraX = (int)p.getLocation().getX() - getWidth()/2;
				
//				current = (int)p.Location.getY();
//				if (current - CameraY > getHeight() * 2 / 3)
//					CameraY = current - (getHeight() * 2 / 3); 
//				else if (current - CameraY < getHeight() * 1 / 3)
//					CameraY = current - (getHeight() * 1 / 3);
				CameraY = (int)p.getLocation().getY()- getHeight()/2;
				
			}
			else if (state == GameState.PAUSED)
			{
				Rectangle SaveButton = new Rectangle(200,100,180,100);
				Rectangle LoadButton = new Rectangle(200,220,180,100);
				
				rasterGraphics.setColor(Color.RED);
				((Graphics2D) rasterGraphics).fill(SaveButton);
				rasterGraphics.setColor(Color.GREEN);
				rasterGraphics.setFont(new Font("Arial",Font.PLAIN,34));
				rasterGraphics.drawString("Save",220,150);
				
				rasterGraphics.setColor(Color.RED);
				((Graphics2D) rasterGraphics).fill(LoadButton);
				rasterGraphics.setColor(Color.GREEN);
				rasterGraphics.drawString("Load",220,270);
				
				if (CLICK)
				{
					if (SaveButton.contains(MouseInfo.getPointerInfo().getLocation()))
						System.out.println("Saving game please wait");
					if (LoadButton.contains(MouseInfo.getPointerInfo().getLocation()))
						System.out.println("Loading game please wait");
					CLICK = false;
				}				
			}
			
			
			//draw the scene from the buffered raster (all at once to avoid flickering)
				getGraphics().drawImage(raster,0,0,getWidth(),getHeight(),null);
			
			//use the start time minus the current time to get delta time - this will 
			//vary as your program runs to make your program run smoothly we are going 
			//to use delta time when we sleep
				deltaTime = System.currentTimeMillis() - time;	
				deltaTime = Math.max(deltaTime, 10);
				try{Thread.sleep(deltaTime);}catch(Exception e){}
				
		}
	}
	private void DrawBackground(Graphics g) 
	{
		g.setColor(new Color(170,180,240));
		g.fillRect(0, 0, 800, 800);
	}	
}
interface Collidable
{
	public Rectangle getCollision();
	public Rectangle getKillCollision();
}
abstract class ScreenObj implements Serializable
{
	public Vector2D Location;
	public Vector2D Size;
}
class Block extends ScreenObj implements Collidable, Serializable
{
	public Color C;
	
	public Block(int X, int Y, int Xsize, int Ysize, Color c)
	{
		Location = new Vector2D(X,Y);
		Size = new Vector2D(Xsize,Ysize);
		C = c;
	}
	public Rectangle getCollision()
	{
		//System.out.println( new Rectangle((int)Location.getX(), (int)Location.getY(), (int)Size.getX(), (int)Size.getY()));
		return new Rectangle((int)Location.getX(), (int)Location.getY(), (int)Size.getX(), (int)Size.getY());
	}
	public Rectangle getKillCollision()
	{
		//System.out.println( new Rectangle((int)Location.getX(), (int)Location.getY(), (int)Size.getX(), (int)Size.getY()));
		return new Rectangle((int)Location.getX(), (int)Location.getY(), (int)Size.getX(), (int)Size.getY());
	}

	public void draw(Graphics g)
	{
		g.setColor(C);
		g.fillRect((int)Location.getX() - ClassDemoForPlatformerLab.CameraX, (int)Location.getY()- ClassDemoForPlatformerLab.CameraY, (int)Size.getX(), (int)Size.getY());
		//draw this in case the sprite doesn't work
		//Rectangle r = new Rectangle((int)Location.getX() - ClassDemoForPlatformerLab.CameraX , (int)Location.getY(), (int)Size.getX(), (int)Size.getY());
		//((Graphics2D) g).fill(r);
		g.setColor(Color.WHITE);
		g.drawRect((int)Location.getX() - ClassDemoForPlatformerLab.CameraX, (int)Location.getY()- ClassDemoForPlatformerLab.CameraY, (int)Size.getX(), (int)Size.getY());
	}
	private int speed = 1;

	private Vector2D Velocity = new Vector2D(1,0);

	private Vector2D Location;

	public Vector2D getLocation()
	{
		return new Vector2D(Location);//composition
	}

	public void setLocation(Vector2D location)
	{
		Location = new Vector2D(location); //composition
	}
	
	public void Move()
	{
		int last = 50;
		int first = 200;
		int speed = this.speed;
		//System.out.println(getLocation());
		setLocation(getLocation().add(Velocity.multiply(.4f)));
		
		if (getLocation().getX() < 50 && speed != 1)
		{
			System.out.println("below 50");
			this.speed = 1;
			Velocity = new Vector2D(1,0);
		}
		if (getLocation().getX() > 150 && speed != -1)
		{

			this.speed = -1;
			System.out.println("Above 150");

			Velocity = new Vector2D(-1,0);
		}


		
//		
//		while(true)
//		{
//		if(getLocation().getX() > 200)
//			Veol
//		}
	}
	

}
class Entity extends ScreenObj implements Collidable, Serializable
{

	public Rectangle getCollision() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getKillCollision() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getKillCollision'");
	}
	
}

class PlatPlayer implements KeyListener,  Serializable
{
	//if other things need sprite then it should be a regular class instead of an inner class. It could be an inner class of a "SpriteObject" base class though.
	class Sprite
	{
		public BufferedImage spriteSheet;
		public BufferedImage currentFrame;
		public int animationNumber = 1;
		public int frameNumber = 0;
		public int tileXSize = 120;
		public int tileYSize = 147;
		public static final int FRAME_NUMBER = 9;
		
		public Sprite(String fileName)
		{	
			try {
				spriteSheet = ImageIO.read(new File(fileName+".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}	
			//init currentFrame
			currentFrame = spriteSheet.getSubimage(0,0, tileXSize, tileYSize);
		}
		public void Update()
		{
			frameNumber = (frameNumber + 1) % FRAME_NUMBER;
			currentFrame = spriteSheet.getSubimage(frameNumber*tileXSize, animationNumber*tileYSize, tileXSize, tileYSize);
		}	
	}
	
	private Color C;
	//These variables are how much we are going to change the current X and Y per loop
	private float speed;
	private Vector2D Velocity;
	private Vector2D Location;
	private Vector2D Size = new Vector2D(50,70);
	private final Vector2D GRAVITY = new Vector2D(0,1);
	
	public static final int MAX_X_VELOCITY = 28, JUMP_VELOCITY = 27;
	
	private boolean UP, DOWN, RIGHT, LEFT, GROUND, SPACE;
	
	private PlatPlayer.Sprite sprite = new PlatPlayer.Sprite(ClassDemoForPlatformerLab.PATH+"alienspritesheet");
	
	enum facing
	{
		FacingRight,
		FacingLeft,
	}
	facing direction;	
	
	JFrame frame;
	
	public PlatPlayer(int x, int y, Color c, ClassDemoForPlatformerLab frame)
	{
		speed = .4f;
		Velocity = new Vector2D(1,0);
		setLocation(new Vector2D(x,y));		
		C=c;
		this.frame = frame;
	}	
	

	public Vector2D getLocation()
	{
		return new Vector2D(Location);//composition
	}

	public void setLocation(Vector2D location)
	{
		Location = new Vector2D(location); //composition
	}

	long lastUpdate = 0;
	public void Move()
	{		
		
		//update the ball's current location			
			setLocation(getLocation().add(Velocity.multiply(speed)));
		
		//if I'm jumping then I'm not on the ground
		if (Velocity.getY() < 0) //I'm moving up so I can't be on the ground
			GROUND = false;
		
		//respond to movement keys
			if (UP && GROUND) //UP also known as JUMP
			{
				Velocity = Velocity.add(new Vector2D(0,-JUMP_VELOCITY));
				GROUND = false;
			}
			if (LEFT)
			{
				if (GROUND)
					Velocity = Velocity.add(new Vector2D(-2,0));
				else
					Velocity = Velocity.add(new Vector2D(-0.8f,0));
			}
			if (RIGHT)
			{
				if (GROUND)
					Velocity = Velocity.add(new Vector2D(2,0));
				else
					Velocity = Velocity.add(new Vector2D(0.8f,0));
			}
		//handle animations
			boolean moving = true;
			
			//running left and right
				if (Velocity.getX() > 0.8)
				{
					direction = facing.FacingRight;
					sprite.animationNumber = 1;
				}
				else if (Velocity.getX()<-0.8)
				{
					direction = facing.FacingLeft;
					sprite.animationNumber = 4;
				}
				else
					moving = false;
			//jumping
				if (Math.abs(Velocity.getY()) > 0.2)
				{
					if ( direction == facing.FacingRight)
						sprite.animationNumber = 2;
					else if (direction == facing.FacingLeft)
						sprite.animationNumber = 5;
					moving = true;
				}
		//update the sprite every 100 milliseconds
			if (System.currentTimeMillis() - lastUpdate > 100)
			{
				lastUpdate = System.currentTimeMillis();
			
				if (!moving && direction == facing.FacingRight)
					sprite.animationNumber = 0;
				else if (!moving && direction == facing.FacingLeft)
					sprite.animationNumber = 3;
				sprite.Update();
			}
		//If on the ground
			if (GROUND)
				Velocity = Velocity.multiply(.9f);//Friction
			else //!ground also know as air
			{
				//Velocity = Velocity.multiply(.99f);//Friction
				if (Math.abs(Velocity.getX()) > MAX_X_VELOCITY)
					Velocity.setX(Velocity.getX() > 0 ? MAX_X_VELOCITY : -MAX_X_VELOCITY);//Don't do this except in platformers
			}
		//Gravity thou art a crewl b....
			Velocity = Velocity.add(GRAVITY);
			GROUND = false;
			
		//Did I fall off
			if (getLocation().getY() > 5000) //Then you dead yo
				Die();				
	}
	/**
	 * remove lives and reset to a starting or saved position
	 */
	public void Die() 
	{
		setLocation(new Vector2D(100,-100));
		Velocity = new Vector2D(0,0);
	}
	public void checkCollision(Collidable c)
	{
		//TODO: ADD a MTD (minimum translation distance)
		Ellipse2D.Float myCollision = new Ellipse2D.Float(getLocation().getX()-Size.getX()/2,getLocation().getY()-Size.getY()/2, Size.getX(),Size.getY());
		
		float collisionSize = Size.getX()/8;
		float startXCollision = Size.getX()/4; //10
		float endXCollision = Size.getX()*3/4; //30
		float XCollisionWidth = endXCollision - startXCollision;
		float OffsetStartCollisionX = Size.getX()*3/8; //15
		float OffsetStartCollisionY = Size.getY()*3/8; //15
		
		float startXLeftCollision =  Size.getX()/2;
		float startYCollision = Size.getY()/4;
		float endYCollision = Size.getY()*3/4; //30
		float YCollisionWidth = endYCollision - startYCollision;
		
		if (myCollision.intersects(c.getCollision()))
		{
			//System.out.println(myCollision.intersects(c.getCollision()));
			//I hit something but where?
			Ellipse2D.Float bottomCollision = new Ellipse2D.Float((int)getLocation().getX()-startXCollision,(int)getLocation().getY()+OffsetStartCollisionY, XCollisionWidth,collisionSize);
			if (Velocity.getY() > 0 && bottomCollision.intersects(c.getCollision()))
			{
				//then i'm standing on something				
					Velocity.setY(0);
					GROUND = true;
			}
			Ellipse2D.Float rightCollision = new Ellipse2D.Float((int)getLocation().getX()+OffsetStartCollisionX,(int)getLocation().getY()-startYCollision, collisionSize,YCollisionWidth);
			if (Velocity.getX() > 0 && rightCollision.intersects(c.getCollision()))
			{
				//then i'm running into something on the right				
					Velocity.setX(0);
			}
			Ellipse2D.Float leftCollision = new Ellipse2D.Float((int)getLocation().getX()-startXLeftCollision,(int)getLocation().getY()-startYCollision, collisionSize,YCollisionWidth);
			if (Velocity.getX() < 0 && leftCollision.intersects(c.getCollision()))
			{
				//then i'm running into something on the left
				System.out.println(c.getCollision());
					Velocity.setX(0);
			}

		}
	}
		public void checkkillCollision(Collidable c)
		{
			//TODO: ADD a MTD (minimum translation distance)
			Ellipse2D.Float myCollision = new Ellipse2D.Float(getLocation().getX()-Size.getX()/2,getLocation().getY()-Size.getY()/2, Size.getX(),Size.getY());
			
			float collisionSize = Size.getX()/8;
			float startXCollision = Size.getX()/4; //10
			float endXCollision = Size.getX()*3/4; //30
			float XCollisionWidth = endXCollision - startXCollision;
			float OffsetStartCollisionX = Size.getX()*3/8; //15
			float OffsetStartCollisionY = Size.getY()*3/8; //15
			
			float startXLeftCollision =  Size.getX()/2;
			float startYCollision = Size.getY()/4;
			float endYCollision = Size.getY()*3/4; //30
			float YCollisionWidth = endYCollision - startYCollision;
			
			if (myCollision.intersects(c.getCollision()))
			{
				//System.out.println(myCollision.intersects(c.getCollision()));
				//I hit something but where?
				Ellipse2D.Float bottomCollision = new Ellipse2D.Float((int)getLocation().getX()-startXCollision,(int)getLocation().getY()+OffsetStartCollisionY, XCollisionWidth,collisionSize);
				if (Velocity.getY() > 0 && bottomCollision.intersects(c.getCollision()))
				{
					Die();
				}
				Ellipse2D.Float rightCollision = new Ellipse2D.Float((int)getLocation().getX()+OffsetStartCollisionX,(int)getLocation().getY()-startYCollision, collisionSize,YCollisionWidth);
				if (Velocity.getX() > 0 && rightCollision.intersects(c.getCollision()))
				{
					//then i'm running into something on the right				
				Die();	
				}
				Ellipse2D.Float leftCollision = new Ellipse2D.Float((int)getLocation().getX()-startXLeftCollision,(int)getLocation().getY()-startYCollision, collisionSize,YCollisionWidth);
				if (Velocity.getX() < 0 && leftCollision.intersects(c.getCollision()))
				{
					Die();
				}
			}

	}
	public void DrawBall(Graphics g)
	{
		g.setColor(C);
		g.setFont(new Font("Arial",Font.BOLD,20));
		g.drawString("Location: "+getLocation(), 15,55);
		g.drawString("Velocity: "+Velocity, 15,80);
		//g.fillOval((int)(Location.getX() - Size.getX()/2 - ClassDemoForPlatformerLab.CameraX),(int)(Location.getY() - Size.getY()/2 - ClassDemoForPlatformerLab.CameraY), (int)Size.getX(), (int)Size.getY());	
			
		g.drawImage(sprite.currentFrame.getScaledInstance((int)Size.getX(), (int)Size.getY(), BufferedImage.SCALE_FAST),
					(int)(getLocation().getX() - Size.getX()/2 - ClassDemoForPlatformerLab.CameraX),(int)(getLocation().getY() - Size.getY()/2 - ClassDemoForPlatformerLab.CameraY),null);
		//debug draw collision
//		g.setColor(Color.magenta);
//		Ellipse2D.Float bottomCollision = new Ellipse2D.Float((int)Location.getX()-10- ClassDemoForPlatformerLab.CameraX,(int)Location.getY()+15, 20,5);
//		((Graphics2D) g).fill(bottomCollision);
//		Ellipse2D.Float rightCollision = new Ellipse2D.Float((int)Location.getX()+15- ClassDemoForPlatformerLab.CameraX,(int)Location.getY()-10, 5,20);
//		((Graphics2D) g).fill(rightCollision);
//		Ellipse2D.Float leftCollision = new Ellipse2D.Float((int)Location.getX()-20- ClassDemoForPlatformerLab.CameraX,(int)Location.getY()-10, 5,20);
//		((Graphics2D) g).fill(leftCollision);
	}
	//KeyListener
		public void keyTyped(KeyEvent e) { /*do nothing*/ }

	    /** Handle the key-pressed event from the text field. */
	    public void keyPressed(KeyEvent e) 
	    { 
	    	if (e.getKeyCode() == KeyEvent.VK_W)
	    		UP=true;
	    	if (e.getKeyCode() == KeyEvent.VK_S)
	    		DOWN=true;
	    	if (e.getKeyCode() == KeyEvent.VK_D)
	    		RIGHT=true;
	    	if (e.getKeyCode() == KeyEvent.VK_A)
	    		LEFT=true;
	    	if (e.getKeyCode() == KeyEvent.VK_SPACE)
	    		SPACE=true;
	    }
	    
	    /** Handle the key-released event from the text field. */
	    public void keyReleased(KeyEvent e) 
	    {
	    	if (e.getKeyCode() == KeyEvent.VK_W)
	    		UP=false;
	     	if (e.getKeyCode() == KeyEvent.VK_S)
	    		DOWN=false;
	    	if (e.getKeyCode() == KeyEvent.VK_D)
	    		RIGHT=false;
	    	if (e.getKeyCode() == KeyEvent.VK_A)
	    		LEFT=false;
	    	if (e.getKeyCode() == KeyEvent.VK_SPACE)
	    		SPACE=false;
	    }
}