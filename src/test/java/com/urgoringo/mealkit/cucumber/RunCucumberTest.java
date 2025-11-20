package com.urgoringo.mealkit.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Cucumber test runner that executes all .feature files from src/test/resources/spec directory.
 * Uses JUnit Platform Suite to run Cucumber scenarios.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("spec")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.urgoringo.mealkit")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
public class RunCucumberTest {
}
