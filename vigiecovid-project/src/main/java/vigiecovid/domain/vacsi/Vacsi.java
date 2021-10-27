package vigiecovid.domain.vacsi;

import java.time.LocalDate;

public class Vacsi {
	
	private String clage;
	private String dep;
	private LocalDate jour;
	private long dose1;
	private long complet;
	private long cumDose1;
	private long cumComplet;
	private double couvDose1;
	private double couvComplet;
	
	public Vacsi(String clage, String dep, LocalDate jour, long dose1, long complet, long cumDose1, long cumComplet,
			double couvDose1, double couvComplet) {
		super();
		this.clage = clage;
		this.jour = jour;
		this.dose1 = dose1;
		this.complet = complet;
		this.cumDose1 = cumDose1;
		this.cumComplet = cumComplet;
		this.couvDose1 = couvDose1;
		this.couvComplet = couvComplet;
	}

	public Vacsi(String clage, String dep, LocalDate jour) {
		super();
		this.clage = clage;
		this.jour = jour;
		this.dose1 = 0;
		this.complet = 0;
		this.cumDose1 = 0;
		this.cumComplet = 0;
		this.couvDose1 = 0;
		this.couvComplet = 0;
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
	public long getComplet() {
		return complet;
	}
	public long getCumDose1() {
		return cumDose1;
	}
	public long getCumComplet() {
		return cumComplet;
	}
	public double getCouvDose1() {
		return couvDose1;
	}
	public double getCouvComplet() {
		return couvComplet;
	}
	
	public void plus(Vacsi vacsia) {
		this.dose1       += vacsia.dose1;
		this.complet     += vacsia.complet;
		this.cumDose1    += vacsia.cumDose1;
		this.cumComplet  += vacsia.cumComplet;
		this.couvDose1   += vacsia.couvDose1;
		this.couvComplet += vacsia.couvComplet;
	}

}
