// Ananth Chillarige, Forest Yang
// TrigDash.java

//This is our final draft.

/* Game description: The user controls a character that appears to be automatically moving forward on the
ground. The user can press the space bar to jump to avoid obstacles in the air and on the ground. The user
can also jump or run into power-up boxes. After colliding with a power-up box and successfully solving the 
following unit circle problem, the character will gain a various power-up(invincibility, slowing down, etc.)
The game progressively gets faster, and the user gains points based on distance traveled. The game ends when
the user collides with an obstacle.*/

//import classes 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Scanner;
import java.util.Arrays;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter; 

/*------------------- Open Forest  ----------------------------------*/
// main frame class extends JFrame
public class TrigDash extends JFrame
{
	//declare various instances of panels
	private GameHolder holder;
	private CardLayout cards;
	private HomePanel hp;
	private OptionsPanel op;
	private InstructionsHolder ih;
	private GameplayHolder gh; 
	private PowerupPanel pwp;
	private CardLayout gameCards = new CardLayout();
	private Font font;
	private String imageName;

	/*------------------- Close Forest  ----------------------------------*/

	/*------------------- Open Ananth  ----------------------------------*/

	private int score = 0;
	private int charx = 50;
    private int chary = 500;
    private int scorex = 50;
    private int xvel, yvel;
    private int framesPassed = 0;
    private int oldScore = 0;
    private JLabel scoreReport;
    private int invincibility;
    private int flight;

	
	private Timer gameTimer;
	private Timer timeLimiter;
	
	private String name;
	private int frqLimit;
	private int mcLimit;
	private double scrollVelocity;
	private double permScrollVelocity;
	private boolean dead = false;
	private int width;

	private int[] widthArray = new int[1000];
    private int[] spacing = new int[1000];
    private int[] obstX = new int[1000];
    private int[] obstY = new int[1000];
    private boolean[] lethal= new boolean[1000];
    private Rectangle[] rectangles = new Rectangle[1000];
    private Rectangle trigger;	  
    
    //variables used for high scores
    int difficulty = 1; //1 is ez, 2 is md, 3 is hd
    private HighScoreTable hst;
    private JTextArea scoreBoard;
    /*------------------- Close Ananth  ----------------------------------*/

    /*------------------- Open Forest  ----------------------------------*/
	
	//array for angles that are to be used for question generation
	//(will be filled with degrees or radians based on user settings)
	private String[] angles;
	
	//final arrays for the 16 angles of the unit circle
	//and the answers for their trigonometric functions
	private final String[] degrees = {"0","30","45","60","90","120","135","150",
									  "180","210","225","240","270","300","315","330"};
	
	private final String[] radians = {"0","\u03c0/6","\u03c0/4","\u03c0/3",
									  "\u03c0/2","2\u03c0/3","3\u03c0/4","5\u03c0/6",
									  "\u03c0","7\u03c0/6","5\u03c0/4","4\u03c0/3",
									  "3\u03c0/2","5\u03c0/3","7\u03c0/4","11\u03c0/6"};
	
	private final String[][] answers = 
		{
			//answers for sine
			{"0","1/2","\u221a2/2","\u221a3/2",
			 "1","\u221a3/2","\u221a2/2","1/2",
			 "0","-1/2","-\u221a2/2","-\u221a3/2",
			 "-1","-\u221a3/2","-\u221a2/2","-1/2"},
			
			//answers for cosine
			{"1","\u221a3/2","\u221a2/2","1/2",
			 "0","-1/2","-\u221a2/2","-\u221a3/2",
			 "-1","-\u221a3/2","-\u221a2/2","-1/2",
			 "0","1/2","\u221a2/2","\u221a3/2"},
			
			//answers for tangent
			{"0","\u221a3/3","1","\u221a3",
			 "undefined","-\u221a3","-1","-\u221a3/3",
			 "0","\u221a3/3","1","\u221a3",
			 "undefined","-\u221a3","-1","-\u221a3/3"}
		};
/*------------------- Close Forest  ----------------------------------*/

/*------------------- Open Ananth  ----------------------------------*/

	//method for generating obstacles
		//includes properties such as height, width, and spacing
	public void generateObstacles()
	{
		   //generating y coord
			    for(int i=0; i<obstY.length; i++)
			            obstY[i] = (int)(Math.random()*50+400);
			    
			    //generating width
			     for(int i=0; i<widthArray.length; i++)
			            widthArray[i] = (int)(Math.random()*20+30);
			              
			     //generating spacing
			    for(int i=0; i<spacing.length; i++)
			    {
			    	if(i == 0)
			    		spacing[i] = (int)(Math.random()*1+1000);
			    	else
			    		spacing[i] = (int)(Math.random()*300+550);
			    }
					
					
				 //generating x coord
			    for(int i=0; i<obstX.length; i++)
			    {
					if(i == 0)
						obstX[i] = spacing[i];
					else
						obstX[i] = obstX[i-1] + spacing[i];						
				}
															                
			    //generating rectangles
			    for(int i=0; i<rectangles.length; i++)
			            rectangles[i] = new Rectangle(obstX[i],obstY[i],widthArray[i],200);
				trigger = new Rectangle(50,500,50,50);
			    
			    //differentiating between obstacles and power boxes
			    for(int i=0; i<lethal.length; i++) 
			    {
					int decider = (int)(Math.random()*3+1);
					if(decider == 1)
						lethal[i] = false;				
					else
						lethal[i] = true;				
				}
	}


	
	
