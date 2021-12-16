package vigiecovid.domain.dh;

import java.time.LocalDate;

public class Dh {

	public String dep;
	public String sexe;
	public LocalDate jour;
	public long hosp = 0;
	public long rea = 0;
	public long rad = 0;
	public long dc = 0;

	public Dh() {
	}
		
	public Dh(String dep, String sexe, LocalDate jour, long hosp, long rea, long rad, long dc) {
		this.dep = dep;
		this.sexe = sexe;
		this.jour = jour;
		this.hosp = hosp;
		this.rea = rea;
		this.rad = rad;
		this.dc = dc;
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
		return this;
	}

	@Override
	public String toString() {
		return dep+";"+sexe+";"+jour+";"+hosp+";"+rea+";"+rad+";"+dc;
	}

}