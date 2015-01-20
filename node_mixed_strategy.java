import java.util.*;
public class node_mixed_strategy {
	int id;
	int his_count=0;  
	int src=0;  //src=1 if the node has the full segment and 0 otherwise
	int msg[];
	int pull_stat=0;
	int pull_count=0;
	ArrayList<Integer> neighbor=new ArrayList<Integer>();  //set of neighbours
	ArrayList<Integer> disc_neigh=new ArrayList<Integer>(); //set of discovered neighbours
	int status=0;
}

