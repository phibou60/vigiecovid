package vigiecovid.domain.testvir;

import java.time.LocalDate;

public class TestVirQuotFra extends TestVir {

	private LocalDate jour;
	
	public TestVirQuotFra(LocalDate jour, long positifs, long tests) {
		super(positifs, tests);
		this.jour = jour;
	}

	public LocalDate getJour() {
		return jour;
	}

	public TestVirQuotFra clone() {
		return new TestVirQuotFra(jour, getPositifs(), getTests());
	}

	@Override
	public String toString() {
		return jour + ";" + getPositifs() + ";" + getTests();
	}
	

}
