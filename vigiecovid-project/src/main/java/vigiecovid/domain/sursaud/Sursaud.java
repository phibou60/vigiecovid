package vigiecovid.domain.sursaud;

import java.time.LocalDate;

/**
 * Bean permettant de stocker les informations Sursaud.<br>
 * Ces données correspondent aux appels à SOS Médecins et aux passages aux urgences. 
 *
 */
public class Sursaud {
	
	private String dep;
	private LocalDate jour;
	private String categAge;
	private long nbrePassCorona;
	private long nbrePassTot;
	private long nbreHospitCorona;
	private long nbrePassCoronaH;
	private long nbrePassCoronaF;
	private long nbrePassTotH;
	private long nbrePassTotF;
	private long nbreHospitCoronaH;
	private long nbreHospitCoronaF;
	private long nbreActeCorona;
	private long nbreActeTot;
	private long nbreActeCoronaH;
	private long nbreActeCoronaF;
	private long nbreActeTotH;
	private long nbreActeTotF;
	
	public Sursaud() {
		super();
		this.dep = "";
		this.jour = null;
		this.categAge = "";
	}
	
	public Sursaud(String dep, LocalDate jour, String categAge) {
		super();
		this.dep = dep;
		this.jour = jour;
		this.categAge = categAge;
	}
	
	public Sursaud(String dep, LocalDate jour, String categAge, long nbrePassCorona, long nbrePassTot,
			long nbreHospitCorona, long nbrePassCoronaH, long nbrePassCoronaF, long nbrePassTotH, long nbrePassTotF,
			long nbreHospitCoronaH, long nbreHospitCoronaF, long nbreActeCorona, long nbreActeTot, long nbreActeCoronaH,
			long nbreActeCoronaF, long nbreActeTotH, long nbreActeTotF) {
		super();
		this.dep = dep;
		this.jour = jour;
		this.categAge = categAge;
		this.nbrePassCorona = nbrePassCorona;
		this.nbrePassTot = nbrePassTot;
		this.nbreHospitCorona = nbreHospitCorona;
		this.nbrePassCoronaH = nbrePassCoronaH;
		this.nbrePassCoronaF = nbrePassCoronaF;
		this.nbrePassTotH = nbrePassTotH;
		this.nbrePassTotF = nbrePassTotF;
		this.nbreHospitCoronaH = nbreHospitCoronaH;
		this.nbreHospitCoronaF = nbreHospitCoronaF;
		this.nbreActeCorona = nbreActeCorona;
		this.nbreActeTot = nbreActeTot;
		this.nbreActeCoronaH = nbreActeCoronaH;
		this.nbreActeCoronaF = nbreActeCoronaF;
		this.nbreActeTotH = nbreActeTotH;
		this.nbreActeTotF = nbreActeTotF;
	}
	
	public String getDep() {
		return dep;
	}
	public LocalDate getJour() {
		return jour;
	}
	public String getCategAge() {
		return categAge;
	}
	public long getNbrePassCorona() {
		return nbrePassCorona;
	}
	public long getNbrePassTot() {
		return nbrePassTot;
	}
	public long getNbreHospitCorona() {
		return nbreHospitCorona;
	}
	public long getNbrePassCoronaH() {
		return nbrePassCoronaH;
	}
	public long getNbrePassCoronaF() {
		return nbrePassCoronaF;
	}
	public long getNbrePassTotH() {
		return nbrePassTotH;
	}
	public long getNbrePassTotF() {
		return nbrePassTotF;
	}
	public long getNbreHospitCoronaH() {
		return nbreHospitCoronaH;
	}
	public long getNbreHospitCoronaF() {
		return nbreHospitCoronaF;
	}
	public long getNbreActeCorona() {
		return nbreActeCorona;
	}
	public long getNbreActeTot() {
		return nbreActeTot;
	}
	public long getNbreActeCoronaH() {
		return nbreActeCoronaH;
	}
	public long getNbreActeCoronaF() {
		return nbreActeCoronaF;
	}
	public long getNbreActeTotH() {
		return nbreActeTotH;
	}
	public long getNbreActeTotF() {
		return nbreActeTotF;
	}

	public Sursaud plus(Sursaud sursaud) {
		this.nbrePassCorona    += sursaud.nbrePassCorona;
		this.nbrePassTot       += sursaud.nbrePassTot;
		this.nbreHospitCorona  += sursaud.nbreHospitCorona;
		this.nbrePassCoronaH   += sursaud.nbrePassCoronaH;
		this.nbrePassCoronaF   += sursaud.nbrePassCoronaF;
		this.nbrePassTotH      += sursaud.nbrePassTotH;
		this.nbrePassTotF      += sursaud.nbrePassTotF;
		this.nbreHospitCoronaH += sursaud.nbreHospitCoronaH;
		this.nbreHospitCoronaF += sursaud.nbreHospitCoronaF;
		this.nbreActeCorona    += sursaud.nbreActeCorona;
		this.nbreActeTot       += sursaud.nbreActeTot;
		this.nbreActeCoronaH   += sursaud.nbreActeCoronaH;
		this.nbreActeCoronaF   += sursaud.nbreActeCoronaF;
		this.nbreActeTotH      += sursaud.nbreActeTotH;
		this.nbreActeTotF      += sursaud.nbreActeTotF;
		
		return this;
	}
	
}