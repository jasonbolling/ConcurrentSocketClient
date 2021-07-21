import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;

class threadCreator extends Thread{
	Socket socket;
	BufferedReader input;
	PrintWriter output;
	int command;
	int clients;
	static ArrayList<Long> respTimes = new ArrayList<Long>();
	threadCreator(String ip, int port, int commandGiven){
		
		//variable initialization
		
		long timeToComplete;
		this.command = commandGiven;

		//create the socket
		try {
			this.socket = new Socket(ip, port);
			this.output = new PrintWriter(socket.getOutputStream(), true);
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		} catch (UnknownHostException e) {
			System.err.println("host is invalid: "+e);
			System.exit(0);
		} catch (IOException e){
			System.out.println("returned IO exception: " +e);
			System.exit(0);
		}
	}

	public void run(){
		long startTime = System.currentTimeMillis();
		output.println(this.command);
		try {
			while(input.readLine()!=null) {
				System.out.println(input.readLine());
			}
			this.input.close();
			this.output.close();
			this.socket.close();
		} catch(IOException e) {
			System.err.println("IO error "+ e);
			System.exit(0);
		}
		long totalTime = (System.currentTimeMillis() - startTime);
		respTimes.add(totalTime);

	}
}
public class testclient {
	public static void main (String[] args) throws IOException {
		//variables and objects declarations
		int PORT = 0;
		String IPAddress = "";
		int runs = 0;
		int input = 0;
		threadCreator[] threads = new threadCreator[100];
		long totalTime = 0;
		PrintWriter textOut;
		Scanner in = new Scanner(System.in);
		BufferedReader textIn;
		int option;

		System.out.println("Please enter IP address:");
		IPAddress = in.nextLine();
		System.out.println("Please enter PORT: ");
		//catch user input errors
		try {
			PORT = in.nextInt();
		}
		catch(Exception e) {
			System.out.println("must be integer input, please try again");
			System.exit(0);
		}

		
		//catch user input errors
		System.out.println("Please select a command number: \n1. date and time \n2. Uptime \n3. memory use \n4. Netstat \n5. current users \n6. running processes\n7. exit");
		try {
			input = in.nextInt();
			if(input == 7){
				System.out.println("exiting...");
				System.exit(0);
			}
			else if(input != 1 && input != 2 && input != 3 && input != 4 && input != 5 && input !=6){
				System.out.println("unrecoginzed input, please retry");
				System.exit(0);
			}
			System.out.println("Please enter how many times to run your command: \n1, 5, 10, 15, 20, 25, 100");

			//catch user input errors 
			try {
				runs = in.nextInt();
				if(runs == 1 || runs == 5 || runs == 10 || runs == 15 || runs == 20 || runs == 25 || runs == 100){
				}
				else {
					System.out.println("must be one of the given choices, please try again");
					System.exit(0);
				}
				
				
			}
			catch(Exception e) {
				System.out.println("must be single number input, please try again");
				System.exit(0);
			}
			
		}
		catch(Exception e) {
			System.out.println("must be single number input, please try again");
			System.exit(0);
		}

		//loop to create the threads
		for(int i = 0; i < runs; i++){
			threads[i] = new threadCreator(IPAddress, PORT, input);
			System.out.print(i);
		}
		System.out.println("threads created");
		//loop to execute threads
		for(int j = 0; j < runs; j++){
			threads[j].start();
		}
		System.out.println("threads started");
		//loop to join threads
		for(int k = 0; k < runs; k++){
			try {
				threads[k].join();
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}

		//calculate times
		long timeSum = 0;
		for(long time: threadCreator.respTimes){
			timeSum += time;
			System.out.print(time);
		}
		long avgTime = timeSum / (long)runs; 

		//outputs
		System.out.println("sum of times is "+timeSum);
		System.out.println("average time for completion is "+avgTime);
		threadCreator.respTimes.clear();
		System.exit(1);
	}
}