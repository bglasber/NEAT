package com.glasbergen.neat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NeuralNetwork {

	private int numInputs;
	private int numOutputs;
	private List<Node> nodesInNetwork;
	private List<Node> outputNodes;
	private double fitness;
	private Species species;
	
	public NeuralNetwork(int numInputs, int numOutputs){
		nodesInNetwork = new LinkedList<>();
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
		outputNodes = new LinkedList<>();
		for( int i = 0; i < numInputs; i++){
			nodesInNetwork.add(new Node(i));
		}
		for( int i = 0; i < numOutputs; i++){
			outputNodes.add(new Node(i));
		}
		initializeDependencies();
	}
	
	/**
	 * For testing purposes, allow initialization of the neural network with preset weights
	 * @param numInputs
	 * @param numOutputs
	 * @param inputWeights
	 */
	public NeuralNetwork(int numInputs, int numOutputs, double inputWeights[]){
		nodesInNetwork = new LinkedList<>();
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
		outputNodes = new LinkedList<>();
		for( int i = 0; i < numInputs; i++){
			nodesInNetwork.add(new Node(i));
		}
		nodesInNetwork.add(NodeUtils.getBiasNode());
		for( int i = 0; i < numOutputs; i++){
			outputNodes.add(new Node(i));
		}
		initializeDependencies(inputWeights);
	}
	/**
	 * Initialize dependencies between nodes in a new neural network
	 * Assumes the only nodes in nodesInNetwork are the inputs
	 * Values should be between -1.0 and 1.0 
	 * All outputs will be connected to all inputs
	 * @param inputWeights 
	 */

	private void initializeDependencies(double[] inputWeights) {
		int ind = 0;
		for( Node outputNode : outputNodes ){
			for( Node inputNode : nodesInNetwork ){
				outputNode.setDependency(inputNode, inputWeights[ind++]);
			}
		}
		
	}
	
	/**
	 * Initialize dependencies between nodes in a new neural network
	 * Assumes the only nodes in nodesInNetwork are the inputs
	 * Assigns a random value between -1.0 and 1.0 as the weight
	 * All outputs will be connected to all inputs
	 * @param inputWeights 
	 */
	private void initializeDependencies() {
		for( Node outputNode : outputNodes ){
			for( Node inputNode : nodesInNetwork ){
				double initial = MathTools.getRandDouble();
				outputNode.setDependency(inputNode, initial);
			}
		}
	}

	/**
	 * Iterate over the nodes in the network, skipping past the input nodes
	 * to compute the results in the hidden layers
	 * Then compute the results for the outputs 
	 * @param inputs
	 */
	public double[] propagate2(double inputs[]){
		double d[] = new double[outputNodes.size()];
		for( int i = 0; i < inputs.length; i++ ){
			nodesInNetwork.get(i).setResult(inputs[i]);
		}
		int skipCount = inputs.length;
		for( Node node : nodesInNetwork ){
			if( skipCount-- > 0 ){
				continue;
			}
			node.computeResult();
		}
		int ind = 0;
		for( Node node : outputNodes ){
			node.computeResult();
			d[ind++] = node.getResult();
		}
		return d;
	}
	
	/**
	 * Mutate the weights in the network
	 * @return
	 */
	public NeuralNetwork perturb(){
		for( Node n : nodesInNetwork ){
			n.perturbDependencies();
		}
		for( Node n : outputNodes){
			n.perturbDependencies();
		}
		return null;
		
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
		species.rememberIfBest(this, fitness);
	}
	
	public double getFitness(){
		return fitness;
	}

	/**
	 * Dump the network out in some human-readable way
	 */
	public void dumpNetwork() {
		System.out.println("Network Fitness: " + fitness);
		System.out.println("Network Dependencies: ");
		for(Node node: nodesInNetwork){
			System.out.println("\tNode " + node.getId() + " Dependencies:");
			Map<Node, Double> nodeDepends = node.getAllDependencies();
			for( Node dep : nodeDepends.keySet() ){
				System.out.println("\t\tNode " + dep.getId() + ", weight: " + nodeDepends.get(dep));
			}
			System.out.println("\t\tBias Node, weight: " + node.getBiasWeight());
			
		}
		System.out.println("Output Nodes:");
		int i = 0;
		for(Node node : outputNodes){
			System.out.println("Output Node " + node.getId() + " Dependencies:");
			Map<Node, Double> nodeDepends = node.getAllDependencies();
			for( Node dep : nodeDepends.keySet() ){
				System.out.println("\t\tNode " + dep.getId() + ", weight: " + nodeDepends.get(dep));
			}
			System.out.println("\t\tBias Node, weight: " + node.getBiasWeight());
		}
	}

	/**
	 * Perform the addNewNode mutation
	 * Split a connection, add a new node
	 * connection to new node has same weight as old one
	 * connection from new node to old destination has weight of 1.0
	 */
	public void addNewNode() {
		//TODO: this is messed up
		Node newNode = new Node(NodeUtils.getNextId());
		Node randOut = MathTools.getRandomElement(outputNodes);
		Set<Node> outputNodeDeps = randOut.getAllDependencies().keySet();
		int randInd = MathTools.getInt(outputNodeDeps.size());
		Iterator<Node> it = outputNodeDeps.iterator();
		Node n = null;
		for(int i = 0; i <= randInd; i++){
			n = it.next();
		}
		// New node should "copy" this connection
		newNode.setDependency(n, randOut.getAllDependencies().get(n));
		// Out node removes "old" connection
		randOut.getAllDependencies().remove(n);
		//Out node adds new connection to the new node with 1.0 weight
		randOut.setDependency(newNode, MathTools.getUniformCenteredAtZero());
		nodesInNetwork.add(newNode);
	}

	/**
	 * Perform the addNewLink mutation
	 * Add a new connection between two previously unconnected nodes
	 * new weight is random
	 */
	public void addNewLink() {
		Iterator<Node> hiddenLayerIterator = nodesInNetwork.iterator();
		for(int i = 0; i < numInputs; i++){
			hiddenLayerIterator.next();
		}
		if( !tryCreateNewLinkTo(hiddenLayerIterator) ) {
			tryCreateNewLinkTo(outputNodes.iterator());
		}
	}

	/**
	 * Iterate over the ordered list of targets and try to create a link
	 * from one of those nodes to a node in the nodesInNetworkList
	 * @param possibleTargets
	 * @return
	 */
	private boolean tryCreateNewLinkTo(Iterator<Node> possibleTargets) {
		while( possibleTargets.hasNext() ){
			Node nodeToLinkTo = possibleTargets.next();
			if( nodeToLinkTo.getAllDependencies().keySet().size() == nodesInNetwork.size() -1 ){
				continue;
			} else {
				for( Node n : nodesInNetwork ){
					if( nodeToLinkTo.getId() != n.getId() && !nodeToLinkTo.getAllDependencies().containsKey(n) ){
						nodeToLinkTo.setDependency(n, MathTools.getRandDouble());
						return true;
					}
				}
			}		
		}
		return false;
	}

	public List<Node> getNetworkNodes() {
		return nodesInNetwork;
	}

	public List<Node> getOutputNodes() {
		return outputNodes;
	}

	/**
	 * Count the total number of connections (aka genes) in the neural network
	 * @return
	 */
	public int getNumGenes() {
		//TODO: may need to store this as a variable to prevent computing this every time
		int numGenesInNetwork = 0;
		for(Node n : nodesInNetwork ){
			numGenesInNetwork += n.getAllDependencies().size();
		}
		for(Node n : outputNodes){
			numGenesInNetwork += n.getAllDependencies().size();
		}
		return numGenesInNetwork;
	}

	/**
	 * Find the number of excess genes between the two neural networks
	 * @param networkToAdd
	 * @return
	 */
	public int getNumExcessGenes(NeuralNetwork networkToAdd) {
		int excessGenes = 0;
		int lastId = nodesInNetwork.get(nodesInNetwork.size()-1).getId();
		//Count all genes that point to an id past our last node id
		for(Node n : networkToAdd.getNetworkNodes()){
			if( n.getId() > lastId ){
				excessGenes += n.getAllDependencies().size();
			}
		}
		return excessGenes;
	}

	/**\
	 * Find the number of disjoint genes between the two neural networks
	 * @param networkToAdd
	 * @return
	 */
	public int getNumDisjointGenes(NeuralNetwork networkToAdd) {
		int numDisjointGenes = 0;
		Iterator<Node> myNodesIterator = nodesInNetwork.iterator();
		Iterator<Node> theirNodesIterator = networkToAdd.getNetworkNodes().iterator();
		numDisjointGenes = countDisjointGenes(numDisjointGenes, myNodesIterator, theirNodesIterator);
		numDisjointGenes = countDisjointGenes(numDisjointGenes, outputNodes.iterator(), networkToAdd.getOutputNodes().iterator());
		return numDisjointGenes;
	}

	private int countDisjointGenes(int numDisjointGenes, Iterator<Node> myNodesIterator,
			Iterator<Node> theirNodesIterator) {
		for(;;){
			if( !myNodesIterator.hasNext() ){
				break;
			} else if( !theirNodesIterator.hasNext() ){
				numDisjointGenes += numGenesRemainingInIterator(myNodesIterator);
				break;
			}
			Node myNode = myNodesIterator.next();
			Node theirNode = theirNodesIterator.next();
			
			while( myNode.getId() != theirNode.getId() ){
				if( myNode.getId() < theirNode.getId() ){
					numDisjointGenes += myNode.getAllDependencies().size();
					if( !myNodesIterator.hasNext() ){
						break;
					}
					myNode = myNodesIterator.next();
				} else {
					numDisjointGenes += theirNode.getAllDependencies().size();
					if( !theirNodesIterator.hasNext() ){
						numDisjointGenes += numGenesRemainingInIterator(myNodesIterator);
						break;
					}
					theirNode = theirNodesIterator.next();
						
				}
			}
			
			Set<Node> myDeps = myNode.getAllDependencies().keySet();
			Set<Node> theirDeps = theirNode.getAllDependencies().keySet();
			for( Node n: myDeps ){
				if( !theirNode.getAllDependencies().containsKey(n) ){
					numDisjointGenes++;
				}
			}
			for( Node n : theirDeps ){
				if( !myNode.getAllDependencies().containsKey(n) ) {
					numDisjointGenes++;
				}
			}
		}
		return numDisjointGenes;
	}

	/**
	 * Count how many objects are left in the iterator of nodes
	 * @param iter
	 * @return
	 */
	private int numGenesRemainingInIterator(Iterator<Node> iter) {
		int count = 0;
		while(iter.hasNext()){	
			count += iter.next().getAllDependencies().size();
		}
		return count;
	}

	/**
	 * Compute the average weight differences between the shared genes
	 * on the two neural networks
	 * @param networkToAdd
	 * @return
	 */
	public double computeAverageWeightDifferences(NeuralNetwork networkToAdd) {
		Iterator<Node> ourNodeIterator = nodesInNetwork.iterator();
		Iterator<Node> theirNodeIterator = networkToAdd.getNetworkNodes().iterator();
		SumWeightsResult result = new SumWeightsResult();
		computeWeightedDifferences(ourNodeIterator, theirNodeIterator, result);
		ourNodeIterator = outputNodes.iterator();
		theirNodeIterator = networkToAdd.getOutputNodes().iterator();
		computeWeightedDifferences(ourNodeIterator, theirNodeIterator, result);
		if( result.totalNumberOfEntries == 0 ){
			return Double.MAX_VALUE;
		}
		return result.summedSquaredWeights / result.totalNumberOfEntries;
	}

	private void computeWeightedDifferences(Iterator<Node> networkNodeIterator, Iterator<Node> otherNetworkNodeIterator,
			SumWeightsResult result) {
		while( networkNodeIterator.hasNext() && otherNetworkNodeIterator.hasNext() ){
			Node myNode = networkNodeIterator.next();
			Node theirNode = otherNetworkNodeIterator.next();
			while( myNode.getId() != theirNode.getId() ){
				if( myNode.getId() < theirNode.getId() ){
					if( !networkNodeIterator.hasNext() ){
						return;
					}
					myNode = networkNodeIterator.next();
				} else { 
					if( !otherNetworkNodeIterator.hasNext() ){
						return;
					}
					theirNode = otherNetworkNodeIterator.next();
				}
			}
			Set<Node> myDepends = myNode.getAllDependencies().keySet();
			for( Node n : myDepends ){
				if( theirNode.getAllDependencies().containsKey(n) ){
					double difference = (myNode.getAllDependencies().get(n) - theirNode.getAllDependencies().get(n));
					result.summedSquaredWeights += difference * difference;
					result.totalNumberOfEntries++;
				}
			}
		}
	}
	
	private void averageWeightDifferences(Iterator<Node> moreFitIter, Iterator<Node> lessFitIter) {
		while( moreFitIter.hasNext() && lessFitIter.hasNext() ){
			Node myNode = moreFitIter.next();
			Node theirNode = lessFitIter.next();
			while( myNode.getId() != theirNode.getId() ){
				if( myNode.getId() < theirNode.getId() ){
					if( !moreFitIter.hasNext() ){
						return;
					}
					myNode = moreFitIter.next();
				} else { 
					if( !lessFitIter.hasNext() ){
						return;
					}
					theirNode = lessFitIter.next();
				}
			}
			Set<Node> myDepends = myNode.getAllDependencies().keySet();
			for( Node n : myDepends ){
				if( theirNode.getAllDependencies().containsKey(n) ){
					double averageResult = (myNode.getAllDependencies().get(n) + theirNode.getAllDependencies().get(n)) / 2 ;
					myNode.setDependency(n, averageResult);
				}
			}
		}
	}
	
	public void setSpecies(Species spec){
		this.species = spec;
	}

	public Species getSpecies() {
		return species;
	}

	/**
	 * Performance crippling attempt to duplicate a NeuralNetwork
	 * @return
	 */
	public NeuralNetwork cloneNetwork() {
		//Create a new object that is a clone of the old
		NeuralNetwork newNetwork = new NeuralNetwork(numInputs, numOutputs);
		
		//Clear existing connections
		for( Node n : newNetwork.outputNodes ){
			n.getAllDependencies().clear();
		}
		int ind = 0;
		for(Node n : nodesInNetwork){
			if( ind++ < numInputs ){
				continue;
			}
			newNetwork.nodesInNetwork.add(new Node(n.getId()));
		}
		for( int i = numInputs-1; i < nodesInNetwork.size(); i++ ){
			for(Node depNode : nodesInNetwork.get(i).getAllDependencies().keySet() ) {
					Node theirNodeCopy = findEquivalentNode( newNetwork.nodesInNetwork, depNode );
					newNetwork.nodesInNetwork.get(i).setDependency(theirNodeCopy, nodesInNetwork.get(i).getAllDependencies().get(depNode));
			}
		}
		for( int i = 0; i < numOutputs; i++ ){
			for(Node depNode : outputNodes.get(i).getAllDependencies().keySet()){
					Node theirNodeCopy = findEquivalentNode(newNetwork.nodesInNetwork, depNode);
					newNetwork.outputNodes.get(i).setDependency(theirNodeCopy, outputNodes.get(i).getAllDependencies().get(depNode));
			}
		}
		newNetwork.setSpecies(species);
		return newNetwork;
	}
	
	/**
	 * "Breed" two networks
	 * Averages the connections that they do share, all other connections come from more fit network
	 * Precondition: Network fitnesses must be set
	 * TODO: 25% chance to copy over "broken links"
	 * @param other
	 * @return
	 */
	public NeuralNetwork crossOver(NeuralNetwork other){
		NeuralNetwork moreFit = this.getFitness() > other.getFitness() ? this : other;
		NeuralNetwork lessFit = this.getFitness() < other.getFitness() ? this : other;
		moreFit = moreFit.cloneNetwork(); //don't clobber old network
		Iterator<Node> moreFitIterator = moreFit.nodesInNetwork.iterator();
		Iterator<Node> lessFitIterator = lessFit.nodesInNetwork.iterator();
		averageWeightDifferences(moreFitIterator, lessFitIterator);
		moreFitIterator = moreFit.outputNodes.iterator();
		lessFitIterator = lessFit.outputNodes.iterator();
		averageWeightDifferences(moreFitIterator, lessFitIterator);
		return moreFit;
	}

	//TODO: this will likely be a performance bottleneck
	//TODO: keep hashmap of nodes to easily find them
	private Node findEquivalentNode(List<Node> nodesInNetwork2, Node depNode) {
		for(Node n : nodesInNetwork2 ){
			if( n.getId() == depNode.getId() ){
				return n;
			}
		}
		return null;
	}
}
