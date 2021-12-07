package vigiecovid.domain.vacsi;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class VacsiTest {

	@Test
	public void testVacsi() {
		Vacsi vacsi = new Vacsi("02", "60", LocalDate.of(2021, 12, 07), 1, 2, 3, 4, 5, 6, 7, 8, 9);
		assertEquals("02", vacsi.getClage());
		assertEquals("60", vacsi.getDep());
		assertEquals("2021-12-07", vacsi.getJour().toString());
		
		assertEquals(1, vacsi.getDose1());
		assertEquals(2, vacsi.getComplet());
		assertEquals(3, vacsi.getRappel());
		
		assertEquals(4, vacsi.getCumDose1());
		assertEquals(5, vacsi.getCumComplet());
		assertEquals(6, vacsi.getCumRappel());
		
		assertEquals(700, Math.round(vacsi.getCouvDose1()*100));
		assertEquals(800, Math.round(vacsi.getCouvComplet()*100));
		assertEquals(900, Math.round(vacsi.getCouvRappel()*100));

	}

	@Test
	public void testVacsiPlus() {
		Vacsi vacsi = new Vacsi("02", "60", LocalDate.of(2021, 12, 07), 1, 2, 3, 4, 5, 6, 7, 8, 9);
		Vacsi vacsi2 = new Vacsi("02", "60", LocalDate.of(2021, 12, 07), 10, 20, 30, 40, 50, 60, 70,
				80, 90);
		
		vacsi.plus(vacsi2);
		assertEquals("02", vacsi.getClage());
		assertEquals("60", vacsi.getDep());
		assertEquals("2021-12-07", vacsi.getJour().toString());
		
		assertEquals(11, vacsi.getDose1());
		assertEquals(22, vacsi.getComplet());
		assertEquals(33, vacsi.getRappel());
		
		assertEquals(44, vacsi.getCumDose1());
		assertEquals(55, vacsi.getCumComplet());
		assertEquals(66, vacsi.getCumRappel());
		
		assertEquals(7700, Math.round(vacsi.getCouvDose1()*100));
		assertEquals(8800, Math.round(vacsi.getCouvComplet()*100));
		assertEquals(9900, Math.round(vacsi.getCouvRappel()*100));

	}

}
