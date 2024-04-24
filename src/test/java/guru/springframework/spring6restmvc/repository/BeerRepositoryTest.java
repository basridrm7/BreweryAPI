package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entity.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.service.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testGetBeerListByName() {
        Page<Beer> page = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null);

        assertThat(page.getContent().size()).isEqualTo(336);
    }

    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("My Beer")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("234234234234")
                .price(new BigDecimal("11.99"))
                .build());

        beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }

    @Test
    void testSaveBeer_withTooLongName() {
        assertThrows(ConstraintViolationException.class, () -> {
            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("My Beer With Too Long Name, My Beer With Too Long Name, My Beer With Too Long Name, " +
                            "My Beer With Too Long Name, My Beer With Too Long Name, My Beer With Too Long Name, " +
                            "My Beer With Too Long Name, My Beer With Too Long Name, My Beer With Too Long Name,")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("234234234234")
                    .price(new BigDecimal("11.99"))
                    .build());

            beerRepository.flush();
        });
    }
}