	public static void main(String[] args)
	{
		TrigDash game = new TrigDash();
		// System.out.println("sin(2\u03c0/3) = \u221a3/2");
	}
	
	//constructor with main frame (card layout)
	public TrigDash()
	{
		super("Trig Dash");
		setSize(1000,600);
		setLocation (0,0);	// sets location of panel on the computer screen
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		font = new Font("Helvetica",Font.BOLD,14);
		name = "";
		frqLimit = 20;
		mcLimit = 25;
		scrollVelocity = permScrollVelocity = 7;
		invincibility = -1;
		flight = -1;
		
		//name of image for loading character art
		imageName = "character.png";
		
		//Open Forest
		for(int n = 0;n < degrees.length;n++)
			degrees[n] += "\u00b0";
		
		angles = new String[16];
		for(int i = 0;i < angles.length;i++)
			angles[i] = degrees[i];
			
		font = new Font("Sans-Serif",Font.PLAIN,20);
		//close Forest
		
		holder = new GameHolder();
		setContentPane(holder);
		setVisible(true);
	}

	/*------------------- Close Ananth  ----------------------------------*/

	/*------------------- Open Forest  ----------------------------------*/
	
	//reset
		//reset everything
	// main holder panel for card layout
	public class GameHolder extends JPanel
	{
		public GameHolder()
		{
			cards = new CardLayout();
			setLayout(cards);
			
			// add various main panels of the game
			hp = new HomePanel();
			add(hp,"HomePanel");
			
			op = new OptionsPanel();
			add(op,"OptionsPanel");
			
			ih = new InstructionsHolder();
			add(ih,"InstructionsHolder");

			gh = new GameplayHolder();
			add(gh,"GameplayHolder");
		}
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
		}
	}
	//HomePanel class extends JPanel implements ActionListener
	public class HomePanel extends JPanel
	{	
		LogoPanel lp;
		ButtonsPanel bp;
		
		//constructor (border Layout)
		public HomePanel()
		{
			setLayout(new BorderLayout(0,0));
			// panel with logo on top, 
			// panel with buttons for navigation on bottom
			lp = new LogoPanel();
			bp = new ButtonsPanel();
			
			add(bp,BorderLayout.SOUTH);
			add(lp,BorderLayout.CENTER);
		}
		
		// panel that contains logo and name field
		public class LogoPanel extends JPanel implements ActionListener
		{
			private JTextField nameField;
			private Image logo;
			private JButton saveName;
			
			public LogoPanel()
			{
				setBackground(Color.WHITE);
				
				nameField = new JTextField("Name",10);
				nameField.addActionListener(this);
				add(nameField);
				
				saveName = new JButton("Save");
				saveName.addActionListener(this);
				add(saveName);
				
				logo = Toolkit.getDefaultToolkit().getImage("logo.png");
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(logo, 200, 100, this);
			}
			public void actionPerformed(ActionEvent e)
			{
				// save user's Name
				name = nameField.getText();
			}
		}
		
		public class ButtonsPanel extends JPanel implements ActionListener
		{
			private JButton playButton, optionsButton, instructionsButton;
			public ButtonsPanel()
			{
				setBackground(Color.BLACK);
				
				/* Dimension buttonsD = new Dimension(1,150);
				setPreferredSize(buttonsD); */
				
				// make buttons for "Play", "Options", "Instructions"
				playButton = new JButton("Play");
				playButton.addActionListener(this);
				add(playButton);
				
				optionsButton = new JButton("Options");
				optionsButton.addActionListener(this);
				add(optionsButton);
				
				instructionsButton = new JButton("Instructions");
				instructionsButton.addActionListener(this);
				add(instructionsButton);
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
			}
			public void actionPerformed(ActionEvent e)
			{
				// show appropriate panel based on button pressed 
				String buttonName = e.getActionCommand();
				if(buttonName.equals("Play"))
				{
					cards.show(holder,"GameplayHolder");
					gameTimer.start();
				}
					
				else if(buttonName.equals("Options"))
					cards.show(holder,"OptionsPanel");
				else if(buttonName.equals("Instructions"))
					cards.show(holder, "InstructionsHolder");
			}
		}
	}
				
	//GameHolder class extends JPanel implements ActionListener
	public class GameplayHolder extends JPanel
	{
		private GamePanel gp;
		private PausePanel pp;
		private GameOverPanel gop;
		//private PowerupPanel pwp;
   		
		//constructor (Card Layout)
		public GameplayHolder()
		{
			
			setLayout(gameCards);
			
			//add GamePanel
			gp = new GamePanel();
			add(gp,"GamePanel");
			//add PausePanel
			pp= new PausePanel();
			add(pp,"PausePanel");
			//add GameOverPanel
			gop = new GameOverPanel();
			add(gop,"GameOverPanel");
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
		}
		/*------------------- Close Forest  ----------------------------------*/
		
		/*-------------- Open Ananth -----------------------------------*/
		public class GamePanel extends JPanel implements KeyListener
		{
			//declaring and initializing field variables
		    
		    private int JUMPVEL = -23;
		    private final int GRAVITY_CONSTANT = 1; 
		    private GameTimerHandler gth;
		    
		    public GamePanel() 
		    {
			   
			    generateObstacles();
			    addKeyListener(this);
		        //timer that increments the framesPassed to increase background scrolling pace over time
		        //also manipulates yvelocity as well as characters y position
			    gth = new GameTimerHandler();
		        gameTimer = new Timer(17, gth);	
			}
		    
		    //method for drawing the rectangles
		    public void drawRectangles(Graphics g) 
		    {
				for(int x=0; x<rectangles.length; x++) 
		         {                        
					if(lethal[x] == false) //powerup
						g.setColor(Color.WHITE);
					else
						g.setColor(Color.BLACK); //obstacle
					g.fillRect((int)rectangles[x].getX(),(int)rectangles[x].getY(),
						(int)rectangles[x].getWidth(),(int)rectangles[x].getHeight());               
				}
				g.setColor(Color.BLACK);
				for(int i=0; i<=9000000; i+=1000000)
					g.fillRect(0,550,1000000,50);		
			}
		    
		    //translates the panel and draws hurt box as well as calls methods to generate obstacles/powerups
		    public void paintComponent(Graphics g) 
		    {					        
		         //making character rectangle
			    trigger = new Rectangle(charx,chary,50,50);
		        super.paintComponent(g);
		        requestFocus();
		        g.translate(-charx+100, 0);          
				drawRectangles(g);
				g.setColor(Color.BLACK);
				
				for(int i=0; i<rectangles.length; i++)
				{
					if(rectangles[i].intersects(trigger))
					{
						if(lethal[i]==false)
						{
							gameTimer.stop();
							width = (int)(rectangles[i].getWidth())+(int)(trigger.getWidth());
							// g.translate(-scrollVelocity * framesPassed - width,0);
							scorex+=width;
							
							// initialize and add PowerupPanel here so that new one
							// is shown for each collision
							pwp = new PowerupPanel();
							gh.add(pwp,"PowerupPanel");
							gameCards.show(gh,"PowerupPanel");
							timeLimiter.start();
						}	
						else if(invincibility <= 0)
						{
							gameTimer.stop();
							//adding score to JLabel message in the gameOver Panel
							scoreReport.setText("Congrats! You earned "+score+" points!");
							
							if(difficulty == 1)
								hst = new HighScoreTable("ez.txt");
							else if(difficulty == 2)
								hst = new HighScoreTable("md.txt");
							else
								hst = new HighScoreTable("hd.txt");
							hst.addScore(score,name);
							//resetting the text area
							scoreBoard.setText("");
							//printing out top 10 scores
							for (int n=0; n<hst.getData().length; n++)
							{
								if(n<10)
								{
									String highName = hst.getData()[n].getName();
									int highScore = hst.getData()[n].getScore();
									System.out.println(highName+","+highScore);
									scoreBoard.append(n+1+"."+highName+"\t"+highScore+"\n");	
								}
								
							}
							gameCards.show(gh,"GameOverPanel");							
						}
							
						
					}													
				}  
				
				//character hurt box drawing
				g.drawRect((int)trigger.getX(),(int)trigger.getY(),(int)trigger.getWidth(),(int)trigger.getHeight());
				
				if(invincibility > 0 || flight > 0)
					imageName = "character-purple.png";
				else
					imageName = "character.png";
				
				Image character = Toolkit.getDefaultToolkit().getImage(imageName);
				g.drawImage(character, charx-28, chary - 46, this);
				
				g.drawString("Score:"+score,scorex,50);
				if(invincibility >= 0)
					g.drawString("Invincibility remaining: " + invincibility, scorex, 100);
				if(flight >= 0)
					g.drawString("Flight time remaining: " + flight, scorex, 120);
				//collision detection
				      
		    }

		    /*--------------------------------------LISTENERS---------------------------------------------*/
		    public void keyTyped(KeyEvent e){}
		    public void keyReleased(KeyEvent e){}
		    //when space bar is pressed set yvel back so character starts to go up again
		    public void keyPressed(KeyEvent e)
		    {
		        if(e.getKeyCode() == KeyEvent.VK_SPACE) 
		        {
		        	if(chary==500 || flight > 0)
		            	yvel=JUMPVEL;
		        }
		        else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		        {
		        	gameTimer.stop();
		        	gameCards.show(gh,"PausePanel");
		        }
		        	
		    }
		    
			public class GameTimerHandler implements ActionListener
			{
				public void actionPerformed(ActionEvent e)
	            {
	                framesPassed++;
	                score+=25;
	                charx += scrollVelocity;
	                scorex += scrollVelocity;
	                yvel+=GRAVITY_CONSTANT;
	                if(chary+yvel<=500 && chary+yvel>=0)
	                    chary += yvel;
	                else if(chary + yvel > 500)
	                	chary = 500;
	                else if(chary + yvel < 0)
	                	chary = 0;
	                if(framesPassed%60 == 0)
	                {
						flight--;
						invincibility--;
					}
					scrollVelocity += 0.0001;    		                
	                repaint();
	            }
			}
		}
		

		
		
		//panel that is shown when user presses escape key during game play.
		//has options for quitting the game or resuming
		public class PausePanel extends JPanel implements ActionListener
		{
			private JButton quitButton,resumeButton;
			public PausePanel()
			{
				quitButton = new JButton("quit");
				quitButton.addActionListener(this);
				add(quitButton);
				
				resumeButton = new JButton("resume");
				resumeButton.addActionListener(this);
				add(resumeButton);
				
				JLabel degRadLabel = new JLabel("Units");
				add(degRadLabel);
				
				ButtonGroup degRad  = new ButtonGroup();
				
				JRadioButton degrees = new JRadioButton("Degrees");
				degrees.addActionListener(this);
				add(degrees);
				degRad.add(degrees);
				
				JRadioButton radians = new JRadioButton("Radians");
				radians.addActionListener(this);
				add(radians);
				degRad.add(radians);
				
				JRadioButton randUnits = new JRadioButton("Random");
				randUnits.addActionListener(this);
				add(randUnits);
				degRad.add(randUnits);
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
			}
			public void actionPerformed(ActionEvent e)
			{
				String str = e.getActionCommand();
				if(str.equals("resume"))
				{
					gameCards.show(gh,"GamePanel");
					gameTimer.start();

				} //resumes game
					
				else if(str.equals("quit"))
				{
					gameCards.show(gh,"GamePanel");
					generateObstacles();
					score = 0;
		       		charx = 50;
		       		chary = 500;
		       		scorex = 50;
		       		yvel = 0;
		       		framesPassed = 0;
		       		
		       		scrollVelocity = permScrollVelocity;
		       		flight = -1;
		       		invincibility = -1;
		       		imageName = "character.png";
		       		
					cards.show(holder,"HomePanel");
				} //goes back to HomePanel
				// set the angles array to be used in generating questions
			// as degrees or radians
				else if(str.equals("Degrees"))
				{
					for(int i = 0;i < angles.length;i++)
						angles[i] = degrees[i];
				}
				else if(str.equals("Radians"))
				{
					for(int i = 0;i < angles.length;i++)
						angles[i] = radians[i];
				}
				else if(str.equals("Random"))
				{
					int choose = 0;
					for(int i = 0;i < angles.length;i++)
					{
						choose = (int)(Math.random()*2+0);
						if(choose == 0)
							angles[i] = radians[i];
						else
							angles[i] = degrees[i];
					}
				}
			}
		}

		// panel shown when the user loses
		public class GameOverPanel extends JPanel implements ActionListener
		{
			private JButton backHome, playAgain;
			private JPanel buttonPanel;
			private JPanel goToOptions;

			public GameOverPanel()
			{
				setLayout(new BorderLayout());

				buttonPanel = new JPanel();
				
				// add buttons to return to home, or immediately play again
				backHome = new JButton("Back to Home");
				backHome.addActionListener(this);
				buttonPanel.add(backHome);
				
				playAgain = new JButton("Play Again");
				playAgain.addActionListener(this);
				buttonPanel.add(playAgain);

				// display the user's score, as well as its ranking among all scores
				scoreReport = new JLabel();
				System.out.println(score);

				scoreBoard = new JTextArea(10,50);
				scoreBoard.setEditable(false);
				
				goToOptions = new JPanel();
				JLabel suggestion = new JLabel("Want to try a different difficulty?");
				goToOptions.add(suggestion);
				JButton optionsButton = new JButton("Options");
				optionsButton.addActionListener(this);
				goToOptions.add(optionsButton);

				add(scoreReport,BorderLayout.NORTH);
				add(buttonPanel,BorderLayout.SOUTH);
				add(goToOptions,BorderLayout.EAST);
				add(scoreBoard,BorderLayout.CENTER);
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
			}
			public void actionPerformed(ActionEvent e)
			{
				// reset variables/settings for the next gameplay session
				String str = e.getActionCommand();
				generateObstacles();
				score = 0;
				charx = 50;
				chary = 500;
				scorex = 50;
				yvel = 0;
				framesPassed = 0;
				
				scrollVelocity = permScrollVelocity;
				flight = -1;
				invincibility = -1;
				imageName = "character.png";
				
				gameCards.show(gh,"GamePanel");
				
				if(str.equals("Play Again"))
				{	
					gameTimer.start();

				} //resumes game
					
				else if(str.equals("Back to Home"))
				{
					cards.show(holder,"HomePanel");
				} //goes back to HomePanel
				
				else
					cards.show(holder,"OptionsPanel");
			}
		}
	}	

	/*-------------- Close Ananth -----------------------------------*/

	/*-------------- Open Forest -----------------------------------*/
	
	//class OptionsPanel extends JPanel implements ActionListener	
	public class OptionsPanel extends JPanel implements ActionListener
	{
		public OptionsPanel() //constructor
		{
			setBackground(Color.WHITE);
			setLayout(new FlowLayout(FlowLayout.CENTER,1000,40));
			
			//add button to return to Home
			JButton back = new JButton("Back");
			back.addActionListener(this);
			add(back);
			
			//add radio buttons for difficulty
			JLabel difficultyLabel = new JLabel("Difficulty");
			add(difficultyLabel);
			
			ButtonGroup difficulty = new ButtonGroup();
			
			JRadioButton easy = new JRadioButton("Easy");
			easy.addActionListener(this);
			add(easy);
			difficulty.add(easy);
			easy.setSelected(true);
			
			JRadioButton medium = new JRadioButton("Medium");
			medium.addActionListener(this);
			add(medium);
			difficulty.add(medium);
			
			JRadioButton hard = new JRadioButton("Hard");
			hard.addActionListener(this);
			add(hard);
			difficulty.add(hard);
			
			//add radio buttons for degrees/radians
			JLabel degRadLabel = new JLabel("Units");
			add(degRadLabel);
			
			ButtonGroup degRad  = new ButtonGroup();
			
			JRadioButton degrees = new JRadioButton("Degrees");
			degrees.addActionListener(this);
			add(degrees);
			degRad.add(degrees);
			degrees.setSelected(true);
			
			JRadioButton radians = new JRadioButton("Radians");
			radians.addActionListener(this);
			add(radians);
			degRad.add(radians);
			
			JRadioButton randUnits = new JRadioButton("Random");
			randUnits.addActionListener(this);
			add(randUnits);
			degRad.add(randUnits);

		}
		public void paintComponent(Graphics g)//paintComponent
		{
			super.paintComponent(g);//super.paintComponent(g)
		}
		public void actionPerformed(ActionEvent e)
		{
			String buttonName = e.getActionCommand();
			
			// return to Home if back button is pressed
			if(buttonName.equals("Back"))
				cards.show(holder, "HomePanel");
			
			//change time limits and speed of game
			//appropriately based on difficulty
			if(buttonName.equals("Easy"))
			{
				mcLimit = 20;
				frqLimit = 25;
				scrollVelocity = 7;
				difficulty = 1;
				
			}
			else if(buttonName.equals("Medium"))
			{
				mcLimit = 10;
				frqLimit = 15;
				scrollVelocity = 10;
				difficulty = 2;
			}
			else if(buttonName.equals("Hard"))
			{
				mcLimit = 5;
				frqLimit = 10;
				scrollVelocity = 13;
				difficulty = 3;
			}
			
			permScrollVelocity = scrollVelocity;
			
			
			// set the angles array to be used in generating questions
			// as degrees or radians
			if(buttonName.equals("Degrees"))
			{
				for(int i = 0;i < angles.length;i++)
					angles[i] = degrees[i];
			}
			else if(buttonName.equals("Radians"))
			{
				for(int i = 0;i < angles.length;i++)
					angles[i] = radians[i];
			}
			else if(buttonName.equals("Random"))
			{
				int choose = 0;
				for(int i = 0;i < angles.length;i++)
				{
					choose = (int)(Math.random()*2+0);
					if(choose == 0)
						angles[i] = radians[i];
					else
						angles[i] = degrees[i];
				}
			}
			// System.out.println(mcLimit);
		}
	}

	/*------------------------- Open Ananth--------------------------------*/
	public class InstructionsHolder extends JPanel
	{
		private InstructionsPanel1 ip1;
		private InstructionsPanel2 ip2;
		private InstructionsPanel3 ip3;
		private InstructionsPanel4 ip4;
		private CardLayout instructionsCards;

		// holds 4 instructions panels that the user can navigate between
		public InstructionsHolder()
		{
			instructionsCards = new CardLayout();
			setLayout(instructionsCards);

			ip1 = new InstructionsPanel1();
			add(ip1,"InstructionsPanel1");
			
			ip2 = new InstructionsPanel2();
			add(ip2,"InstructionsPanel2");
			
			ip3 = new InstructionsPanel3();
			add(ip3,"InstructionsPanel3");
			
			ip4 = new InstructionsPanel4();
			add(ip4,"InstructionsPanel4");
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
		}
		
		// first instructions panel
		public class InstructionsPanel1 extends JPanel implements ActionListener
		{
			private Image screenshot1;
			public InstructionsPanel1()
			{
				setBackground(Color.WHITE);
				
				JLabel instructionsLabel1a = new JLabel("Welcome to Trig Dash! In this game, "+
					"you control a character that is moving forward.");
				add(instructionsLabel1a);
				JLabel instructionsLabel1b = new JLabel("You will encounter boxes of "+
					"different colors. Black boxes are obstacles, while white boxes contain power-ups.");
				add(instructionsLabel1b);
				
				JButton next = new JButton("Next");
				next.addActionListener(this);
				add(next);
				
				JButton back = new JButton("Back to home");
				back.addActionListener(this);
				add(back);
				
				screenshot1 = Toolkit.getDefaultToolkit().getImage("1.png");
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(screenshot1, 50, 50, 900, 500, this);
			}
			public void actionPerformed(ActionEvent e)
			{
				String str = e.getActionCommand();
				if(str.equals("Next"))
					instructionsCards.next(ih);
				else
					cards.show(holder, "HomePanel");
			}
		}
		
		// second instructions panel
		public class InstructionsPanel2 extends JPanel implements ActionListener
		{
			private Image screenshot2;
			public InstructionsPanel2()
			{
				setBackground(Color.WHITE);
				
				JLabel instructionsLabel2 = new JLabel("Press the spacebar while your character is on the "+
						 "ground to make him jump!");
				JLabel instructionsLabel2b = new JLabel("Try not to hit the obstacles, but run into the white boxes "+
						 	 "to receive a random fun powerup.");
				add(instructionsLabel2);
				add(instructionsLabel2b);

				JButton next = new JButton("Next");
				next.addActionListener(this);
				add(next);
				JButton previous = new JButton("Previous");
				previous.addActionListener(this);
				add(previous);
				
				screenshot2 = Toolkit.getDefaultToolkit().getImage("2.png");
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(screenshot2, 50, 50, 900, 500, this);
			}
			public void actionPerformed(ActionEvent e)
			{
				String str = e.getActionCommand();

				if(str.equals("Next"))
					instructionsCards.next(ih);
				else
					instructionsCards.previous(ih);
			}
		}
		
		// third instructions panel
		public class InstructionsPanel3 extends JPanel implements ActionListener
		{
			private Image screenshot3;
			public InstructionsPanel3()
			{
				setBackground(Color.WHITE);
				
				JLabel instructionsLabel3a = new JLabel("When you run into a white box, you have to answer a  "+
					"unit circle question within a specified time, before gaining access to your powerup.");
				add(instructionsLabel3a);
				
				JLabel instructionsLabel3b = new JLabel(" You can either earn flight, a slowed down background, or invincibility.");
				add(instructionsLabel3b);

				JButton next = new JButton("Next");
				next.addActionListener(this);
				add(next);
				JButton previous = new JButton("Previous");
				previous.addActionListener(this);
				add(previous);
				
				screenshot3 = Toolkit.getDefaultToolkit().getImage("3.png");
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(screenshot3, 50, 50, 900, 500, this);
			}
			public void actionPerformed(ActionEvent e)
			{
				String str = e.getActionCommand();

				if(str.equals("Next"))
					instructionsCards.next(ih);
				else
					instructionsCards.previous(ih);
			}
		}
		
		// final instructions panel, allow them to access gameplay
		public class InstructionsPanel4 extends JPanel implements ActionListener
		{
			private Image screenshot4;
			public InstructionsPanel4()
			{
				setBackground(Color.WHITE);
				
				JLabel instructionsLabel4a = new JLabel("The objective of the game is to survive as long as you can. Press 'esc' to pause.");
				add(instructionsLabel4a);

				JLabel instructionsLabel4b = new JLabel("If you feel that the game is too easy, go to the options menu in the home panel and change the "
					+"difficulty.");
				add(instructionsLabel4b);
				
				JLabel instructionsLabel4c = new JLabel("There, you can also change between radians and degrees. Enjoy playing Trig Dash!");
				add(instructionsLabel4c);
				
				JButton next = new JButton("Play");
				next.addActionListener(this);
				add(next);
				JButton previous = new JButton("Previous");
				previous.addActionListener(this);
				add(previous);
				
				screenshot4 = Toolkit.getDefaultToolkit().getImage("4.png");
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(screenshot4, 50, 70, 900, 500, this);
			}
			public void actionPerformed(ActionEvent e)
			{
				String str = e.getActionCommand();

				if(str.equals("Play"))
				{
					cards.show(holder,"GameplayHolder");
					gameTimer.start();
					instructionsCards.first(ih);
				}
				else
					instructionsCards.previous(ih);
			}
		}
	}
	/*------------------------- Close Ananth--------------------------------*/
	
	/*------------------------- Open Forest--------------------------------*/
	public class PowerupPanel extends JPanel
	{
		//constructor
			//import pictures of us(maybe)
			//add JLabel for our names
		//paintComponent
			//super.paintComponent(g)
		
		private QuestionPanel qp;
		private FeedbackPanel fp;
		private CardLayout questionCards;
		
		private JLabel answerRight; // congratulations
		private JLabel answerWrong; // answer explanation
		private boolean correct; // boolean to determine whether arc is drawn
		
		private int angleIndex;
		private int trigRow;
		
		private String longFunc;
		private String funcFormula;
		
		private String powerUp;
		private JLabel powerUpLabel;
	
		public PowerupPanel() //constructor
		{
			setBackground(Color.PINK);
			
			questionCards = new CardLayout();
			setLayout(questionCards);
			
			correct = true;
			
			qp = new QuestionPanel();
			fp = new FeedbackPanel();
			add(qp,"QuestionPanel");
			add(fp,"FeedbackPanel");
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
		}
		public class QuestionPanel extends JPanel implements ActionListener
		{
			private JTextField answerField;
			private int questionType;
			private String questionAngle;
			private TimeLimitHandler tlh;
			private int timeLimit;
			private String timeLimitString;
			
			public QuestionPanel()
			{
				setBackground(Color.WHITE);
				
				setLayout(new FlowLayout(FlowLayout.CENTER,1000,50));
				
				//chooses random angle from array and presents in JLabel
				//for question
				angleIndex = (int)(Math.random()*16+0);
				trigRow = (int)(Math.random()*3+0);
				
				String func = "";
				if(trigRow == 0)
				{
					func = "sin";
					longFunc = "sine";
					funcFormula = "y/r";
				}
				else if(trigRow == 1)
				{
					func = "cos";
					longFunc = "cosine";
					funcFormula = "x/r";
				}
				else
				{
					func = "tan";
					longFunc = "tangent";
					funcFormula = "y/x";
				}
				
				JLabel question = new JLabel("What is " + func + "(" + 
											 angles[angleIndex] + ")" + "?");
				add(question);
				
				//Math.Random to choose between multiple choice or free response
				questionType = (int)(Math.random()*2+0);
				
				if(questionType == 0)
				{
					// add button so user can type sqrt symbol
					JButton sqrt = new JButton("\u221a");
					sqrt.addActionListener(this);
					add(sqrt);
					
					// add text field for user to type answer
					answerField = new JTextField(10);
					answerField.addActionListener(this);
					add(answerField);
					
					timeLimit = frqLimit;
				}
				else
				{
					int answerCol, answerRow;
					answerCol = angleIndex;
					answerRow = trigRow;
					
					// make 4 radio buttons for 4 choices
					JRadioButton [] choices = new JRadioButton[4];
					ButtonGroup choiceGroup = new ButtonGroup();
					
					String [] answerChoice = new String[4];
					answerChoice[0] = answers[trigRow][angleIndex];
					
					choices[0] = new JRadioButton(answerChoice[0]);
					
					for(int i = 1;i <= choices.length-1;i++)
					{
						// randomly choose any answer in the answer array,
						// while making sure it is not the correct answer
						while(answers[answerRow][answerCol].equals(answerChoice[0])
							  || answers[answerRow][answerCol].equals(answerChoice[1])
							  || answers[answerRow][answerCol].equals(answerChoice[2]))
						{
							answerCol = (int)(Math.random()*16+0);
							answerRow = (int)(Math.random()*3+0);
						}
						answerChoice[i] = answers[answerRow][answerCol];
						choices[i] = new JRadioButton(answerChoice[i]);
						
						answerCol = angleIndex;
						answerRow = trigRow;
					}
					
					shuffle(choices); // shuffle the choices so the ordering is random
					
					for(int i = 0;i < choices.length;i++)
					{
						choices[i].addActionListener(this);
						choiceGroup.add(choices[i]);
						add(choices[i]);
					}
					
					timeLimit = mcLimit;
				}
				tlh = new TimeLimitHandler();
				timeLimiter = new Timer(1000,tlh);
			}
		
			public void shuffle(JRadioButton [] buttons)
			{
				int randomIndex = 0;
				JRadioButton storage = null;
				
				// switch each button with a random other button in the array
				for(int i = 0;i < buttons.length;i++)
				{
					randomIndex = (int)(Math.random()*buttons.length);
					storage = buttons[i];
					buttons[i] = buttons[randomIndex];
					buttons[randomIndex] = storage;
				}
			}
	
			
			public void givePowerup() //givePowerup()
			{ 
				//use Math.random() to decide which powerup to grant
				//if flight
				int rand = (int)(Math.random()*3+1);
				if(rand == 1)
				{
					// allow user to jump while not on ground
					flight = 10;
					powerUp = "You can fly now! Just keep pressing space.";
				}
				else if(rand == 2)
				{
					// temporarily take away hurt box
					invincibility = 10;
					powerUp = "You are now invincible!";
				}
				else if(rand == 3)
				{
					// snailode
					scrollVelocity*=.8;
					powerUp = "The game has slowed down!";
				}
				powerUpLabel.setText(powerUp);
				powerUpLabel.setVisible(true);
				
				score+=1000;
			}
			
			public void paintComponent(Graphics g)//paintComponent
			{
				// g.setFont(font);
				super.paintComponent(g);
				//if !answerRight 
					//draw image of correct answer & provide explanation
				//else
					//congrats & say which powerup
				timeLimitString = "Time remaining: " + timeLimit;
				g.drawString(timeLimitString,100,100);
			}
			//actionPerformed()
			public void actionPerformed(ActionEvent e)
			{
				String typedText = "";
				String buttonName = e.getActionCommand();
				if(questionType == 1)//if multiple choice
				{
					System.out.println("answer selected");
					if(buttonName.equals(answers[trigRow][angleIndex]))//if command matches answer in answer Array 
					{	
						givePowerup();//give powerup
						answerRight.setVisible(true);
					}
					else //else
					{
						answerWrong.setVisible(true);
						correct = false;
					}
					timeLimiter.stop();
					questionCards.show(pwp,"FeedbackPanel");
				}
				else //else if free response
				{
					if(buttonName.equals("\u221a")) //if sqrt button is pressed
					{
						//type sqrt sign
						typedText = answerField.getText();
						typedText += "\u221a";
						answerField.setText(typedText);
					}
					else//else get text for text field
					{
						typedText = answerField.getText();
						if(typedText.equals(answers[trigRow][angleIndex])) //if match
						{
							givePowerup();//give powerup
							//set answerRight visible
							answerRight.setVisible(true);
						}
						else
						{
							answerWrong.setVisible(true);
							correct = false; 
						}
						timeLimiter.stop();
						questionCards.show(pwp,"FeedbackPanel");
					}
				}
				fp.repaint(); //repaint FeedbackPanel to draw arc if necessary
			}
			
			public class TimeLimitHandler implements ActionListener
			{
				public void actionPerformed(ActionEvent e)
				{
					timeLimit--;
					System.out.println(timeLimit);
					qp.repaint();
					
					if(timeLimit == 0)
					{
						answerWrong.setVisible(true);
						correct = false;
						fp.repaint();
						
						questionCards.show(pwp,"FeedbackPanel");
						timeLimiter.stop();
					}
				}
			}
		}		
		public class FeedbackPanel extends JPanel implements ActionListener
		{
			private Image unitCircle;
			
			public FeedbackPanel()
			{
				setBackground(Color.WHITE);
				powerUp = "";
				
				answerRight = new JLabel("Congratulations!");
				add(answerRight);
				answerRight.setVisible(false);
				
				
				
				String explanation = "Incorrect. As shown in the unit circle, the "
									 + longFunc + " of "  + degrees[angleIndex]
									 + ", or " + radians[angleIndex] + " radians, is "
									 + funcFormula + " = " + answers[trigRow][angleIndex]
									 + ".";
									 
				answerWrong = new JLabel(explanation);
				add(answerWrong);
				answerWrong.setVisible(false);
				
				powerUpLabel = new JLabel(powerUp);
				add(powerUpLabel);
				powerUpLabel.setVisible(false);
				
				JButton backToGame = new JButton("Back to game");
				backToGame.addActionListener(this);
				add(backToGame);
				
				unitCircle = Toolkit.getDefaultToolkit().getImage("unit-circle.gif");
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				if(!correct)
				{
					int degreeParam = Integer.parseInt(degrees[angleIndex].substring
													  (0,degrees[angleIndex].indexOf("\u00b0")));
					g.setColor(Color.RED);
					g.drawImage(unitCircle,200,50,this);
					
					Graphics2D g2 = (Graphics2D) g;
					g2.setStroke(new BasicStroke(5));
					g2.drawArc(277,127,355,355,0,degreeParam);
				}
			}
			public void actionPerformed(ActionEvent e)
			{
				charx+=width;
				gameCards.show(gh,"GamePanel");
				gameTimer.start();
			}
		}
}
}

/*------------------------ Close Forest ------------------------*/
		