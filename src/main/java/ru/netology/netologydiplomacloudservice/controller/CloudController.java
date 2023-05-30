package ru.netology.netologydiplomacloudservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiplomacloudservice.api.dto.FileDto;
import ru.netology.netologydiplomacloudservice.api.dto.FileInfoDto;
import ru.netology.netologydiplomacloudservice.service.CloudService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CloudController {

    private final CloudService cloudService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(@RequestParam String filename, @RequestBody MultipartFile file) {
        cloudService.saveFile(filename, file);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@RequestParam String filename) {
        cloudService.deleteFile(filename);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    public FileDto downloadFile(@RequestParam String filename) {
        return cloudService.downloadFile(filename);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    public void editFileName(@RequestParam String filename, @RequestBody Map<String, String> newName) {
        cloudService.editFilename(filename, newName.get("filename"));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<FileInfoDto> getFiles(@RequestParam int limit) {
        return cloudService.getFiles(limit);
    }
}
