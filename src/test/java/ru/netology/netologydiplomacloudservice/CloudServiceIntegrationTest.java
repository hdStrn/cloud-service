package ru.netology.netologydiplomacloudservice;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloudServiceIntegrationTest {

    @Container
    private static final GenericContainer<?> cloudService =
        new GenericContainer<>("netology-diploma-cloud-service-cloud_service").withExposedPorts(8080);
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private int mappedPort;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        this.mappedPort = cloudService.getMappedPort(8080);
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testUploadFileWhenPostCorrectDataThenStatusOk() throws Exception {
        final String url = "http://localhost:" + mappedPort + "/file";
        final String filename = "test.txt";
        final String fileContent = "just for testing";

        MockMultipartFile file = new MockMultipartFile(
            "file", filename, MediaType.MULTIPART_FORM_DATA.getType(), fileContent.getBytes());

        mockMvc.perform(multipart(url).file(file).param("filename", filename))
            .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @WithMockUser(authorities = {"ROLE_USER"})
    void testUploadFileWhenPostWithUserRoleThenReturnStatus401() throws Exception {
        final String url = "http://localhost:" + mappedPort + "/file";
        final String filename = "test2.txt";
        final String fileContent = "just for testing";

        MockMultipartFile file = new MockMultipartFile(
            "file", filename, MediaType.MULTIPART_FORM_DATA.getType(), fileContent.getBytes());

        mockMvc.perform(multipart(url).file(file).param("filename", filename))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testUploadFileWhenPostWithExistingFilenameThenReturnError() throws Exception {
        final String url = "http://localhost:" + mappedPort + "/file";
        final String filename = "test.txt";
        final String fileContent = "just for testing";

        MockMultipartFile file = new MockMultipartFile(
            "file", filename, MediaType.MULTIPART_FORM_DATA.getType(), fileContent.getBytes());

        mockMvc.perform(multipart(url).file(file).param("filename", filename))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @Order(4)
    @WithMockUser(authorities = {"ROLE_USER"})
    public void testGetFilesWhenGetThenReturnStatusListOfFiles() throws Exception {
        final String url = "http://localhost:" + mappedPort + "/list";

        mockMvc.perform(get(url).param("limit", "3"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].filename").value("test.txt"))
            .andExpect(jsonPath("$[0].size").value(16));
    }

    @Test
    @Order(5)
    @WithMockUser(authorities = {"ROLE_USER"})
    void testDownloadFileWhenGetWithCorrectFilenameThenReturnStatusOk() throws Exception {
        final String url = "http://localhost:" + mappedPort + "/file";
        final String filename = "test.txt";

        mockMvc.perform(get(url).param("filename", filename))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.hash").exists())
            .andExpect(jsonPath("$.file").exists());;
    }

    @Test
    @Order(6)
    @WithMockUser(authorities = {"ROLE_USER"})
    void testDownloadFileWhenGetWithNotExistingFilenameThenReturnError() throws Exception {
        final String url = "http://localhost:" + mappedPort + "/file";
        final String filename = "test3.txt";

        mockMvc.perform(get(url).param("filename", filename))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.message", containsString("not exist")));
    }

    @Test
    @Order(7)
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testDeleteFileWhenDeleteWithCorrectFilenameThenReturnStatusOk() throws Exception {
        final String url = "http://localhost:" + mappedPort + "/file";
        final String filename = "test.txt";

        mockMvc.perform(delete(url).param("filename", filename))
            .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testDeleteFileWhenDeleteWithNotExistingFilenameThenReturnError() throws Exception {
        final String url = "http://localhost:" + mappedPort + "/file";
        final String filename = "test.txt";

        mockMvc.perform(delete(url).param("filename", filename))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.message", containsString("not exist")));
    }
}
