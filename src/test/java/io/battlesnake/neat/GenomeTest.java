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
		ConnectionGene connection = new ConnectionGene(0, 1, 0.3f, true, InnovationNrGenerator.getNext());

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
		
		assertEquals(0, inConnection.getInNodeInnovationNr());
		assertEquals(2, inConnection.getOutNodeInnovationNr());
		
		assertEquals(2, outConnection.getInNodeInnovationNr());
		assertEquals(1, outConnection.getOutNodeInnovationNr());
	}
}
