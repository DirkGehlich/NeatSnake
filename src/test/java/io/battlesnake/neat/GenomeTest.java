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
		ConnectionGene connection = new ConnectionGene(0, 1, 0.3f, true, InnovationNrGenerator.getNext());

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
		assertEquals(-0.5f, connection.getWeight());
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
		assertEquals(-0.5f, connection.getWeight());
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
		ConnectionGene connection = new ConnectionGene(1, 2, 0.3f, true, InnovationNrGenerator.getNext());

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

		assertEquals(1, inConnection.getInNodeInnovationNr());
		assertEquals(4, inConnection.getOutNodeInnovationNr());

		assertEquals(4, outConnection.getInNodeInnovationNr());
		assertEquals(2, outConnection.getOutNodeInnovationNr());
	}

	@Test
	void performWeightMutation_perturb_multiplyRandomFactor() {
		NodeGene gene1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		ConnectionGene connection = new ConnectionGene(1, 2, 0.3f, true, InnovationNrGenerator.getNext());

		when(random.nextFloat()).thenReturn(0.99f, 0.89f);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);
		genome.addConnectionGene(connection);

		genome.performWeightMutation();

		assertEquals(0.294f, genome.getConnectionGenes().get(0).getWeight(), 0.001f);
	}

	@Test
	void performWeightMutation_mutate_setRandomFactor() {
		NodeGene gene1 = new NodeGene(Type.Input, InnovationNrGenerator.getNext());
		NodeGene gene2 = new NodeGene(Type.Output, InnovationNrGenerator.getNext());
		ConnectionGene connection = new ConnectionGene(1, 2, 0.3f, true, InnovationNrGenerator.getNext());

		when(random.nextFloat()).thenReturn(0.99f, 0.91f);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);
		genome.addConnectionGene(connection);

		genome.performWeightMutation();

		assertEquals(0.99f, genome.getConnectionGenes().get(0).getWeight(), 0.01f);
	}

	@Test
	void calculateCompatibilityDistance_disjointAndExcess_calculate() {

		// Genome 1
		Genome genome1 = new Genome(new Random());
		genome1.addConnectionGene(new ConnectionGene(1, 4, 0.1f, true, 1));
		genome1.addConnectionGene(new ConnectionGene(2, 4, 0.2f, false, 2));
		genome1.addConnectionGene(new ConnectionGene(3, 4, 0.3f, true, 3));
		genome1.addConnectionGene(new ConnectionGene(2, 5, 0.4f, true, 4));
		genome1.addConnectionGene(new ConnectionGene(5, 4, 0.5f, true, 5));
		genome1.addConnectionGene(new ConnectionGene(1, 5, 0.6f, true, 8));

		// Genome 2
		Genome genome2 = new Genome(new Random());
		genome2.addConnectionGene(new ConnectionGene(1, 4, 0.9f, true, 1));
		genome2.addConnectionGene(new ConnectionGene(2, 4, 0.8f, false, 2));
		genome2.addConnectionGene(new ConnectionGene(3, 4, 0.7f, true, 3));
		genome2.addConnectionGene(new ConnectionGene(2, 5, 0.6f, true, 4));
		genome2.addConnectionGene(new ConnectionGene(5, 4, 0.5f, false, 5));
		genome2.addConnectionGene(new ConnectionGene(5, 6, 0.4f, true, 6));
		genome2.addConnectionGene(new ConnectionGene(6, 4, 0.3f, true, 7));
		genome2.addConnectionGene(new ConnectionGene(3, 5, 0.2f, true, 9));
		genome2.addConnectionGene(new ConnectionGene(1, 6, 0.1f, true, 10));

		Population population = new Population();
		float distance = population.calculateCompatibilityDistance(genome1, genome2);
		
		assertEquals(5.16, distance, 0.001f);
	}
}
