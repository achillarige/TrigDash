// Ananth Chillarige, Forest Yang
// TrigDash.java

//This is our second draft.

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
/* import java.util.Timer;	
import java.util.TimerTask; */

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
	private GameplayHolder gh; 
	private PowerupPanel pwp;
	CardLayout gameCards = new CardLayout();

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

	
	private Timer gameTimer;
	private String name;
	private int frqLimit;
	private int mcLimit;
	private int scrollVelocity;
	private boolean dead = false;

	private int[] widthArray = new int[1000];
    private int[] spacing = new int[1000];
    private int[] obstX = new int[1000];
    private int[] obstY = new int[1000];
    private boolean[] lethal= new boolean[1000];
    private Rectangle[] rectangles = new Rectangle[1000];
    private Rectangle trigger;	  

    //variables used for high scores
    int difficulty = 0; //1 is ez, 2 is md, 3 is hd
    private int[] ezArray = new int[10];
    private int[] mdArray = new int[10];
    private int[] hdArray = new int[10];
    private File ezList = new File("ez.txt");
    private File mdList = new File("md.txt");
    private File hdList = new File("hd.txt");
    //private Scanner fr = new Scanner();
    private PrintWriter writer;

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
	/* GameHolder gh = new GameHolder();
	OptionsPanel op = new OptionsPanel();
	InstructionsHolder ih = new InstructionsHolder();
	CreditsPanel cp = new CreditsPanel(); */
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
					int decider = (int)(Math.random()*4+1);
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
		
		name = "";
		frqLimit = 20;
		mcLimit = 25;
		scrollVelocity = 7;
		//Open Forest
		for(int n = 0;n < degrees.length;n++)
			degrees[n] += "\u00b0";
		
		angles = new String[16];
		for(int i = 0;i < angles.length;i++)
			angles[i] = degrees[i];
		
		angles = new String[16];
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
			
			public LogoPanel()
			{
				setBackground(Color.PINK);
				
				nameField = new JTextField("Name",10);
				nameField.addActionListener(this);
				add(nameField);
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
			}
			public void actionPerformed(ActionEvent e)
			{
				// save user's Name
				name = nameField.getText();
			}
		}
		
		public class ButtonsPanel extends JPanel implements ActionListener
		{
			private JButton playButton, optionsButton, instructionsButton, creditsButton;
			public ButtonsPanel()
			{
				setBackground(Color.CYAN);
				
				/* Dimension buttonsD = new Dimension(1,150);
				setPreferredSize(buttonsD); */
				
				// make buttons for "Play", "Options", "Instructions", "Credits"
				playButton = new JButton("Play");
				playButton.addActionListener(this);
				add(playButton);
				
				optionsButton = new JButton("Options");
				optionsButton.addActionListener(this);
				add(optionsButton);
				
				instructionsButton = new JButton("Instructions");
				instructionsButton.addActionListener(this);
				add(instructionsButton);
				
				creditsButton = new JButton("Credits");
				creditsButton.addActionListener(this);
				add(creditsButton);
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

				else if(buttonName.equals("Credits"))
				{
				
					// timeLimiter.start();
					// System.out.println(timeLimit);
				}
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
			//add PowerUpPanel
			//add LearningPanel
			//add GameOverPanel
			gop = new GameOverPanel();
			add(gop,"GameOverPanel");

			pwp = new PowerupPanel();
			add(pwp,"PowerupPanel");
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
		    public GamePanel() 
		    {
			   
			    generateObstacles();
			    addKeyListener(this);
		        //timer that increments the framesPassed to increase background scrolling pace over time
		        //also manipulates yvelocity as well as characters y position
		        gameTimer = new Timer(17, new ActionListener()
		        {
		            public void actionPerformed(ActionEvent e)
		            {
		                framesPassed++;
		                score+=25;
		                charx += scrollVelocity;
		                scorex += scrollVelocity;
		                yvel+=GRAVITY_CONSTANT;
		                if(chary+yvel<=500)
		                    chary += yvel;		                
		                repaint();
		            }
		        });		
			}
		    
		    //method for drawing the rectangles
		    public void drawRectangles(Graphics g) 
		    {
				for(int x=0; x<rectangles.length; x++) 
		         {                        
					if(lethal[x] == false) //powerup
						g.setColor(Color.CYAN);
					else
						g.setColor(Color.RED); //obstacle
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
		        g.translate(-scrollVelocity * framesPassed, 0);          
				drawRectangles(g);
				g.setColor(Color.BLACK);
				//character hurt box drawing
				g.drawRect((int)trigger.getX(),(int)trigger.getY(),(int)trigger.getWidth(),(int)trigger.getHeight());
				g.drawString("Score:"+score,scorex,50);
				//collision detection
				for(int i=0; i<rectangles.length; i++)
				{
					if(rectangles[i].intersects(trigger))
					{
						if(lethal[i]==false)
						{
							gameTimer.stop();
							gameCards.show(gh,"PowerupPanel");	
						}	
						else
						{
							gameTimer.stop();
							//adding score to JLabel message in the gameOver Panel
							scoreReport.setText("Congrats! You earned "+score+" points!");
							//taking file and converting Array
							//using different file depending on difficulty
							/*File file;
							if(difficulty == 1)
								file = ezList;
							else if(difficulty == 2)
								file = mdList;
							else
								file = hdList;
							fr = new Scanner(file);
							if(difficulty == 1)
							{
								for(i=0;i<ezArray.length;i++)
									//use string methods to properly retreive scores

							}
							else if(difficulty == 2)
							{
								for(i=0;i<mdArray.length;i++)
									//use string methods to properly retreive scores

							}
							if(difficulty == 3)
							{
								for(i=0;i<hdArray.length;i++)
									//use string methods to properly retreive scores

							}

							//saving new score to array and sorting
							//depending on difficulty using a different array
							if(difficulty == 1)
							{
								for(i=0;i<ezArray.length;i++)
								{
									if(i == 0)
									{
										if(ezArray[i]<score)
											ezArray[i] = score;	
									}
									else if(ezArray[i] < score  && ezArray[i-1] != 0)
										ezArray[i] = score;									
								}


							}
							else if(difficulty == 2)
							{
								for(i=0;i<mdArray.length;i++)
								{
									if(i == 0)
									{
										if(mdArray[i]<score)
											mdArray[i] = score;	
									}
									else if(mdArray[i] < score  && mdArray[i-1] != 0)
										mdArray[i] = score;									
								}


							}
							else if(difficulty == 3)
							{
								for(i=0;i<hdArray.length;i++)
								{
									if(i == 0)
									{
										if(hdArray[i]<score)
											hdArray[i] = score;	
									}
									else if(hdArray[i] < score  && hdArray[i-1] != 0)
										hdArray[i] = score;									
								}


							}

							//instantiating PrintWriter and printing to the appropriate Text File
							try
							{
								writer = new PrintWriter(file);
							}
							catch (FileNotFoundException e)
							{
								System.out.println("File Not Found");
								System.exit(1);
							}*/
							gameCards.show(gh,"GameOverPanel");							
						}
							
						
					}													
				}        
		    }

		    /*--------------------------------------LISTENERS---------------------------------------------*/
		    public void keyTyped(KeyEvent e){}
		    public void keyReleased(KeyEvent e){}
		    //when space bar is pressed set yvel back so character starts to go up again
		    public void keyPressed(KeyEvent e)
		    {
		        if(e.getKeyCode() == KeyEvent.VK_SPACE) 
		        {
		        	if(chary==500)
		            	yvel=JUMPVEL;
		        }
		        else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		        {
		        	gameTimer.stop();
		        	gameCards.show(gh,"PausePanel");
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
					cards.show(holder,"HomePanel");
				} //goes back to HomePanel
			}

		}

		public class GameOverPanel extends JPanel implements ActionListener
		{
			private JButton backHome, playAgain;
			private JPanel buttonPanel;
			

			public GameOverPanel()
			{
				setLayout(new BorderLayout());

				buttonPanel = new JPanel();
				
				backHome = new JButton("Back to Home");
				backHome.addActionListener(this);
				buttonPanel.add(backHome);
				
				playAgain = new JButton("Play Again");
				playAgain.addActionListener(this);
				buttonPanel.add(playAgain);

				scoreReport = new JLabel();
				System.out.println(score);
				add(scoreReport, BorderLayout.NORTH);

				add(buttonPanel,BorderLayout.SOUTH);
			}
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
			}
			public void actionPerformed(ActionEvent e)
			{
				String str = e.getActionCommand();
				if(str.equals("Play Again"))
				{
					generateObstacles();
					score = 0;
		       		charx = 50;
		       		chary = 500;
		       		scorex = 50;
		       		yvel = 0;
		       		framesPassed = 0;
					gameCards.show(gh,"GamePanel");
					gameTimer.start();

				} //resumes game
					
				else if(str.equals("Back to Home"))
				{
					gameCards.show(gh,"GamePanel");
					generateObstacles();
					score = 0;
		       		charx = 50;
		       		chary = 500;
		       		scorex = 50;
		       		yvel = 0;
		       		framesPassed = 0;
					cards.show(holder,"HomePanel");
				} //goes back to HomePanel
			}




		}
		
		
				
		//PowerUpPanel class extends JPanel implements ActionListener
			//constructor
				//chooses random question from array and presents in JLabel
				//Math.Random to choose between multiple choice or free response
				//construct Timer for timelimit
				//construct TimerTask that decrements counter
				//schedule Timer to run TimerTask every second
				//if the counter is 0, then go back to GamePanel
				//if answer is 
	
			//givePowerup() 
			/*
				use Math.random() to decide which powerup to grant
				if flight
				print
					allow user to jump while not on ground
					flight += 10
				if invincibility
					temporarily take away hurt box
					invincibility +=10
				if snail mode
					scrollVelocity*=.9
			 */
			
			//paintComponent
				//super.paintComponent(g)
				//if !answerRight 
					//draw image of correct answer & provide explanation
				//else
					//congrats & say which powerup
			//actionPerformed()
				//if multiple choice					
					//if command matches answer in answer Array 
						//give powerup
						//set answerRight to true
					//else
						//set answerRight to false 
				//else if free response
					//if sqrt button is pressed
						//type sqrt sign
					//else get text for text field
						//if match
							//give powerup
							//set answerRight to true
						//else
							//set answerRight to false
				//repaint()
							
							
		//class GameOverPanel extends JPanel implements ActionListener
			//constructor 
				//add button to 'play again'
				//add button to go back to 'home'
				//score is equal to framespassed
				//try catch for printer and scanner to append scores to highscores.txt
				//take data from highscores.txt, convert to array
				//add TextArea(uneditable) for highscore board
					//high score board is sorted using array sort method
			//paintComponent
				//super.paintComponent(g)
	}	

	/*-------------- Close Ananth -----------------------------------*/

	/*-------------- Open Forest -----------------------------------*/
	
	//class OptionsPanel extends JPanel implements ActionListener	
	public class OptionsPanel extends JPanel implements ActionListener
	{
		public OptionsPanel() //constructor
		{
			setBackground(Color.PINK);
			
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
			add(degrees);
			degRad.add(degrees);
			degrees.setSelected(true);
			
			JRadioButton radians = new JRadioButton("Radians");
			add(radians);
			degRad.add(radians);

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
			
			// set the angles array to be used in generating questions
			// as degrees or radians
			if(buttonName.equals("Degrees"))
			{
				for(int i = 0;i <= angles.length;i++)
					angles[i] = degrees[i];
			}
			else if(buttonName.equals("Radians"))
			{
				for(int i = 0;i <= angles.length;i++)
					angles[i] = radians[i];
			}
			// System.out.println(mcLimit);
		}
	}
	
	
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
			// private TimeLimitHandler tlh;
			
			public QuestionPanel()
			{
				setBackground(Color.PINK);
				
				//chooses random angle from array and presents in JLabel
				//for question
				angleIndex = (int)(Math.random()*16+0);
				trigRow = (int)(Math.random()*3+0);
				questionAngle = angles[angleIndex];
				
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
											 questionAngle + ")" + "?");
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
					
					//timeLimit = frqLimit;
				}
				else
				{
					int answerCol, answerRow;
					answerCol = angleIndex;
					answerRow = trigRow;
					
					// make 4 radio buttons for 4 choices
					JRadioButton [] choices = new JRadioButton[4];
					ButtonGroup choiceGroup = new ButtonGroup();
					
					choices[choices.length-1] = new JRadioButton(answers[trigRow][angleIndex]);
					
					for(int i = 0;i <= choices.length-2;i++)
					{
						// randomly choose any answer in the answer array,
						// while making sure it is not the correct answer
						while(answerCol == angleIndex && answerRow == trigRow)
						{
							answerCol = (int)(Math.random()*16+0);
							answerRow = (int)(Math.random()*3+0);
						}
						choices[i] = new JRadioButton(answers[answerRow][answerCol]);
						
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
					
					//timeLimit = mcLimit;
				}
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
	
			//givePowerup() 
			/*
				use Math.random() to decide which powerup to grant
				if flight
				print
					allow user to jump while not on ground
					flight += 10
				if invincibility
					temporarily take away hurt box
					invincibility +=10
				if snail mode
					scrollVelocity*=.9
			 */
			
			public void paintComponent(Graphics g)//paintComponent
			{
				super.paintComponent(g);
				//if !answerRight 
					//draw image of correct answer & provide explanation
				//else
					//congrats & say which powerup
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
						//give powerup
						answerRight.setVisible(true);
					}
					else //else
					{
						answerWrong.setVisible(true);
						correct = false;
					}
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
							//give powerup
							//set answerRight visible
							answerRight.setVisible(true);
						}
						else
						{
							answerWrong.setVisible(true);
							correct = false; 
						}
				
						questionCards.show(pwp,"FeedbackPanel");
					}
				}
				fp.repaint(); //repaint FeedbackPanel to draw arc if necessary
			}
		}		
		public class FeedbackPanel extends JPanel implements ActionListener
		{
			private Image unitCircle;
			
			public FeedbackPanel()
			{
				setBackground(Color.YELLOW);
				
				answerRight = new JLabel("Congratz");
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
				gameCards.show(gh,"GamePanel");
				gameTimer.start();
			}
		}
}
}

/*------------------------ Close Forest ------------------------*/
		
