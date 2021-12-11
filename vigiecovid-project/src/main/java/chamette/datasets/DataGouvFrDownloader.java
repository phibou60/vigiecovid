package chamette.datasets;

import chamette.tools.RestTool;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

public class DataGouvFrDownloader extends CommonDataset implements Downloadable {

	private static final Logger LOGGER = Logger.getLogger(DataGouvFrDownloader.class);
	
	private String dgDatasets;
	private String id;
	private String lastDownloaded = "<none>";
	private String mode;
	private String directoryToSaveTo;

	public DataGouvFrDownloader(String filename, String dgDatasets, String id, String mode, String directoryToSaveTo) {
		super(filename);
		this.dgDatasets = dgDatasets;
		this.id = id;
		this.mode = mode;
		this.directoryToSaveTo = directoryToSaveTo;
	}

	public boolean checkUpdate() throws Exception {
		boolean nouveau = false;

		if (mode != null && mode.equalsIgnoreCase("files") && directoryToSaveTo != null) {

			File folder = new File(directoryToSaveTo);
			if (folder.exists()) {
				File file = new File(directoryToSaveTo, getName() + ".csv");
				if (file.exists()) {
					LOGGER.info("Read CSV file: " + file.getAbsolutePath());
		
					try {
						List<String> lines = Files.readAllLines(file.toPath());
						String[] dataLines = lines.toArray(new String[lines.size()]);
						setData(dataLines);
						return true;
					} catch (Exception e) {
						LOGGER.error("Exception: ", e);
					}
				} else {
					LOGGER.info("Unknown CSV file: " + file.getAbsolutePath());
				}
			} else {
				LOGGER.info("Unknown CSV directory: " + folder.getAbsolutePath());
			}
		}

		LOGGER.info("Look for update for file \"" + getName() + "\". Last found file is " + lastDownloaded);

		RestTool restHelper = new RestTool("http://www.data.gouv.fr/api/1/");
		restHelper.ignoreSSLCertificatVerification();
		restHelper.get("datasets/" + dgDatasets);

		if (restHelper.result != null) {
			LOGGER.trace("restHelper.content: " + restHelper.content);
			JsonArray resources = restHelper.result.getJsonArray("resources");

			for (int i = 0; i < resources.size(); i++) {
				JsonObject resource = resources.getJsonObject(i);

				LOGGER.debug("- " + resource.getString("title") + ", id:" + resource.getString("id") + " >> "
						+ resource.getString("url"));

				if (resource.getString("id").equals(id)) {
					LOGGER.debug(">> found ...");

					if (!resource.getString("url").equals(lastDownloaded)) {
						lastDownloaded = resource.getString("url");
						LOGGER.info(" >> found update " + resource.getString("url"));

						RestTool restHelperDownload = new RestTool(resource.getString("url"));
						restHelperDownload.ignoreSSLCertificatVerification();
						restHelperDownload.get();

						String[] lines = restHelperDownload.content.split("\n");
						setData(lines);
						LOGGER.debug("   done. " + restHelperDownload.content.length() + " chars downloaded. "
								+ lines.length + " lines");
						saveToFile();
						nouveau = true;
					}
				}

			}
		}
		return nouveau;
	}

	public void saveToFile() throws Exception {
		if (directoryToSaveTo == null) {
			LOGGER.debug("directoryToSaveTo == null");
			return;
		}
		LOGGER.info(" > save in folder: " + directoryToSaveTo);
		File folder = new File(directoryToSaveTo);
		if (directoryToSaveTo == null) {
			LOGGER.warn("Unknown directory to save to: " + directoryToSaveTo);
			return;
		}
		File file = new File(folder, getName() + ".csv");
		if (directoryToSaveTo == null) {
			LOGGER.warn("Unknown directory to save to: " + directoryToSaveTo);
			return;
		}
		try {
			String[] lines = (String[]) getData();
			Files.write(file.toPath(), Arrays.asList(lines));
			LOGGER.info("   done");
		} catch (Exception e) {
			LOGGER.warn("Exception:", e);
		}

	}
}