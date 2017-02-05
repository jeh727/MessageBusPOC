package MessageBus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ConfigurationStoreTest {

	@Test
	public void createPatternTest() throws Exception {
		System.out.print("createPatternTest...");
		int id = -1;
		try {
			id = ConfigurationStore.createPattern();
		} finally {
			ConfigurationStore.deletePattern(id);
		}
		System.out.println("DONE");
	}

	@Test
	public void getPatternIdsTest() throws Exception {
		System.out.print("getPatternIdsTest...");

		int id = ConfigurationStore.createPattern();
		try {
			List<Integer> ids = ConfigurationStore.getPatternIds();
			assertTrue(ids.size() > 0);
		} finally {
			ConfigurationStore.deletePattern(id);
		}
		System.out.println("DONE");
	}

	@Test
	public void createNodeTest() throws Exception {
		System.out.print("createNodeTest...");

		int patternId = -1;
		int nodeId = -1;

		patternId = ConfigurationStore.createPattern();
		try {
			nodeId = ConfigurationStore.createNode(patternId, "TestNode", "{}", "{}", "{}");
		} finally {
			ConfigurationStore.deleteNode(nodeId);
			ConfigurationStore.deletePattern(patternId);
		}

		patternId = ConfigurationStore.createPattern();
		try {
			JSONObject obj = new JSONObject();
			nodeId = ConfigurationStore.createNode(patternId, "TestNode", obj, obj, obj);
		} finally {
			ConfigurationStore.deleteNode(nodeId);
			ConfigurationStore.deletePattern(patternId);
		}
		System.out.println("DONE");
	}

	@Test
	public void setNodeEnabledTest() throws Exception {
		System.out.print("setNodeEnabledTest...");

		int patternId = ConfigurationStore.createPattern();
		JSONObject obj = new JSONObject();
		int nodeId = ConfigurationStore.createNode(patternId, "TestNode", obj, obj, obj);

		try {
			ConfigurationStore.setNodeEnabled(nodeId, true);
			ConfigurationStore.NodeResult res = ConfigurationStore.getNode(nodeId);
			assertEquals(true, res.enabled);
			
			ConfigurationStore.setNodeEnabled(nodeId, false);
			res = ConfigurationStore.getNode(nodeId);
			assertEquals(false, res.enabled);
			
		} finally {
			ConfigurationStore.deleteNode(nodeId);
			ConfigurationStore.deletePattern(patternId);
		}
		
		System.out.println("DONE");
	}

	@Test
	public void setPatternNodesEnabledTest() throws Exception {
		System.out.print("setPatternNodesEnabledTest...");

		int patternId = ConfigurationStore.createPattern();
		int patternId2 = ConfigurationStore.createPattern();
		List<Integer> nodeIds = new ArrayList<Integer>();
		
		JSONObject inputs = new JSONObject();
		inputs.put("input", 10);
		JSONObject config = new JSONObject();
		config.put("config", 20);
		JSONObject outputs = new JSONObject();
		outputs.put("output", 30);
		
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode1", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode2", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode3", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId2, "TestNode4", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId2, "TestNode5", inputs, config, outputs));
		

		try {
			//turn on patternId only
			ConfigurationStore.setPatternNodesEnabled(patternId, true);
			List<ConfigurationStore.NodeResult> nodes = ConfigurationStore.getPatternNodeConfigs(patternId);
			for(ConfigurationStore.NodeResult node : nodes) {
				assertEquals(true, node.enabled);
			}
			
			nodes = ConfigurationStore.getPatternNodeConfigs(patternId2);
			for(ConfigurationStore.NodeResult node : nodes) {
				assertEquals(false, node.enabled);
			}
			
			//turn on patternId2 as well
			ConfigurationStore.setPatternNodesEnabled(patternId2, true);
			nodes = ConfigurationStore.getPatternNodeConfigs(patternId2);
			for(ConfigurationStore.NodeResult node : nodes) {
				assertEquals(true, node.enabled);
			}
			
			//disable patternId
			ConfigurationStore.setPatternNodesEnabled(patternId, false);
			nodes = ConfigurationStore.getPatternNodeConfigs(patternId);
			for(ConfigurationStore.NodeResult node : nodes) {
				assertEquals(false, node.enabled);
			}
			
			nodes = ConfigurationStore.getPatternNodeConfigs(patternId2);
			for(ConfigurationStore.NodeResult node : nodes) {
				assertEquals(true, node.enabled);
			}
			
		} finally {
			for(int nodeId : nodeIds) {
				ConfigurationStore.deleteNode(nodeId);
			}
			ConfigurationStore.deletePattern(patternId);
			ConfigurationStore.deletePattern(patternId2);
		}
		
		System.out.println("DONE");
	}
	
	@Test
	public void getNodeTest() throws Exception {
		System.out.print("getNodeTest...");

		int patternId = ConfigurationStore.createPattern();
		JSONObject inputs = new JSONObject();
		inputs.put("input", 10);
		JSONObject config = new JSONObject();
		config.put("config", 20);
		JSONObject outputs = new JSONObject();
		outputs.put("output", 30);

		int nodeId = ConfigurationStore.createNode(patternId, "TestNode", inputs, config, outputs);
		try {
			ConfigurationStore.NodeResult res = ConfigurationStore.getNode(nodeId);

			assertEquals(nodeId, res.id);
			assertEquals(patternId, res.patternId);
			assertEquals("TestNode", res.nodeType);
			assertEquals(false, res.enabled);
			assertEquals(inputs.toString(), res.inputs.toString());
			assertEquals(config.toString(), res.config.toString());
			assertEquals(outputs.toString(), res.outputs.toString());
			
		} finally {
			ConfigurationStore.deleteNode(nodeId);
			ConfigurationStore.deletePattern(patternId);
		}

		System.out.println("DONE");
	}


	@Test
	public void getNodeTypeConfigsTest() throws Exception {
		System.out.print("getNodeTypeConfigsTest...");
		
		int patternId = ConfigurationStore.createPattern();
		List<Integer> nodeIds = new ArrayList<Integer>();
		
		JSONObject inputs = new JSONObject();
		inputs.put("input", 10);
		JSONObject config = new JSONObject();
		config.put("config", 20);
		JSONObject outputs = new JSONObject();
		outputs.put("output", 30);
		
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode2", inputs, config, outputs));
		
		try {
			List<ConfigurationStore.NodeResult> nodes = ConfigurationStore.getNodeTypeConfigs("TestNode");
			assertEquals(2, nodes.size());
			
			assertEquals((int)nodeIds.get(0), nodes.get(0).id);
			assertEquals(patternId, nodes.get(0).patternId);
			assertEquals("TestNode", nodes.get(0).nodeType);
			assertEquals(false, nodes.get(0).enabled);
			assertEquals(inputs.toString(), nodes.get(0).inputs.toString());
			assertEquals(config.toString(), nodes.get(0).config.toString());
			assertEquals(outputs.toString(), nodes.get(0).outputs.toString());
			
			assertEquals((int)nodeIds.get(1), nodes.get(1).id);
			assertEquals(patternId, nodes.get(1).patternId);
			assertEquals("TestNode", nodes.get(1).nodeType);
			assertEquals(false, nodes.get(1).enabled);
			assertEquals(inputs.toString(), nodes.get(1).inputs.toString());
			assertEquals(config.toString(), nodes.get(1).config.toString());
			assertEquals(outputs.toString(), nodes.get(1).outputs.toString());
			
			
			nodes = ConfigurationStore.getNodeTypeConfigs("TestNode2");
			assertEquals(1, nodes.size());
			assertEquals((int)nodeIds.get(2), nodes.get(0).id);
			assertEquals(patternId, nodes.get(0).patternId);
			assertEquals("TestNode2", nodes.get(0).nodeType);
			assertEquals(false, nodes.get(0).enabled);
			assertEquals(inputs.toString(), nodes.get(0).inputs.toString());
			assertEquals(config.toString(), nodes.get(0).config.toString());
			assertEquals(outputs.toString(), nodes.get(0).outputs.toString());
			
		} finally {
			for(int nodeId : nodeIds) {
				ConfigurationStore.deleteNode(nodeId);
			}
			ConfigurationStore.deletePattern(patternId);
		}
	}
	
	@Test
	public void getPatternNodeConfigsTest() throws Exception {
		System.out.print("getNodeTypeConfigsTest...");
		
		int patternId = ConfigurationStore.createPattern();
		int patternId2 = ConfigurationStore.createPattern();
		List<Integer> nodeIds = new ArrayList<Integer>();
		
		JSONObject inputs = new JSONObject();
		inputs.put("input", 10);
		JSONObject config = new JSONObject();
		config.put("config", 20);
		JSONObject outputs = new JSONObject();
		outputs.put("output", 30);
		
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode1", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode2", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId, "TestNode3", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId2, "TestNode4", inputs, config, outputs));
		nodeIds.add(ConfigurationStore.createNode(patternId2, "TestNode5", inputs, config, outputs));
		
		try {
			List<ConfigurationStore.NodeResult> nodes = ConfigurationStore.getPatternNodeConfigs(patternId);
			assertEquals(3, nodes.size());
			
			assertEquals((int)nodeIds.get(0), nodes.get(0).id);
			assertEquals(patternId, nodes.get(0).patternId);
			assertEquals("TestNode1", nodes.get(0).nodeType);
			assertEquals(false, nodes.get(0).enabled);
			assertEquals(inputs.toString(), nodes.get(0).inputs.toString());
			assertEquals(config.toString(), nodes.get(0).config.toString());
			assertEquals(outputs.toString(), nodes.get(0).outputs.toString());
			
			assertEquals((int)nodeIds.get(1), nodes.get(1).id);
			assertEquals(patternId, nodes.get(1).patternId);
			assertEquals("TestNode2", nodes.get(1).nodeType);
			assertEquals(false, nodes.get(1).enabled);
			assertEquals(inputs.toString(), nodes.get(1).inputs.toString());
			assertEquals(config.toString(), nodes.get(1).config.toString());
			assertEquals(outputs.toString(), nodes.get(1).outputs.toString());

			assertEquals((int)nodeIds.get(2), nodes.get(2).id);
			assertEquals(patternId, nodes.get(2).patternId);
			assertEquals("TestNode3", nodes.get(2).nodeType);
			assertEquals(false, nodes.get(2).enabled);
			assertEquals(inputs.toString(), nodes.get(2).inputs.toString());
			assertEquals(config.toString(), nodes.get(2).config.toString());
			assertEquals(outputs.toString(), nodes.get(2).outputs.toString());

			nodes = ConfigurationStore.getPatternNodeConfigs(patternId2);
			assertEquals(2, nodes.size());
			
			assertEquals((int)nodeIds.get(3), nodes.get(0).id);
			assertEquals(patternId2, nodes.get(0).patternId);
			assertEquals("TestNode4", nodes.get(0).nodeType);
			assertEquals(false, nodes.get(0).enabled);
			assertEquals(inputs.toString(), nodes.get(0).inputs.toString());
			assertEquals(config.toString(), nodes.get(0).config.toString());
			assertEquals(outputs.toString(), nodes.get(0).outputs.toString());
			
			assertEquals((int)nodeIds.get(4), nodes.get(1).id);
			assertEquals(patternId2, nodes.get(1).patternId);
			assertEquals("TestNode5", nodes.get(1).nodeType);
			assertEquals(false, nodes.get(1).enabled);
			assertEquals(inputs.toString(), nodes.get(1).inputs.toString());
			assertEquals(config.toString(), nodes.get(1).config.toString());
			assertEquals(outputs.toString(), nodes.get(1).outputs.toString());
			
		} finally {
			for(int nodeId : nodeIds) {
				ConfigurationStore.deleteNode(nodeId);
			}
			ConfigurationStore.deletePattern(patternId);
			ConfigurationStore.deletePattern(patternId2);
		}
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(ConfigurationStoreTest.class);

		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}

		System.out.println(result.wasSuccessful());
	}
}
