import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DiseaseSpreadSimulation {
    private static char[][] grid;
    private static Random rand = new Random();

    private static File csv = new File("./analysisChangingN2.csv");
    private static int TRIAL_INDEX = 1;
    public static void main(String[] args) throws IOException, CsvException {

        CSVReader reader = new CSVReaderBuilder(new FileReader(csv)).build();
        List<String[]> lines = reader.readAll();
        TRIAL_INDEX = lines.isEmpty() ? 1 : Integer.parseInt(lines.getLast()[0]) + 1;

        int n = 19;
        int timeStep = 30;
        double alpha = .5;
        double beta = .5;


        // Declare grid
        int gridSize = (int) Math.sqrt(n*n); // take perfect square of individuals
        grid = new char[gridSize][gridSize]; // assign perfect square to array size

        // Initialize the grid to set all individuals to susceptible
        for (int row = 0; row < gridSize; row++)
        {
            for (int col = 0; col < gridSize; col++)
            {
                grid[row][col] = 'S'; // "S" = Susceptible
            }
        }


        // Randomly assign Patient Zero
        int randomRow = rand.nextInt(gridSize); // Random row index
        int randomCol = rand.nextInt(gridSize); // Random column index
        grid[randomRow][randomCol] = 'I'; // Set this random individual as infected


        // Print grid at timestep 0 before passing it to simulateStep()
        printStats(0, alpha, beta);



        // Simulate the disease spread starting at timeStep 1
        for (int t = 1; t <= timeStep; t++)
        {
            simulateStep(t, alpha, beta);
        }
    }


    private static void simulateStep(int timeStep, double alpha, double beta) throws IOException
    {
        // Create a new grid to store the next state of the individuals
        int gridSize = grid.length;
        char[][] newGrid = new char[gridSize][gridSize];


        // Copy current grid to newGrid
        for (int row = 0; row < gridSize; row++)
        {
            System.arraycopy(grid[row], 0, newGrid[row], 0, gridSize); // System class method to copy
        }                                                                             // from one array to another


        // Iterate over each individual in the grid
        for (int row = 0; row < gridSize; row++)
        {
            for (int col = 0; col < gridSize; col++)
            {
                if (grid[row][col] == 'S')
                {
                    // If the individual is susceptible, check if they will get infected
                    int infectedNeighbors = countInfectedNeighbors(row, col);
                    double infectionProbability = infectedNeighbors * alpha; //Each neighbor increases infection chance


                    //Biased random number to increase odds of infection
                    if (rand.nextDouble() < infectionProbability)
                    {
                        newGrid[row][col] = 'I'; // Become infected
                    }
                } else if (grid[row][col] == 'I')
                {
                    // If the individual is infected, check if they will recover
                    if (rand.nextDouble() < beta)
                    {
                        newGrid[row][col] = 'R'; // "R" = Recovered
                    }
                }
            }
        }


        // Update the grid with the new states
        grid = newGrid;


        // Output the grid for this time step
        printStats(timeStep, alpha, beta);
    }


    private static int countInfectedNeighbors(int row, int col)
    {
        int infectedCount = 0;
        int gridSize = grid.length;


        // Check all 4 possible neighbors
        int[] dirRow = {-1, 1, 0, 0}; // Directions for row movement to check left and right neighbors
        int[] dirCol = {0, 0, -1, 1}; // Directions for column movement to check above and below neighbors


        for (int i = 0; i < 4; i++)
        {
            int nrow = row + dirRow[i];
            int ncol = col + dirCol[i];


            // Make sure the neighbor is within bounds
            if (nrow >= 0 && nrow < gridSize && ncol >= 0 && ncol < gridSize)
            {
                if (grid[nrow][ncol] == 'I')
                {
                    infectedCount++;
                }
            }
        }
        //System.out.println("infected neighbors: " + infectedCount); // displays infected neighbors for each individual
        return infectedCount;
    }


    private static void outputToFile(int timeStep, double alpha, double beta, int s, int i, int r, int total, double infectionRatio) throws IOException {
        CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(csv, true)).withSeparator(',').build();
        String[] items = {
                String.valueOf(TRIAL_INDEX),
                String.valueOf(timeStep),
                String.valueOf(alpha),
                String.valueOf(beta),
                String.valueOf(s),
                String.valueOf(i),
                String.valueOf(r),
                String.valueOf(total),
                String.valueOf(infectionRatio)
        };
        writer.writeNext(items);
        writer.close();
    }


    private static void printStats(int timeStep, double alpha, double beta) throws IOException {
        int infected = 0, recovered = 0, susceptible = 0;


        //For loop to find count for I,R,S
        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid[row].length; col++)
            {
                if (grid[row][col] == 'I')
                {
                    infected++;
                } else if (grid[row][col] == 'R')
                {
                    recovered++;
                } else //default
                {
                    susceptible++;
                }
            }
        }


        //Variable declaration for total individuals and the infection ratio
        int total = infected + recovered + susceptible;
        double infectionRatio = (double) infected / total;
        outputToFile(timeStep, alpha, beta, susceptible, infected, recovered, total, infectionRatio);

        System.out.println("\nTime Step " + timeStep + ":\n");
        System.out.println("Number Of Infected Individuals (I): " + infected);
        System.out.println("Number Of Recovered Individuals (R): " + recovered);
        System.out.println("Number Of Susceptible Individuals (S): " + susceptible);
        System.out.printf("Percentage Of Individuals Affected: %.2f", infectionRatio*100);
        System.out.println("%");
        System.out.println();
        System.out.println("-----------------------------------------");


        //For Loop to display grid
        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid.length; col++)
            {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println("");
        }
        System.out.println("-----------------------------------------");
    }

}
