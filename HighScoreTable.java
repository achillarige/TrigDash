/*
Ananth Chillarige
HighScoreTable.java
Class for retrieving data necessary for high score board.
Takes in a file, converts to array, adds existing data and new data
and then reconverts to an array and sorts. This will make an array ready 
to be displayed in the GameOverPanel. Using object oriented capabilities that
Java offers to make a better class structure and design.
*/

//necessary libraries
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter; 
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;

//main class that reads text file, then creates array of ScoreEntry objects 
//of the appropriate length. Then adds that to the text file along with a new entry
//and then saves it into an array again. Sorts after that, in descending order.
public class HighScoreTable 
{
	private String filename;
	private ScoreEntry[] entries;
	private boolean dne = false;

	public HighScoreTable(String filenameIn)
	{
		filename = filenameIn;
		reload(filenameIn);
	}
	//reloads the array by reading through text file and adding to an array
	public void reload(String filename)
	{
		Scanner reader = null;
		
		//instantiating Scanner instance for line count
		try
		{
			reader = new Scanner(new File(filename));
		}
		catch (FileNotFoundException e)
		{
			dne = true;
			if(dne)
			{
				entries = new ScoreEntry[0];
			}
		}
		
		int lineCount = 0;
		
		//counting number of lines so that I can make the appropriate array length
		if(!dne)
		{
				while(reader.hasNextLine())
			{
				if(!reader.nextLine().trim().equals(""))
					lineCount++;
			}
			
			//instantiating Scanner instance for array storage
			try
			{		
				reader = new Scanner(new File(filename));
			}
			catch (FileNotFoundException e)
			{
				System.out.println("File not found.");
				System.exit(1);
			}
			
			//creating array of appropriate length
			entries = new ScoreEntry[lineCount];
			
			//storing names and scores in the array
			int x = 0;
            while(reader.hasNextLine())
            {
                String line = reader.nextLine();
                if(line.trim().length()>0)
                {
                        //extracting name and score
                        String nameIn = line.substring(0,line.lastIndexOf(':'));
                        int scoreIn = Integer.parseInt(line.substring(line.lastIndexOf(':')+1,line.length()));
                        //creating new object
                        entries[x] = new ScoreEntry(nameIn,scoreIn);
                        x++;
                }
            }
			//sorting by descending order
			Arrays.sort(entries);	
		}
		
	}

	//adds latest score to text file as well as array
	public void addScore(int newScore, String newName)
	{
		PrintWriter writer = null;
		//instantiating printwriter
		try
		{
			writer = new PrintWriter(new File(filename));
		}
		catch (IOException e)
		{
			System.out.println("Cannot append to file");
			System.exit(1);
		}
		//adding old data
		for(int y=0; y<entries.length; y++)
		{
			writer.println(entries[y].getName()+':'+entries[y].getScore());
		}
		//writing new data
		writer.println(newName+':'+newScore);
		writer.close();
		//reloading the array
		reload(filename);
	}

	//makes array accessable 
	public ScoreEntry[] getData()
	{
    	return entries;
	}
}

//class for scoreEntry's consisting of names and scores
//Will have an array of this object that I will pass through when printing
	//will be able to retrieve names and scores through getter methods
class ScoreEntry implements Comparable<ScoreEntry> 
//enabling an object sorty by property by having comparable
{
	private String name;
	private int score;
	
	//object that takes in name and score
	public ScoreEntry(String n, int s)
	{
		name = n;
		score = s;
	}
	
	//creating retreivable name
	public String getName()
	{
		return name;
	}
	
	//creating retrievable score
	public int getScore() 
	{
		return score;
	}
	
	//sorting the array by descending order (highest to lowest)
	public int compareTo(ScoreEntry other)
	{
     return other.score-this.score;
	}

}