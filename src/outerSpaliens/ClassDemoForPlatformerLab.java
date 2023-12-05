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
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import outerSpaliens.ClassDemoForPlatformerLab.GameState;

import javax.swing.JPanel;



public class ClassDemoForPlatformerLab extends JFrame
{
	public final static String PATH = ".\\src\\outerSpaliens\\";
	private int currentLevel = 0;
    private PlatPlayer player;
	public static void main(String[] args) {
		ClassDemoForPlatformerLab f = new ClassDemoForPlatformerLab();
        JFrame frame = new JFrame();
		f.setVisible(true);
		f.setVisible(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new myPanel(f)); // Pass the game instance to the panel
        frame.setSize(800, 800);
        frame.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(800, 800);    
        f.setup();
        f.draw();
	}
	
	//buffer for drawing off screen
	private Image raster;
	//graphics for the buffer
	private Graphics rasterGraphics;
	//this is the current x and y of the ball
	
	private Image background;
    private Image spaceship;
    int MAX_LEVEL = 3;
	
	public static int CameraX=0, CameraY = 0;
	
	ArrayList<Block> blocks = new ArrayList<Block>();
	ArrayList<Block> killblocks = new ArrayList<Block>();
	ArrayList<Entity> enemies = new ArrayList<Entity>();
    private Goal goal;
    private Entity entity;

	
	GameState state = GameState.MENU;
	
	public void startGame() {
		this.setVisible(true);
        state = GameState.RUNNING;
    }
	
	boolean CLICK = false;
	
