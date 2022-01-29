package vigiecovid.config;

import javax.servlet.ServletContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import chamette.datasets.DataGouvFrDownloader;
import chamette.datasets.Datasets;

@Configuration
public class AppConfig {
	
    @Bean(destroyMethod = "stopDownloadEngine")
    public Datasets getDatasets() {
	
    	Datasets datasets = new Datasets();
		
		String mode = System.getenv("VIGIECOVID_MODE");
		if (mode == null) {
			mode = "connected";
		}
	
		String folder = System.getenv("VIGIECOVID_FOLDER");
	
		datasets.add(new DataGouvFrDownloader("donnees-hospitalieres-covid19",
				"5e7e104ace2080d9162b61d8", "63352e38-d353-4b54-bfd1-f1b3ee1cabd7", mode, folder));
		datasets.add(new DataGouvFrDownloader("donnees-hospitalieres-nouveaux-covid19",
				"5e7e104ace2080d9162b61d8", "6fadff46-9efd-4c53-942a-54aca783c30c", mode, folder));
		datasets.add(new DataGouvFrDownloader("donnees-hospitalieres-classe-age-covid19",
				"5e7e104ace2080d9162b61d8", "08c18e08-6780-452d-9b8c-ae244ad529b3", mode, folder));
		datasets.add(new DataGouvFrDownloader("sp-pos-quot-dep", "5ed117db6c161bd5baf070be",
				"406c6a23-e283-4300-9484-54e78c8ae675", mode, folder));
		datasets.add(new DataGouvFrDownloader("sp-pos-quot-fra", "5ed117db6c161bd5baf070be",
				"dd0de5d9-b5a5-4503-930a-7b08dc0adc7c", mode, folder));
		datasets.add(new DataGouvFrDownloader("sursaud-covid19-quotidien-departement",
				"5e74ecf52eb7514f2d3b8845", "eceb9fb4-3ebc-4da3-828d-f5939712600a", mode, folder));
	
		datasets.add(new DataGouvFrDownloader("vacsi-a-fra", "6010206e7aa742eb447930f7",
				"54dd5f8d-1e2e-4ccb-8fb8-eac68245befd", mode, folder));
		
		datasets.startDownloadEngine();
		
		return datasets;
	}

}