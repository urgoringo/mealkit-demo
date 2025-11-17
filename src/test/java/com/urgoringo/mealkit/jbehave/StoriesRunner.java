package com.urgoringo.mealkit.jbehave;

import com.urgoringo.mealkit.TestContainersConfiguration;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

/**
 * JBehave story runner that executes all .md story files from src/test/resources/spec directory.
 * Integrates with Spring Boot Test and TestContainers for database testing.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
public class StoriesRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void runStories() {
        Embedder embedder = new Embedder();

        Configuration configuration = new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(this.getClass()))
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withDefaultFormats()
                        .withFormats(Format.CONSOLE, Format.TXT, Format.HTML, Format.XML)
                        .withFailureTrace(true));

        embedder.useConfiguration(configuration);

        InjectableStepsFactory stepsFactory = new SpringStepsFactory(configuration, applicationContext);
        embedder.useStepsFactory(stepsFactory);

        // Find all .md files in the spec directory
        // Get the path to the test resources directory
        String testResourcesPath = this.getClass().getClassLoader().getResource("spec").getPath();
        String basePath = testResourcesPath.substring(0, testResourcesPath.lastIndexOf("/spec"));

        List<String> storyPaths = new StoryFinder().findPaths(
                basePath,
                "spec/**/*.md",  // Look in spec directory for .md files
                ""
        );

        embedder.runStoriesAsPaths(storyPaths);
    }
}
