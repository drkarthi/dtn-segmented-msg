import java.util.*;
import java.io.*;
public class giveup_real_graph_mixed
{
	public static int idsource(ArrayList<Integer> source,int x)
	{
		if(source.size()==0) return -1;
		for(int i=0;i<source.size();i++)
		{
			if(x==source.get(i))
				return i;
		}
		return -1;
	}
	public static void main(String args[])
	{
		ArrayList<Integer> mid=new ArrayList<Integer>(); //temporary list for adding pushers (sources)
		ArrayList<Integer> midnot=new ArrayList<Integer>(); //temporary list for adding pullers (nodes discovered by others)
		ArrayList<Integer> source=new ArrayList<Integer>(); //list of sources (those which can push and be pulled from)
		ArrayList<Integer> activesources=new ArrayList<Integer>(); //list of active sources - those which can push
		ArrayList<Integer> notsource=new ArrayList<Integer>(); // list of not sources (those which can not push nor be pulled from)
		int no_of_nodes=10876; // description of the data
		int msg_size=4; // number of parts in the message
		int iterations = 100; // iterations to take mean
		double desired_probability=0.9; // in switch-off this is a p value (lower bound for the probability to find every neighbours from the node)
									// in blind this is a stop condition: a fraction of the network with the message
									// I WIlL CHANGE IT!
		double des2=0.01;
		boolean switchoff = true; // if true, switchoff strategy will be used; if false, blind strategy will be used
		boolean pull = true; // if true, push and pull will be used; if false, only push
		int wastage=0; // total wastage
		int bandwidth=0; // total useful contacts, wastage+bandwidth= total energy consumed by agents during the broadcast
		int no_of_transfer_packets=0;
		int transfer_count=0;
		//initialization
		Node []nod=new Node[no_of_nodes+10]; //+10 comes from the fact, that there are few omitted nodes in the p2p-Gnutella04.txt 
		for(int i=0;i<no_of_nodes+10;i++)
		{
			nod[i]=new Node();
		}
		String temp[];
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
			System.out.println("Error is :"+e);
		}
		Node []discovered_connections=new Node[no_of_nodes+10]; 		// arraylist is a better option
				
