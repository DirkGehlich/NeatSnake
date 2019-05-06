package io.battlesnake.neat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.battlesnake.neat.NodeGene.Type;

class GenomeTest {

	@Mock
	private Random random;

	@InjectMocks
	private Genome genome;

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		InnovationNrGenerator.reset();
	}

	@Test
	void performAddConnectionMutation_noNodeGenes_dontMutate() {
		genome.performAddConnectionMutation();

		assertEquals(0, genome.getConnectionGenes().size());
	}

	@Test
	void performAddConnectionMutation_alreadyConnected_dontMutate() {
		NodeGene gene1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		ConnectionGene connection = new ConnectionGene(gene1.getInnovationNr(), gene2.getInnovationNr(), 0.3f, true, InnovationNrGenerator.getNext());

		when(random.nextInt(2)).thenReturn(0);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);
		genome.addConnectionGene(connection);

		genome.performAddConnectionMutation();

		assertEquals(1, genome.getConnectionGenes().size());
	}

	@Test
	void performAddConnectionMutation_sameLayer_dontMutate() {
		NodeGene gene1 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());

		when(random.nextInt(2)).thenReturn(0, 1);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);

		genome.performAddConnectionMutation();

		assertEquals(0, genome.getConnectionGenes().size());
	}

	@Test
	void performAddConnectionMutation_notInverted_mutate() {
		NodeGene gene1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());

		when(random.nextInt(2)).thenReturn(0, 1);
		when(random.nextFloat()).thenReturn(0.25f); // weight
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);

		genome.performAddConnectionMutation();

		assertEquals(1, genome.getConnectionGenes().size());
		ConnectionGene connection = genome.getConnectionGenes().get(0);
		assertEquals(1, connection.getInNodeInnovationNr());
		assertEquals(2, connection.getOutNodeInnovationNr());
		assertEquals(-15f, connection.getWeight());
		assertEquals(true, connection.isEnabled());
	}

	@Test
	void performAddConnectionMutation_inverted_mutate() {
		NodeGene gene1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());

		when(random.nextInt(2)).thenReturn(1, 0);
		when(random.nextFloat()).thenReturn(0.25f); // weight
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);

		genome.performAddConnectionMutation();

		assertEquals(1, genome.getConnectionGenes().size());
		ConnectionGene connection = genome.getConnectionGenes().get(0);
		assertEquals(1, connection.getInNodeInnovationNr());
		assertEquals(2, connection.getOutNodeInnovationNr());
		assertEquals(-15f, connection.getWeight());
		assertEquals(true, connection.isEnabled());
	}

	@Test
	void performAddNodeMutation_noConnections_dontMutate() {
		genome.performAddNodeMutation();

		assertEquals(0, genome.getNodeGenes().size());
		assertEquals(0, genome.getConnectionGenes().size());
	}

	@Test
	void performAddNodeMutation_connections_mutate() {
		NodeGene gene1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		ConnectionGene connection = new ConnectionGene(gene1.getInnovationNr(), gene2.getInnovationNr(), 0.3f, true, InnovationNrGenerator.getNext());

		when(random.nextInt(1)).thenReturn(0);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);
		genome.addConnectionGene(connection);

		genome.performAddNodeMutation();

		assertEquals(3, genome.getNodeGenes().size());
		assertEquals(3, genome.getConnectionGenes().size());

		ConnectionGene inConnection = genome.getConnectionGenes().get(1);
		ConnectionGene outConnection = genome.getConnectionGenes().get(2);
		assertEquals(1f, inConnection.getWeight());
		assertEquals(0.3f, outConnection.getWeight());

		assertEquals(gene1.getInnovationNr(), inConnection.getInNodeInnovationNr());
		assertEquals(4, inConnection.getOutNodeInnovationNr());

		assertEquals(4, outConnection.getInNodeInnovationNr());
		assertEquals(gene2.getInnovationNr(), outConnection.getOutNodeInnovationNr());
	}
	
	@Test
	void performWeightMutation_perturb_multiplyRandomFactor() {
		NodeGene gene1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		ConnectionGene connection = new ConnectionGene(gene1.getInnovationNr(), gene2.getInnovationNr(), 0.3f, true, InnovationNrGenerator.getNext());

		when(random.nextFloat()).thenReturn(0.99f, 0.89f);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);
		genome.addConnectionGene(connection);

		genome.performWeightMutation();

		assertEquals(1.77, genome.getConnectionGenes().get(0).getWeight(), 0.001f);
	}

	@Test
	void performWeightMutation_mutate_setRandomFactor() {
		NodeGene gene1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		ConnectionGene connection = new ConnectionGene(gene1.getInnovationNr(), gene2.getInnovationNr(), 0.3f, true, InnovationNrGenerator.getNext());

		when(random.nextFloat()).thenReturn(0.12f, 0.91f);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);
		genome.addConnectionGene(connection);

		genome.performWeightMutation();

		assertEquals(-22.8f, genome.getConnectionGenes().get(0).getWeight(), 0.01f);
	}

	@Test
	void calculateCompatibilityDistance_disjointAndExcess_calculate() {

		Parameters.c1 = 0.1f;
		Parameters.c2 = 0.2f;
		Parameters.c3 = 0.3f;
		
		NodeGene nodeGene1 = new NodeGene(Type.Input, 1);
		NodeGene nodeGene2 = new NodeGene(Type.Input, 2);
		NodeGene nodeGene3 = new NodeGene(Type.Bias, 3);
		NodeGene nodeGene4 = new NodeGene(Type.Hidden, 4);
		NodeGene nodeGene5 = new NodeGene(Type.Hidden, 5);
		NodeGene nodeGene6 = new NodeGene(Type.Output, 6);
		
		// Genome 1
		Genome genome1 = new Genome(new Random());
		genome1.addConnectionGene(new ConnectionGene(nodeGene1.getInnovationNr(), nodeGene4.getInnovationNr(), 0.1f, true, 1));
		genome1.addConnectionGene(new ConnectionGene(nodeGene2.getInnovationNr(), nodeGene4.getInnovationNr(), 0.2f, false, 2));
		genome1.addConnectionGene(new ConnectionGene(nodeGene3.getInnovationNr(), nodeGene4.getInnovationNr(), 0.3f, true, 3));
		genome1.addConnectionGene(new ConnectionGene(nodeGene2.getInnovationNr(), nodeGene5.getInnovationNr(), 0.4f, true, 4));
		genome1.addConnectionGene(new ConnectionGene(nodeGene5.getInnovationNr(), nodeGene4.getInnovationNr(), 0.5f, true, 5));
		genome1.addConnectionGene(new ConnectionGene(nodeGene1.getInnovationNr(), nodeGene5.getInnovationNr(), 0.6f, true, 8));

		// Genome 2
		Genome genome2 = new Genome(new Random());
		genome2.addConnectionGene(new ConnectionGene(nodeGene1.getInnovationNr(), nodeGene4.getInnovationNr(), 0.9f, true, 1));
		genome2.addConnectionGene(new ConnectionGene(nodeGene2.getInnovationNr(), nodeGene4.getInnovationNr(), 0.8f, false, 2));
		genome2.addConnectionGene(new ConnectionGene(nodeGene3.getInnovationNr(), nodeGene4.getInnovationNr(), 0.7f, true, 3));
		genome2.addConnectionGene(new ConnectionGene(nodeGene2.getInnovationNr(), nodeGene5.getInnovationNr(), 0.6f, true, 4));
		genome2.addConnectionGene(new ConnectionGene(nodeGene5.getInnovationNr(), nodeGene4.getInnovationNr(), 0.5f, false, 5));
		genome2.addConnectionGene(new ConnectionGene(nodeGene5.getInnovationNr(), nodeGene6.getInnovationNr(), 0.4f, true, 6));
		genome2.addConnectionGene(new ConnectionGene(nodeGene6.getInnovationNr(), nodeGene4.getInnovationNr(), 0.3f, true, 7));
		genome2.addConnectionGene(new ConnectionGene(nodeGene3.getInnovationNr(), nodeGene5.getInnovationNr(), 0.2f, true, 9));
		genome2.addConnectionGene(new ConnectionGene(nodeGene1.getInnovationNr(), nodeGene6.getInnovationNr(), 0.1f, true, 10));

		float distance = genome1.calculateCompatibilityDistanceTo(genome2);
		
		assertEquals(0.92, distance, 0.001f);
	}
	
	private Genome createXORGenome() {
		Genome genome = new Genome(new Random());
		
		NodeGene i1 = new NodeGene(Type.Input, 11);
		NodeGene i2 = new NodeGene(Type.Input, 12);
		NodeGene b1 = new NodeGene(Type.Bias, 13);
		NodeGene h1 = new NodeGene(Type.Hidden, 21);
		NodeGene h2 = new NodeGene(Type.Hidden, 22);
		NodeGene o1 = new NodeGene(Type.Output, 31);
		
		genome.addNodeGene(i1);
		genome.addNodeGene(i2);
		genome.addNodeGene(b1);
		genome.addNodeGene(h1);
		genome.addNodeGene(h2);
		genome.addNodeGene(o1);
		
		genome.addConnectionGene(new ConnectionGene(i1.getInnovationNr(), h1.getInnovationNr(), 20f, true, 41));
		genome.addConnectionGene(new ConnectionGene(i1.getInnovationNr(), h2.getInnovationNr(), -20f, true, 42));
		genome.addConnectionGene(new ConnectionGene(i2.getInnovationNr(), h1.getInnovationNr(), 20f, true, 43));
		genome.addConnectionGene(new ConnectionGene(i2.getInnovationNr(), h2.getInnovationNr(), -20f, true, 44));
		genome.addConnectionGene(new ConnectionGene(b1.getInnovationNr(), h1.getInnovationNr(), -10f, true, 45));
		genome.addConnectionGene(new ConnectionGene(b1.getInnovationNr(), h2.getInnovationNr(), 30f, true, 46));
		genome.addConnectionGene(new ConnectionGene(b1.getInnovationNr(), o1.getInnovationNr(), -30f, true, 47));
		genome.addConnectionGene(new ConnectionGene(h1.getInnovationNr(), o1.getInnovationNr(), 20f, true, 48));
		genome.addConnectionGene(new ConnectionGene(h2.getInnovationNr(), o1.getInnovationNr(), 20f, true, 49));
		
		return genome;
	}
	
	@Test
	void calculate_XOR_1hiddenLayer_2HiddenNeurons_1Bias_Test0_0() {
		Genome genome = createXORGenome();		
		double[] inputs = new double[] {0,0};
		
		double[] outputs = genome.calculate(inputs);
		
		assertEquals(1, outputs.length);
		assertEquals(0, outputs[0], 0.01);
	}
	
	@Test
	void calculate_XOR_1hiddenLayer_2HiddenNeurons_1Bias_Test1_0() {
		Genome genome = createXORGenome();		
		double[] inputs = new double[] {1,0};
		
		double[] outputs = genome.calculate(inputs);
		
		assertEquals(1, outputs.length);
		assertEquals(1, outputs[0], 0.01);
	}

	@Test
	void calculate_XOR_1hiddenLayer_2HiddenNeurons_1Bias_Test0_1() {
		Genome genome = createXORGenome();		
		double[] inputs = new double[] {0,1};
		
		double[] outputs = genome.calculate(inputs);
		
		assertEquals(1, outputs.length);
		assertEquals(1, outputs[0], 0.01);
	}

	@Test
	void calculate_XOR_1hiddenLayer_2HiddenNeurons_1Bias_Test1_1() {
		Genome genome = createXORGenome();		
		double[] inputs = new double[] {1,1};
		
		double[] outputs = genome.calculate(inputs);
		
		assertEquals(1, outputs.length);
		assertEquals(0, outputs[0], 0.01);
	}
}
