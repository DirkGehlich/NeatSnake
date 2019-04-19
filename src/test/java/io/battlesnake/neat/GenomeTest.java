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
	}
	
	@Test
	void performAddConnectionMutation_noNodeGenes_dontMutate() {
		genome.performAddConnectionMutation();

		assertEquals(genome.getConnectionGenes().size(), 0);
	}

	@Test
	void performAddConnectionMutation_alreadyConnected_dontMutate() {
		NodeGene gene1 = new NodeGene(Type.Input);
		NodeGene gene2 = new NodeGene(Type.Output);
		ConnectionGene connection = new ConnectionGene(0, 1, 0.3f, true, 1);

		when(random.nextInt(2)).thenReturn(0);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);
		genome.addConnectionGene(connection);

		genome.performAddConnectionMutation();

		assertEquals(genome.getConnectionGenes().size(), 1);
	}

	@Test
	void performAddConnectionMutation_sameLayer_dontMutate() {
		NodeGene gene1 = new NodeGene(Type.Output);
		NodeGene gene2 = new NodeGene(Type.Output);

		when(random.nextInt(2)).thenReturn(0, 1);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);

		genome.performAddConnectionMutation();

		assertEquals(genome.getConnectionGenes().size(), 0);
	}

	@Test
	void performAddConnectionMutation_notInverted_mutate() {
		NodeGene gene1 = new NodeGene(Type.Input);
		NodeGene gene2 = new NodeGene(Type.Output);

		when(random.nextInt(2)).thenReturn(0, 1);
		when(random.nextFloat()).thenReturn(0.25f); // weight
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);

		genome.performAddConnectionMutation();

		assertEquals(genome.getConnectionGenes().size(), 1);
		ConnectionGene connection = genome.getConnectionGenes().get(genome.getConnectionGenes().size() - 1);
		assertEquals(connection.getInNodeIdx(), 0);
		assertEquals(connection.getOutNodeIdx(), 1);
		assertEquals(connection.getWeight(), -0.5f);
		assertEquals(connection.isEnabled(), true);
	}
	
	@Test
	void performAddConnectionMutation_inverted_mutate() {
		NodeGene gene1 = new NodeGene(Type.Input);
		NodeGene gene2 = new NodeGene(Type.Output);

		when(random.nextInt(2)).thenReturn(1, 0);
		when(random.nextFloat()).thenReturn(0.25f); // weight
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);

		genome.performAddConnectionMutation();

		assertEquals(genome.getConnectionGenes().size(), 1);
		ConnectionGene connection = genome.getConnectionGenes().get(genome.getConnectionGenes().size() - 1);
		assertEquals(connection.getInNodeIdx(), 0);
		assertEquals(connection.getOutNodeIdx(), 1);
		assertEquals(connection.getWeight(), -0.5f);
		assertEquals(connection.isEnabled(), true);
	}
	
	@Test
	void performAddNodeMutation_noConnections_dontMutate() {
		genome.performAddNodeMutation();

		assertEquals(genome.getNodeGenes().size(), 0);
		assertEquals(genome.getConnectionGenes().size(), 0);
	}
	
	@Test
	void performAddNodeMutation_connections_mutate() {
		NodeGene gene1 = new NodeGene(Type.Input);
		NodeGene gene2 = new NodeGene(Type.Output);
		ConnectionGene connection = new ConnectionGene(0, 1, 0.3f, true, 1);

		when(random.nextInt(1)).thenReturn(0);
		genome.addNodeGene(gene1);
		genome.addNodeGene(gene2);
		genome.addConnectionGene(connection);

		genome.performAddNodeMutation();

		assertEquals(genome.getNodeGenes().size(), 3);
		assertEquals(genome.getConnectionGenes().size(), 3);
		
		ConnectionGene inConnection = genome.getConnectionGenes().get(1);
		ConnectionGene outConnection = genome.getConnectionGenes().get(2);
		assertEquals(inConnection.getWeight(), 1f);
		assertEquals(outConnection.getWeight(), 0.3f);
		
		assertEquals(inConnection.getInNodeIdx(), 0);
		assertEquals(inConnection.getOutNodeIdx(), 2);
		
		assertEquals(outConnection.getInNodeIdx(), 2);
		assertEquals(outConnection.getOutNodeIdx(), 1);
	}
}
