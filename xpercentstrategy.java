import java.io.*;
import java.util.*;
public class xpercentstrategy {
	public static	int isPresent(ArrayList<Integer> al,int data)
	{
		for(int i=0;i<al.size();i++)
		{
			if(data==al.get(i))
				return 1;
		}
		return 0;
	}

	public static void main(String args[])
	{
		int no_of_nodes=10879;
		int no_of_connected_nodes = 10876;
		int nodes=1; 													// no of source nodes
		int flag=0;
		int pull_attempts=0;
		int pulls=0;
		int push_attempts=0;
		int push=0;
		int cov=Integer.parseInt(args[1]);								// coverage(in the range of 10) when push changed to pull 
		//int cov=8;
		float f=(float)(cov/10.0);
		// System.out.println(f);
		int x=(int)Math.round(f*no_of_nodes);
		//System.out.println(x);
		int msg_size=Integer.parseInt(args[0]); 						// no of packets as a command-line argument
		//int msg_size=4;
		int mean = Integer.parseInt(args[3]);
		int minimum=0;
		int maximum=no_of_nodes-1;
		int bandwidth=0;
		float wastage=0;
		int count=1;
		int transfer_count = 0; 										// stores the number of transferred packets during each connection
		int no_of_transfer_packets = 0;
		int random_num = 0;
		int[] gaussian_sample = new int[10000];
		int[] power_law_sample = new int[10000];
		int[] poisson_sample = new int[10000]; 
		String distribution = args[2];
		String temp[];
		ArrayList<Integer> source=new ArrayList<Integer>();				// array containing source nodes
		ArrayList<Integer> mid=new ArrayList<Integer>();
		ArrayList<Integer> nsource=new ArrayList<Integer>(); 			// array containing non-source nodes
		ArrayList<Integer> already_pulled=new ArrayList<Integer>(); 	// stores what??
		node_mixed_strategy []nod=new node_mixed_strategy[no_of_nodes];
		for(int i=0;i<no_of_nodes;i++)
			nod[i]=new node_mixed_strategy(); 							// expilicitly running the constructor?
		for(int i=0;i<no_of_nodes;i++) 									// initialization of nodes
		{
			nod[i].id=i;
			nod[i].msg=new int[msg_size];
		}
		try
		{
			FileInputStream fs=new FileInputStream("gnutella_network.txt"); 			
			DataInputStream in=new DataInputStream(fs);
			BufferedReader br=new BufferedReader(new InputStreamReader(in));
			String strLine;
			while((strLine=br.readLine())!=null)
			{
				temp=strLine.split("\t");
				nod[Integer.parseInt(temp[0])].neighbor.add(Integer.parseInt(temp[1]));
				nod[Integer.parseInt(temp[1])].neighbor.add(Integer.parseInt(temp[0]));
			}
		}catch(Exception e)
		{
			System.out.println("Error is : "+e);
		}
		try
	    {
	      FileInputStream fs = new FileInputStream("gaussian_distribution.txt");
	      DataInputStream in = new DataInputStream(fs);
	      BufferedReader br = new BufferedReader(new InputStreamReader(in));
	      String strLine;
	      int i=0;
	      while((strLine=br.readLine())!=null)
	      {
	        gaussian_sample[i] = Integer.parseInt(strLine);
	        i += 1;
	      }
	      fs.close();
	    }catch(Exception e)
	    {
	      System.out.println("Error is "+e);
	    }
		try
	    {
	      FileInputStream fs = new FileInputStream("powerlaw_distribution.txt");
	      DataInputStream in = new DataInputStream(fs);
	      BufferedReader br = new BufferedReader(new InputStreamReader(in));
	      String strLine;
	      int i=0;
	      while((strLine=br.readLine())!=null)
	      {
	        power_law_sample[i] = Integer.parseInt(strLine);
	        i += 1;
	      }
	      fs.close();
	    }catch(Exception e)
	    {
	      System.out.println("Error is "+e);
	    }
	    try
	    {
	      FileInputStream fs = new FileInputStream("poisson_distribution.txt");
	      DataInputStream in = new DataInputStream(fs);
	      BufferedReader br = new BufferedReader(new InputStreamReader(in));
	      String strLine;
	      int i=0;
	      while((strLine=br.readLine())!=null)
	      {
	        poisson_sample[i] = Integer.parseInt(strLine);
	        i += 1;
	      }
	      fs.close();
	    }catch(Exception e)
	    {
	      System.out.println("Error is "+e);
	    }

		int time=0;
		float partial_wastage = 0;
		Random rn = new Random();  //  A random source is selected to start the broadcast........
		int range = maximum - minimum + 1;
		int randomNum =  rn.nextInt(range) + minimum;
		int start=randomNum; 							
		source.add(start);
		//System.out.println(start);
		for(int i=0;i<msg_size;i++) 					// start node has all the packets
			nod[start].msg[i]=1;
		nod[start].src=1;
		for(int i=0;i<no_of_nodes;i++)
		{
			if(i!=start)
				nsource.add(i); 						// maintaining a list of non-source nodes
		}
		while(true)
		{
			if(nodes<x) 								// if no of source nodes less than threshold
			{
				if(count>1)
					System.out.println("This should not print");
				for(int i=0;i<source.size();i++) 			// for each sender node
				{
					//int flag=0;
					push_attempts++; 						// add a push attempt
					int m=source.get(i);
					int size=nod[m].neighbor.size();
					minimum=0;
					maximum=size-1;
					range=maximum-minimum+1;
					randomNum=rn.nextInt(range)+minimum;
					int n=nod[m].neighbor.get(randomNum); 	// fetch a random neighbour node object
					if(nod[n].src==1)
						wastage++; 							// wastage, if the node has all the packets
					else
					{
						// System.out.println("Enter push else");
						transfer_count = 0;
						no_of_transfer_packets = 0;
						while(no_of_transfer_packets < 1)
						{
							if (distribution.equals("gaussian"))
							{
								random_num = rn.nextInt(10000);
								no_of_transfer_packets = gaussian_sample[random_num];
							}
							else if(distribution.equals("uniform"))
								no_of_transfer_packets = mean;
							else if(distribution.equals("powerlaw"))
							{
								random_num = rn.nextInt(10000);
								no_of_transfer_packets = power_law_sample[random_num];
							}
							else if(distribution.equals("poisson"))
							{
								random_num = rn.nextInt(10000);
								no_of_transfer_packets = poisson_sample[random_num];	
							} 			
						}

						for(int k=0;k<msg_size;k++)    // proceed to send packet
						{
							if((nod[m].msg[k]==1)&&(nod[n].msg[k]==0))  
							{
								push++;
								nod[n].msg[k]=1;       // a successful transmission
								//System.out.println("Message pushed by "+m+" to "+n);
								bandwidth++;
								transfer_count += 1;
								if(k==msg_size-1)
								{
									mid.add(n);
									partial_wastage = 1 - transfer_count/(float)no_of_transfer_packets;
									wastage += partial_wastage;
									break;
								}
								if(transfer_count==no_of_transfer_packets)
									break; 						
							}
						}
					}
				}
			}
			else 												// if num of source nodes has crossed threshold
			{
				// System.out.println("nsource size = "+nsource.size());
				flag=1;
				if(count==1)
				{
					//System.out.println("System is switching to pull");
					//System.out.println(nodes+" "+x);
					count++;
				}
				for(int i=0;i<nsource.size();i++) 				// all non-source nodes try to pull in each time step
				{
					pull_attempts++; 							// add pull attempt
					int n=nsource.get(i);
					int size=nod[n].neighbor.size();
					minimum=0;
					maximum=size-1;
					range=maximum-minimum+1;
					if (range==0)
						continue;
					randomNum=rn.nextInt(range)+minimum;
					int m=nod[n].neighbor.get(randomNum); 		// fetch random neighbour node object m
					if((nod[m].src==1)&&(isPresent(already_pulled,m)==0)) 	// if m is a source node and has not been pulled in this timestep
					{
						// System.out.println("Enter pull if");
						already_pulled.add(m); 					// pull the packet and update m's status
						transfer_count = 0;
						no_of_transfer_packets = 0;
						while(no_of_transfer_packets < 1)
						{
							if (distribution.equals("gaussian"))
							{
								random_num = rn.nextInt(10000);
								no_of_transfer_packets = gaussian_sample[random_num];
							}
							else if(distribution.equals("uniform"))
								no_of_transfer_packets = mean;
							else if(distribution.equals("powerlaw"))
							{
								random_num = rn.nextInt(10000);
								no_of_transfer_packets = power_law_sample[random_num];
							}
							else if(distribution.equals("poisson"))
							{
								random_num = rn.nextInt(10000);
								no_of_transfer_packets = poisson_sample[random_num];	
							}
						}

						for(int k=0;k<msg_size;k++)
						{
							if((nod[m].msg[k]==1)&&(nod[n].msg[k]==0))
							{
								nod[n].msg[k]=1;
								//System.out.println("message pulled from "+m+" by "+n);
								bandwidth++;
								pulls++;
								transfer_count += 1;
								if(k==msg_size-1)
								{
									mid.add(n);
									partial_wastage = 1 - transfer_count/(float)no_of_transfer_packets;
									wastage += partial_wastage;
									break;
								}
								if(transfer_count==no_of_transfer_packets)
									break;
							}
						}
					}
					else
						wastage++;
				}
				// System.out.println("already_pulled size = "+already_pulled.size());
				already_pulled.clear();
			}
			time++;
			//System.out.println(mid.size());
			for(int i=0;i<mid.size();i++)						// updating the no of source nodes
			{
				if(isPresent(source,mid.get(i))==0)
				{
					source.add(mid.get(i));
					nsource.remove(new Integer(mid.get(i)));
					nod[mid.get(i)].src=1;
					nodes++; 									
					//if(flag==1)
						//System.out.println(mid.get(i)+" added to source and removed from nsource");
				}
			}
			//System.out.println(time);
			//System.out.println(time+"\t"+source.size()+"\t"+nsource.size()+"\t"+nodes);
			// System.out.println(pull_attempts+"\t"+pulls);
			//System.out.println(push_attempts+"\t"+push);
			//System.out.println(already_pulled.size());
			pull_attempts=0;
			pulls=0;
			push_attempts=0;
			push=0;
			//System.out.println("----------------");
			mid.clear();
			// System.out.println("Source size = "+source.size());
			if(source.size()==no_of_connected_nodes)
			{
				break;
			}
		}
		System.out.println(time);
		System.out.println(wastage);
		System.out.println(bandwidth);
	}
}