		for(int i=0;i<no_of_nodes+10;i++)
		{
			discovered_connections[i]=new Node();
		}
		// table structure of the results
		//System.out.println("Giveup\titer\tm(cov nodes)\tm(wastage)\tm(bandwidth)\tprob. p\tmean coverage %\tmean wastage\t mean time\t prob(giveup)\tmsg_size");
		System.out.println("Giveup\titer\tvalue of p\tm(cov nodes)\tm(wastage)\tm(bandwidth)\tmean coverage %\tmean wastage\t mean time\t prob(giveup)\tmsg_size");
		//main loop - parameter
		int c1=0;
		int sizeMean = 55;
		double []meanTime = new double[sizeMean];
		double []meanWastage = new double[sizeMean];
		double []meanBandwidth = new double[sizeMean];
		double []meanCoverage = new double[sizeMean];
		double []meanProp = new double[sizeMean];
		for(int i1=0;i1<sizeMean;i1++)
		{
			meanTime[i1]=0.0;
			meanWastage[i1]=0.0;
			meanBandwidth[i1]=0.0;
			meanCoverage[i1]=0.0;				
			meanProp[i1]=0.0;
		}
		for(int it=0;it<iterations;it++) // we run *iterations* independent iterations 
		{	
			//System.out.println("iteracja "+it);
			int iterMean=-1;
			//for(desired_probability=0.01;desired_probability<=0.91;desired_probability+=0.04)
			{
				iterMean++;
				//desired_probability=0.01;
				double probability_of_giveup = 1; // always switch off whenever the condition is fulfilled
				// we will take average
				/*double meanwastage=0.0;
				double meantime = 0.0;
				double meanenergy=0.0;
				double meancov = 0.0;
				*/
				
				//initialization							
				int maximum=no_of_nodes;
				int minimum=0;
				source.clear();
				notsource.clear();
				activesources.clear();
				mid.clear();
				wastage=0;
				bandwidth=0;
				int []counter = new int[no_of_nodes+10];
				int []counter2 = new int[no_of_nodes+10];
				Random rn = new Random();	
				int range = maximum - minimum + 1;
				for(int i=0;i<no_of_nodes+10;i++)
				{
					discovered_connections[i].neighbor.clear();
					counter[i]=0;
					counter2[i]=0;
					nod[i].id=i;
					nod[i].msg=new int[msg_size];
					nod[i].src=0;
					for(int j=0;j<msg_size;j++)
					{
						nod[i].msg[j]=0;
					}
				}
				int randomNum =  rn.nextInt(range) + minimum;
				int start=randomNum; 							// random start node
				while(nod[start].neighbor.size()==0) 			// if the start node does not have any neighbours, pick some other start node
					start=rn.nextInt(range)+minimum;
				source.add(start);
				activesources.add(start); 						// the switched-off nodes are not being removed from sources?
				for(int i=0;i<msg_size;i++)
					nod[start].msg[i]=1;
				nod[start].src=1;

				int time=0;
				int count = 0;
				//stop condition: note it is different for switch-off and different for blind!!
				des2=0.01;
				iterMean=0;
				while(count<no_of_nodes && (switchoff&& (activesources.size()>0 || pull&&(notsource.size() > 0))))//((count<no_of_nodes*(desired_probability)&&count<no_of_nodes)&&(!switchoff)) || (switchoff&& (activesources.size()>0 || pull&&(notsource.size() > 0))))// && bandwidth+1<msg_size*no_of_nodes)
				{		
				//	System.out.print(time+":"+count+","+activesources.size()+"."+notsource.size()+" ");
					count=0;
					for(int i=0;i<no_of_nodes+10;i++)
					{
						if(nod[i].src==1)
						{
							count++; 					// keeps track of number of source nodes
						}
					}
					if(count>no_of_nodes*(des2))
					{
						meanTime[iterMean]=(meanTime[iterMean]*it+time)/((double)(it+1));
						meanWastage[iterMean]=(meanWastage[iterMean]*it+wastage)/((double)(it+1));
						meanBandwidth[iterMean]=(meanBandwidth[iterMean]*it+bandwidth)/((double)(it+1));
						meanCoverage[iterMean]=(meanCoverage[iterMean]*it+count/(double)no_of_nodes)/((double)(it+1));
						meanProp[iterMean]=(meanProp[iterMean]*it+des2)/((double)(it+1));
						iterMean++;
						des2+=0.02;
						if(des2>0.99) // more dense experiment near the "full information in the whole network" case
						{
							des2=1.0-Math.pow(2.0,-6-c1);
							c1++;
							if(1.0-des2<0.001)
								break;
						}
					}
					if(pull) //if boolean pull is true, retrieve all pullers
					{
						for(int i=0;i<notsource.size();i++)
						{
							
							int m=notsource.get(i);
							//first, check if the puller is really true - maybe it became a sender!?
						
							if(nod[m].src==1)
							{
								notsource.remove(i);
								i--;
								continue;
							}
							//ok, pull will sleep if it should (in case of switch off)
							if(counter2[m]>0 && switchoff) 								// what does counter2 store - current sleep time?
							{
								counter2[m]--;
								continue;
							}
							int conn_size = discovered_connections[m].neighbor.size();
							int size=nod[m].neighbor.size();
							minimum=0;
							maximum=size-1;
							range=maximum-minimum+1;
							randomNum=rn.nextInt(range) + minimum;
							int n=nod[m].neighbor.get(Math.abs(randomNum)); 	// choose random neighbour for push or pull?
							boolean found_conn = false;
							for(int k=0;k<conn_size;k++)
							{
								if(discovered_connections[m].neighbor.get(k)==n) found_conn = true;
							}
							if(found_conn == false)
							{
								discovered_connections[m].neighbor.add(n);
								found_conn = false;
								conn_size = discovered_connections[n].neighbor.size();
								for(int k=0;k<conn_size;k++)
								{
									if(discovered_connections[n].neighbor.get(k)==m) found_conn = true;
								}
								if(found_conn == false)
								{
									discovered_connections[n].neighbor.add(m);
								}
							}
							int x=idsource(source,n); // we can pull also from *non-active sources*
							if(x==-1)
							{				
								midnot.add(n);
								int discoveredSize=discovered_connections[m].neighbor.size();
								
								counter2[m]=discoveredSize-2*counter[m]; // we wait as many steps as there are non-sources nodes discovered in the neighbourhood - why the factor 2?
								wastage++;
								continue;
							}
							else 											// efficient pull
							{
								counter[m]++; //we count neighbors which are *sources*
								int flag1=0,cond=0;
								no_of_transfer_packets=0;
								transfer_count=0;
								while(no_of_transfer_packets<1)
								{
									no_of_transfer_packets = (int)Math.round( rn.nextGaussian()+1 );
								}

								for(int k=0;k<msg_size;k++)
								{
									if((nod[n].msg[k]==1)&&(nod[m].msg[k]==0))
									{
										//System.out.println("message pulled from "+m+" by "+n+" packet_id: "+k+" time is "+time);
										nod[m].msg[k]=1;
										flag1=1;
										cond = k;
										bandwidth++;
										transfer_count+=1;
										if(transfer_count==no_of_transfer_packets || k==msg_size-1)
											break;
									}
									else continue;
								}
								if(flag1==0)
								{
									System.out.println("It should be not reachable! If you see this in the terminal, there is a fatal error!");	
									for(int k=0;k<msg_size;k++)
									{
										System.out.println(nod[n].msg[k]+" "+nod[m].msg[k]);
									}
									int discoveredSize=discovered_connections[m].neighbor.size();
									
									counter2[m]=discoveredSize-2*counter[m]; // we wait as many steps as there are non-sources nodes discovered in the neighbourhood
									wastage++;
								}
								else if(cond==msg_size-1)					
								{
									// a pulling node will be a sender!
									nod[m].src=1;
									mid.add(m);
									counter[m]=0;
															
								}
							}
						}
					}
					// loop over the active sources - for push
					for(int i=0;i<activesources.size();i++)
					{
						int m=activesources.get(i);
						int conn_size = discovered_connections[m].neighbor.size();
						if(counter[m]>-Math.log(1-desired_probability)*(conn_size+1)) //switch off if the counter exceeds certain value
						{
							if(switchoff&&rn.nextDouble()<=probability_of_giveup)
							{ 						
								activesources.remove(i);
								i--;
								continue;
							}
						}
						int size=nod[m].neighbor.size();
						minimum=0;maximum=size-1;
						range=maximum-minimum+1;
						randomNum=rn.nextInt(range) + minimum;
						//draw a random neighbor
						int n=nod[m].neighbor.get(Math.abs(randomNum));
						
						boolean found_conn = false; //check if it is the already discovered node
						for(int k=0;k<conn_size;k++)
						{
							if(discovered_connections[m].neighbor.get(k)==n) found_conn = true;
						}
						if(found_conn == false) //if not, add it to the neighbor list
						{
							discovered_connections[m].neighbor.add(n);
							found_conn = false;
							conn_size = discovered_connections[n].neighbor.size();
							for(int k=0;k<conn_size;k++)
							{
								if(discovered_connections[n].neighbor.get(k)==m) found_conn = true;
							}
							if(found_conn == false)
							{
								discovered_connections[n].neighbor.add(m);
							}
						}
						int flag1=0;
						int cond=0;
						//try to push a part of the message
						no_of_transfer_packets = 0;
						transfer_count = 0;
						while(no_of_transfer_packets < 1)
						{
							no_of_transfer_packets = (int)Math.round( rn.nextGaussian()+1 );
						}

						for(int k=0;k<msg_size;k++)
						{
							if(nod[n].msg[k]==0)
							{
								//System.out.println("message transmitted from "+m+" to "+n+" packet_id: "+k+" time is "+time);
								bandwidth++;
								transfer_count += 1;
								nod[n].msg[k]=1;
								flag1=1;
								cond=k;
								if(transfer_count==no_of_transfer_packets || k==msg_size-1)
									break; 				
							}
							else continue;
						}
						if(flag1==0) //in case you tried to push the message to other sender
						{
							counter[m]++;
							wastage++;
						}
						else if(cond==0&&msg_size>1) // add the neighbor to the list of pullers
						{
							midnot.add(n);
						}
						else if(cond==msg_size-1) // the neighbor will become a sender
						{
							nod[n].src=1;
							mid.add(n);
							counter[n]=0;
							int x=idsource(notsource,n);
							if(x!=-1)
							{
								notsource.remove(x);
							}
						}		
					}
					//update the list of the pullers
					for(int i=0; i<midnot.size(); i++)
					{
						
						boolean exists=false;
						if(notsource.contains(midnot.get(i)))
							exists=true;
						if(!exists)
						{
							notsource.add(midnot.get(i));
							int m =midnot.get(i);
							counter[m]=1;
							int discoveredSize=discovered_connections[m].neighbor.size();
							
							counter2[m]=discoveredSize-2*counter[m]; // we wait as many steps as there are non-sources nodes discovered in the neighbourhood
							
						}
					}
					midnot.clear();
					//update the list of the senders
					for(int i=0;i<mid.size();i++)
					{
						boolean exists=false;
						if(source.contains(mid.get(i)))
							exists=true;
						if(!exists)
						{
							activesources.add(mid.get(i));
							source.add(mid.get(i));
						}
						
					}
					mid.clear();
					//finalization
					time++;
					
				}
				//meanwastage=(meanwastage*it+wastage)/((double)(it+1));
				//meantime=(meantime*it+time)/((double)(it+1));
				//meancov=(meancov*it+count/(double)no_of_nodes)/((double)(it+1));
				//meanenergy=(meanenergy*it+bandwidth)/((double)(it+1));
				
			} //end iterations			
			//System.out.println("Mixed\t"+iterations+"\t"+meancov*no_of_nodes+"\t"+meanwastage+"\t"+meanenergy+"\t"+(desired_probability)+"\t"+meancov+"\t"+(meanwastage/(float)(meanwastage+meanenergy))+"\t"+meantime+"\t"+probability_of_giveup+"\t"+msg_size);
		}//end cov loop
		for(int i1=0;i1<sizeMean;i1++)
		{
			if(switchoff)
				System.out.println("MixOFF\t"+iterations+"\t"+meanProp[i1]+"\t"+meanCoverage[i1]*no_of_nodes+"\t"+meanWastage[i1]+"\t"+meanBandwidth[i1]+"\t"+meanCoverage[i1]+"\t"+(meanWastage[i1]/(double)(meanWastage[i1]+meanBandwidth[i1]))+"\t"+meanTime[i1]+"\t"+(1.0)+"\t"+msg_size);
			else
				System.out.println("MixON\t"+iterations+"\t"+meanProp[i1]+"\t"+meanCoverage[i1]*no_of_nodes+"\t"+meanWastage[i1]+"\t"+meanBandwidth[i1]+"\t"+meanCoverage[i1]+"\t"+(meanWastage[i1]/(double)(meanWastage[i1]+meanBandwidth[i1]))+"\t"+meanTime[i1]+"\t"+(1.0)+"\t"+msg_size);
		}
		
	}
}

