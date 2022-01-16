package chamette.datasets;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommonDatasetTest {

	@Test
	public void testCommonDataset() {
		Dataset d1 = new CommonDataset("d1").setData("x1");
		
		Dataset d11 = new CommonDataset("d11").setData("x11");
		d1.addChildDataset(d11);
		Dataset d12 = new CommonDataset("d12").setData("x12");
		d1.addChildDataset(d12);
		
		assertEquals("d1", d1.getName());
		assertEquals("x1", d1.getData());
		assertEquals(2, d1.getChildren().size());
		assertEquals("d11", d1.getChildren().get(0).getName());
	}

}
