package ru.netology.netologydiplomacloudservice.mapper;

import org.mapstruct.Mapper;
import ru.netology.netologydiplomacloudservice.api.dto.FileInfoDto;
import ru.netology.netologydiplomacloudservice.entity.File;

@Mapper(componentModel = "spring")
public interface FileInfoMapper {

    FileInfoDto fileToFileInfoDto(File file);
    File fileInfoDTOToFile(FileInfoDto fileInfoDTO);
}
