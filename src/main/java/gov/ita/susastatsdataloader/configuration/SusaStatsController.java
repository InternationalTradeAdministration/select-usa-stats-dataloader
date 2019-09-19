package gov.ita.susastatsdataloader.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.susastatsdataloader.storage.BlobMetaData;
import gov.ita.susastatsdataloader.storage.Storage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
public class SusaStatsController {

  private Storage storage;
  private ObjectMapper objectMapper;
  private RestTemplate restTemplate;

  public SusaStatsController(Storage storage, ObjectMapper objectMapper, RestTemplate restTemplate) {
    this.storage = storage;
    this.objectMapper = objectMapper;
    this.restTemplate = restTemplate;
  }

  @GetMapping("/api/config")
  public SusaStatsConfig getConfig() throws IOException {
    String blobAsString = storage.getBlobAsString("configuration.json");
    SusaStatsConfigResponse susaStatsConfigResponse = objectMapper.readValue(blobAsString, SusaStatsConfigResponse.class);
    return susaStatsConfigResponse.susaStatsConfig;
  }

  @GetMapping("/api/save-datasets")
  public String saveDatasets() throws IOException {
    String blobAsString = storage.getBlobAsString("configuration.json");
    SusaStatsConfigResponse susaStatsConfigResponse = objectMapper.readValue(blobAsString, SusaStatsConfigResponse.class);
    SusaStatsConfig susaStatsConfig = susaStatsConfigResponse.susaStatsConfig;

    String imfWeoUrl = susaStatsConfig.imfWeoUrl;
    String imfWeoFileName = getFileNameFromUrl(imfWeoUrl);
    storage.save(imfWeoFileName, restTemplate.getForEntity(imfWeoUrl, byte[].class).getBody(), null);

    Map<String, ByteArrayOutputStream> worldbankEodbiDataMap = getWorldbankEodbiDataMap(susaStatsConfig.worldbankEdbiCsvZipUrl);
    for (String fileName : worldbankEodbiDataMap.keySet()) {
      storage.save(fileName, worldbankEodbiDataMap.get(fileName).toByteArray(), null);
    }

    return "done";
  }

  @GetMapping("/api/storage-content-url")
  public String getStorageContnetUrl() {
    return storage.getListBlobsUrl();
  }

  @GetMapping("/api/storage-content")
  public List<BlobMetaData> getStorageMetadata() {
    return storage.getBlobMetadata();
  }

  private final static int BUFFER_SIZE = 2048;

  private Map<String, ByteArrayOutputStream> getWorldbankEodbiDataMap(String csvZipUrl) throws IOException {
    ResponseEntity<byte[]> eodbiMetadataZip = restTemplate.getForEntity(csvZipUrl, byte[].class);
    ByteArrayInputStream bis = new ByteArrayInputStream(Objects.requireNonNull(eodbiMetadataZip.getBody()));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BufferedOutputStream bos = new BufferedOutputStream(baos);

    byte[] buffer = new byte[BUFFER_SIZE];
    while (bis.read(buffer, 0, BUFFER_SIZE) != -1) {
      bos.write(buffer);
    }
    bos.flush();
    bos.close();
    bis.close();
    return unzip(baos);
  }

  private static Map<String, ByteArrayOutputStream> unzip(ByteArrayOutputStream zippedFileOS) {
    try {
      ZipInputStream inputStream = new ZipInputStream(
        new BufferedInputStream(new ByteArrayInputStream(
          zippedFileOS.toByteArray())));
      ZipEntry entry;

      Map<String, ByteArrayOutputStream> result = new HashMap<>();
      while ((entry = inputStream.getNextEntry()) != null) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.out.println("Extracting entry: " + entry);
        int count;
        byte[] data = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(
          outputStream, BUFFER_SIZE);
        while ((count = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
          out.write(data, 0, count);
        }
        out.flush();
        out.close();
        result.put(entry.getName(), outputStream);
      }
      inputStream.close();
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String getFileNameFromUrl(String url) {
    int indexOfLastForwardSlash = 0;

    for (int i = 0; i < url.length(); i++) {
      if (url.charAt(i) == '/') {
        indexOfLastForwardSlash = i;
      }
    }

    return url.substring(indexOfLastForwardSlash + 1);
  }
}
