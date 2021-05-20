/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report;

import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.utils.ReportConfiguration;
import fr.cnes.sonar.report.utils.StringManager;
import org.junit.Before;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Contains common code for report
 */
public abstract class CommonTest {

    /**
     * Severity for stubbed violations.
     */
    private static final String MAJOR = "MAJOR";
    /**
     * Project key.
     */
    protected static final String PROJECT_KEY = "cnesreport";
    /**
     * Branch name.
     */
    protected static final String BRANCH = "master";
    /**
     * Quality Gate name.
     */
    protected static final String QUALITY_GATE_NAME = "CNES";
    /**
     * Stubbed report for report.
     */
    protected Report report;
    /**
     * Stubbed sonarqube server for report.
     */
    protected SonarQubeServer sonarQubeServer;
    /**
     * stubbed parameters for testing.
     */
    protected ReportConfiguration conf;

    /**
     * Setting of all stubbed resources before launching a test.
     */
    @Before
    public void before() {
        report = new StubReport();
        conf = ReportConfiguration.create(new String[]{
                "-s", "http://sonarqube:9000",
                "-p", PROJECT_KEY,
                "-a", "Lequal",
                "-b", BRANCH,
                "-d", new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date()),
                "-o", "./target",
                "-l", "en_US",
                "-r", "src/main/resources/template/code-analysis-template.docx",
                "-x", "src/main/resources/template/issues-template.xlsx"
        });

        report.setProjectName("CNES Report");
        report.setProjectDate(new Date().toString().substring(0,16));
        report.setProjectAuthor("Lequal");

        sonarQubeServer = new SonarQubeServer();
        sonarQubeServer.setStatus("UP");
        sonarQubeServer.setUrl("http://biiiiiiiiiiiiim");
        sonarQubeServer.setVersion("6.7.5", true);

        final List<Issue> issues = new ArrayList<>();
        final Issue i1 = new Issue();
        final Issue i2 = new Issue();
        final Issue i3 = new Issue();
        final Issue i4 = new Issue();
        i1.setComponent("a");
        i1.setKey("z");
        i1.setLine("15");
        i1.setMessage("azerty");
        i1.setProject("genius");
        i1.setSeverity(MAJOR);
        i1.setStatus("OPEN");
        i1.setType("BUG");
        i1.setRule("squid4321");
        i3.setComponent("a");
        i3.setKey("z");
        i3.setLine("15");
        i3.setMessage("azerty");
        i3.setProject("genius");
        i3.setSeverity(MAJOR);
        i3.setStatus("OPEN");
        i3.setType("BUG");
        i2.setComponent("b");
        i2.setKey("x");
        i2.setLine("23");
        i2.setMessage("qwertz");
        i2.setProject("genius");
        i2.setSeverity(MAJOR);
        i2.setStatus("OPEN");
        i2.setType("SECURITY_HOTSPOT");
        i2.setRule("abcd:dcba");
        i4.setType(StringManager.HOTSPOT_TYPE);
        // Adding multiple time to test comparator (DataAdapter.RuleComparator)
        Issue issue;
        for(int i=1;i<10;i++){
            issue = new Issue();
            issue.setRule("squid:1234");
            issue.setMessage("ISSUES");
            issue.setSeverity(MAJOR);
            issue.setKey("key");
        }
        issues.add(i1);
        issues.add(i3);
        issues.add(i2);
        issues.add(i4);
        report.setIssues(issues);

        List<Map<String,String>> rawIssues = new ArrayList<>();
        Map issue1 = new HashMap();
        issue1.put("Comments", new ArrayList<String>());
        issue1.put("ToReview", true);
        issue1.put("someNumber", 1.0);
        rawIssues.add(issue1);

        Map issue2 = new HashMap();
        List list = new ArrayList();
        list.add("Element 1");
        list.add("Element 2");
        issue2.put("Comments", list);
        issue2.put("someNumber", 2.0);
        rawIssues.add(issue2);

        report.setRawIssues(rawIssues);

        final List<Facet> facets = new ArrayList<>();
        final Facet rules = new Facet();
        final Facet severities = new Facet();
        final Facet types = new Facet();
        rules.setProperty("rules");
        severities.setProperty("severities");
        types.setProperty("types");
        final List<Value> values = new ArrayList<>();
        values.add(new Value("squid:S1258", 3));
        rules.setValues(values);
        final List<Value> valuesSeverity = new ArrayList<>();
        valuesSeverity.add(new Value("CRITICAL", 0));
        final List<Value> valuesType = new ArrayList<>();
        valuesType.add(new Value("SECURITY_HOTSPOT", 5));
        severities.setValues(valuesSeverity);
        types.setValues(valuesType);
        facets.add(rules);
        facets.add(severities);
        facets.add(types);
        report.setFacets(facets);

        final ProfileData profileData = new ProfileData();
        profileData.setConf("coucou");
        final List<Rule> rulesOfProfile = new ArrayList<>();
        final Rule rule1 = new Rule();
        rule1.setKey("squid:S1258");
        rule1.setName("Nom swag");
        rule1.setHtmlDesc("Cette description est pas trop longue donc ça va en fait, faut pas s'inquiéter.");
        rule1.setSeverity(MAJOR);
        rule1.setType("BUG");
        rulesOfProfile.add(rule1);
        profileData.setRules(rulesOfProfile);
        final ProfileMetaData profileMetaData = new ProfileMetaData();
        profileMetaData.setName("BG");
        profileMetaData.setKey("BG");
        final QualityProfile qualityProfile = new QualityProfile(profileData, profileMetaData);
        qualityProfile.setProjects((new Project[]{new Project("sonar-cnes-plugin", "sonar-cnes-plugin", "none", "", "", "")}));
        report.setQualityProfiles(Collections.singletonList(qualityProfile));
        final QualityGate qualityGate = new QualityGate();
        qualityGate.setName(QUALITY_GATE_NAME);
        report.setQualityGate(qualityGate);

        final Language language = new Language();
        language.setKey("java");
        language.setName("Java");

        final Map<String, Language> languages = new HashMap<>();
        languages.put(language.getKey(), language);

        final Project project = new Project("key", "Name", "none","Version", "Short description", "");
        project.setQualityProfiles(new ProfileMetaData[0]);
        project.setLanguages(languages);
        report.setProject(project);

        final List<Measure> measures = new ArrayList<>();
        measures.add(new Measure("reliability_rating", "1.0"));
        measures.add(new Measure("duplicated_lines_density", "1.0"));
        measures.add(new Measure("sqale_rating", "2.0"));
        measures.add(new Measure("coverage", "1.0"));
        measures.add(new Measure("ncloc", "1.0"));
        measures.add(new Measure("alert_status", "1.0"));
        measures.add(new Measure("security_rating", "3.0"));
        report.setMeasures(measures);


        Map<String, Double> metricStats = new HashMap<>();
        // Use first component to gets all metrics names

        // for each metric
        metricStats.put("mincomplexity", 0.0);
        metricStats.put("maxcomplexity", 1.0);
        metricStats.put("minncloc", 0.0);
        metricStats.put("maxncloc", 1.0);
        metricStats.put("mincomment_lines_density", 0.0);
        metricStats.put("maxcomment_lines_density", 1.0);
        metricStats.put("minduplicated_lines_density", 0.0);
        metricStats.put("maxduplicated_lines_density", 1.0);
        metricStats.put("mincognitive_complexity", 0.0);
        metricStats.put("maxcognitive_complexity", 1.0);
        metricStats.put("mincoverage", 0.0);
        // Max coverage is not included to raise a NullPointerException


        report.setMetricsStats(metricStats);
    }
}
