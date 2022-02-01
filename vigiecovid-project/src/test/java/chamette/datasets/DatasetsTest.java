package chamette.datasets;

import static org.junit.Assert.*;

import org.junit.Test;

public class DatasetsTest {

	@Test
	public void testDatasets() {
		Dataset d1 = new CommonDataset("d1").setData("x1");
		Dataset d2 = new CommonDataset("d2").setData("x2");
		Dataset d3 = new CommonDataset("d3").setData("x3");
		
		Dataset d11 = new CommonDataset("d11").setData("x11");
		d1.addChildDataset(d11);
		Dataset d12 = new CommonDataset("d12").setData("x12");
		d1.addChildDataset(d12);

		Dataset d111 = new CommonDataset("d111").setData("x111");
		d11.addChildDataset(d111);
		Dataset d112 = new CommonDataset("d112").setData("x112");
		d11.addChildDataset(d112);
		
		Datasets datasets = new Datasets();
		datasets.add(d1);
		datasets.add(d2);
		datasets.add(d3);
		datasets.add(d11);
		datasets.add(d12);
		datasets.add(d111);
		datasets.add(d112);
		
		assertEquals("d1", datasets.get("d1").getName());
		assertEquals("d2", datasets.get("d2").getName());
		assertEquals(7, datasets.size());
		assertEquals("d12", datasets.get("d12").getName());
		assertEquals("d112", datasets.get("d112").getName());
		
		assertEquals("x1", (String) datasets.get("d1").getData());
		assertEquals("x2", (String) datasets.get("d2").getData());
		assertEquals("x12", (String) datasets.get("d12").getData());
		assertEquals("x112", (String) datasets.get("d112").getData());
		
		assertEquals(0, datasets.get("d2").getChildren().size());
		assertEquals(2, datasets.get("d1").getChildren().size());
		
		Dataset d11b = datasets.get("d1").getChildren().get(0);
		assertEquals("d11", d11b.getName());
		assertEquals("d111", d11b.getChildren().get(0).getName());
		assertEquals("x111", d11b.getChildren().get(0).getData());
		
		assertTrue(datasets.containsKey("d1"));
		assertFalse(datasets.containsKey("unk"));
		
		datasets.remove(d3);
		assertEquals(6, datasets.size());
		
		datasets.remove(d1);
		assertEquals(1, datasets.size());
		assertFalse(datasets.containsKey("d1"));
		assertFalse(datasets.containsKey("d11"));
		assertFalse(datasets.containsKey("d111"));
		
	}

}