	public void setup()
	{
		//setup buffered graphics
		raster = this.createImage(800, 800);
		//raster = new BufferedImage(BufferedImage.TYPE_4BYTE_ABGR,500,500);
		rasterGraphics = raster.getGraphics();
		player = new PlatPlayer(100, 100, Color.WHITE, this, goal);
        this.addKeyListener(player);
        player.setLocation(new Vector2D(100, -50));
        player.setVelocity(new Vector2D(0, 0));

        //Just handle pause menu key listener
        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent arg0) {
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (state == GameState.RUNNING) {
                        state = GameState.PAUSED;
                    } else {
                        state = GameState.RUNNING;
                    }
                }
            }

            public void keyTyped(KeyEvent e) {
            }
        });

        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                CLICK = true;
            }

            public void mouseReleased(MouseEvent e) {
                CLICK = false;
            }
        });
        background = new ImageIcon(PATH + "background.png").getImage();
        spaceship = new ImageIcon(PATH + "spaceship.png").getImage();

    }

    public void readLevel(String levelFileName) {
        try {
            Scanner file = new Scanner(new File(PATH + "Level" + currentLevel));
            //Scanner file = new Scanner(new File(PATH + "SimplePlatformerExampleLevel"));

            while (file.hasNextLine()) {
                String line = file.nextLine();
                if (line.startsWith("Block")) {
                    String tokens[] = line.split(",");
                    blocks.add(new Block(Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]),
                            Integer.parseInt(tokens[3]),
                            Integer.parseInt(tokens[4]),
                            new Color(Integer.parseInt(tokens[5]),
                                    Integer.parseInt(tokens[6]),
                                    Integer.parseInt(tokens[7]))
                    ));
                } else if (line.startsWith("KillBlock")) {
                    String tokens[] = line.split(",");
                    killblocks.add(new Block(Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]),
                            Integer.parseInt(tokens[3]),
                            Integer.parseInt(tokens[4]),
                            new Color(Integer.parseInt(tokens[5]),
                                    Integer.parseInt(tokens[6]),
                                    Integer.parseInt(tokens[7]))
                    ));
                } else if (line.startsWith("Goal")) {
                    String tokens[] = line.split(",");
                    goal = new Goal(Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]),
                            Integer.parseInt(tokens[3]),
                            Integer.parseInt(tokens[4]),
                            new Color(Integer.parseInt(tokens[5]),
                                    Integer.parseInt(tokens[6]),
                                    Integer.parseInt(tokens[7]))
                    );
                } else if (line.startsWith("Entity")) {
                    String tokens[] = line.split(",");
                    entity = new Entity(Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]),
                            Integer.parseInt(tokens[3]),
                            Integer.parseInt(tokens[4]),
                            new Color(Integer.parseInt(tokens[5]),
                                    Integer.parseInt(tokens[6]),
                                    Integer.parseInt(tokens[7]))
                    );
                }
            }
        } catch (FileNotFoundException e) {
        }
    }
	enum GameState
	{
		RUNNING,
		PAUSED,
		MENU,
		LEVEL_COMPLETE,
	}
	
	/**
	 * This is the workhorse of the program. This is where the main loop for graphics is done
	 */
	public void draw()
	{
		//create and add the balls to an array to use later
		PlatPlayer p = new PlatPlayer(100,100,Color.WHITE, this, goal);
		this.addKeyListener(p);
		long deltaTime = 0;		
		
		readLevel("Level" + currentLevel);
        //readLevel("SimplePlatformerExampleLevel");
		while(true)
		{
			//get the start time of the loop to use later
				long time = System.currentTimeMillis();
			if (state == GameState.RUNNING && p.getLives() > 0)
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

				drawHUD(rasterGraphics, p.getLives(), currentLevel);
				
				entity.draw(rasterGraphics);
                

                goal.draw(rasterGraphics);
                
                entity.Move();
                p.checkkillCollision(entity);

                //player and other actors(enemies)
                p.Move();
                p.DrawBall(rasterGraphics);
                for (Block b : blocks) {
                    p.checkCollision(b);
                }
         
                for (Block kb : killblocks) {
                    p.checkkillCollision(kb);
                }
                
                for (Block b : blocks) {
                    entity.checkCollision(b);
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
				if (goal.isPlayerAtGoal(p)) {
                    state = GameState.LEVEL_COMPLETE;
                }
			}
			if (state == GameState.LEVEL_COMPLETE) {
                resetGame();
                p.nextLvl();
                levelCompleted();

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
			else if (p.getLives() == 0) {
				Rectangle YouDied = new Rectangle(200,265,400,100);
				Rectangle Restart = new Rectangle(250,400,300,100);

				rasterGraphics.setColor(Color.RED);
				((Graphics2D) rasterGraphics).fill(YouDied);
				rasterGraphics.setColor(Color.WHITE);
				rasterGraphics.setFont(new Font("Arial",Font.PLAIN,50));
				rasterGraphics.drawString("ALIEN DOWN!",240,325);

				rasterGraphics.setColor(Color.RED);
				((Graphics2D) rasterGraphics).fill(Restart);
				rasterGraphics.setColor(Color.WHITE);
				rasterGraphics.drawString("Restart?",305,460);
				if (CLICK)
				{
					if (Restart.contains(MouseInfo.getPointerInfo().getLocation()))
						p.addLives(3);
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
	private void levelCompleted() {
        currentLevel++;
        if (currentLevel <= MAX_LEVEL) {
            loadLevel("Level" + currentLevel);
        } else {
            System.out.println("You win!");
        }
    }

    public void loadLevel(String levelFileName) {
        readLevel(levelFileName);
        state = GameState.RUNNING;
    }

    public void resetGame() {
        blocks.clear();
        killblocks.clear();
        goal = null;
        entity = null;

    }
	private void DrawBackground(Graphics g) 
	{
		int backgroundX = (int) (-CameraX * 0.09);
        int backgroundY = (int) (-CameraY * 0.09);
        int spaceshipX = (int) (-CameraX * 1);
        int spaceshipY = (int) (-CameraY * 1);
        g.drawImage(background, backgroundX, backgroundY, this);
        g.drawImage(spaceship, spaceshipX, spaceshipY, this);
	}
	private void drawHUD(Graphics g, int lives, int currentLevel) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Lives: " + lives, 10, 780); // Position it at the bottom left
		g.drawString("Level: " + currentLevel,670, 780);
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
class Goal extends ScreenObj implements Collidable, Serializable {

    private Image goalImage;

    public Goal(int X, int Y, int Xsize, int Ysize, Color c) {
        Location = new Vector2D(X, Y);
        Size = new Vector2D(Xsize, Ysize);
        goalImage = new ImageIcon(ClassDemoForPlatformerLab.PATH + "Teleporter.png").getImage();
    }

    public Rectangle getCollision() {
        return new Rectangle((int) Location.getX(), (int) Location.getY(), (int) Size.getX(), (int) Size.getY());
    }

    public boolean isPlayerAtGoal(PlatPlayer player) {
        Rectangle playerBounds = new Rectangle((int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getSize().getX(), (int) player.getSize().getY());
        return playerBounds.intersects(getCollision());
    }

    public void draw(Graphics g) {

        int tileWidth = goalImage.getWidth(null);
        int tileHeight = goalImage.getHeight(null);
        g.drawImage(goalImage, (int) Location.getX() - ClassDemoForPlatformerLab.CameraX, (int) Location.getY() - ClassDemoForPlatformerLab.CameraY, null);
    }

    public Rectangle getKillCollision() {
        // TODO Auto-generated method stub
        return null;
    }
}
class Block extends ScreenObj implements Collidable, Serializable {

    public Color C;
    private Image blockImage;

    public Block(int X, int Y, int Xsize, int Ysize, Color c) {
        Location = new Vector2D(X, Y);
        Size = new Vector2D(Xsize, Ysize);
        C = c;
        blockImage = new ImageIcon(ClassDemoForPlatformerLab.PATH + "moon_surface.png").getImage();

    }

    public Rectangle getCollision() {
        //System.out.println( new Rectangle((int)Location.getX(), (int)Location.getY(), (int)Size.getX(), (int)Size.getY()));
        return new Rectangle((int) Location.getX(), (int) Location.getY(), (int) Size.getX(), (int) Size.getY());
    }

    public Rectangle getKillCollision() {
        //System.out.println( new Rectangle((int)Location.getX(), (int)Location.getY(), (int)Size.getX(), (int)Size.getY()));
        return new Rectangle((int) Location.getX(), (int) Location.getY(), (int) Size.getX(), (int) Size.getY());
    }

    public void draw(Graphics g) {
        g.setColor(C);
        g.fillRect((int) Location.getX() - ClassDemoForPlatformerLab.CameraX, (int) Location.getY() - ClassDemoForPlatformerLab.CameraY, (int) Size.getX(), (int) Size.getY());

        int tileWidth = blockImage.getWidth(null);
        int tileHeight = blockImage.getHeight(null);
        for (int x = (int) Location.getX() - ClassDemoForPlatformerLab.CameraX; x < (int) Location.getX() + Size.getX() - ClassDemoForPlatformerLab.CameraX; x += tileWidth) {
            for (int y = (int) Location.getY() - ClassDemoForPlatformerLab.CameraY; y < (int) Location.getY() + Size.getY() - ClassDemoForPlatformerLab.CameraY; y += tileHeight) {
                g.drawImage(blockImage, x, y, null);
            }
        }
        //draw this in case the sprite doesn't work
        //Rectangle r = new Rectangle((int)Location.getX() - ClassDemoForPlatformerLab.CameraX , (int)Location.getY(), (int)Size.getX(), (int)Size.getY());
        //((Graphics2D) g).fill(r);
//			g.setColor(Color.WHITE);
//			g.drawRect((int)Location.getX() - ClassDemoForPlatformerLab.CameraX, (int)Location.getY()- ClassDemoForPlatformerLab.CameraY, (int)Size.getX(), (int)Size.getY());
    }
    private int speed = 1;

    private Vector2D Velocity = new Vector2D(1, 0);

    private Vector2D Location;

    public Vector2D getLocation() {
        return new Vector2D(Location);//composition
    }

    public void setLocation(Vector2D location) {
        Location = new Vector2D(location); //composition
    }

    public void Move() {
        int last = 50;
        int first = 200;
        int speed = this.speed;
        //System.out.println(getLocation());
        setLocation(getLocation().add(Velocity.multiply(.4f)));

        if (getLocation().getX() < 200 && speed != 1) {
            System.out.println("below 50");
            this.speed = 1;
            Velocity = new Vector2D(1, 0);
        }
        if (getLocation().getX() > 300 && speed != -1) {

            this.speed = -1;
            System.out.println("Above 150");

            Velocity = new Vector2D(-1, 0);
        }

//			
//			while(true)
//			{
//			if(getLocation().getX() > 200)
//				Veol
//			}
    }

}

class Entity extends ScreenObj implements Collidable, Serializable {

    private Image entityImage;

    public Entity(int X, int Y, int Xsize, int Ysize, Color c) {
        Location = new Vector2D(X, Y);
        Size = new Vector2D(Xsize, Ysize);

        entityImage = new ImageIcon(ClassDemoForPlatformerLab.PATH + "predator.png").getImage();
    }

    public Rectangle getCollision() {
        return new Rectangle((int) Location.getX(), (int) Location.getY(), (int) Size.getX(), (int) Size.getY());
    }

    public void draw(Graphics g) {
        int tileWidth = entityImage.getWidth(null);
        int tileHeight = entityImage.getHeight(null);
        g.drawImage(entityImage, (int) Location.getX() - ClassDemoForPlatformerLab.CameraX, (int) Location.getY() - ClassDemoForPlatformerLab.CameraY, null);
    }

    public Rectangle getKillCollision() {
        //System.out.println( new Rectangle((int)Location.getX(), (int)Location.getY(), (int)Size.getX(), (int)Size.getY()));
        return new Rectangle((int) Location.getX(), (int) Location.getY(), (int) Size.getX(), (int) Size.getY());
    }
    
    private int speed = 1;

    private Vector2D Velocity = new Vector2D(1, 0);

    private Vector2D Location;

    public Vector2D getLocation() {
        return new Vector2D(Location);//composition
    }

    public void setLocation(Vector2D location) {
        Location = new Vector2D(location); //composition
    }

    public void Move() {
        int last = 50;
        int first = 200;
        int speed = this.speed;
        //System.out.println(getLocation());
        setLocation(getLocation().add(Velocity.multiply(5.0f)));
        
        
        if (getLocation().getX() < 200 && speed != 1) {
            System.out.println("below 50");
            this.speed = 1;
            Velocity = new Vector2D(1, 0);
        }
        if (getLocation().getX() > 300 && speed != -1) {

            this.speed = -1;
            System.out.println("Above 150");

            Velocity = new Vector2D(-1, 0);
        }
    }
    public void checkCollision(Block block) {
    Rectangle entityBounds = new Rectangle((int) Location.getX(), (int) Location.getY(), (int) Size.getX(), (int) Size.getY());

    if (entityBounds.intersects(block.getCollision())) {
        boolean fromLeft = entityBounds.getMaxX() > block.getCollision().getMinX();
        boolean fromRight = entityBounds.getMinX() < block.getCollision().getMaxX();
        boolean fromTop = entityBounds.getMaxY() > block.getCollision().getMinY();
        boolean fromBottom = entityBounds.getMinY() < block.getCollision().getMaxY();
        
        if (fromLeft && Velocity.getX() > 0) {
           
            Location.setX(block.getLocation().getX() - Size.getX());
            Velocity.setX(-Velocity.getX()); 
        } else if (fromRight && Velocity.getX() < 0) {
            
            Location.setX(block.getLocation().getX() + Size.getX());
            Velocity.setX(-Velocity.getX());
        }

        
    }
}

}

class PlatPlayer implements KeyListener,  Serializable
{
	//if other things need sprite then it should be a regular class instead of an inner class. It could be an inner class of a "SpriteObject" base class though.
	private Goal goal;
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
	private int lives = 3;
	private Color C;
    //These variables are how much we are going to change the current X and Y per loop
    private float speed;
    private Vector2D Velocity;
    private Vector2D Location;
    private Vector2D Size = new Vector2D(50, 70);
    private final Vector2D GRAVITY = new Vector2D(0, 1);

    public static final int MAX_X_VELOCITY = 20, JUMP_VELOCITY = 27;

    private boolean UP, DOWN, RIGHT, LEFT, GROUND, SPACE;

    private PlatPlayer.Sprite sprite = new PlatPlayer.Sprite(ClassDemoForPlatformerLab.PATH + "alienspritesheet");

    enum facing {
        FacingRight,
        FacingLeft,
    }
    facing direction;

    JFrame frame;

    public PlatPlayer(int x, int y, Color c, ClassDemoForPlatformerLab frame, Goal goal) {
        speed = .4f;
        Velocity = new Vector2D(1, 0);
        setLocation(new Vector2D(x, y));
        C = c;
        this.frame = frame;
        this.goal = goal;
    }

    public Vector2D getSize() {
        return Size;
    }

    public Vector2D getLocation() {
        return new Vector2D(Location);//composition
    }

    public void setVelocity(Vector2D velocity) {
        Velocity = new Vector2D(velocity);
    }

    public void setLocation(Vector2D location) {
        Location = new Vector2D(location); //composition
    }

    long lastUpdate = 0;

    public void Move() {

        //update the ball's current location			
        setLocation(getLocation().add(Velocity.multiply(speed)));

        //if I'm jumping then I'm not on the ground
        if (Velocity.getY() < 0) //I'm moving up so I can't be on the ground
        {
            GROUND = false;
        }

        //respond to movement keys
        if (UP && GROUND) //UP also known as JUMP
        {
            Velocity = Velocity.add(new Vector2D(0, -JUMP_VELOCITY));
            GROUND = false;
        }
        if (LEFT) {
            if (GROUND) {
                Velocity = Velocity.add(new Vector2D(-2, 0));
            } else {
                Velocity = Velocity.add(new Vector2D(-0.8f, 0));
            }
        }
        if (RIGHT) {
            if (GROUND) {
                Velocity = Velocity.add(new Vector2D(2, 0));
            } else {
                Velocity = Velocity.add(new Vector2D(0.8f, 0));
            }
        }
        //handle animations
        boolean moving = true;

        //running left and right
        if (Velocity.getX() > 0.8) {
            direction = facing.FacingRight;
            sprite.animationNumber = 1;
        } else if (Velocity.getX() < -0.8) {
            direction = facing.FacingLeft;
            sprite.animationNumber = 4;
        } else {
            moving = false;
        }
        //jumping
        if (Math.abs(Velocity.getY()) > 0.2) {
            if (direction == facing.FacingRight) {
                sprite.animationNumber = 2;
            } else if (direction == facing.FacingLeft) {
                sprite.animationNumber = 5;
            }
            moving = true;
        }
        //update the sprite every 100 milliseconds
        if (System.currentTimeMillis() - lastUpdate > 100) {
            lastUpdate = System.currentTimeMillis();

            if (!moving && direction == facing.FacingRight) {
                sprite.animationNumber = 0;
            } else if (!moving && direction == facing.FacingLeft) {
                sprite.animationNumber = 3;
            }
            sprite.Update();
        }
        //If on the ground
        if (GROUND) {
            Velocity = Velocity.multiply(.9f);//Friction
        } else //!ground also know as air
        {
            //Velocity = Velocity.multiply(.99f);//Friction
            if (Math.abs(Velocity.getX()) > MAX_X_VELOCITY) {
                Velocity.setX(Velocity.getX() > 0 ? MAX_X_VELOCITY : -MAX_X_VELOCITY);//Don't do this except in platformers
            }
        }
        //Gravity thou art a crewl b....
        Velocity = Velocity.add(GRAVITY);
        GROUND = false;

        //Did I fall off
        if (getLocation().getY() > 5000) //Then you dead yo
        {
            Die();
        }

    }

	// remove lives and reset to a starting or saved position
	public void Die() 
	{
		if (lives > 0) { //checks if lives is above 0
            lives--;
		}
	
		setLocation(new Vector2D(100,-100));
		Velocity = new Vector2D(0,0);
        
        
	}
	public void nextLvl() {
        setLocation(new Vector2D(100, -50));
        Velocity = new Vector2D(0, 0);
    }
	//getters and setters for Lives
	public int getLives() {
        return lives;
    }
	public void addLives(int lives) {
		this.lives += lives;
	}

	 public void checkCollision(Collidable c) {
        //TODO: ADD a MTD (minimum translation distance)
        Ellipse2D.Float myCollision = new Ellipse2D.Float(getLocation().getX() - Size.getX() / 2, getLocation().getY() - Size.getY() / 2, Size.getX(), Size.getY());

        float collisionSize = Size.getX() / 8;
        float startXCollision = Size.getX() / 4; //10
        float endXCollision = Size.getX() * 3 / 4; //30
        float XCollisionWidth = endXCollision - startXCollision;
        float OffsetStartCollisionX = Size.getX() * 3 / 8; //15
        float OffsetStartCollisionY = Size.getY() * 3 / 8; //15

        float startXLeftCollision = Size.getX() / 2;
        float startYCollision = Size.getY() / 4;
        float endYCollision = Size.getY() * 3 / 4; //30
        float YCollisionWidth = endYCollision - startYCollision;

        if (myCollision.intersects(c.getCollision())) {
            //System.out.println(myCollision.intersects(c.getCollision()));
            //I hit something but where?
            Ellipse2D.Float bottomCollision = new Ellipse2D.Float((int) getLocation().getX() - startXCollision, (int) getLocation().getY() + OffsetStartCollisionY, XCollisionWidth, collisionSize);
            if (Velocity.getY() > 0 && bottomCollision.intersects(c.getCollision())) {
                //then i'm standing on something				
                Velocity.setY(0);
                GROUND = true;
            }
            Ellipse2D.Float rightCollision = new Ellipse2D.Float((int) getLocation().getX() + OffsetStartCollisionX, (int) getLocation().getY() - startYCollision, collisionSize, YCollisionWidth);
            if (Velocity.getX() > 0 && rightCollision.intersects(c.getCollision())) {
                //then i'm running into something on the right				
                Velocity.setX(0);
            }
            Ellipse2D.Float leftCollision = new Ellipse2D.Float((int) getLocation().getX() - startXLeftCollision, (int) getLocation().getY() - startYCollision, collisionSize, YCollisionWidth);
            if (Velocity.getX() < 0 && leftCollision.intersects(c.getCollision())) {
                //then i'm running into something on the left
                System.out.println(c.getCollision());
                Velocity.setX(0);
            }

        }
    }

    public void checkkillCollision(Collidable c) {
        //TODO: ADD a MTD (minimum translation distance)
        Ellipse2D.Float myCollision = new Ellipse2D.Float(getLocation().getX() - Size.getX() / 2, getLocation().getY() - Size.getY() / 2, Size.getX(), Size.getY());

        float collisionSize = Size.getX() / 8;
        float startXCollision = Size.getX() / 4; //10
        float endXCollision = Size.getX() * 3 / 4; //30
        float XCollisionWidth = endXCollision - startXCollision;
        float OffsetStartCollisionX = Size.getX() * 3 / 8; //15
        float OffsetStartCollisionY = Size.getY() * 3 / 8; //15

        float startXLeftCollision = Size.getX() / 2;
        float startYCollision = Size.getY() / 4;
        float endYCollision = Size.getY() * 3 / 4; //30
        float YCollisionWidth = endYCollision - startYCollision;

        if (myCollision.intersects(c.getCollision())) {
            //System.out.println(myCollision.intersects(c.getCollision()));
            //I hit something but where?
            Ellipse2D.Float topCollision = new Ellipse2D.Float((int) getLocation().getX() - startXCollision, (int) getLocation().getY() + OffsetStartCollisionY, XCollisionWidth, collisionSize);
            if (Velocity.getY() > 0 && topCollision.intersects(c.getCollision())) {
                Die();
            }
            Ellipse2D.Float rightCollision = new Ellipse2D.Float((int) getLocation().getX() + OffsetStartCollisionX, (int) getLocation().getY() - startYCollision, collisionSize, YCollisionWidth);
            if (Velocity.getX() > 0 && rightCollision.intersects(c.getCollision())) {
                //then i'm running into something on the right				
                Die();
            }
            Ellipse2D.Float leftCollision = new Ellipse2D.Float((int) getLocation().getX() - startXLeftCollision, (int) getLocation().getY() - startYCollision, collisionSize, YCollisionWidth);
            if (Velocity.getX() < 0 && leftCollision.intersects(c.getCollision())) {
                Die();
            }
        }

    }
    public void checkkillCollision(Entity entity) {
        //TODO: ADD a MTD (minimum translation distance)
        Ellipse2D.Float myCollision = new Ellipse2D.Float(getLocation().getX() - Size.getX() / 2, getLocation().getY() - Size.getY() / 2, Size.getX(), Size.getY());

        float collisionSize = Size.getX() / 8;
        float startXCollision = Size.getX() / 4; //10
        float endXCollision = Size.getX() * 3 / 4; //30
        float XCollisionWidth = endXCollision - startXCollision;
        float OffsetStartCollisionX = Size.getX() * 3 / 8; //15
        float OffsetStartCollisionY = Size.getY() * 3 / 8; //15

        float startXLeftCollision = Size.getX() / 2;
        float startYCollision = Size.getY() / 4;
        float endYCollision = Size.getY() * 3 / 4; //30
        float YCollisionWidth = endYCollision - startYCollision;

        if (myCollision.intersects(entity.getCollision())) {
            //System.out.println(myCollision.intersects(c.getCollision()));
            //I hit something but where?
            Ellipse2D.Float topCollision = new Ellipse2D.Float((int) getLocation().getX() - startXCollision, (int) getLocation().getY() + OffsetStartCollisionY, XCollisionWidth, collisionSize);
            if (Velocity.getY() > 0 && topCollision.intersects(entity.getCollision())) {
                Die();
            }
            Ellipse2D.Float rightCollision = new Ellipse2D.Float((int) getLocation().getX() + OffsetStartCollisionX, (int) getLocation().getY() - startYCollision, collisionSize, YCollisionWidth);
            if (Velocity.getX() > 0 && rightCollision.intersects(entity.getCollision())) {
                //then i'm running into something on the right				
                Die();
            }
            Ellipse2D.Float leftCollision = new Ellipse2D.Float((int) getLocation().getX() - startXLeftCollision, (int) getLocation().getY() - startYCollision, collisionSize, YCollisionWidth);
            if (Velocity.getX() < 0 && leftCollision.intersects(entity.getCollision())) {
                Die();
            }
        }

    }
    public void DrawBall(Graphics g) {
        g.setColor(C);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Location: " + getLocation(), 15, 55);
        g.drawString("Velocity: " + Velocity, 15, 80);
        //g.fillOval((int)(Location.getX() - Size.getX()/2 - ClassDemoForPlatformerLab.CameraX),(int)(Location.getY() - Size.getY()/2 - ClassDemoForPlatformerLab.CameraY), (int)Size.getX(), (int)Size.getY());	

        g.drawImage(sprite.currentFrame.getScaledInstance((int) Size.getX(), (int) Size.getY(), BufferedImage.SCALE_FAST),
                (int) (getLocation().getX() - Size.getX() / 2 - ClassDemoForPlatformerLab.CameraX), (int) (getLocation().getY() - Size.getY() / 2 - ClassDemoForPlatformerLab.CameraY), null);
        //debug draw collision
//			g.setColor(Color.magenta);
//			Ellipse2D.Float bottomCollision = new Ellipse2D.Float((int)Location.getX()-10- ClassDemoForPlatformerLab.CameraX,(int)Location.getY()+15, 20,5);
//			((Graphics2D) g).fill(bottomCollision);
//			Ellipse2D.Float rightCollision = new Ellipse2D.Float((int)Location.getX()+15- ClassDemoForPlatformerLab.CameraX,(int)Location.getY()-10, 5,20);
//			((Graphics2D) g).fill(rightCollision);
//			Ellipse2D.Float leftCollision = new Ellipse2D.Float((int)Location.getX()-20- ClassDemoForPlatformerLab.CameraX,(int)Location.getY()-10, 5,20);
//			((Graphics2D) g).fill(leftCollision);
    }
    //KeyListener

    public void keyTyped(KeyEvent e) {
        /*do nothing*/ }

    /**
     * Handle the key-pressed event from the text field.
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            UP = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            DOWN = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            RIGHT = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            LEFT = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            SPACE = true;
        }
    }

    /**
     * Handle the key-released event from the text field.
     */
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            UP = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            DOWN = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            RIGHT = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            LEFT = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            SPACE = false;
        }
    }
}
