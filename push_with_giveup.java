import java.util.*;
import java.io.*;
public class push_with_giveup
{
  public static int isPresent(int a,ArrayList<Integer> al)
  {
    for(int i=0;i<al.size();i++)
    {
      if(a==al.get(i))
		return 1;
    }
    return 0;
  }
  public static void main(String args[])
  {
    int nodes=1; 															// maintains the number of sender nodes
    int no_of_nodes=10879;
    int no_of_connected_nodes=10876;
    int msg_size=Integer.parseInt(args[0]);
    int mean = Integer.parseInt(args[2]);
    int maximum=no_of_nodes-1;
    int minimum=0;
    float wastage=0;
    float partial_wastage = 0;
    int bandwidth=0;
    int no_of_transfer_packets=0;
    int transfer_count=0;
    int random_num = 0;
    int[] gaussian_sample = new int[10000];
    int[] power_law_sample = new int[10000];
    int[] poisson_sample = new int[10000];
    double p=0.9;           //The predefined parameter(p). How was the parameter obtained?
    String distribution = args[1];
    String temp[];
    Node_push_with_giveup nod[]=new Node_push_with_giveup[no_of_nodes];
    ArrayList<Integer> source=new ArrayList<Integer>();
    ArrayList<Integer> mid=new ArrayList<Integer>();
    ArrayList<Integer> remov=new ArrayList<Integer>();
    
    for(int i=0;i<no_of_nodes;i++)
    {
      nod[i]=new Node_push_with_giveup(); 		// the arraylist has been declared already. Is this required?
    }
    for(int i=0;i<no_of_nodes;i++) 				// initializing the nodes
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
    Random rn = new Random();  
    int range = maximum - minimum + 1;
    int randomNum =  rn.nextInt(range) + minimum;
    int start=randomNum;
    source.add(start); 								// random start node
    for(int i=0;i<msg_size;i++)
	    nod[start].msg[i]=1;
    nod[start].src=1;
    //System.out.println("done");
    while(source.size()>0) 							// while all nodes are not switched off
    {
		for(int i=0;i<source.size();i++)
		{
		  int s=source.get(i);
		  minimum=0;
		  maximum=nod[s].neighbor.size()-1;
		  range=maximum-minimum+1;
		  if(range==0)
		  	continue; 					
		  randomNum=rn.nextInt(range)+minimum;		// choose random neighbour
		  int d=nod[s].neighbor.get(randomNum);
		  if(nod[d].src==1)             			//the destination already has the packet and hence it is an unsuccessful communication
		  {
		   	// System.out.println("wastage part");s
		    wastage++;
		    if(isPresent(d,nod[s].dis_neighbor)==0) // checking whether the neighbor already discovered else add each other to the list of discovered neighbors
		    {
			    nod[s].dis_neighbor.add(d);
			    nod[d].dis_neighbor.add(s);
		    }  
		    nod[s].counter++;          				// counter tracking unsuccessful pushes is increased
		    if(nod[s].counter>Math.log(1/(1-p))*(nod[s].dis_neighbor.size()+1)) //checked whether the counter exceeds threshold, dis_neighbor.size() is the discovered neighbor size at this time 
		      remov.add(s); 						// sender node added to switch-off list
		  }

		  else										// neighbour doesn't have the packet
		  {
		  	// System.out.println("Enter else");
		  	no_of_transfer_packets = 0;
		  	transfer_count = 0;
		  	while(no_of_transfer_packets<1)
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

		    for(int j=0;j<msg_size;j++)
		    {
		      if((nod[s].msg[j]==1)&&(nod[d].msg[j]==0))
		      {
						bandwidth++;           //successful communication
						transfer_count += 1;
						nod[d].msg[j]=1;
						if(j==msg_size-1)
            {
						  mid.add(d);
              partial_wastage = 1 - transfer_count/(float)no_of_transfer_packets;
              wastage += partial_wastage;
              break;
            }
						if(isPresent(d,nod[s].dis_neighbor)==0)   // checking whether the neighbor already discovered else add each other to the list of discovered neighbors
						{
							nod[s].dis_neighbor.add(d);
							nod[d].dis_neighbor.add(s);
						}
						if(transfer_count==no_of_transfer_packets)  
							break; 								
		      }
		    }
		  }
		}
		time++;
		//System.out.println(source.size());
		//System.out.println(mid.size());
		//System.out.println(remov.size());
		//System.out.println(source);
		//System.out.println(mid);
		//System.out.println(remov);
		for(int i=0;i<mid.size();i++)
		{
		 // System.out.println("adding part");
		  source.add(mid.get(i));
		  nod[mid.get(i)].src=1;
		  nodes++;
		}
		//System.out.println("to added at this round "+mid.size());
		for(int i=0;i<remov.size();i++)
		 {
		  source.remove(new Integer(remov.get(i))); 				// remove the switched-off nodes from source
		 }
		// System.out.println("to removed at this round "+remov.size());
		mid.clear();
		remov.clear();
		//System.out.println(time+"\t"+source.size());
		//System.out.println("------------------------------------------");
		//if(time==10)break;
    }
    System.out.println(time);
    System.out.println(wastage);
    System.out.println(bandwidth);
    // System.out.println("coverage: "+(float)(nodes)/10876);
  }
}
