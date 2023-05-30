package ru.netology.netologydiplomacloudservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    schema = "cloud_service",
    indexes = {@Index(columnList = "filename", name = "filename_idx")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    private String hash;
    private String filename;
    private Long size;
    private LocalDateTime createdTime;
}
