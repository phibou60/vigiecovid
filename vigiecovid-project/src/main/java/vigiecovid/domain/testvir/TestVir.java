package vigiecovid.domain.testvir;

public class TestVir {

	public long positifs = 0;
	public long tests = 0;

	public TestVir(long positifs, long tests) {
		this.positifs = positifs;
		this.tests = tests;
	}
	
	public long getTests() {
		return tests;
	}
	
	public long getPositifs() {
		return positifs;
	}
	
	public double getPc() {
		return 100D * positifs / tests;
	}

	
}