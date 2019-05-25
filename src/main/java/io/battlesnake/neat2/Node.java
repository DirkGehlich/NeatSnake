package io.battlesnake.neat2;

import java.util.ArrayList;

public class Node {
	
	public int number;
	public double inputSum = 0;
	public double outputValue = 0;
	public ArrayList<ConnectionGene> outputConnections = new ArrayList<ConnectionGene>();
	public int layer = 0;
	
	public Node(int no) {
		this.number = no;
	}
	
	public void engage() {
		if (layer != 0) {
			outputValue = sigmoid(inputSum);
		}
		
		for (int i=0; i<outputConnections.size(); i++) {
			if (outputConnections.get(i).enabled) {
				outputConnections.get(i).toNode.inputSum += outputConnections.get(i).weight * outputValue;
			}
		}
	}
	
	private double sigmoid(double x) {
		double y = 1 / (1 + Math.pow(Math.E, -4.9*x));
		return y;
	}
	
	public boolean isConnectedTo(Node node) {
		if (node.layer == layer) {
			return false;
		}
		
		if (node.layer < layer)  {
			for (int i=0; i<node.outputConnections.size(); ++i) {
				if (node.outputConnections.get(i).toNode == this) {
					return true;
				}
			}
		} 
		else {
			for (int i=0; i< outputConnections.size(); ++i) {
				if (outputConnections.get(i).toNode == node) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Node clone() {
		Node clone = new Node(this.number);
		clone.layer = this.layer;
		return clone;
	}
	
}
