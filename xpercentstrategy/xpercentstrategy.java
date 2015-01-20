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
		int no_of_connected_nodes = 10986;
		int nodes=1; 													// no of source nodes
		int flag=0;
		int pull_attempts=0;
		int pulls=0;
		int push_attempts=0;
		int push=0;
		int cov=Integer.parseInt(args[1]);								// coverage(in the range of 10) when push changed to pull 
		//int cov=8;
		float f=(float)(cov/10.0);
		System.out.println(f);
		int x=(int)Math.round(f*no_of_nodes);
		//System.out.println(x);
		int msg_size=Integer.parseInt(args[0]); 						// no of packets as a command-line argument
		//int msg_size=4;
		int minimum=0;
		int maximum=no_of_nodes-1;
		int bandwidth=0;
		int wastage=0;
		int count=1;
		int transfer_count = 0; 										// stores the number of transferred packets during each connection
		int no_of_transfer_packets = 0;
		String temp[];
		ArrayList<Integer> source=new ArrayList<Integer>();				// array containing source nodes
		ArrayList<Integer> mid=new ArrayList<Integer>();
		ArrayList<Integer> nsource=new ArrayList<Integer>(); 			// array containing non-source nodes
		ArrayList<Integer> already_pulled=new ArrayList<Integer>();
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
		int time=0;
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
						// System.out.println("Enter else");
						transfer_count = 0;
						no_of_transfer_packets = 0;
						while(no_of_transfer_packets < 1)
						{
							// System.out.println("Enter while");
							no_of_transfer_packets = (int)Math.round( rn.nextGaussian()+1 );
							System.out.println("no_of_transfer_packets = "+no_of_transfer_packets);
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
									mid.add(n);
								if(transfer_count==no_of_transfer_packets || k==msg_size-1)
									break; 						
							}
						}
					}
				}
			}
			else 												// if no source nodes has crossed threshold
			{
				flag=1;
				if(count==1)
				{
					//System.out.println("System is switching to pull");
					//System.out.println(nodes+" "+x);
					count++;
				}
				for(int i=0;i<nsource.size();i++)
				{
					pull_attempts++;
					int n=nsource.get(i);
					int size=nod[n].neighbor.size();
					minimum=0;
					maximum=size-1;
					range=maximum-minimum+1;
					randomNum=rn.nextInt(range)+minimum;
					int m=nod[n].neighbor.get(randomNum);
					if((nod[m].src==1)&&(isPresent(already_pulled,m)==0))
					{
						already_pulled.add(m);
						for(int k=0;k<msg_size;k++)
						{
							if((nod[m].msg[k]==1)&&(nod[n].msg[k]==0))
							{
								nod[n].msg[k]=1;
								//System.out.println("message pulled from "+m+" by "+n);
								bandwidth++;
								pulls++;
								if(k==msg_size-1)
									mid.add(n);
								break;
							}
						}
					}
					else
						wastage++;
				}
				//System.out.println(already_pulled.size());
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
			System.out.println(pull_attempts+"\t"+pulls);
			//System.out.println(push_attempts+"\t"+push);
			//System.out.println(already_pulled.size());
			pull_attempts=0;
			pulls=0;
			push_attempts=0;
			push=0;
			//System.out.println("----------------");
			mid.clear();
			System.out.println("Source size = "+source.size());
			if(source.size()==no_of_connected_nodes)
			{
				//System.out.println("Time: "+time);
				//System.out.println("Wastage: "+wastage);
				//System.out.println("Bandwidth: "+bandwidth);
				break;
			}
		}
	}
}

