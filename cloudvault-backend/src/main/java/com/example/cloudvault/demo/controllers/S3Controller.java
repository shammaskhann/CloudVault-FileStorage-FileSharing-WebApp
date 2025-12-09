package com.example.cloudvault.demo.controllers;


import com.example.cloudvault.demo.services.S3Service;
import com.example.cloudvault.demo.services.SavedFilesService;
import com.example.cloudvault.demo.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

//@RestController
//@RequestMapping("/api/s3")
//public class S3Controller {
//    @Autowired
//    private S3Service s3Service;
//
//    @PostMapping("/upload")
//    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException, IOException {
//        String url = s3Service.uploadFile(file);
//        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status",true,"url", url) );
//    }
//
//    @GetMapping("/download/{filename}")
//    public ResponseEntity<byte[]> download(@PathVariable String filename) {
//        byte[] data = s3Service.downloadFile(filename);
//        return ResponseEntity.ok()
//                .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
//                .body(data);
//    }
//}


@Slf4j
@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private SavedFilesService savedFilesService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        log.info("Uploading file " + file.getOriginalFilename());
        // Upload to S3
        String url = s3Service.uploadFile(file);
        log.info("Uploaded file " + url);
        // Save in DB
        Long userId = userService.findByEmail(userDetails.getUsername()).getUserId();
        log.info("User id " + userId);
        savedFilesService.saveFile(userId, url);
        log.info("Saved file " + url);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("status", true, "url", url));
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) {
        byte[] data = s3Service.downloadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(data);
    }

    //Delete File from S3 and DB
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable String fileId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
      try{
          // Delete from S3
          s3Service.deleteFile(fileId);

          // Delete from DB
          Long userId = userService.findByEmail(userDetails.getUsername()).getUserId();
          savedFilesService.deleteFile(Long.parseLong(fileId));

          return ResponseEntity.status(HttpStatus.OK)
                  .body(Map.of("status", true, "message", "File deleted successfully"));
      }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", false, "message", e.getMessage()));
      }
    }
}
