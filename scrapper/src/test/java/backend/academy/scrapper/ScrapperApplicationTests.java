package backend.academy.scrapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Disabled
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ScrapperApplicationTests {

    @Test
    void contextLoads() {}
}
