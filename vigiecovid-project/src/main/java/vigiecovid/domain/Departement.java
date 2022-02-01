package vigiecovid.domain;

public class Departement {

	private String dep;
	private String lib;
	private int nbArrondissements;
	private int nbCantons;
	private int nbCommunes;
	private long populationTotale;
	
	public Departement(String dep, String lib, int nbArrondissements, int nbCantons, int nbCommunes,
			long populationTotale) {
		super();
		this.dep = dep;
		this.lib = lib;
		this.nbArrondissements = nbArrondissements;
		this.nbCantons = nbCantons;
		this.nbCommunes = nbCommunes;
		this.populationTotale = populationTotale;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public String getLib() {
		return lib;
	}

	public void setLib(String lib) {
		this.lib = lib;
	}

	public int getNbArrondissements() {
		return nbArrondissements;
	}

	public void setNbArrondissements(int nbArrondissements) {
		this.nbArrondissements = nbArrondissements;
	}

	public int getNbCantons() {
		return nbCantons;
	}

	public void setNbCantons(int nbCantons) {
		this.nbCantons = nbCantons;
	}

	public int getNbCommunes() {
		return nbCommunes;
	}

	public void setNbCommunes(int nbCommunes) {
		this.nbCommunes = nbCommunes;
	}

	public long getPopulationTotale() {
		return populationTotale;
	}

	public void setPopulationTotale(long populationTotale) {
		this.populationTotale = populationTotale;
	}
	
}
