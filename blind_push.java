import java.io.*;
import java.util.*;
public class blind_push
{
  public static void main(String args[])
  {
    int no_of_nodes=10879;
    int msg_size=Integer.parseInt(args[0]);               // msg_size taken as a command-line argument
    String distribution = args[1];
    int mean = Integer.parseInt(args[2]);
    int maximum=no_of_nodes-1;
    int minimum=0;
    float wastage=0;
    int bandwidth=0;
    int k=0;
    int random_num = 0;
    int total_packet_exchanges = 0;                       ////
    int[] count_packet = new int[msg_size];               // array storing the distribution of number of packets transferred
    Arrays.fill(count_packet,0);                          ////
    float[] probability_num_packets = new float[msg_size];    ////
    Arrays.fill(probability_num_packets,0);
    ArrayList<Integer> source=new ArrayList<Integer>();   // array storing the nodes that are senders 
    ArrayList<Integer> mid=new ArrayList<Integer>();      // a temporary array storing new sources
    Node nod[]=new Node[no_of_nodes];                     
    String temp[];
    int[] gaussian_sample = new int[10000];
    int[] power_law_sample = new int[10000];
    int[] poisson_sample = new int[10000];
    // creation of N nodes
    for(int i=0;i<no_of_nodes;i++)
    {
      nod[i]=new Node();
    }
    // initializing the nodes with their id and the status of msg?
    for(int i=0;i<no_of_nodes;i++)
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
      fs.close();
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
    int count=0;
    float partial_wastage = 0;
    Random rn = new Random();  
    int range = maximum - minimum + 1;
    int randomNum =  rn.nextInt(range) + minimum;
    int start=randomNum;                            // some random node as the source
    // System.out.println("Source node = "+start);
    source.add(start);
    for(int i=0;i<msg_size;i++)                     // start node has all the packets
	    nod[start].msg[i]=1;
    nod[start].src=1;
    while(source.size()<10876)
    {
      // System.out.println("Source size: "+source.size());
      // System.out.println("Bandwidth: "+bandwidth);
      // System.out.println("Time: "+time);
      for(int i=0;i<source.size();i++)
      {
      	int s=source.get(i);                       // s is the source node
      	minimum=0;
      	maximum=nod[s].neighbor.size()-1;
      	range = maximum - minimum + 1;             
      	randomNum=rn.nextInt(range)+minimum;       // choosing a random neighbour of the source
      	int d=nod[s].neighbor.get(randomNum);      // fetch the neighbour node object
      	if(nod[d].src==1)                          
      	  wastage++;
      	else
      	{
          count = 0;
          k=0;
          // generation of random number for the number of packets to be transferred
          while(k<1)
          {
            if (distribution.equals("gaussian"))
              {
                random_num = rn.nextInt(10000);
                k = gaussian_sample[random_num];
              }
            else if (distribution.equals("uniform"))
                k = mean;
            else if (distribution.equals("powerlaw"))
              {
                random_num = rn.nextInt(10000);
                k = power_law_sample[random_num];
              } 
            else if (distribution.equals("poisson"))
              {
                random_num = rn.nextInt(10000);
                k = poisson_sample[random_num];
              }                
      	  }
          if(k<msg_size)
            count_packet[k-1] += 1;
          else
            count_packet[msg_size-1] += 1;
          for(int j=0;j<msg_size;j++)              // packets transferred in order
      	  {
      	    if((nod[s].msg[j]==1)&&(nod[d].msg[j]==0))   // if the source has the packet and the neighbour does not have the packet
      	    {
      	      bandwidth+=1;
      	      nod[d].msg[j]=1;
      	      if(j==msg_size-1)                          // if last packet is transferred
      		      mid.add(d);                               // neighbour becomes a sender
      	      count += 1;
              if(j==msg_size-1)
              {
                partial_wastage = (1-count/(float)k);
                wastage += partial_wastage;
                break;
              }
              if(count==k)
                break;	                           
      	    }
      	  }
      	}
      }
      time++;                                           // next time step
      for(int i=0;i<mid.size();i++)
      {
      	source.add(mid.get(i));
      	nod[mid.get(i)].src=1;
      }
      mid.clear();
    }
    for(int i : count_packet)
      total_packet_exchanges += i;
    for(int i=0;i<msg_size;i++)
      probability_num_packets[i] = count_packet[i]/(float)total_packet_exchanges;
    System.out.println(time);
    System.out.println(wastage);
    System.out.println(bandwidth);
    // System.out.println("Count array : "+Arrays.toString(count_packet));
    // System.out.println("Total number of exchanges = "+total_packet_exchanges);
    // System.out.println("Probabilities = "+Arrays.toString(probability_num_packets));
  }
}
