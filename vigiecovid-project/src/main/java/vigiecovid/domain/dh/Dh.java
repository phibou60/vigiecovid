package vigiecovid.domain.dh;

import java.time.LocalDate;

public class Dh {

	private String dep;
	private String sexe;
	private LocalDate jour;
	private long hosp = 0;
	private long rea = 0;
	private long rad = 0;
	private long dc = 0;
	private long count = 0;
		
	public Dh(String dep, String sexe, LocalDate jour, long hosp, long rea, long rad, long dc) {
		this.dep = dep;
		this.sexe = sexe;
		this.jour = jour;
		this.hosp = hosp;
		this.rea = rea;
		this.rad = rad;
		this.dc = dc;
		count = 1;
	}
	
	public Dh(Dh clone) {
		this.dep = clone.getDep();
		this.sexe = clone.getSexe();
		this.jour = clone.getJour();
		this.hosp = clone.getHosp();
		this.rea = clone.getRea();
		this.rad = clone.getRad();
		this.dc = clone.getDc();
		count = 1;
	}

	public String getDep() {
		return dep;
	}

	public String getSexe() {
		return sexe;
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
	
	public long get(String metric) {
		switch (metric) {
		  case "dc":
		    return dc;
		  case "hosp":
			  return hosp;
		  case "rea":
			  return rea;
		  default:
			  return rad;
		}
	}

	public Dh plus(Dh d2) {
		hosp += d2.getHosp();
		rea += d2.getRea();
		rad += d2.getRad();
		dc += d2.getDc();
		count++;
		return this;
	}

	public Dh avg() {
		hosp = hosp / count;
		rea = rea / count;
		rad = rad / count;
		dc = dc / count;
		count = 1;
		return this;
	}

	@Override
	public String toString() {
		return dep+";"+sexe+";"+jour+";"+hosp+";"+rea+";"+rad+";"+dc;
	}

}