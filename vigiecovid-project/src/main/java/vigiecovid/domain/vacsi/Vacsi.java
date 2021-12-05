package vigiecovid.domain.vacsi;

import java.time.LocalDate;

public class Vacsi {
	
	private String clage;
	private String dep;
	private LocalDate jour;
	private long dose1;
	private long complet;
	private long rappel;
	private long cumDose1;
	private long cumComplet;
	private long cumRappel;
	private double couvDose1;
	private double couvComplet;
	private double couvRappel;
	
	public Vacsi(String clage, String dep, LocalDate jour, long dose1, long complet, long rappel,
			long cumDose1, long cumComplet, long cumRappel,
			double couvDose1, double couvComplet, double couvRappel) {
		super();
		this.clage = clage;
		this.jour = jour;
		this.dose1 = dose1;
		this.complet = complet;
		this.rappel = rappel;
		
		this.cumDose1 = cumDose1;
		this.cumComplet = cumComplet;
		this.cumRappel = cumRappel;
		
		this.couvDose1 = couvDose1;
		this.couvComplet = couvComplet;
		this.couvRappel = couvRappel;
	}

	public Vacsi(String clage, String dep, LocalDate jour) {
		super();
		this.clage = clage;
		this.jour = jour;
		this.dose1 = 0;
		this.complet = 0;
		this.rappel = 0;
		
		this.cumDose1 = 0;
		this.cumComplet = 0;
		this.cumRappel = 0;
		
		this.couvDose1 = 0;
		this.couvComplet = 0;
		this.couvRappel = 0;
	}
	
	public String getClage() {
		return clage;
	}
	public String getDep() {
		return dep;
	}
	public LocalDate getJour() {
		return jour;
	}
	public long getDose1() {
		return dose1;
	}
	public long getRappel() {
		return rappel;
	}
	public long getComplet() {
		return complet;
	}
	public long getCumDose1() {
		return cumDose1;
	}
	public long getCumComplet() {
		return cumComplet;
	}
	public long getCumRappel() {
		return cumRappel;
	}
	public double getCouvDose1() {
		return couvDose1;
	}
	public double getCouvComplet() {
		return couvComplet;
	}
	public double getCouvRappel() {
		return couvRappel;
	}
	
	public void plus(Vacsi vacsia) {
		this.dose1 += vacsia.dose1;
		this.complet += vacsia.complet;
		this.complet += vacsia.complet;
		this.cumDose1 += vacsia.cumDose1;
		this.cumComplet += vacsia.cumComplet;
		this.cumRappel += vacsia.cumRappel;
		this.couvDose1 += vacsia.couvDose1;
		this.couvComplet += vacsia.couvComplet;
		this.couvRappel += vacsia.couvRappel;
	}

}
