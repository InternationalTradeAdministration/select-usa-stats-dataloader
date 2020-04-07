package gov.ita.dataloader.ingest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ita.dataloader.ingest.configuration.DataloaderConfig;
import gov.ita.dataloader.security.AuthenticationFacade;
import gov.ita.dataloader.storage.BlobMetaData;
import gov.ita.dataloader.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class IngestController {

  private Storage storage;
  private AutomatedIngestProcessor automatedIngestProcessor;
  private AuthenticationFacade authenticationFacade;
  private ObjectMapper objectMapper;
  private TranslationProcessor translationProcessor;
  private Map<String, CompletableFuture> processes = new HashMap<>();

  public IngestController(Storage storage,
                          AutomatedIngestProcessor automatedIngestProcessor,
                          AuthenticationFacade authenticationFacade,
                          ObjectMapper objectMapper,
                          TranslationProcessor translationProcessor) {
    this.storage = storage;
    this.automatedIngestProcessor = automatedIngestProcessor;
    this.authenticationFacade = authenticationFacade;
    this.objectMapper = objectMapper;
    this.translationProcessor = translationProcessor;
  }

  @GetMapping(value = "/api/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
  private DataloaderConfig getDataSetConfigs(@RequestParam("containerName") String containerName) {
    return getDataloaderConfig(containerName);
  }

  //  @PreAuthorize("hasRole('ROLE_TSI_AllUsers')")
  @GetMapping("/api/ingest")
  public String startIngestProcess(@RequestParam("containerName") String containerName) {
    if (processes.get(containerName) != null && !processes.get(containerName).isDone())
      return "running";
    CompletableFuture<String> process = automatedIngestProcessor.process(
      getDataloaderConfig(containerName).getDataSetConfigs(),
      containerName,
      authenticationFacade.getUserName(),
      5000);
    processes.put(containerName, process);
    return "started";
  }

  @GetMapping(value = "/api/automated-ingest/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public ProcessorStatus getAutomatedIngestStatus(@RequestParam("containerName") String containerName) {
    return new ProcessorStatus(
      processes.get(containerName) != null ? processes.get(containerName).isDone() : null,
      automatedIngestProcessor.getLog(containerName)
    );
  }

  @GetMapping("/api/automated-ingest/log/clear")
  public void clearAutomatedIngestLog(@RequestParam("containerName") String containerName) {
    automatedIngestProcessor.clearLog(containerName);
  }

  @GetMapping(value = "/api/automated-ingest/stop", produces = MediaType.APPLICATION_JSON_VALUE)
  public void stopAutomatedIngest(@RequestParam("containerName") String containerName) {
    automatedIngestProcessor.stopProcessing(containerName);
  }

  //  @PreAuthorize("hasRole('ROLE_TSI_AllUsers')")
  @PutMapping("/api/file")
  public void saveFile(@RequestParam("file") MultipartFile file,
                       @RequestParam("containerName") String containerName,
                       @RequestParam("pii") Boolean pii) throws IOException {
    storage.save(file.getOriginalFilename(), file.getBytes(), authenticationFacade.getUserName(), containerName, true, pii);
    storage.makeSnapshot(containerName, file.getOriginalFilename());
    translationProcessor.initProcessing(containerName, file.getOriginalFilename(), file.getBytes(), authenticationFacade.getUserName());
  }

  //  @PreAuthorize("hasRole('ROLE_TSI_AllUsers')")
  @DeleteMapping("/api/file")
  public void deleteFile(@RequestParam("fileName") String fileName,
                         @RequestParam("containerName") String containerName,
                         @RequestParam(name = "snapshot", defaultValue = "") String snapshot) {
    if (snapshot.equals("")) {
      storage.delete(containerName, fileName);
    } else {
      storage.delete(containerName, fileName, snapshot);
    }
  }

  //  @PreAuthorize("hasRole('ROLE_TSI_AllUsers')")
  @GetMapping("/api/reprocess/file")
  public void reProcessFile(@RequestParam("containerName") String containerName, @RequestParam("fileName") String fileName) {
    translationProcessor.reProcess(containerName, fileName);
  }

  //  @PreAuthorize("hasRole('ROLE_TSI_AllUsers')")
  @PutMapping("/api/configuration")
  public void saveConfiguration(@RequestBody DataloaderConfig dataloaderConfig,
                                @RequestParam("containerName") String containerName) throws JsonProcessingException {
    byte[] dataSetConfigsJsonBytes = objectMapper.writeValueAsString(dataloaderConfig).getBytes();
    storage.save("configuration.json", dataSetConfigsJsonBytes, authenticationFacade.getUserName(), containerName, true, false);
    storage.makeSnapshot(containerName, "configuration.json");
  }

  @GetMapping("/api/storage-content-url")
  public String getStorageContentUrl(@RequestParam("containerName") String containerName) {
    return storage.getListBlobsUrl(containerName);
  }

  @GetMapping(value = "/api/container-metadata", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BlobMetaData> getStorageMetadata(@RequestParam("containerName") String containerName) {
    return storage.getBlobMetadata(containerName, true).stream()
      .map(this::handleLegacyBlobMetadata)
      .collect(Collectors.toList());
  }

  private BlobMetaData handleLegacyBlobMetadata(BlobMetaData blobMetaData) {
    Map<String, String> metadata = blobMetaData.getMetadata();
    if (metadata == null) {
      metadata = new HashMap<>();
      metadata.put("uploaded_by", "---");
      metadata.put("user_upload", "true");
      metadata.put("pii", "false");
    }

    if (!metadata.containsKey("uploaded_by")) metadata.put("uploaded_by", "---");
    if (!metadata.containsKey("user_upload")) metadata.put("user_upload", "true");
    if (!metadata.containsKey("pii")) metadata.put("pii", "false");
    blobMetaData.setMetadata(metadata);
    return blobMetaData;
  }

  @PostMapping(value = "/api/download-blob")
  public ResponseEntity<Resource> downloadBlob(@RequestBody BlobDownloadRequest blobDownloadRequest) {
    String containerName = blobDownloadRequest.getContainerName();
    String blobName = blobDownloadRequest.getBlobName();
    String snapshot = blobDownloadRequest.getSnapshot();
    byte[] blob = (blobDownloadRequest.getSnapshot() != null) ?
      storage.getBlob(containerName, blobName, snapshot) :
      storage.getBlob(containerName, blobName);
    ByteArrayResource resource = new ByteArrayResource(blob);

    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment;filename=\"" + blobName + "\"")
      .contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(blob.length)
      .body(resource);
  }

  private DataloaderConfig getDataloaderConfig(String containerName) {
    try {
      byte[] blob = storage.getBlob(containerName, "configuration.json");
      return objectMapper.readValue(blob, DataloaderConfig.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
