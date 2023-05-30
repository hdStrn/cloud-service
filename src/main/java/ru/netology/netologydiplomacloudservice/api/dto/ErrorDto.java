package ru.netology.netologydiplomacloudservice.api.dto;

import lombok.Data;
import ru.netology.netologydiplomacloudservice.utils.NumericIdGenerator;

@Data
public class ErrorDto {

    private Integer id;
    private String message;

    public ErrorDto(String message) {
        this.id = NumericIdGenerator.generateId();
        this.message = message;
    }
}
