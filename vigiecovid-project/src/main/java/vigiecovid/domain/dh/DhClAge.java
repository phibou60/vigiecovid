package vigiecovid.domain.dh;

import java.time.LocalDate;

public class DhClAge {

	public String reg;
	public String clAge;
	public LocalDate jour;
	public long hosp = 0;
	public long rea = 0;
	public long rad = 0;
	public long dc = 0;

	public DhClAge() {
	}
		
	public DhClAge(String reg, String clAge, LocalDate jour, long hosp, long rea, long rad,
			long dc) {
		this.reg = reg;
		this.clAge = clAge;
		this.jour = jour;
		this.hosp = hosp;
		this.rea = rea;
		this.rad = rad;
		this.dc = dc;
	}

	public String getReg() {
		return reg;
	}

	public String getClAge() {
		return clAge;
	}

	public LocalDate getJour() {
		return jour;
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

	public DhClAge plus(DhClAge d2) {
		hosp += d2.getHosp();
		rea += d2.getRea();
		rad += d2.getRad();
		dc += d2.getDc();
		return this;
	}

}