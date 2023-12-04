package outerSpaliens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class myPanel extends JPanel
{
    ClassDemoForPlatformerLab game;
    String fileName = "outerspalienmenu.png";
    BufferedImage image = loadImage(fileName);
    public myPanel(ClassDemoForPlatformerLab game)
    {
        this.setLayout(new BorderLayout(20,20));
        this.game = game;
        JPanel side = new JPanel();
        //how to arrange our components on our panel
            //see https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html for more info
        side.setLayout(new GridLayout(10,2,20,20));		
        //these are the components that will be in the side panel
        {
            //see setFont if you want to make the name bigger or in a different font, or the Game Name could be an image
            side.add(new JLabel("Outer Spalien"));
                
            //Yay, buttons
            JButton startButton = new JButton("Play");
            //what happens when you click the button?
            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) 
                {
                    //tell the game to go to start somehow (set an enum, or boolean or string, DO NOT Directly call the game to start in a method here)
                    System.out.println("Go to start game stuff");	
                    startGame();
                    SwingUtilities.getWindowAncestor(startButton).dispose();				
            }});
            //Everything has a setSize, setPreferredSize, setMaximumSize and setMinimumSize. Which one that is used depends on the layout that is set
            startButton.setMaximumSize(new Dimension(100,40));
            side.add(startButton);
            
            JButton creditButton = new JButton("Credits");
            creditButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) 
                {
                    System.out.println("Go to credits stuff");					
            }});
            creditButton.setMaximumSize(new Dimension(100,40));
            side.add(creditButton);
            
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) 
                {
                    System.exit(0);
                    
            }});
            exitButton.setMaximumSize(new Dimension(100,40));
            side.add(exitButton);
        }
        //padding stuffs can be done with borders or with blank components
        final int PADDING = 50;
        this.setBorder(BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING));
        this.add(side,BorderLayout.EAST);
        
        //add a fun thing in the center
        JLabel center = new JLabel();
        center.setIcon(new ImageIcon(loadImage(fileName)));
        this.add(center);
        
    }
    private static BufferedImage loadImage(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                return ImageIO.read(file);
            } else {
                System.out.println("Could not find " + fileName);
                return null;
            }
        } 
        catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            return null;
        }
    }
    public void startGame() {
        game.startGame();
        
    }


}


