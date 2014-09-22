import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class BuildTree 
{
	private ArrayList<String> featureList=new ArrayList<String>();
	private ArrayList<ArrayList<String>> data =new ArrayList<ArrayList<String>>();
	private HashMap<String, ArrayList<String>> possibleFeatureVals=new HashMap<String, ArrayList<String>>();
	private  HashMap<String, Integer> occs=new HashMap<String, Integer>();
	private  int total=0;
	private int correctlyClassified=0;
	private int incorrectlyClassified=0;
	
	public void pred(String[]dat,Node root)
	{
		String lab=root.getLabel();
		//get index from the feature list
		int ind=featureList.indexOf(lab);
		HashMap<String, Node> children= root.getNextChildren();
		//get the corresponding value from the data
		String dVal=dat[ind];
		Node n=children.get(dVal);
		if(n!=null && n.getIsLeaf())
		{
			System.out.print("actual "+dat[dat.length-1]);
			System.out.println("predicted "+n.getPredictedLabel());
			if(dat[dat.length-1].equals(n.getPredictedLabel()))
			{
				correctlyClassified++;
			}
			else
			{
				incorrectlyClassified++;
			}
		}
		else if(n==null)
		{
			incorrectlyClassified++;
		}
		else
		{
			pred(dat,n);
		}
	}

	public void classify(Node root)
	{
		Boolean first=true;
		String line = "";
		String split = ",";
		//String csvFile = "C:/Users/aamin/Desktop/PlayTennis.csv";
		String csvFile = "/u/adatar/Downloads/zoo-test.csv";
		BufferedReader br = null;
		
		try 
		{

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				String[] dat = line.split(split);
				if(first)
				{
					for(int i=0;i<dat.length;i++)
					{
						featureList.add(dat[i]);
						ArrayList<String> fVals=new ArrayList<String>();
						possibleFeatureVals.put(dat[i],fVals);
					}
					first=false;
				}
				else
				{
					pred(dat, root);
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}


	}
	public void readCSV()
	{
		boolean first=true;
		String line = "";
		String split = ",";
		//String csvFile = "C:/Users/aamin/Desktop/PlayTennis.csv";
		String csvFile = "/u/adatar/Downloads/zoo-train.csv";
		//String csvFile = "C:/Users/aamin/Desktop/OnlineCourseData.csv";
		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				String[] dat = line.split(split);
				if(first)
				{
					for(int i=0;i<dat.length;i++)
					{
						featureList.add(dat[i]);
						ArrayList<String> fVals=new ArrayList<String>();
						possibleFeatureVals.put(dat[i],fVals);
					}
					first=false;
				}
				else
				{
					ArrayList<String> temp =new ArrayList<String>();
					for(int i=0;i<dat.length;i++)
					{
						temp.add(dat[i]);
						ArrayList<String> possVals=possibleFeatureVals.get(featureList.get(i));
						if(!possVals.contains(dat[i]))
						{
							possVals.add(dat[i]);
							possibleFeatureVals.put(featureList.get(i), possVals);
						}
					}
					data.add(temp);

					//added for initial entropy calculation
					if(occs.containsKey(dat[(dat.length-1)]))
					{
						int val=occs.get(dat[(dat.length-1)]);
						occs.put(dat[(dat.length-1)],++val);
						total++;
					}
					else
					{
						occs.put(dat[(dat.length-1)],1);
						total++;
					}
				}

			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public Node decideNextFeature(double eOfS,ArrayList<Integer> featuresToExclude,ArrayList<ArrayList<String>>  rowsToConsider)
	{
		String nextAttribue=null;
		double maxInfoGain=-1;
		//calcuate the information gain for each of the attributes.
		for(int i=0;i<featureList.size()-1;i++)
		{
			String feature=featureList.get(i);
			int index=featureList.indexOf(feature);
			if(!featuresToExclude.contains(index))
			{

				//build a hash map for each of the possible outcomes and the counts of the labels
				HashMap<String, HashMap<String, Integer>> possOutcomes=new HashMap<String, HashMap<String, Integer>>();
				ArrayList<String> featureVals=possibleFeatureVals.get(feature);

				//populate the initial hashmap
				for(String f:featureVals)
				{
					possOutcomes.put(f,new HashMap<String,Integer>());
				}


				for(int j=0;j<rowsToConsider.size();j++)
				{
					ArrayList<String> line=rowsToConsider.get(j);
					if(possOutcomes.containsKey(line.get(i)))
					{
						//get the corresponding label hashmap
						HashMap<String,Integer> labMap=possOutcomes.get(line.get(i));
						if(labMap.containsKey(line.get(line.size()-1)))
						{
							int val=labMap.get(line.get(line.size()-1));
							labMap.put(line.get(line.size()-1),++val);
						}
						else
						{
							labMap.put(line.get(line.size()-1),1);
						}

					}
				}
				//calculate the individual entropys of every outcome
				double totalFeatureEntropy=0.0;
				for(String f:featureVals)
				{
					HashMap<String,Integer> outcome=possOutcomes.get(f);
					int count=0;
					for(String key:outcome.keySet())
					{
						count+=outcome.get(key);
					}
					double mult=((double)count/(double)total);
					double fEnt=(double)(mult*calculateEntropy(outcome, count));
					totalFeatureEntropy+=fEnt;
				}
				System.out.println("information gain for " +feature+ " " + (eOfS-totalFeatureEntropy));
				if((eOfS-totalFeatureEntropy)>maxInfoGain)
				{
					maxInfoGain=(eOfS-totalFeatureEntropy);
					nextAttribue=feature;
				}
			}
		}//end of for fo feature iteration
		System.out.println("the next node to consider is " +nextAttribue);

		Node n=new Node();
		n.setLabel(nextAttribue);
		HashMap<String, Node> nextChildren=new HashMap<String, Node>();
		ArrayList<String> fvals=possibleFeatureVals.get(nextAttribue);
		for(String s:fvals)
		{
			nextChildren.put(s, new Node());
		}
		n.setNextChildren(nextChildren);

		return n;
	}

	public void determineRoot()
	{	
		//count the occureences of each of the labels of the target variable
		HashMap<String, Integer> occs=new HashMap<String, Integer>();
		int total=0;
		for(ArrayList<String> line:data)
		{
			if(occs.containsKey(line.get(line.size()-1)))
			{
				int val=occs.get(line.get(line.size()-1));
				occs.put(line.get(line.size()-1),++val);
				total++;
			}
			else
			{
				occs.put(line.get(line.size()-1),1);
				total++;
			}
		}

		double eOfS=calculateEntropy(occs, total);
		double totalFeatureEntropy=0.0;

		String nextAttribue=null;
		double maxInfoGain=-1;
		//calcuate the information gain for each of the attributes.
		for(int i=0;i<featureList.size()-1;i++)
		{
			String feature=featureList.get(i);
			//build a hash map for each of the possible outcomes and the counts of the labels
			HashMap<String, HashMap<String, Integer>> possOutcomes=new HashMap<String, HashMap<String, Integer>>();
			ArrayList<String> featureVals=possibleFeatureVals.get(feature);

			//populate the initial hashmap
			for(String f:featureVals)
			{
				possOutcomes.put(f,new HashMap<String,Integer>());
			}

			for(ArrayList<String> line:data)
			{
				if(possOutcomes.containsKey(line.get(i)))
				{
					//get the corresponding label hashmap
					HashMap<String,Integer> labMap=possOutcomes.get(line.get(i));
					if(labMap.containsKey(line.get(line.size()-1)))
					{
						int val=labMap.get(line.get(line.size()-1));
						labMap.put(line.get(line.size()-1),++val);
					}
					else
					{
						labMap.put(line.get(line.size()-1),1);
					}

				}
			}
			//calculate the individual entropys of every outcome
			totalFeatureEntropy=0.0;
			for(String f:featureVals)
			{
				HashMap<String,Integer> outcome=possOutcomes.get(f);
				int count=0;
				for(String key:outcome.keySet())
				{
					count+=outcome.get(key);
				}
				double mult=((double)count/(double)total);
				double fEnt=(double)(mult*calculateEntropy(outcome, count));
				totalFeatureEntropy+=fEnt;
			}
			System.out.println("information gain for " +feature+ (eOfS-totalFeatureEntropy));
			if((eOfS-totalFeatureEntropy)>maxInfoGain)
			{
				maxInfoGain=(eOfS-totalFeatureEntropy);
				nextAttribue=feature;
			}
		}//end of for fo feature iteration
		System.out.println("the next node to consider is " +nextAttribue);




	}

	public double calculateEntropy(HashMap<String,Integer> occus,int total)
	{
		double totalEntropy=0.0;
		for (String key: occus.keySet())
		{
			int val=occus.get(key);
			double multFactor=(double)val/total;
			//calculate the log value
			double logVal=Math.log(multFactor)/Math.log(2.0);
			double finalVal=(multFactor*-1)*logVal;
			totalEntropy+=finalVal;


		}
		System.out.println("ent "+totalEntropy);
		return totalEntropy;
	}

	@SuppressWarnings("unchecked")
	public void growTree(Node root,ArrayList<Integer> featureToExclude,ArrayList<ArrayList<String>>  rowsToConsider,int depth,int depthToStop)
	{
		HashMap<String, Node> nextChild=root.getNextChildren();

		if(depth==depthToStop)
		{
			for(String s:nextChild.keySet())
			{
				ArrayList<Object> rv=getCurrentDatSet(rowsToConsider, s, root.getLabel());
				if(rv!=null)
				{
					ArrayList<ArrayList<String>> rtc=(ArrayList<ArrayList<String>> )rv.get(0);
					Node n=new Node();
					n.setIsLeaf(true);
					String lab=getPredictedLabel(rtc);
					n.setPredictedLabel(lab);
					nextChild.put(s,n);
				}
			}
		}
		else
		{
			for(String s:nextChild.keySet())
			{
				int d=depth;
				double eofs=0.0;
				int tot=0;
				ArrayList<ArrayList<String>> rtc=new ArrayList<ArrayList<String>>();
				ArrayList<Object> rv=getCurrentDatSet(rowsToConsider, s, root.getLabel());
				if(rv!=null)
				{
					rtc=(ArrayList<ArrayList<String>> )rv.get(0);
					eofs=(Double)rv.get(1);
				}
				System.out.println("eofs "+eofs+"total "+tot);
				//stopping condition for recursion
				if(eofs==0.0)
				{
					//create a blank node that would say its the final leaf node
					Node n=new Node();
					n.setIsLeaf(true);
					String lab=getPredictedLabel(rtc);
					n.setPredictedLabel(lab);
					nextChild.put(s,n);
					continue;
				}
				Node nd=decideNextFeature(eofs, featureToExclude, rtc);

				nextChild.put(s,nd);
				int index=featureList.indexOf(nd.getLabel());
				featureToExclude.add(index);

				growTree(nd,featureToExclude,rtc,++d,depthToStop);

			}
			//remove the last feature from the list since it needs to bereconsidered in a seperate branch
			int ind=featureToExclude.size();
			featureToExclude.remove(ind-1);

			System.out.println("depth is " +depth);
		}

	}

	public String getPredictedLabel(ArrayList<ArrayList<String>> rowsToConsider)
	{
		HashMap<String, Integer> prediction=new HashMap<String, Integer>();
		for(ArrayList<String> l:rowsToConsider)
		{
			String lab=l.get(l.size()-1);
			if(prediction.containsKey(lab))
			{
				int val=prediction.get(lab);
				val++;

				prediction.put(lab, val);
			}
			else
			{
				prediction.put(lab, 1);
			}
		}

		//find the max of all the values
		int max=-1;
		String predictedLabel=null;
		for(String key:prediction.keySet())
		{
			if(prediction.get(key)>max)
			{
				max=prediction.get(key);
				predictedLabel=key;
			}
		}
		return predictedLabel;
	}

	public ArrayList<Object> getCurrentDatSet(ArrayList<ArrayList<String>> rowsToConsider,String featureVal,String feature)
	{
		ArrayList<ArrayList<String>>  newRowsToConsider=new ArrayList<ArrayList<String>>();
		HashMap<String, Integer> occ=new HashMap<String, Integer>();
		int totl=0;
		//		if(rowsToConsider.size()==0)
		//		{
		int index=featureList.indexOf(feature);
		//iterate over the entire data set
		for(int j=0;j<rowsToConsider.size();j++)
		{
			ArrayList<String> line=rowsToConsider.get(j);

			if(line.get(index).equals(featureVal))
			{
				newRowsToConsider.add(line);

				if(occ.containsKey(line.get(line.size()-1)))
				{
					int val=occ.get(line.get(line.size()-1));
					occ.put(line.get(line.size()-1),++val);
					totl++;
				}
				else
				{
					occ.put(line.get(line.size()-1),1);
					totl++;
				}
			}

		}
		//}
		//		else
		//		{
		//			//iterate over current rows to consider.
		//		}
		double eOfS=calculateEntropy(occ, totl);
		ArrayList<Object> rv=new ArrayList<Object>();
		rv.add(newRowsToConsider);
		rv.add(eOfS);
		rv.add(totl);
		return rv;

	}

	public static void main(String[] args) throws IOException
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the depth of the tree. Please note if the depth is a higher value than the fully built tree, the full tree would be considered");
		int d=Integer.parseInt(br.readLine());
		Tree t=new Tree();//create a new instance of a tree
		BuildTree b=new BuildTree();
		b.readCSV();
		//b.calculateEntropy();
		//b.determineRoot();
		double eOfS=b.calculateEntropy(b.occs, b.total);
		System.out.println("eofs 1" + " 1" +
				""+eOfS);
		Node root=b.decideNextFeature(eOfS,new ArrayList<Integer>(),b.data);
		t.setRoot(root);
		int index=b.featureList.indexOf(root.getLabel());
		ArrayList<Integer> featureToExclude=new ArrayList<Integer>();
		featureToExclude.add(index);
		b.growTree(root,featureToExclude,b.data,0,d);
		//b.traverseTree(t.getRoot(),new ArrayList<String>());
		b.classify(root);
//		System.out.println("correct"+b.correctlyClassified);
//		System.out.println("incorrect"+b.incorrectlyClassified);
//		double total=b.correctlyClassified+b.incorrectlyClassified;
//		double accuracy=(double)b.correctlyClassified/total;
//		System.out.println("accuracy is"+accuracy);
	}

	public void traverseTree(Node root,ArrayList<String> res)
	{
		//print the root first
		//System.out.print(root.getLabel()+" = ");
		res.add(root.getLabel()+" = ");
		//iterate over each of the children
		HashMap<String, Node> nextChildren=root.getNextChildren();
		for(String key:nextChildren.keySet())
		{
			//System.out.print(key+ "-->");
			res.add(key+ "-->");
			Node ch=nextChildren.get(key);
			if(ch.getIsLeaf())
			{
				//System.out.print("outcome"+ch.getPredictedLabel());
				res.add("outcome "+ch.getPredictedLabel());
				for(String s:res)
				{
					System.out.print(s);
				}
				int ind=res.size();
				res.remove(ind-1);
				res.remove(res.size()-1);
				System.out.println();
			}
			else
			{
				traverseTree(ch,res);
			}

		}
		res.remove(res.size()-1);
		if(res.size()>0)
		{
			res.remove(res.size()-1);
		}


	}

}
