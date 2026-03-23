package guru.springframework.spring7restmvc.repositories;

import guru.springframework.spring7restmvc.controller.BeerController;
import guru.springframework.spring7restmvc.entities.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("localpostgres")
public class PostgresIT {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    void testListBeers() {
        List<Beer> beers = beerRepository.findAll();
        assertThat(beers.size()).isGreaterThan(0);
    }

    @Test
    void testDeleteById() throws Exception {
        UUID testId = beerRepository.findAll().getFirst().getId();

        mockMvc.perform(delete(BeerController.BEER_PATH_ID,testId)
                        .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        beerRepository.flush();
        assertThat(beerRepository.findById(testId)).isEmpty();
    }
}