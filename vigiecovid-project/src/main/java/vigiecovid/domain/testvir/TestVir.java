package vigiecovid.domain.testvir;

public class TestVir {

	public long positifs;
	public long tests;
	private long count = 1;

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
	
	public double get(String metric) {
		switch (metric) {
		  case "positifs":
		    return getPositifs();
		  case "tests":
			  return getTests();
		  case "pc":
			  return getPc();
		  default:
			  return getTests();
		}
	}

	public TestVir plus(TestVir t2) {
		positifs += t2.getPositifs();
		tests += t2.getTests();
		count++;
		return this;
	}

	public TestVir avg() {
		positifs = positifs / count;
		tests = tests / count;
		count = 1;
		return this;
	}
	
}