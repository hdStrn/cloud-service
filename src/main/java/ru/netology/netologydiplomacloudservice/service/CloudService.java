package ru.netology.netologydiplomacloudservice.service;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiplomacloudservice.api.dto.FileDto;
import ru.netology.netologydiplomacloudservice.api.dto.FileInfoDto;

import java.util.List;

public interface CloudService {

    void saveFile(String filename, MultipartFile file);
    void deleteFile(String filename);
    FileDto downloadFile(String filename);
    void editFilename(String filename, String newName);
    List<FileInfoDto> getFiles(int limit);
}
