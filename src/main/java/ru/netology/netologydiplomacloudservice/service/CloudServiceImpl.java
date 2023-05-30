package ru.netology.netologydiplomacloudservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiplomacloudservice.api.dto.FileDto;
import ru.netology.netologydiplomacloudservice.api.dto.FileInfoDto;
import ru.netology.netologydiplomacloudservice.entity.File;
import ru.netology.netologydiplomacloudservice.exception.FileProcessingException;
import ru.netology.netologydiplomacloudservice.exception.IncorrectInputDataException;
import ru.netology.netologydiplomacloudservice.mapper.FileInfoMapper;
import ru.netology.netologydiplomacloudservice.repository.CloudRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CloudServiceImpl implements CloudService {

    private final CloudRepository cloudRepository;
    private final FileManager fileManager;
    private final FileInfoMapper mapper;

    @Override
    public void saveFile(String filename, MultipartFile file) {
        try {
            log.info("Checking the existence of file {}", filename);
            if (cloudRepository.findByFilename(filename).isPresent()) {
                throw new IncorrectInputDataException(String.format("File with name %s already exists", filename));
            }

            if (file == null) {
                throw new IncorrectInputDataException("File is not attached to request");
            }

            File uploadedFile = createFileInfo(filename, file);

            log.info("Uploading file {} to storage..", filename);
            fileManager.uploadFile(file.getBytes(), uploadedFile.getHash(), filename);
            log.info("File {} uploaded to storage", filename);

            log.info("Saving file info of {} to database..", filename);
            cloudRepository.save(uploadedFile);
            log.info("File info of {} saved to database", filename);

        } catch (IOException ex) {
            throw new FileProcessingException(ex.getMessage());
        }
    }

    @Override
    public void deleteFile(String filename) {
        File fileToDelete = getExistingFile(filename);

        try {
            log.info("Deleting file {} from storage..", filename);
            fileManager.deleteFile(fileToDelete.getHash());
            log.info("file {} deleted from storage", filename);

            log.info("Deleting file info of {} from database", filename);
            cloudRepository.delete(fileToDelete);
            log.info("File info of {} deleted from database", filename);

        } catch (Exception ex) {
            throw new FileProcessingException(ex.getMessage());
        }
    }

    @Override
    public FileDto downloadFile(String filename) {
        File file = getExistingFile(filename);

        try {
            log.info("Downloading file {} from storage..", filename);
            String hash = file.getHash();
            Resource fileContent = fileManager.downloadFile(hash);
            log.info("File {} downloaded from storage", filename);

            return FileDto.builder()
                .hash(hash)
                .file(fileContent.toString())
                .build();

        } catch (Exception ex) {
            throw new FileProcessingException(ex.getMessage());
        }
    }

    @Override
    public void editFilename(String filename, String newName) {
        File file = getExistingFile(filename);
        file.setFilename(newName);
        cloudRepository.save(file);
    }

    @Override
    public List<FileInfoDto> getFiles(int limit) {
        log.info("Getting the file list..");
        return cloudRepository.findAll(Pageable.ofSize(limit))
            .map(mapper::fileToFileInfoDto)
            .toList();
    }

    private File createFileInfo(String filename, MultipartFile file) throws IOException {
        LocalDateTime createdTime = LocalDateTime.now();

        String hash = UUID.nameUUIDFromBytes(
            ArrayUtils.addAll(file.getBytes(), createdTime.toString().getBytes())).toString();

        return File.builder()
            .hash(hash)
            .filename(filename)
            .size(file.getSize())
            .createdTime(createdTime)
            .build();
    }

    private File getExistingFile(String filename) {
        return cloudRepository.findByFilename(filename).orElseThrow(
            () -> new IncorrectInputDataException(String.format("File with name %s does not exist", filename)));
    }
}
