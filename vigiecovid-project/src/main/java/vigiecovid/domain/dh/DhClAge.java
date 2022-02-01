package vigiecovid.domain.dh;

import java.time.LocalDate;

public class DhClAge {

	private String reg;
	private String clAge;
	private LocalDate jour;
	private long hosp = 0;
	private long rea = 0;
	private long rad = 0;
	private long dc = 0;

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

	@Override
	public String toString() {
		return reg+";"+clAge+";"+jour+";"+hosp+";"+rea+";"+rad+";"+dc;
	}
}