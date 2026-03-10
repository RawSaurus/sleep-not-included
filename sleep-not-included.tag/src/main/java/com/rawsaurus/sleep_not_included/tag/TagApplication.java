package com.rawsaurus.sleep_not_included.tag;

import com.rawsaurus.sleep_not_included.tag.model.Tag;
import com.rawsaurus.sleep_not_included.tag.model.Type;
import com.rawsaurus.sleep_not_included.tag.repo.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.ArrayList;
import java.util.List;

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
public class TagApplication {

	public static void main(String[] args) {
		SpringApplication.run(TagApplication.class, args);
	}

    @Bean
    @Profile("dev")
    public CommandLineRunner runner(TagRepository tagRepo){
        return args -> {
            if(!tagRepo.existsById(1L)){
                List<Tag> tagsToSave = new ArrayList<>();
                Tag tag1 = Tag.builder()
                        .name("Base")
                        .type(Type.DLC)
                        .build();
                tagsToSave.add(tag1);
                Tag tag2 = Tag.builder()
                        .name("Spaced Out")
                        .type(Type.DLC)
                        .build();
                tagsToSave.add(tag2);
                Tag tag3 = Tag.builder()
                        .name("Frozen Planet")
                        .type(Type.DLC)
                        .build();
                tagsToSave.add(tag3);
                Tag tag4 = Tag.builder()
                        .name("SPOM")
                        .type(Type.BUILD)
                        .build();
                tagsToSave.add(tag4);
                Tag tag5 = Tag.builder()
                        .name("Rocket")
                        .type(Type.BUILD)
                        .build();
                tagsToSave.add(tag5);
                Tag tag6 = Tag.builder()
                        .name("Ranch")
                        .type(Type.BUILD)
                        .build();
                tagsToSave.add(tag6);
                Tag tag7 = Tag.builder()
                        .name("Farm")
                        .type(Type.BUILD)
                        .build();
                tagsToSave.add(tag7);
                Tag tag8 = Tag.builder()
                        .name("Refinement")
                        .type(Type.BUILD)
                        .build();
                tagsToSave.add(tag8);
                Tag tag9 = Tag.builder()
                        .name("Industrial")
                        .type(Type.BUILD)
                        .build();
                tagsToSave.add(tag9);
                Tag tag10 = Tag.builder()
                        .name("Decoration")
                        .type(Type.BUILD)
                        .build();
                tagsToSave.add(tag10);
                Tag tag11 = Tag.builder()
                        .name("Other")
                        .type(Type.BUILD)
                        .build();
                tagsToSave.add(tag11);
                tagRepo.saveAll(tagsToSave);
            }
        };
    }

}
