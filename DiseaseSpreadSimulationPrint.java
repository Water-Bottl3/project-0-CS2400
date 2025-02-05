import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class DiseaseSpreadSimulationPrint {
    // Global variables
    private static char[][] grid;
    private static Random rand = new Random();

    public static void main(String[] args) throws IOException
    {
        Scanner input = new Scanner(System.in);


        // User input for number of individuals
        System.out.print("Enter the number of individuals (must be a perfect square): ");
        int n = input.nextInt();
        // Input validation for perfect square
        while (Math.sqrt(n) % 1 != 0) // loop until perfect square is inputted
        {
            System.out.println("Try again! Please enter a perfect square number.");
            System.out.print("Enter the number of individuals (must be a perfect square): ");
            n = input.nextInt();
        }


        // Number of time steps (T)
        System.out.print("Enter the number of time steps (T): ");
        int timeStep = input.nextInt(); // Number of time steps to simulate


        // Infection rate (α)
        System.out.print("Enter the infection rate (α) [0.0 to 1.0]: ");
        double alpha = input.nextDouble(); // Infection rate
        // Input validation to only accept values between 0-1 inclusive
        while (alpha < 0 || alpha > 1)
        {
            System.out.println("Please enter a value between 0.0 and 1.0 for the infection rate.");
            System.out.print("Enter the infection rate (α) [0.0 to 1.0]: ");
            alpha = input.nextDouble(); // user inputs new infection rate
        }


        // Recovery rate (β)
        System.out.print("Enter the recovery rate (β) [0.0 to 1.0]: ");
        double beta = input.nextDouble(); // user inputs recovery rate
        // Input validation to only accept values between 0-1 inclusive
        while (beta < 0 || beta > 1)
        {
            System.out.println("Try again! Please enter a value between 0.0 and 1.0.");
            System.out.print("Enter the recovery rate (β) [0.0 to 1.0]: ");
            beta = input.nextDouble(); // user inputs new recovery rate
        }


        // Declare grid
        int gridSize = (int) Math.sqrt(n); // take perfect square of individuals
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
        printStats(0);
        //outputToFile(0);


        // Simulate the disease spread starting at timeStep 1
        for (int t = 1; t <= timeStep; t++)
        {
            simulateStep(t, alpha, beta);
        }


        input.close();
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
        //outputToFile((timeStep));
        printStats((timeStep));
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

    private static void outputToFile(int timeStep) throws IOException
    {
        File file = new File("./grid-timestep-" + timeStep + ".txt"); //Create .txt file
        FileWriter writer = new FileWriter(file); //Write to .txt file


        //Display the current grid
        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid[row].length; col++)
            {
                writer.write(grid[row][col] + " ");
            }
            writer.write("\n");
        }
        writer.close(); //Closes the file
    }

    private static void printStats(int timeStep)
    {
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




        System.out.println("\nTime Step " + timeStep + ":\n");
        System.out.println("Number Of Infected Individuals (I): " + infected);
        System.out.println("Number Of Recovered Individuals (R): " + recovered);
        System.out.println("Number Of Susceptible Individuals (S): " + susceptible);
        System.out.printf("Percentage Of Individuals Affected: %.3f", infectionRatio);
        System.out.println("");
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
