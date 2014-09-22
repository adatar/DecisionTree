import java.util.ArrayList;
import java.util.HashMap;


public class Node 
{
	Node()
	{
		this.isLeaf=false;
		this.label=null;
		this.nextChildren=new HashMap<String, Node>();
		this.parent=null;
		this.possibleFeatureVals=new HashMap<String, ArrayList<String>>();
	}
	private String predictedLabel;
	public String getPredictedLabel() 
	{
		return predictedLabel;
	}
	public void setPredictedLabel(String predictedLabel)
	{
		this.predictedLabel = predictedLabel;
	}
	
	private String label;
	public String getLabel() 
	{
		return label;
	}
	public void setLabel(String label) 
	{
		this.label = label;
	}

	private HashMap<String, ArrayList<String>> possibleFeatureVals;
	
	public HashMap<String, ArrayList<String>> getPossibleFeatureVals()
	{
		return possibleFeatureVals;
	}
	public void setPossibleFeatureVals(HashMap<String, ArrayList<String>> possibleFeatureVals) 
	{
		this.possibleFeatureVals = possibleFeatureVals;
	}
	
	private HashMap<String, Node> nextChildren;
	public HashMap<String, Node> getNextChildren()
	{
		return nextChildren;
	}
	public void setNextChildren(HashMap<String, Node> nextChildren)
	{
		this.nextChildren = nextChildren;
	}
	
	private Node parent;
	public Node getParent()
	{
		return parent;
	}
	public void setParent(Node parent) 
	{
		this.parent = parent;
	}
	
	private Boolean isLeaf;
	public Boolean getIsLeaf()
	{
		return isLeaf;
	}
	public void setIsLeaf(Boolean isLeaf) 
	{
		this.isLeaf = isLeaf;
	}
	
	

}
