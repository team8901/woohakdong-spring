package woohakdong.server.common.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SchoolRepository schoolRepository;

    public DataInitializer(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (schoolRepository.count() == 0) {
            schoolRepository.save(School.builder()
                    .schoolName("아주대학교")
                    .schoolDomain("ajou.ac.kr")
                    .build());
        }
    }
}
