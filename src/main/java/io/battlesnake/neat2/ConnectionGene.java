package io.battlesnake.neat2;

import java.util.Random;

public class ConnectionGene {
	
	public Node fromNode;
	public Node toNode;
	public double weight;
	public boolean enabled;
	public int innovationNo;
	private Random random = new Random();
	
	public ConnectionGene(Node from, Node to, double w, int inno) {
		this.fromNode = from;
		this.toNode = to;
		this.weight = w;
		this.innovationNo = inno;
	}
	
	public void mutateWeight() {
		double rand2 = random.nextDouble();
		if (rand2 < 0.1) {
			this.weight = random.nextDouble() * 2.0 - 1.0;
		}
		else {
			this.weight += random.nextGaussian() / 50.0;
			
			if (this.weight > 1) {
				this.weight = 1;
			}
			else if (this.weight < -1) {
				this.weight = -1;
			}
		}
	}
	
	public ConnectionGene clone(Node from, Node to) {
		ConnectionGene clone = new ConnectionGene(from, to, this.weight, this.innovationNo);
		clone.enabled = this.enabled;
		
		return clone;
	}

}
