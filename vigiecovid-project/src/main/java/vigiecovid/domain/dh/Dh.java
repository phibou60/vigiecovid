package vigiecovid.domain.dh;

public class Dh {

	public long hosp = 0;
	public long rea = 0;
	public long rad = 0;
	public long dc = 0;

	public Dh() {
	}
		
	public Dh(long hosp, long rea, long rad, long dc) {
		this.hosp = hosp;
		this.rea = rea;
		this.rad = rad;
		this.dc = dc;
	}

	public long getHosp() {
		return hosp;
	}

	public long getRea() {
		return rea;
	}

	public long getRad() {
		return rad;
	}
	
	public long getDc() {
		return dc;
	}

	public Dh plus(Dh d2) {
		hosp += d2.getHosp();
		rea += d2.getRea();
		rad += d2.getRad();
		dc += d2.getDc();
		return this;
	}

}